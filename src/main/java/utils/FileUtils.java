package utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author tao wong
 * @description TODO
 * @date 2024-02-15 18:09
 */
@Slf4j
public class FileUtils {
    public static void writeToFile(String filePath, long offset, byte[] data) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            raf.seek(offset);
            raf.write(data);
        } catch (IOException e) {
            log.error("write to file error: {}", e.getLocalizedMessage());
        }
    }
}
