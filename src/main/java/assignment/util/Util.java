package assignment.util;

import assignment.exception.DataStoreException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;

import static assignment.constants.KeyValueStoreConstants.*;

public final class Util {

    private Util() { }

    public static String completeDataStorePath(String dataStorePath) {
        return dataStorePath + File.separator + UUID.randomUUID().toString() + STORE_FILE_EXTENSION;
    }

    public static String formKeyValuePair(String key, Object value) {
        return delimitedKey(key) + value;
    }

    public static String delimitedKey(String key) {
        return key + KEY_VALUE_DELIMITER;
    }

    public static String validateKey(String key) {
        if (key == null) {
            throw new DataStoreException(ERROR_MESSAGE_KEY_NULL);
        }
        if (key.length() > 32) {
            throw new DataStoreException(ERROR_MESSAGE_KEY_OUT_OF_RANGE);
        }
        return key;
    }

    public static String validateJson(JSONObject json) {
        if (json == null) {
            throw new DataStoreException(ERROR_MESSAGE_JSON_NULL);
        }
        String jsonStr = json.toString();
        if (jsonStr.getBytes(Charset.defaultCharset()).length / 1024 > 16) {
            throw new DataStoreException(ERROR_MESSAGE_JSON_OUT_OF_RANGE);
        }
        return jsonStr;
    }
}
