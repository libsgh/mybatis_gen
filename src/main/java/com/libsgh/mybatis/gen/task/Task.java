package com.libsgh.mybatis.gen.task;

import cn.hutool.cache.CacheListener;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import com.libsgh.mybatis.gen.service.GenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName task
 * @Description TODO
 * @Author Libs
 * @Date 2022/12/14 13:28
 * @Version 1.0
 */
@Component
public class Task {

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void delGenFiles(){
        for (String key : GenService.timedCache.keySet()) {
            long time = GenService.timedCache.get(key);
            if(time < System.currentTimeMillis()){
                boolean delResult = FileUtil.del(key);
                if(delResult){
                    GenService.timedCache.remove(key);
                }
            }
        }
    }
}
