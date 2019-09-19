package ru.i_novus.config.web;

import net.n2oapp.framework.config.compile.pipeline.operation.CompileCacheOperation;
import net.n2oapp.framework.config.compile.pipeline.operation.SourceCacheOperation;
import net.n2oapp.framework.mvc.cache.ClientCacheTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.i_novus.config.web.cache_operation.ConfigCompileCacheOperation;
import ru.i_novus.config.web.cache_operation.ConfigSourceCacheOperation;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class ConfigWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigWebApplication.class, args);
    }

    @Bean
    public SourceCacheOperation getSourceCacheOperation() {
        return new ConfigSourceCacheOperation();
    }

    @Bean
    public CompileCacheOperation getCompileCacheOperation() {
        return new ConfigCompileCacheOperation();
    }

    @Bean
    public ClientCacheTemplate getClientCacheTemplate() {
        return new ClientCacheTemplate() {
            @Override
            protected long getLastModifiedFromServer(HttpServletRequest httpServletRequest) {
                Long lastModified = null;
//                Long lastModified = getCache().get(req.getRequestURI(), Long.class);
                return lastModified != null ? lastModified : -1;
            }
        };
    }

}
