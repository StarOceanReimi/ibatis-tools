package me.liqiu.mybatisgeneratetools.transformer;

import me.liqiu.mybatisgeneratetools.model.TableModel;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Transformer<R> extends Function<TableModel, R>, Predicate<TableModel> {

}
