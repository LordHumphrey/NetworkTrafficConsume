package pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-13 23:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadInfo {
    URL fileUrl;
    String filePath, fileName, remainTime;
    LongAdder downloadedSize;
    Long totalSize;
    AtomicLong remainSize, speed, preSize;

    public DownloadInfo(URL fileUrl, String filePath, String fileName) {
        this.fileUrl = fileUrl;
        this.filePath = filePath;
        this.fileName = fileName;
        this.downloadedSize = new LongAdder();
        this.totalSize = 0L;
        this.remainSize = new AtomicLong(0);
        this.speed = new AtomicLong(0);
        this.preSize = new AtomicLong(0);
    }

    private String formatSize(double size) {
        if (size <= 0) {
            return "0.00 B";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = (int) (Math.log10(size) / 3);
        double adjustedSize = size / Math.pow(1024, unitIndex);
        return String.format("%.2f%s", adjustedSize, units[unitIndex]);
    }

    private String formatTimeUnit(double seconds) {
        if (seconds < 60) {
            return String.format("%.2f s", seconds);
        } else if (seconds < 3600) {
            return String.format("%.2f min %.2f s", seconds / 60, seconds % 60);
        } else if (seconds < 86400) {
            return String.format("%.2f h %.2f min %.2f s", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        } else {
            return String.format(
                    "%.2f d %.2f h %.2f min %.2f s",
                    seconds / 86400, (seconds % 86400) / 3600, (seconds % 3600) / 60, seconds % 60);
        }
    }

    private void calculateSpeed() {
        speed.set(downloadedSize.sum() - preSize.get());
        preSize.set(downloadedSize.sum());
    }

    private void calculateRemainTime() {
        remainSize.set(totalSize - downloadedSize.sum());
        if (speed.get() != 0) {
            String remainTime = String.format("%.2s", remainSize.get() / speed.get());
            this.remainTime = "Infinity".equalsIgnoreCase(remainTime) ? "-" : remainTime;
        } else {
            this.remainTime = "-";
        }
    }

    public String downloadInfoOutputString() {
        this.calculateSpeed();
        this.calculateRemainTime();
        if (speed.get() == 0.0) {
            return String.format(
                    "Downloaded: %s, TotalSize: %s, Speed: %s, RemainTime: %s",
                    formatSize(downloadedSize.doubleValue()), formatSize(totalSize), "0.00 B/s", "-");
        }
        return String.format(
                "Downloaded: %s, TotalSize: %s, Speed: %s, RemainTime: %s",
                formatSize(downloadedSize.doubleValue()),
                formatSize(totalSize),
                formatSize(speed.get()),
                formatTimeUnit(Double.parseDouble(remainTime)));
    }

    public String getFullPath() {
        return Paths.get(filePath, fileName).toString();
    }
}
