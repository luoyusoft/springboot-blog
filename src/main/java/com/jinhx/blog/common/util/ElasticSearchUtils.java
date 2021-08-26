package com.jinhx.blog.common.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.jinhx.blog.common.constants.ElasticSearchConstants;
import com.jinhx.blog.entity.article.Article;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * RabbitMqUtils
 *
 * @author jinhx
 * @since 2019-03-07
 */
@Component
public class ElasticSearchUtils {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     * @param index
     */
    public boolean createIndex(String index) throws Exception {
        // 判断索引是否存在
        if(existIndex(index)){
           return true;
        }
        // 创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     * @param index
     */
    public boolean existIndex(String index) throws Exception {
        // 判断索引是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引
     * @param index
     */
    public boolean deleteIndex(String index) throws Exception {
        // 判断索引是否存在
        if(!existIndex(index)){
            return true;
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        return acknowledgedResponse.isAcknowledged();
    }

    /**
     * 新增文档
     * @param index
     * @param id
     * @param content
     */
    public boolean addDocument(String index, String id, String content) throws Exception {
        if(!createIndex(index)){
            return false;
        }

        IndexRequest indexRequest = new IndexRequest(index);
        // 设置超时时间
        indexRequest.id(id);
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        // 转换为json字符串
        indexRequest.source(content, XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.status().getStatus() == 200;
    }

    /**
     * 判断是否存在文档
     * @param index
     * @param id
     */
    public boolean isExistsDocument(String index, String id) throws Exception {
        // 判断是否存在文档
        GetRequest getRequest = new GetRequest(index, id);
        // 不获取返回的_source的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
    }

    /**
     * 获取文档
     * @param index
     * @param id
     */
    public String getDocument(String index, String id) throws Exception {
        // 获取文档
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.getSourceAsString();
    }

    /**
     * 更新文档
     * @param index
     * @param id
     * @param content
     */
    public boolean updateDocument(String index, String id, String content) throws Exception {
        // 更新文档
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.timeout(TimeValue.timeValueSeconds(1));
        updateRequest.doc(content, XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        return updateResponse.status().getStatus() == 200;
    }

    /**
     * 删除文档
     * @param index
     * @param id
     */
    public boolean deleteDocument(String index, String id) throws Exception {
        if(!isExistsDocument(index, id)){
            return true;
        }

        // 删除文档
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        deleteRequest.timeout(TimeValue.timeValueSeconds(1));
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        return deleteResponse.status().getStatus() == 200;
    }

    /**
     * 批量插入
     * @param index
     * @param contents
     */
    public boolean bulkRequest(String index, List<Article> contents) throws Exception {
        // 批量插入
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(1));
        contents.forEach(x -> {
            bulkRequest.add(
                    new IndexRequest(index)
                            .id(x.getArticleId().toString())
                            .source(JsonUtils.objectToJson(x), XContentType.JSON));
        });
        BulkResponse bulkItemResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkItemResponse.hasFailures();
    }

    /**
     * 搜索请求
     * @param index
     * @param keyword
     */
    public List<Map<String, Object>> searchRequest(String index, String keyword, List<String> highlightBuilderList, List<String> searchList) throws Exception {
        if (CollectionUtils.isEmpty(searchList)){
            return Collections.emptyList();
        }
        // 搜索请求
        SearchRequest searchRequest;
        if(StringUtils.isEmpty(index)){
            searchRequest = new SearchRequest();
        }else {
            searchRequest = new SearchRequest(index);
        }
        // 条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 第几页
        searchSourceBuilder.from(0);
        // 每页多少条数据
        searchSourceBuilder.size(100);
        // 配置高亮
        if (CollectionUtils.isNotEmpty(highlightBuilderList)){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilderList.forEach(highlightBuilder::field);
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            //如果要多个字段高亮，这项要为false，未知效果
//        highlightBuilder.requireFieldMatch(false);
            //下面这两项,如果你要高亮如文字内容等有很多字的字段，必须配置，不然会导致高亮不全，文章内容缺失等
            //最大高亮分片数
            highlightBuilder.fragmentSize(800000);
            //从第一个分片获取高亮片段
            highlightBuilder.numOfFragments(0);
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        // 精确查询
//        QueryBuilders.termQuery();
        // 匹配所有
//        QueryBuilders.matchAllQuery();
        // 最细粒度划分：ik_max_word，最粗粒度划分：ik_smart
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, searchList.toArray(new String[0])).analyzer("ik_max_word"));
        if (!StringUtils.isEmpty(keyword)){
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, searchList.toArray(new String[0])));
        }
//        searchSourceBuilder.query(QueryBuilders.matchQuery("content", keyWord));

        //根据查询相关度进行排序
        searchSourceBuilder.sort(new ScoreSortBuilder());
        //再根据时间进行排序
        searchSourceBuilder.sort("id");
        //避免分页之后相关性乱了
        searchSourceBuilder.trackScores(true);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()){
            // 原来的结果
            Map<String, Object> sourceMap = searchHit.getSourceAsMap();
            // 高亮结果
            if (CollectionUtils.isNotEmpty(highlightBuilderList)){
                Map<String, HighlightField> highlightFieldMap = searchHit.getHighlightFields();
                highlightBuilderList.forEach(highlightBuilderListItem -> {
                    HighlightField title = highlightFieldMap.get(highlightBuilderListItem);
                    // 解析高亮字段，替换掉原来的字段
                    if (title != null){
                        Text[] fragments = title.getFragments();
                        StringBuilder n_title = new StringBuilder();
                        for (Text text : fragments){
                            n_title.append(text);
                        }
                        sourceMap.put(highlightBuilderListItem, n_title.toString());
                    }
                });
            }

            results.add(sourceMap);
        }
        return results;
    }

    /**
     * 搜索所有文章id
     * @param index
     */
    public List<Integer> searchAllRequest(String index) throws Exception {
        // 搜索请求
        SearchRequest searchRequest;
        if(StringUtils.isEmpty(index)){
            searchRequest = new SearchRequest();
        }else {
            searchRequest = new SearchRequest(index);
        }
        // 条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 第几页
        searchSourceBuilder.from(0);
        // 每页多少条数据
        searchSourceBuilder.size(1000);
        // 匹配所有
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(ElasticSearchConstants.ELASTIC_SEARCH_TIMEOUT));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Integer> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()){
            results.add(Integer.valueOf(searchHit.getId()));
        }
        return results;
    }

}
