package com.fitpay.fitpaysampleclient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tgs on 5/17/16.
 */
public class SessionStorage {

    private static SessionStorage instance;
    private Map<String, Object> data;

    private SessionStorage() {
        // private constructor - singleton
    }

    public static synchronized SessionStorage getInstance() {
        if (null == instance) {
            instance = new SessionStorage();
            instance.data = new HashMap<>();
        }
        return instance;
    }


    public Object getData(String key) {
        return data.get(key);
    }

    public void putData(String key, Object o) {
        data.put(key, o);
    }

    public void removeData(String key) {
        data.remove(key);
    }
}
