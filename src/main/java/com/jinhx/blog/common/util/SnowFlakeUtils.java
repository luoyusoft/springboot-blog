package com.jinhx.blog.common.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;

import javax.annotation.PostConstruct;

/**
 * SnowFlakeUtils
 *
 * @author jinhx
 * @since 2019-08-06
 */
public class SnowFlakeUtils {

    /**
     * 获取机器位分布式锁key
     */
    private static final String BLOG_ID_WORKER_ID = "blog:id:worker:id";

    /**
     * 获取业务位分布式锁key
     */
    private static final String BLOG_ID_DETACENTER_ID = "blog:id:datacenter:id";

    /**
     * 机器位最大个数，即范围0-31，一定是2的n次方
     */
    private static final int WORKER_ID_COUNT = 32;

    /**
     * 业务位最大个数，即范围0-31，一定是2的n次方
     */
    private static final int DETACENTER_ID_COUNT = 32;

    /**
     * 默认机器位
     */
    private static long WORKER_ID = 0L;

    /**
     * 默认业务位
     */
    private static long DETACENTER_ID = 0L;

    @PostConstruct
    public void init(){
        try{
            WORKER_ID = RedisUtils.increment(BLOG_ID_WORKER_ID) & (WORKER_ID_COUNT - 1);
            DETACENTER_ID = RedisUtils.increment(BLOG_ID_DETACENTER_ID) & (DETACENTER_ID - 1);
        }catch(Exception e){
            WORKER_ID = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
//            WORKER_ID = NetUtil.getLocalhostStr().hashCode();
        }

    }

    public static long getId(long workerId, long datacenterId){
        return IdUtil.getSnowflake(workerId, datacenterId).nextId();
    }

    public static long getId(){
        return IdUtil.getSnowflake(WORKER_ID, DETACENTER_ID).nextId();
    }

}
