package com.github.zjiajun.adult.downloader;

import com.github.zjiajun.adult.http.RetrofitClient;
import com.github.zjiajun.adult.request.Request;
import com.github.zjiajun.adult.response.Response;
import com.github.zjiajun.adult.scheduler.Scheduler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhujiajun
 * @since 2017/2/3
 *
 * 抽象公用下载器
 */
public abstract class AbstractDownloader implements Downloader {

    private final Scheduler scheduler;
    private final RetrofitClient retrofitClient;

    public AbstractDownloader(Scheduler scheduler) {
        this.scheduler = scheduler;
        retrofitClient = RetrofitClient.getInstance();
    }

    protected abstract void beforeDownload(Request request);

    protected abstract void afterDownload(Response response);

    @Override
    public void download() {
        while (!Thread.currentThread().isInterrupted()) {
            Request request = scheduler.takeRequest();
            beforeDownload(request);
            try {
                Response response = retrofitClient.execute(request);

                afterDownload(response);

                if (!request.isLogin()) {
                    scheduler.putResponse(response);
                }

                TimeUnit.SECONDS.sleep(3);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
