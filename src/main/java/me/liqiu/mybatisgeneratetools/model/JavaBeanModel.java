package me.liqiu.mybatisgeneratetools.model;

import lombok.Data;

import java.util.List;

@Data
public class JavaBeanModel {

    private String packageName;

    private List<ClassField> classFields;

    private List<String> importClasses;

    private String className;

    private String tableName;

    private List<String> tableColumnNames;

    public String getFilePath() {

        return (packageName + "." + className).replaceAll("\\.", "/") + ".java";
    }
}
