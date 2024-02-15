/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-14 0:02
 */
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pojo.DownloadInfo;
import utils.AsyncFileDownloader;
import utils.Config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
class CoreDispatcherTest {
    @BeforeAll
    static void init() {}

    @Test
    void getFileSize() throws MalformedURLException {
        URL url = URI.create("https://mirrors.pku.edu.cn/debian-cd/current/amd64/iso-dvd/debian-12.5.0-amd64-DVD-1.iso")
                .toURL();
        DownloadInfo downloadInfo =
                DownloadInfo.builder().fileUrl(url).fileName(url.getFile()).build();
        CoreDispatcher coreDispatcher = CoreDispatcher.builder()
                .downloadInfo(downloadInfo)
                .asyncFileDownloader(new AsyncFileDownloader())
                .build();
        coreDispatcher.getFileSize();
        log.debug("文件大小:{}", downloadInfo.getTotalSize());
    }

    @Test
    void downloadFile() throws MalformedURLException {
        for (int i = 0; i < 128; i++) {

            URL url = URI.create("https://issuepcdn.baidupcs.com/issue/netdisk/yunguanjia/BaiduNetdisk_7.37.5.3.exe")
                    .toURL();
            //        URL url =
            // URI.create("https://mirrors.pku.edu.cn/debian-cd/current/amd64/iso-dvd/debian-12.5.0-amd64-DVD-1.iso")
            //                .toURL();
            String[] urlFileNameSpilt = url.getFile().split("/");
            DownloadInfo downloadInfo = DownloadInfo.builder()
                    .fileUrl(url)
                    .downloadedSize(new LongAdder())
                    .preSize(new AtomicLong())
                    .speed(new AtomicLong())
                    .remainSize(new AtomicLong())
                    .filePath(Config.SAVE_PATH.getStringValue())
                    .fileName(urlFileNameSpilt[urlFileNameSpilt.length - 1])
                    .build();
            CoreDispatcher coreDispatcher = CoreDispatcher.builder()
                    .downloadInfo(downloadInfo)
                    .asyncFileDownloader(new AsyncFileDownloader())
                    .build();
            coreDispatcher.getFileSize();
            coreDispatcher.outputDownloadInfo();
            coreDispatcher.downloadFile();
        }
    }
}