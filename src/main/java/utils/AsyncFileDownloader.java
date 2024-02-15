package utils;

import lombok.extern.slf4j.Slf4j;
import pojo.DownloadInfo;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-13 23:28
 */
@Slf4j
public class AsyncFileDownloader implements FileDownloader<CompletableFuture<HttpResponse<InputStream>>> {

    @Override
    public CompletableFuture<HttpResponse<InputStream>> downloadFile(
            DownloadInfo downloadInfo, Map<String, String> headersMap) {
        ExecutorService executorService = ThreadPoolUtils.getExecutorService();

        HttpClient httpClient = HttpClient.newBuilder()
                .executor(executorService)
                .connectTimeout(Duration.ofMillis(Config.TIMEOUT.getIntValue()))
                .build();
        HttpRequest httpRequest = null;
        try {
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(downloadInfo.getFileUrl().toURI())
                    .GET()
                    .timeout(Duration.ofMillis(Config.TIMEOUT.getIntValue()));
            headersMap.forEach(httpRequestBuilder::header);
            httpRequest = httpRequestBuilder.build();

            log.debug("Sending HTTP request: {}", httpRequest);

            return httpClient
                    .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
                    .thenApply(response -> {
                        log.debug("Received HTTP response: {}", response);
                        return response;
                    });
        } catch (URISyntaxException e) {
            log.error("Error occurred while creating HTTP request", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<HttpResponse<InputStream>> fetchFileSize(
            DownloadInfo downloadInfo, Map<String, String> headersMap) {
        try (HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(Config.TIMEOUT.getIntValue()))
                .build()) {
            HttpRequest httpRequest = null;
            try {
                HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                        .uri(downloadInfo.getFileUrl().toURI())
                        .HEAD()
                        .timeout(Duration.ofMillis(Config.TIMEOUT.getIntValue()));
                headersMap.forEach(httpRequestBuilder::header);
                httpRequest = httpRequestBuilder.build();
                return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
