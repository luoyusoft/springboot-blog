package com.jinhx.blog.service.operation.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jinhx.blog.common.constants.ModuleTypeConstants;
import com.jinhx.blog.common.enums.ResponseEnums;
import com.jinhx.blog.common.exception.MyException;
import com.jinhx.blog.common.threadpool.ThreadPoolEnum;
import com.jinhx.blog.entity.article.Article;
import com.jinhx.blog.entity.article.ArticleBuilder;
import com.jinhx.blog.entity.article.dto.ArticleVOsQueryDTO;
import com.jinhx.blog.entity.article.vo.ArticleVO;
import com.jinhx.blog.entity.base.PageData;
import com.jinhx.blog.entity.base.QueryPage;
import com.jinhx.blog.entity.operation.Top;
import com.jinhx.blog.entity.operation.TopAdaptorBuilder;
import com.jinhx.blog.entity.operation.VideoAdaptorBuilder;
import com.jinhx.blog.entity.operation.vo.TopVO;
import com.jinhx.blog.entity.video.Video;
import com.jinhx.blog.entity.video.vo.VideoVO;
import com.jinhx.blog.mapper.operation.TopMapper;
import com.jinhx.blog.service.article.ArticleMapperService;
import com.jinhx.blog.service.article.ArticleService;
import com.jinhx.blog.service.cache.CacheServer;
import com.jinhx.blog.service.operation.TopService;
import com.jinhx.blog.service.video.VideoMapperService;
import com.jinhx.blog.service.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * TopServiceImpl
 *
 * @author jinhx
 * @since 2019-02-22
 */
@Service
@Slf4j
public class TopServiceImpl extends ServiceImpl<TopMapper, Top> implements TopService {

    @Resource
    private ArticleMapperService articleMapperService;

    @Resource
    private VideoMapperService videoMapperService;

    @Resource
    private ArticleService articleService;

    @Resource
    private VideoService videoService;

    @Autowired
    private CacheServer cacheServer;

    /**
     * 将Top转换为TopVO
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO
     */
    @Override
    public TopVO adaptorTopToTopVO(TopAdaptorBuilder<Top> topAdaptorBuilder){
        if(Objects.isNull(topAdaptorBuilder) || Objects.isNull(topAdaptorBuilder.getData())){
            return null;
        }

        Top top = topAdaptorBuilder.getData();
        TopVO topVO = new TopVO();
        BeanUtils.copyProperties(top, topVO);

        if(ModuleTypeConstants.ARTICLE.equals(topVO.getModule())){
            ArticleVOsQueryDTO articleVOsQueryDTO = new ArticleVOsQueryDTO();
            articleVOsQueryDTO.setLogStr("con=info");
            articleVOsQueryDTO.setPublish(Article.PUBLISH_TRUE);
            articleVOsQueryDTO.setArticleIds(Lists.newArrayList(topVO.getLinkId()));
            articleVOsQueryDTO.setArticleBuilder(ArticleBuilder.builder()
                    .categoryListStr(true)
                    .tagList(true)
                    .recommend(true)
                    .top(true)
                    .author(true)
                    .build());

            List<ArticleVO> articleVOs = articleService.getArticleVOs(articleVOsQueryDTO);

            if (CollectionUtils.isNotEmpty(articleVOs) && !Objects.isNull(articleVOs.get(0))){
                ArticleVO articleVO = articleVOs.get(0);
                if (topAdaptorBuilder.getDescription()){
                    topVO.setDescription(articleVO.getDescription());
                }

                if (topAdaptorBuilder.getReadNum()){
                    topVO.setReadNum(articleVO.getReadNum());
                }

                if (topAdaptorBuilder.getLikeNum()){
                    topVO.setLikeNum(articleVO.getLikeNum());
                }

                if (topAdaptorBuilder.getCover()){
                    topVO.setCover(articleVO.getCover());
                }

                if (topAdaptorBuilder.getTagList()){
                    topVO.setTagList(articleVO.getTagList());
                }

                if (topAdaptorBuilder.getTitle()){
                    topVO.setTitle(articleVO.getTitle());
                }
            }
        }

        if(ModuleTypeConstants.VIDEO.equals(topVO.getModule())){
            VideoVO videoVO = videoService.getVideoVO(topVO.getLinkId(), Video.PUBLISH_TRUE, new VideoAdaptorBuilder.Builder<Video>().setAll().build());
            if (!Objects.isNull(videoVO)){
                if (topAdaptorBuilder.getWatchNum()){
                    topVO.setWatchNum(videoVO.getWatchNum());
                }

                if (topAdaptorBuilder.getLikeNum()){
                    topVO.setLikeNum(videoVO.getLikeNum());
                }

                if (topAdaptorBuilder.getCover()){
                    topVO.setCover(videoVO.getCover());
                }

                if (topAdaptorBuilder.getTagList()){
                    topVO.setTagList(videoVO.getTagList());
                }

                if (topAdaptorBuilder.getTitle()){
                    topVO.setTitle(videoVO.getTitle());
                }
            }
        }

        return topVO;
    }

    /**
     * 将Top列表按需转换为TopVO列表
     *
     * @param topAdaptorBuilder topAdaptorBuilder
     * @return TopVO列表
     */
    @Override
    public List<TopVO> adaptorTopsToTopVOs(TopAdaptorBuilder<List<Top>> topAdaptorBuilder){
        if(Objects.isNull(topAdaptorBuilder) || CollectionUtils.isEmpty(topAdaptorBuilder.getData())){
            return Collections.emptyList();
        }
        List<TopVO> topVOs = Lists.newArrayList();
        topAdaptorBuilder.getData().forEach(top -> {
            if (Objects.isNull(top)){
                return;
            }

            topVOs.add(adaptorTopToTopVO(new TopAdaptorBuilder.Builder<Top>()
                    .setDescription(topAdaptorBuilder.getDescription())
                    .setReadNum(topAdaptorBuilder.getReadNum())
                    .setWatchNum(topAdaptorBuilder.getWatchNum())
                    .setLikeNum(topAdaptorBuilder.getLikeNum())
                    .setCover(topAdaptorBuilder.getCover())
                    .setTagList(topAdaptorBuilder.getTagList())
                    .setTitle(topAdaptorBuilder.getTitle())
                    .build(top)));
        });

        return topVOs;
    }

    /**
     * 分页查询
     *
     * @param page page
     * @param limit limit
     * @return PageUtils
     */
    @Override
    public PageData queryPage(Integer page, Integer limit) {
        IPage<Top> topIPage = baseMapper.selectPage(new QueryPage<Top>(page, limit).getPage(),
                new LambdaQueryWrapper<Top>().orderByAsc(Top::getOrderNum));

        if (CollectionUtils.isEmpty(topIPage.getRecords())){
            return new PageData(topIPage);
        }

        List<TopVO> topVOs = adaptorTopsToTopVOs(new TopAdaptorBuilder.Builder<List<Top>>()
                .setTitle()
                .build(topIPage.getRecords()));

        IPage<TopVO> topVOIPage = new Page<>();
        BeanUtils.copyProperties(topIPage, topVOIPage);
        topVOIPage.setRecords(topVOs);

        return new PageData(topVOIPage);
    }

    /**
     * 获取置顶列表
     *
     * @param module module
     * @param title title
     * @return List<TopVO>
     */
    @Override
    public List<TopVO> select(Integer module, String title) {
        List<TopVO> TopVOList = Lists.newArrayList();

        if (ModuleTypeConstants.ARTICLE.equals(module)){
            List<Article> articles = articleMapperService.listArticlesByPublishAndTitle(title);
            if (CollectionUtils.isNotEmpty(articles)){
                articles.forEach(articlesItem -> {
                    TopVO TopVO = new TopVO();
                    TopVO.setTitle(articlesItem.getTitle());
                    TopVO.setLinkId(articlesItem.getId());
                    TopVO.setModule(module);
                    TopVOList.add(TopVO);
                });
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(module)){
            List<Video> videoList = videoMapperService.listVideosByPublishAndTitle(title);
            if (videoList != null && videoList.size() > 0){
                videoList.forEach(videoListItem -> {
                    TopVO TopVO = new TopVO();
                    TopVO.setTitle(videoListItem.getTitle());
                    TopVO.setLinkId(videoListItem.getId());
                    TopVO.setModule(module);
                    TopVOList.add(TopVO);
                });
            }
        }

        return TopVOList;
    }

    /**
     * 新增
     *
     * @param top top
     */
    @Override
    public void insertTop(Top top) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getOrderNum, top.getOrderNum())) > 0) {
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }
        if (ModuleTypeConstants.ARTICLE.equals(top.getModule())){
            Article article = articleMapperService.getArticle(top.getLinkId(), Article.PUBLISH_TRUE);
            if(Objects.isNull(article)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶内容不存在");
            }
            Top oldTop = baseMapper.selectOne(new LambdaQueryWrapper<Top>()
                    .eq(Top::getLinkId, top.getLinkId())
                    .eq(Top::getModule, top.getModule()));
            if(Objects.isNull(oldTop)){
                baseMapper.insert(top);
            }else {
                baseMapper.update(top, new LambdaUpdateWrapper<Top>()
                        .eq(Top::getLinkId, top.getLinkId())
                        .eq(Top::getModule, top.getModule())
                        .set(Top::getOrderNum, top.getOrderNum()));
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(top.getModule())){
            Video video = videoMapperService.getVideo(top.getLinkId(), Video.PUBLISH_TRUE);
            if(Objects.isNull(video)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶内容不存在");
            }
            Top oldTop = baseMapper.selectOne(new LambdaQueryWrapper<Top>()
                    .eq(Top::getLinkId, top.getLinkId())
                    .eq(Top::getModule, top.getModule()));
            if(Objects.isNull(oldTop)){
                baseMapper.insert(top);
            }else {
                baseMapper.update(top, new LambdaUpdateWrapper<Top>()
                        .eq(Top::getLinkId, top.getLinkId())
                        .eq(Top::getModule, top.getModule())
                        .set(Top::getOrderNum, top.getOrderNum()));
            }
        }

        cleanListAllCache();
    }

    /**
     * 更新
     *
     * @param top top
     */
    @Override
    public void updateTop(Top top) {
        Top existTop = baseMapper.selectOne(new LambdaQueryWrapper<Top>()
                .eq(Top::getOrderNum, top.getOrderNum()));
        if (!Objects.isNull(existTop) && !existTop.getId().equals(top.getId())){
            throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "该顺序已被占用");
        }
        if (ModuleTypeConstants.ARTICLE.equals(top.getModule())){
            Article article = articleMapperService.getArticle(top.getLinkId(), Article.PUBLISH_TRUE);
            if(Objects.isNull(article)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶内容不存在");
            }
            Top oldTop = baseMapper.selectOne(new LambdaQueryWrapper<Top>()
                    .eq(Top::getLinkId, top.getLinkId())
                    .eq(Top::getModule, top.getModule()));
            if(Objects.isNull(oldTop)){
                baseMapper.insert(top);
            }else {
                baseMapper.update(top, new LambdaUpdateWrapper<Top>()
                        .eq(Top::getLinkId, top.getLinkId())
                        .eq(Top::getModule, top.getModule())
                        .set(Top::getOrderNum, top.getOrderNum()));
            }
        }

        if (ModuleTypeConstants.VIDEO.equals(top.getModule())){
            Video video = videoMapperService.getVideo(top.getLinkId(), Video.PUBLISH_TRUE);
            if(Objects.isNull(video)) {
                throw new MyException(ResponseEnums.PARAM_ERROR.getCode(), "置顶内容不存在");
            }
            Top oldTop = baseMapper.selectOne(new LambdaQueryWrapper<Top>()
                    .eq(Top::getLinkId, top.getLinkId())
                    .eq(Top::getModule, top.getModule()));
            if(Objects.isNull(oldTop)){
                baseMapper.insert(top);
            }else {
                baseMapper.update(top, new LambdaUpdateWrapper<Top>()
                        .eq(Top::getLinkId, top.getLinkId())
                        .eq(Top::getModule, top.getModule())
                        .set(Top::getOrderNum, top.getOrderNum()));
            }
        }

        cleanListAllCache();
    }

    /**
     * 置顶
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateTopTop(Integer id) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getOrderNum, Top.ORDER_NUM_TOP)) > 0) {
            List<Top> Tops = baseMapper.selectList(new LambdaQueryWrapper<Top>()
                    .orderByDesc(Top::getOrderNum));
            Tops.forEach(topsItem -> {
                topsItem.setOrderNum(topsItem.getOrderNum() + 1);
                // 修改顺序，注意从大的开始，不然会有唯一索引冲突
                baseMapper.update(topsItem, new LambdaUpdateWrapper<Top>()
                        .eq(Top::getId, topsItem.getId())
                        .set(Top::getOrderNum, topsItem.getOrderNum()));
            });
        }

        if(baseMapper.update(null, new LambdaUpdateWrapper<Top>()
                .eq(Top::getId, id)
                .set(Top::getOrderNum, Top.ORDER_NUM_TOP)) < 1){
            throw new MyException(ResponseEnums.UPDATE_FAILR.getCode(), "更新数据失败");
        }

        cleanListAllCache();
    }

    /**
     * 删除
     *
     * @param ids ids
     */
    @Override
    public void deleteTopsByIds(List<Integer> ids) {
        baseMapper.deleteBatchIds(ids);
        cleanListAllCache();
    }

    /**
     * 查找最大顺序
     *
     * @return 最大顺序
     */
    @Override
    public Integer selectTopMaxOrderNum() {
        return baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .select(Top::getOrderNum)
                .orderByDesc(Top::getOrderNum)
                .last("limit 1")).get(0).getOrderNum();
    }

    /**
     * 是否已置顶
     *
     * @param module module
     * @param linkId linkId
     * @return 是否已置顶
     */
    @Override
    public Boolean isTopByModuleAndLinkId(Integer module, Integer linkId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Top>()
                .eq(Top::getModule, module)
                .eq(Top::getLinkId, linkId)) > 0;
    }

    /**
     * 清除缓存
     */
    private void cleanListAllCache(){
        ThreadPoolEnum.COMMON.getThreadPoolExecutor().execute(() ->{
            cacheServer.cleanListAllCache();
        });
    }

    /********************** portal ********************************/

    /**
     * 查询列表
     *
     * @param module module
     * @return List<TopVO>
     */
    @Override
    public List<TopVO> listTopVO(Integer module) {
        List<Top> tops = baseMapper.selectList(new LambdaQueryWrapper<Top>()
                .eq(Top::getModule, module)
                .orderByAsc(Top::getOrderNum)
                // 只能调用一次,多次调用以最后一次为准 有sql注入的风险,请谨慎使用
                .last("limit 10"));
        if (CollectionUtils.isEmpty(tops)){
            return Collections.emptyList();
        }

        return adaptorTopsToTopVOs(new TopAdaptorBuilder.Builder<List<Top>>()
                .setAll()
                .build(tops));
    }

}
