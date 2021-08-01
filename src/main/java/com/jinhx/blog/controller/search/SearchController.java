package com.jinhx.blog.controller.search;

import com.jinhx.blog.common.aop.annotation.LogView;
import com.jinhx.blog.entity.base.Response;
import com.jinhx.blog.entity.search.vo.SearchListVO;
import com.jinhx.blog.service.search.SearchServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SearchController
 *
 * @author jinhx
 * @since 2019-03-07
 */
@RestController
public class SearchController {

    @Autowired
    private SearchServer searchServer;

    /********************** portal ********************************/

    /**
     * 搜索，包括文章，视频
     *
     * @param keyword 关键字
     * @return 搜索结果，包括文章，视频
     */
    @GetMapping("/search")
    @LogView(module = 3)
    public Response search(@RequestParam(value = "keyword", required = false) String keyword) throws Exception {
        SearchListVO searchListVO = searchServer.search(keyword);
        return Response.success(searchListVO);
    }

}
