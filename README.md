# Simple file based key value store

Key value store with basic create, read and delete operations with time-to-live support.

# Usage

```
   KeyValueStore store1 = new KeyStore();
   
   //Optional location configuration for data store
   KeyValueStore store2 = new KeyStore("/home/store");
   
   store1.put("abc", new JSONObject(Collections.singletonMap("name", "bob")));
   store1.get("abc");
   store1.remove("abc");
```


