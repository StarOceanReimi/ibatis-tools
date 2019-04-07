package me.liqiu.mybatisgeneratetools.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PostConstructTypeListener implements TypeListener  {
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        encounter.register(new PostConstructInjectionListener());
    }
}
