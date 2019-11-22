package assignment.constants;

public final class KeyValueStoreConstants {

    public static final String DEFAULT_STORE_PATH = "data";
    public static final String STORE_FILE_EXTENSION = ".store";

    public static final String KEY_VALUE_DELIMITER = "=";

    public static final String ERROR_MESSAGE_KEY_NULL = "Key cannot be null";
    public static final String ERROR_MESSAGE_JSON_NULL = "Json cannot be null";
    public static final String ERROR_MESSAGE_KEY_OUT_OF_RANGE = "Key cannot be more than 32 chars";
    public static final String ERROR_MESSAGE_JSON_OUT_OF_RANGE = "Json size cannot exceed 16KB";
    public static final String ERROR_MESSAGE_STORE_SIZE_OUT_OF_RANGE = "Data store size limit reached";

    private KeyValueStoreConstants() {}
}
