package me.liqiu.mybatisgeneratetools.transformer;

import me.liqiu.mybatisgeneratetools.model.TableModel;

public interface TableTransformFilter {

    boolean filter(TableModel model);
}
