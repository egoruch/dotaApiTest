package com.eb.schedule.shared.bean;

import com.google.gson.annotations.SerializedName;


/**
 * Created by Egor on 26.05.2016.
 */
public class GameBean {

    public int id;

    @SerializedName("st")
    public Long startTime;

    @SerializedName("r")
    public TeamBean radiant;

    @SerializedName("d")
    public TeamBean dire;

    @SerializedName("l")
    public LeagueBean league;

    @SerializedName("t")
    public String seriesType;

    @SerializedName("rw")
    public int radiantWin;

    @SerializedName("dw")
    public int direWin;

    public GameBean(int id, Long startTime, TeamBean radiant, TeamBean dire, LeagueBean league, String seriesType, int radiantWin, int direWin) {
        this.id = id;
        this.startTime = startTime;
        this.radiant = radiant;
        this.dire = dire;
        this.league = league;
        this.seriesType = seriesType;
        this.radiantWin = radiantWin;
        this.direWin = direWin;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GameBean{");
        sb.append("id=").append(id);
        sb.append(", radiant=").append(radiant);
        sb.append(", dire=").append(dire);
        sb.append(", league=").append(league);
        sb.append(", seriesType='").append(seriesType).append('\'');
        sb.append(", radiantWin=").append(radiantWin);
        sb.append(", direWin=").append(direWin);
        sb.append(", startTime=").append(startTime);
        sb.append('}');
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public TeamBean getRadiant() {
        return radiant;
    }

    public void setRadiant(TeamBean radiant) {
        this.radiant = radiant;
    }

    public TeamBean getDire() {
        return dire;
    }

    public void setDire(TeamBean dire) {
        this.dire = dire;
    }

    public LeagueBean getLeague() {
        return league;
    }

    public void setLeague(LeagueBean league) {
        this.league = league;
    }

    public String getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    public int getRadiantWin() {
        return radiantWin;
    }

    public void setRadiantWin(int radiantWin) {
        this.radiantWin = radiantWin;
    }

    public int getDireWin() {
        return direWin;
    }

    public void setDireWin(int direWin) {
        this.direWin = direWin;
    }
}