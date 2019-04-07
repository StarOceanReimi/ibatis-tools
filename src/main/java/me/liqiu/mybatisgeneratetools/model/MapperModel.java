package me.liqiu.mybatisgeneratetools.model;

import lombok.Data;

import java.util.List;

@Data
public class MapperModel {

    private String mapperName;

    private List<String> resultMaps;

    public String getFilePath() {
        return mapperName.replaceAll("\\.", "/") + "Mapper.xml";
    }
}
