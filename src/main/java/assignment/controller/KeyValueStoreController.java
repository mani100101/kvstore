package assignment.controller;

import assignment.datastore.FileStore;
import assignment.model.DataStoreKey;

import java.util.PriorityQueue;

/**
 * @author Manikandan E
 */
public class KeyValueStoreController {

    private final FileStore fileStore;

    // Min heap to store short living keys at top to do eviction
    private final PriorityQueue<DataStoreKey> timedKeys =
            new PriorityQueue<>((key1, key2) -> (int) (key1.timeToLive() - key2.timeToLive()));

    public KeyValueStoreController(String path) {
        this.fileStore = new FileStore(path);
        startEvictionService();
    }

    public void put(String key, String value, long timeToLive) {
        if (timeToLive == -1) {
            fileStore.put(key, value);
            return;
        }
        synchronized (timedKeys) {
            fileStore.put(key, value);
            timedKeys.add(new DataStoreKey(key, timeToLive * 1000));
        }
    }

    public String get(String key) {
        return fileStore.get(key);
    }

    public void remove(String key) {
        fileStore.remove(key);
    }

    private void evictExpiredKeys() {
        while (!timedKeys.isEmpty() && timedKeys.peek().expired()) {
            fileStore.remove(timedKeys.poll().key());
        }
    }

    private void startEvictionService() {
        new Thread(() -> {
            while (true) {
                synchronized (timedKeys) {
                    evictExpiredKeys();
                }
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
