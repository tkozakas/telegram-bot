package org.churk.telegrambot.utility;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
public class MediaUtility {
    private final List<File> tempFiles = List.of();
    private static final int MAX_WIDTH = 640;
    private static final int MAX_HEIGHT = 480;
    private static final String DEBUG_INFO = "info";
    private static final String TEMP_DIRECTORY = "/tmp/";

    public File convertGifToMp4(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }

        String fileName = FilenameUtils.getBaseName(url);
        Path mp4FilePath = Paths.get(TEMP_DIRECTORY, fileName + ".mp4");

        String scaleFilter = String.format("scale='min(%d,iw)':-2,scale=-2:'min(%d,ih)'", MAX_WIDTH, MAX_HEIGHT);
        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(url))
                .addOutput(UrlOutput.toPath(mp4FilePath)
                        .setFormat("mp4")
                        .addArguments("-movflags", "faststart")
                        .addArguments("-pix_fmt", "yuv420p")
                        .addArguments("-vf", scaleFilter)
                        .addArguments("-crf", "23")
                        .addArguments("-loglevel", DEBUG_INFO))
                .execute();
        log.info("GIF converted to MP4: {}", mp4FilePath);
        tempFiles.add(mp4FilePath.toFile());
        return mp4FilePath.toFile();
    }

    public void deleteTempFiles() {
        tempFiles.stream().filter(File::exists).forEachOrdered(File::delete);
    }
}
