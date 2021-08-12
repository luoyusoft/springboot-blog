package com.jinhx.blog.common.api;

import com.jinhx.blog.common.util.JsonUtils;
import com.jinhx.blog.entity.gitalk.InitGitalkRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RefreshScope
public class GitalkApi {

    // 请求地址前缀
    @Value("${gitalk.repos-url}")
    private String GITHUB_REPOS_URL;

    // git用戶名
    @Value("${gitalk.username}")
    private String USERNAME;

    // git博客的仓库名
    @Value("${gitalk.repo}")
    private String REPO;

    // blogUrl 博客首页地址
    @Value("${gitalk.blog-url}")
    private String BLOG_URL;

    // 获取到的Token
    @Value("${gitalk.token}")
    private String TOKEN;

    /**
     * @param initGitalkRequest
     * @return
     */
    public boolean initArticle(InitGitalkRequest initGitalkRequest) throws Exception {
        String url = GITHUB_REPOS_URL + USERNAME + "/" + REPO + "/issues";

        String param = String.format("{\"title\":\"%s\",\"labels\":[\"%s\", \"%s\"],\"body\":\"%s%s\\n\\n\"}"
                , initGitalkRequest.getTitle() + " | Jinhx", initGitalkRequest.getId(),
                initGitalkRequest.getType().substring(0, 1).toUpperCase() + initGitalkRequest.getType().substring(1),
                BLOG_URL, "/" + initGitalkRequest.getType() + "/" + initGitalkRequest.getId());
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(param, HTTP.UTF_8);
        post.setHeader("accept", "*/*");
        post.setHeader("connection", "Keep-Alive");
        post.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        post.setHeader("Authorization", "token " + TOKEN);
        post.setEntity(entity);
        log.info("请求Github进行Gitalk初始化接口，请求参数：{}", JsonUtils.objectToJson(post.getEntity()));
        HttpResponse response = client.execute(post);
        log.info("请求Github进行Gitalk初始化接口，响应参数：{}", JsonUtils.objectToJson(response.getEntity()));

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("accept", "*/*");
//        headers.set("connection", "Keep-Alive");
//        headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
//        headers.set("Authorization", "token " + TOKEN);
//
//        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
//        multiValueMap.add("title", initGitalkRequest.getTitle() + " | Jinhx");
//        multiValueMap.add("body",  BLOG_URL + "/" + initGitalkRequest.getType() + "/" + initGitalkRequest.getId() + "\\n\\n");
//
//        List<String> labels = new ArrayList<>();
//        labels.add(String.valueOf(initGitalkRequest.getId()));
//        labels.add(initGitalkRequest.getType().substring(0, 1).toUpperCase() + initGitalkRequest.getType().substring(1));
//        multiValueMap.add("labels", labels);
//
//        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multiValueMap, headers);
//
//        log.info("请求Github进行Gitalk初始化接口，请求参数：{}", request.toString());
//        //构建返回参数
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
//        log.info("请求Github进行Gitalk初始化接口，响应参数：{}", responseEntity.toString());

        if(response.getStatusLine().getStatusCode() != 200){
            return false;
        }
        return true;
    }

}
