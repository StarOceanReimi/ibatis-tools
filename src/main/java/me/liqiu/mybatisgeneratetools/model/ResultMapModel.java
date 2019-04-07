package me.liqiu.mybatisgeneratetools.model;

import lombok.Data;

import java.util.List;

@Data
public class ResultMapModel {

    private String objectName;

    private String objectFullName;

    private List<String> tableColumnNames;

    private List<String> objectPropertyNames;
}
