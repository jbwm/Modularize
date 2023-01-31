package pl.koral.apitest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SimpleBinderModule extends AbstractModule {

    private final ApiTest plugin;

    public SimpleBinderModule(ApiTest plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(ApiTest.class).toInstance(this.plugin);
    }

}
