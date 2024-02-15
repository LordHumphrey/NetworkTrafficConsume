package utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pojo.DownloadInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-14 9:48
 */
@Slf4j
class AsyncFileDownloaderTest {

    @Test
    void fetchFileSize() {
        URL url;
        try {
            url = URI.create("https://mirrors.pku.edu.cn/debian-cd/current/amd64/iso-dvd/debian-12.5.0-amd64-DVD-1.iso")
                    .toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        DownloadInfo downloadInfo =
                DownloadInfo.builder().fileUrl(url).fileName(url.getFile()).build();
        AsyncFileDownloader asyncFileDownloader = new AsyncFileDownloader();
        CompletableFuture<HttpResponse<InputStream>> httpResponseCompletableFuture = asyncFileDownloader.fetchFileSize(
                downloadInfo, Map.of("User-Agent", Config.User_Agent.getStringValue()));

        HttpResponse<InputStream> inputStreamHttpResponse = null;
        try {
            inputStreamHttpResponse = httpResponseCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        // Output status code
        int statusCode = inputStreamHttpResponse.statusCode();
        System.out.println("Status code: " + statusCode);

        // Output headers
        System.out.println("Headers:");
        inputStreamHttpResponse.headers().map().forEach((k, v) -> System.out.println(k + ": " + String.join(", ", v)));

        // Output body
        try (InputStream inputStream = inputStreamHttpResponse.body()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Body: " + body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}