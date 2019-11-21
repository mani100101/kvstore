package com.freshworks.assignment.datastore;

import com.freshworks.assignment.exception.DataStoreException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Manikandan E
 */
public class FileStore {

    private static final String FILE_NAME = "kv.txt";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String keyValueStorePath;

    private final Lock readLock;

    private final Lock writeLock;

    public FileStore(String keyValueStorePath) {
        this.keyValueStorePath = keyValueStorePath;

        //Initializes key value store..
        init();

        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    public void put(String key, String value) {
        writeLock.lock();
        Path path = Paths.get(keyValuePath());
        try (Stream<String> lines = Files.lines(path)) {
            if (lines.anyMatch(line -> line.startsWith(delimitedKey(key)))) {
                throw new RuntimeException("Key "+ key +" already exists. Use a different one.");
            }
            if ((path.toFile().length() / (1024 * 1024 * 1024)) < 1) {
                Files.write(path, (formKeyValuePair(key, value) + System.lineSeparator()).getBytes(),
                        StandardOpenOption.APPEND);
            } else {
                throw new DataStoreException("File size exceeded...!!!");
            }
        } catch (IOException e) {
            throw new DataStoreException("Error accessing the data store", e);
        } finally {
            writeLock.unlock();
        }

    }

    public String get(String key) {
        readLock.lock();
        try (Stream<String> lines = Files.lines(Paths.get(keyValuePath()))) {
            return lines.filter(line -> line.startsWith(delimitedKey(key))).findFirst()
                    .map(s -> s.split(KEY_VALUE_DELIMITER)[1]).orElse(null);
        } catch (IOException e) {
            throw new DataStoreException("Error accessing the data store", e);
        } finally {
            readLock.unlock();
        }
    }

    public void remove(String key) {
        writeLock.lock();
        try (Stream<String> lines = Files.lines(Paths.get(keyValuePath()))) {
            List<String> updatedContent = lines.filter(line -> !line.startsWith(delimitedKey(key))).collect(Collectors.toList());
            Files.write(Paths.get(keyValuePath()), updatedContent, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new DataStoreException("Error accessing the data store", e);
        } finally {
            writeLock.unlock();
        }
    }

    private void init() {
        Path path = Paths.get(keyValuePath());
        try {
            Files.createDirectories(path.getParent());
            if (!path.toFile().exists()) {
                Files.createFile(path);
                System.out.println("path.toAbsolutePath().toString() = " + path.toAbsolutePath().toString());
            }
        } catch (IOException e) {
            throw new DataStoreException("Error initializing data store", e);
        }
    }

    private String keyValuePath() {
        return keyValueStorePath + File.separator + FILE_NAME;
    }

    private static String formKeyValuePair(String key, Object value) {
        return delimitedKey(key) + value;
    }

    private static String delimitedKey(String key) {
        return key + KEY_VALUE_DELIMITER;
    }
}
