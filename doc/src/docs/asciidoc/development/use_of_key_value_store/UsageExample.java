package com.example.my.pkg;

import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStoreManager;
import org.springframework.stereotype.Component;

@Component
public class UsageExample {

    @Autowired
    private KeyValueStoreManager manager;

    public void doSomethingWithKeyValueStore() {
        // Use String-String map
        try (KeyValueStore<String, String> map = this.manager.getMap("name-of-map")) {
            // put a new entry
            map.put("key", "value");

            // optimizes underlying data structure (depends on backend) (optional)
            map.optimize();

            // after use of map, "KeyValueStore#close" should be invoked.
            // in this example, we use try-resource statement,
            // "#close" is automatically called.
            // map.close();
        }

        // Invocation of "manager.getMap(name)" is equivalent to
        // "manager.getMap(name, String.class, String.class)."
        // If you want, for example, KeyValueStore<Stirng, Integer>
        // instead of KeyValueStore<String, String>,
        // you have to explicitly specify types of keys and values.
        try (KeyValueStore<String, Integer> map
                 = this.manager.getMap("name-of-map-2", String.class, Integer.class)) {

            doSomethingWithStringIntegerMap(map);
        }
    }

}
