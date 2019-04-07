package me.liqiu.mybatisgeneratetools.model;

import lombok.Data;

import java.util.List;

@Data
public class TableModel {

    private String tableName;

    private List<String> columnNames;

    private List<String> columnTypes;

}
