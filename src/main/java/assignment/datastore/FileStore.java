package assignment.datastore;

import assignment.exception.DataStoreException;
import assignment.util.Util;

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

import static assignment.constants.KeyValueStoreConstants.ERROR_MESSAGE_STORE_SIZE_OUT_OF_RANGE;
import static assignment.constants.KeyValueStoreConstants.KEY_VALUE_DELIMITER;

/**
 * @author Manikandan E
 */
public class FileStore {

    private final String keyValueStorePath;

    private final Lock readLock;

    private final Lock writeLock;

    public FileStore(String keyValueStorePath) {
        this.keyValueStorePath = keyValueStorePath;

        //Initializes data store..
        init();

        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    private void init() {
        Path path = Paths.get(keyValueStorePath);
        try {
            Files.createDirectories(path.getParent());
            if (!path.toFile().exists()) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new DataStoreException("Error initializing data store", e);
        }
    }

    public void put(String key, String value) {
        writeLock.lock();
        Path path = Paths.get(keyValueStorePath);
        try (Stream<String> lines = Files.lines(path)) {
            if (lines.anyMatch(line -> line.startsWith(Util.delimitedKey(key)))) {
                throw new DataStoreException("Key "+ key +" already exists. Use a different one.");
            }
            if ((path.toFile().length() / (1024 * 1024 * 1024)) < 1) {
                Files.write(path, (Util.formKeyValuePair(key, value) + System.lineSeparator()).getBytes(),
                        StandardOpenOption.APPEND);
            } else {
                throw new DataStoreException(ERROR_MESSAGE_STORE_SIZE_OUT_OF_RANGE);
            }
        } catch (IOException e) {
            throw new DataStoreException("Error accessing the data store", e);
        } finally {
            writeLock.unlock();
        }
    }

    public String get(String key) {
        readLock.lock();
        try (Stream<String> lines = Files.lines(Paths.get(keyValueStorePath))) {
            return lines.filter(line -> line.startsWith(Util.delimitedKey(key))).findFirst()
                    .map(s -> s.split(KEY_VALUE_DELIMITER)[1]).orElse(null);
        } catch (IOException e) {
            throw new DataStoreException("Error accessing the data store", e);
        } finally {
            readLock.unlock();
        }
    }

    public void remove(String key) {
        writeLock.lock();
        try (Stream<String> lines = Files.lines(Paths.get(keyValueStorePath))) {
            List<String> updatedContent = lines.filter(line -> !line.startsWith(Util.delimitedKey(key)))
                    .collect(Collectors.toList());
            Files.write(Paths.get(keyValueStorePath), updatedContent, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new DataStoreException("Error accessing the data store", e);
        } finally {
            writeLock.unlock();
        }
    }
}
