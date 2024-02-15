package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.net.http.HttpResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResult {
    private HttpResponse<InputStream> response;
    private long start;
    private long end;

    // getters and setters
}
