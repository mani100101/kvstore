package assignment;

import assignment.controller.KeyValueStoreController;
import assignment.exception.DataStoreException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author Manikandan E
 */
public class KeyValueStore {

    private final KeyValueStoreController controller;

    private KeyValueStore() {
        this.controller = new KeyValueStoreController("data");
    }

    public static KeyValueStore instance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final KeyValueStore INSTANCE = new KeyValueStore();
    }

    public void put(String key, JSONObject value, long timeToLiveInSeconds) {
        controller.put(validateKey(key), validateJson(value), timeToLiveInSeconds);
    }

    public void put(String key, JSONObject value) {
        put(key, value, -1);
    }

    public JSONObject get(String key) {
        String json = controller.get(validateKey(key));
        if (json == null) {
            return null;
        }
        return new JSONObject(json);
    }

    public void remove(String key) {
        controller.remove(validateKey(key));
    }

    private String validateKey(String key) {
        Objects.requireNonNull(key, "Key cannot be null");
        if (key.length() > 32) {
            throw new DataStoreException("Key cannot be more than 32 chars");
        }
        return key;
    }

    private String validateJson(JSONObject json) {
        Objects.requireNonNull(json, "Json cannot be null");
        String jsonStr = json.toString();
        if (jsonStr.getBytes(Charset.defaultCharset()).length / 1024 > 16) {
            throw new DataStoreException("Json size cannot exceed 16KB");
        }
        return jsonStr;
    }
}
