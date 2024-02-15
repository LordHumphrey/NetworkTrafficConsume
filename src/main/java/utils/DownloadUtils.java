package utils;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DownloadUtils {

    /**
     * Extracts the start and end values from the "Content-Range" header.
     *
     * @param response The HTTP response.
     * @return A long array where the first element is the start value and the second element is the end value.
     */
    public static long[] extractRange(HttpResponse<InputStream> response, long expectedStart, long expectedEnd) {
        String contentRange = response.headers().firstValue("content-range").orElse("");
        Pattern pattern = Pattern.compile("bytes (\\d+)-(\\d+)/\\d+");
        Matcher matcher = pattern.matcher(contentRange);
        if (matcher.find()) {
            long start = Long.parseLong(matcher.group(1));
            long end = Long.parseLong(matcher.group(2));
            if (start != expectedStart || end != expectedEnd) {
                log.warn(
                        "The start and end values in the response do not match the expected values. Expected: {}-{}, Actual: {}-{}",
                        expectedStart,
                        expectedEnd,
                        start,
                        end);
            }
            return new long[] {start, end};
        }
        return new long[] {0, 0};
    }

    /**
     * Handles the download response.
     *
     * @param response The HTTP response.
     * @return The HTTP response if the download was successful, null otherwise.
     */
    public static HttpResponse<InputStream> handleDownloadResponse(HttpResponse<InputStream> response) {
        if (response.statusCode() >= 400) {
            log.error("Received HTTP error code: {}", response.statusCode());
            return null;
        } else {
            return response;
        }
    }
}