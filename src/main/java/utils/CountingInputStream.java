package utils;

import pojo.DownloadInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author taowong
 */
public class CountingInputStream extends InputStream {
    private final InputStream in;
    private final DownloadInfo downloadInfo;

    public CountingInputStream(InputStream in, DownloadInfo downloadInfo) {
        this.in = in;
        this.downloadInfo = downloadInfo;
    }

    @Override
    public int read() throws IOException {
        int result = in.read();
        if (result != -1) {
            downloadInfo.getDownloadedSize().increment();
        }
        return result;
    }
}