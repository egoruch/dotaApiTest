package com.eb.schedule.shared.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Egor on 26.05.2016.
 */
public class LeagueBean {

    public int id;

    @SerializedName("n")
    public String name;

    public LeagueBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LeagueBean{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
