package com.will.studio.bestroute.backend;

import java.io.Serializable;

/**
 * Created by egaozhi on 2015-12-21.
 * Project: BestRoute
 */
public class RouteItem implements Serializable {

    private String from;
    private String to;
    private String time;
    private String filePath;
    private double fromLat = 0.0;
    private double fromLng = 0.0;
    private double toLat = 0.0;
    private double toLng = 0.0;
    private int alarmRequestCode;

    public RouteItem(String from, String to, String time) {
        this.from = from;
        this.to = to;
        this.time = time;
        alarmRequestCode = generateCode();
    }

    @Override
    public String toString() {
        return "RouteItem{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", time='" + time + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fromLat=" + fromLat +
                ", fromLng=" + fromLng +
                ", toLat=" + toLat +
                ", toLng=" + toLng +
                ", alarmRequestCode=" + alarmRequestCode +
                '}';
    }

    public int getAlarmRequestCode() {
        return alarmRequestCode;
    }

    public double getFromLat() {
        return fromLat;
    }

    public void setFromLat(double fromLat) {
        this.fromLat = fromLat;
    }

    public double getFromLng() {
        return fromLng;
    }

    public void setFromLng(double fromLng) {
        this.fromLng = fromLng;
    }

    public double getToLat() {
        return toLat;
    }

    public void setToLat(double toLat) {
        this.toLat = toLat;
    }

    public double getToLng() {
        return toLng;
    }

    public void setToLng(double toLng) {
        this.toLng = toLng;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private int generateCode() {
        return time.hashCode();
    }

    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteItem)) return false;

        RouteItem routeItem = (RouteItem) o;

        if (Double.compare(routeItem.getFromLat(), getFromLat()) != 0) return false;
        if (Double.compare(routeItem.getFromLng(), getFromLng()) != 0) return false;
        if (Double.compare(routeItem.getToLat(), getToLat()) != 0) return false;
        if (Double.compare(routeItem.getToLng(), getToLng()) != 0) return false;
        if (getAlarmRequestCode() != routeItem.getAlarmRequestCode()) return false;
        if (!getFrom().equals(routeItem.getFrom())) return false;
        if (!getTo().equals(routeItem.getTo())) return false;
        //noinspection SimplifiableIfStatement
        if (!getTime().equals(routeItem.getTime())) return false;
        return getFilePath().equals(routeItem.getFilePath());

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getFrom().hashCode();
        result = 31 * result + getTo().hashCode();
        result = 31 * result + getTime().hashCode();
        result = 31 * result + getFilePath().hashCode();
        temp = Double.doubleToLongBits(getFromLat());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getFromLng());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getToLat());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getToLng());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getAlarmRequestCode();
        return result;
    }
}
