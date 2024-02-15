package utils;

import lombok.Getter;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-13 16:50
 */
@Getter
public enum Config {
    SAVE_PATH("D:\\Development\\Code\\Java\\NetworkTrafficConsume\\src\\main\\resources"),
    DOWNLOAD_THREADS(16),
    TIMEOUT(5000),
    User_Agent(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0");

    private String stringValue;
    private Integer intValue;

    Config(String s) {
        this.stringValue = s;
    }

    Config(Integer i) {
        this.intValue = i;
    }
}