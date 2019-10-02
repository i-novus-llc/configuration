package ru.i_novus.config.web;

import net.n2oapp.framework.config.compile.pipeline.operation.CompileCacheOperation;
import net.n2oapp.framework.config.compile.pipeline.operation.SourceCacheOperation;
import net.n2oapp.framework.mvc.cache.ClientCacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.i_novus.config.web.cache.ConfigCompileCacheOperation;
import ru.i_novus.config.web.cache.ConfigModifiedClientCacheTemplate;
import ru.i_novus.config.web.cache.ConfigSourceCacheOperation;

@SpringBootApplication
public class ConfigWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigWebApplication.class, args);
    }

    @Autowired
    private CacheManager cacheManager;

    @Bean
    public SourceCacheOperation getSourceCacheOperation() {
        return new ConfigSourceCacheOperation();
    }

    @Bean
    public CompileCacheOperation getCompileCacheOperation() {
        return new ConfigCompileCacheOperation();
    }

    @Bean
    @Primary
    public ClientCacheTemplate getModifiedClientCacheTemplate() {
        return new ConfigModifiedClientCacheTemplate(cacheManager);
    }
}
