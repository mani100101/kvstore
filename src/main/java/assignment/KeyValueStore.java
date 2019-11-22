package assignment;

import assignment.controller.KeyValueStoreController;
import assignment.exception.DataStoreException;
import assignment.util.Util;
import org.json.JSONObject;

import static assignment.constants.KeyValueStoreConstants.DEFAULT_STORE_PATH;

/**
 * @author Manikandan E
 */
public class KeyValueStore {

    private final KeyValueStoreController controller;

    public KeyValueStore() {
        this(DEFAULT_STORE_PATH);
    }

    public KeyValueStore(String dataStorePath) {
        this.controller = new KeyValueStoreController(Util.completeDataStorePath(dataStorePath));
    }

    /**
     * Add data to the store
     *
     * @param key key
     * @param value value
     * @param timeToLiveInSeconds time to live
     * @throws DataStoreException data store exception
     */
    public void put(String key, JSONObject value, long timeToLiveInSeconds) {
        controller.put(Util.validateKey(key), Util.validateJson(value), timeToLiveInSeconds);
    }

    /**
     * Add data to the store
     *
     * @param key key
     * @param value value
     * @throws DataStoreException data store exception
     */
    public void put(String key, JSONObject value) {
        put(key, value, -1);
    }

    /**
     * Get data from store
     *
     * @param key key
     * @return json value
     * @throws DataStoreException data store exception
     */
    public JSONObject get(String key) {
        String json = controller.get(Util.validateKey(key));
        if (json == null) {
            return null;
        }
        return new JSONObject(json);
    }

    /**
     * Remove data from store
     *
     * @param key key
     * @throws DataStoreException data store exception
     */
    public void remove(String key) {
        controller.remove(Util.validateKey(key));
    }
}
