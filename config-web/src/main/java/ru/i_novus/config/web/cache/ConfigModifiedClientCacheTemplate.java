package ru.i_novus.config.web.cache;

import net.n2oapp.framework.ui.servlet.ModifiedClientCacheTemplate;
import org.springframework.cache.CacheManager;

import javax.servlet.http.HttpServletRequest;

public class ConfigModifiedClientCacheTemplate extends ModifiedClientCacheTemplate {

    public ConfigModifiedClientCacheTemplate(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    protected long getLastModifiedFromServer(HttpServletRequest req) {
        return req.getPathInfo().matches(".*/systems/([A-Za-z0-9\\_]+)/update$") ?
                -1 :
                super.getLastModifiedFromServer(req);
    }
}
