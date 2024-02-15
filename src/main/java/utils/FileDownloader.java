package utils;

import pojo.DownloadInfo;

import java.util.Map;

public interface FileDownloader<T> {
    T downloadFile(DownloadInfo downloadInfo, Map<String, String> headersMap);

    T fetchFileSize(DownloadInfo downloadInfo, Map<String, String> headersMap);
}