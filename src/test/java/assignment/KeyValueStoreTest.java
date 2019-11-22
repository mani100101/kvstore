package assignment;

import assignment.exception.DataStoreException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class KeyValueStoreTest {

    private static final KeyValueStore keyValueStore = new KeyValueStore();

    @Test(expected = DataStoreException.class)
    public void testKeyValidity() {
        keyValueStore.get("djsfksafjksajfkjsakfjlskjfksajkfsakfasjfaksfasfkdfkjefijiefjijsdiajf");
    }

    @Test(expected = DataStoreException.class)
    public void testPutNullKey() {
        keyValueStore.put(null, new JSONObject());
    }

    @Test(expected = DataStoreException.class)
    public void testPutNullJson() {
        keyValueStore.put("abc", null);
    }

    @Test(expected = DataStoreException.class)
    public void testPutDuplicateKey() {
        JSONObject input = new JSONObject(Collections.singletonMap("name", "bob"));
        keyValueStore.put("duplicate", input);
        keyValueStore.put("duplicate", input);
    }

    @Test
    public void testPut() {
        JSONObject input = new JSONObject(Collections.singletonMap("name", "bob"));
        keyValueStore.put("abc", input);
        JSONObject output = keyValueStore.get("abc");
        Assert.assertEquals("Json not equal", output.toString(), input.toString());
    }

    @Test
    public void testPutWithTTL() throws InterruptedException {
        JSONObject input = new JSONObject(Collections.singletonMap("name", "alice"));
        keyValueStore.put("def", input, 15);
        JSONObject outputBeforeEviction = keyValueStore.get("def");
        Assert.assertEquals("Json null before evict", outputBeforeEviction.toString(), input.toString());
        Thread.sleep(20*1000);
        JSONObject outputAfterEviction = keyValueStore.get("def");
        Assert.assertNull("Json null after evict", outputAfterEviction);
    }

    @Test
    public void testRemove() {
        JSONObject input = new JSONObject(Collections.singletonMap("name", "alice"));
        keyValueStore.put("xyz", input);
        keyValueStore.remove("xyz");
        JSONObject afterRemove = keyValueStore.get("xyz");
        Assert.assertNull("Json not null after remove", afterRemove);
    }
}