import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pojo.DownloadInfo;
import utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-13 23:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CoreDispatcher {
    DownloadInfo downloadInfo;
    AsyncFileDownloader asyncFileDownloader = new AsyncFileDownloader();

    public void getFileSize() {
        asyncFileDownloader
                .fetchFileSize(downloadInfo, Map.of("User-Agent", Config.User_Agent.getStringValue()))
                .thenAccept(response -> {
                    log.info("Fetch file Length, processing response...");
                    log.debug("Response headers: {}", response.headers());
                    downloadInfo.setTotalSize(response.headers()
                            .firstValue("Content-Length")
                            .map(Long::parseLong)
                            .orElse(0L));
                    downloadInfo.setRemainSize(new AtomicLong(downloadInfo.getTotalSize()));
                    log.info("File size set: {}", downloadInfo.getTotalSize());
                })
                .join();
    }

    public void downloadFile() {
        log.debug("Starting download for file: {}", downloadInfo.getFileUrl());
        long fragmentSize = (long) (downloadInfo.getTotalSize() / Config.DOWNLOAD_THREADS.getIntValue());
        log.debug("Fragment size: {}", fragmentSize);
        Map<String, String> headersMap = new HashMap<>();
        List<CompletableFuture<HttpResponse<InputStream>>> futures = new ArrayList<>();
        headersMap.put("User-Agent", Config.User_Agent.getStringValue());
        for (int i = 0; i < Config.DOWNLOAD_THREADS.getIntValue(); i++) {
            final long start = i * fragmentSize, end;
            if (i == Config.DOWNLOAD_THREADS.getIntValue() - 1) {
                end = downloadInfo.getTotalSize() - 1;
            } else {
                end = (i + 1) * fragmentSize;
            }
            headersMap.put("Range", "bytes=" + start + "-" + end);
            futures.add(asyncFileDownloader
                    .downloadFile(downloadInfo, headersMap)
                    .thenApply(response -> {
                        long[] range = DownloadUtils.extractRange(response, start, end);
                        try (InputStream inputStream = response.body()) {
                            byte[] bytes = inputStream.readAllBytes();
                            FileUtils.writeToFile(downloadInfo.getFullPath(), range[0], bytes);

                            //                            downloadInfo.getDownloadedSize().add(bytes.length);

                            log.debug("Wrote {} bytes to file", range[1] - range[0] + 1);
                        } catch (IOException e) {
                            log.warn("Error occurred while writing to file: {}", e.getMessage());
                        }
                        return response;
                    })
                    .whenComplete((response, ex) -> {
                        if (ex != null) {
                            log.warn(
                                    "Error occurred while downloading file: {},Start: {},End: {}, Error message: {}",
                                    downloadInfo.getFileName(),
                                    start,
                                    end,
                                    ex.getMessage());
                        }
                    }));
            log.debug("Thread-{}: start={}, end={}", i, start, end);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        log.info("Download for file: {} completed", downloadInfo.getFileName());
    }

    public void outputDownloadInfo() {

        try {
            ScheduledExecutorService singleThreadScheduledExecutor = ThreadPoolUtils.getSingleThreadScheduledExecutor();
            singleThreadScheduledExecutor.scheduleAtFixedRate(
                    () -> {
                        try {
                            log.info(downloadInfo.downloadInfoOutputString());
                        } catch (Exception e) {
                            log.error("Error occurred while calculating download speed: {}", e.getMessage());
                        }
                    },
                    1,
                    1,
                    TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error occurred while scheduling the task: {}", e.getMessage());
        }
    }
}
