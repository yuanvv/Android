package com.pachain.android.entity;

import java.util.ArrayList;

public class VotingOnionEntity {
    private String Name;
    private ArrayList<OnionKeyEntity> Keys;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<OnionKeyEntity> getKeys() {
        return Keys;
    }

    public void setKeys(ArrayList<OnionKeyEntity> keys) {
        Keys = keys;
    }
}
