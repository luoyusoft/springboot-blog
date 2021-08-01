package com.jinhx.blog.controller.cache;

import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.service.cache.CacheServer;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CacheController
 *
 * @author jinhx
 * @since 2019-11-07
 */
@RestController
public class CacheController {

    @Autowired
    private CacheServer cacheServer;

    /**
     * 清除所有缓存
     */
    @DeleteMapping("/manage/cache/cleanAll")
    @RequiresPermissions("cache:cleanAll")
    public Response cleanAllCache() {
        cacheServer.cleanAllCache();
        return Response.success();
    }

}
