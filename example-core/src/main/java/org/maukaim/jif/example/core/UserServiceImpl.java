package org.maukaim.jif.example.core;

import java.util.HashMap;
import java.util.Map;


public class UserServiceImpl implements UserService {
    private final Map<String, Integer> inventory;

    public UserServiceImpl(String name) {
        this(name, new HashMap<>());
    }

    public UserServiceImpl(String name, Map<String, Integer> defaultInventory) {
        this.inventory = defaultInventory;
    }

    @Override
    public Map<String, Integer> getInventory() {
        return this.inventory;
    }
}
