package com.eb.schedule;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Egor on 14.06.2016.
 */
public class ItemImageUtils {

    private final Logger logger = LoggerFactory.getLogger(ItemImageUtils.class);

    private static final String GET_ITEM_LOGO = "http://media.steampowered.com/apps/dota2/images/items/%s_lg.png";

    private final String dataFolder;
    private final byte[] defaultImage;

    private final LoadingCache<String, byte[]> images = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, byte[]>() {
                        public byte[] load(String imageId) {
                            return processImage(imageId);
                        }
                    });

    public ItemImageUtils(String dataFolder) throws IOException {
        this.dataFolder = dataFolder;
        this.defaultImage = getDefaultImage(dataFolder);
    }

    private byte[] getDefaultImage(String dataFolder) throws IOException {
        Path path = Paths.get(dataFolder).resolve("-1.png");
        return Files.readAllBytes(path);
    }

    public byte[] getImage(String id) {
        try {
            return images.get(id);
        } catch (ExecutionException ignore) {
        }
        return defaultImage;
    }

    private byte[] processImage(String name) {
        try {
            String url = String.format(GET_ITEM_LOGO, name);

            try (InputStream inputStream = Unirest.get(url).asBinary().getBody()) {
                int read = 0;
                byte[] bytes = new byte[1024];
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }

                    saveImage(outputStream, name);
                    return outputStream.toByteArray();
                }
            }
        } catch (Exception e) {
            logger.error("couldn't get image with id: " + name);
        }
        return defaultImage;
    }

    private void saveImage(ByteArrayOutputStream outputStream, String name) throws Exception {
        try (FileOutputStream file = new FileOutputStream(new File(dataFolder, name + ".png"))) {
            outputStream.writeTo(file);
        }
    }
}