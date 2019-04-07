package me.liqiu.mybatisgeneratetools;

import com.google.common.base.Strings;
import com.google.common.collect.Streams;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.liqiu.mybatisgeneratetools.guice.InjectProperty;
import me.liqiu.mybatisgeneratetools.guice.PostConstructTypeListener;
import me.liqiu.mybatisgeneratetools.guice.PropertiesFilesTypeListener;
import me.liqiu.mybatisgeneratetools.model.MapperModel;
import me.liqiu.mybatisgeneratetools.model.TableModel;
import me.liqiu.mybatisgeneratetools.transformer.JavaBeanTransformer;
import me.liqiu.mybatisgeneratetools.transformer.ResultMapTransformer;
import me.liqiu.mybatisgeneratetools.transformer.ScriptTransformerHelper;
import me.liqiu.mybatisgeneratetools.util.DbUtil;
import me.liqiu.mybatisgeneratetools.util.FreemarkerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

@Singleton
@Slf4j
public class Application {

    private static class ApplicationModule extends AbstractModule {

        private Properties applicationConfig;

        public ApplicationModule(Properties applicationConfig) {
            this.applicationConfig = applicationConfig;
        }

        @Provides
        @Singleton
        ScriptEngine buildScriptEngine() {
            ScriptEngineManager manager = new ScriptEngineManager();
            return manager.getEngineByExtension("js");
        }

        @Override
        protected void configure() {

            val listener = new PropertiesFilesTypeListener(applicationConfig);
            val postConstructListener = new PostConstructTypeListener();
            bindListener(Matchers.any(), listener);
            bindListener(Matchers.any(), postConstructListener);
            binder().bind(Properties.class)
                    .annotatedWith(Names.named("appConfig"))
                    .toInstance(applicationConfig);
        }
    }

    @Inject
    private DbUtil db;

    @Inject
    private ResultMapTransformer resultMapTransformer;

    @Inject
    private JavaBeanTransformer javaBeanTransformer;

    @Inject
    private ScriptTransformerHelper scriptHelper;

    @InjectProperty("outputDir")
    private String outputDir;

    @InjectProperty("inputTableName")
    private String inputTableName;

    @InjectProperty("mapper.mapperName")
    private String mapperName;

    @InjectProperty("db.columnStrategy")
    private String dbColumnStrategy;

    public void run(String[] args) {


        log.info("fetching database..");
        List<TableModel> models = null;

        if ("model".equals(dbColumnStrategy)) {
            if(Strings.isNullOrEmpty(inputTableName)) {
                List<String> tableNames = db.getTableNamesBySchema("my_sample_db");
                models = tableNames.stream()
                        .map(db::buildModelByTableName)
                        .collect(toList());
            } else {
                models = Stream.of(inputTableName)
                        .map(db::buildModelByTableName)
                        .collect(toList());
            }
        } else {
            val viewNameMap = scriptHelper.getViewNameMap();
            models = viewNameMap.entrySet().stream()
                    .map((entry) -> {
                        val model = (TableModel) db.buildModelBySql(entry.getValue());
                        model.setTableName(entry.getKey());
                        return model;
                    })
                    .collect(toList());
        }

        log.info("generating models..");

        File baseDir = Paths.get(USER_DIR, outputDir).toFile();
        if(!baseDir.exists()) {
            if(!baseDir.mkdirs()) {
                throw new RuntimeException("can not make base dir: " + baseDir.toString());
            }
        }

        FreemarkerTemplate resultMapTemplate = new FreemarkerTemplate("template/ResultMap.ftl");
        List<String> resultMaps = models.stream()
                .filter(resultMapTransformer)
                .map(resultMapTransformer)
                .map(resultMapTemplate::compileToString)
                .collect(toList());
        ;

        FreemarkerTemplate mapperTemplate = new FreemarkerTemplate("template/MapperTemplate.ftl");
        MapperModel mapperModel = new MapperModel();
        mapperModel.setMapperName(mapperName);
        mapperModel.setResultMaps(resultMaps);
        val mapperContent = mapperTemplate.compileToString(mapperModel);
        writeToFile(mapperModel.getFilePath(), mapperContent);

        FreemarkerTemplate modelTemplate = new FreemarkerTemplate("template/ModelTemplate.ftl");
        val javaBeanModel = models.stream()
                .filter(javaBeanTransformer)
                .map(javaBeanTransformer)
                .collect(toList());
        Stream<String> filePathStream = javaBeanModel.stream().map(m -> m.getFilePath());
        Stream<String> fileContentStream = javaBeanModel.stream().map(modelTemplate::compileToString);
        Streams.forEachPair(filePathStream, fileContentStream, this::writeToFile);


    }

    public void writeToFile(String path, String content) {
        File file = Paths.get(USER_DIR, outputDir, path).toFile();
        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            CharSource.wrap(content).copyTo(Files.asCharSink(file, UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        log.info("program start..");
        log.info("loading config..");
        Properties properties = new Properties();
        String configPath = "config.properties";
        if(args.length > 0)
            configPath = args[0];

        File f = Paths.get(USER_DIR, configPath).toFile();

        if(!f.exists()) {
            log.error("missing config file.");
            System.exit(-1);
        }

        properties.load(Files.newReader(f, UTF_8));
        log.info("initialize di framework..");
        Injector injector = Guice.createInjector(new ApplicationModule(properties));
        Application app = injector.getInstance(Application.class);
        app.run(args);
        log.info("generation completed.");
    }
}















