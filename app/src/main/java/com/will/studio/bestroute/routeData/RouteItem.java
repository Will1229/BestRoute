package com.will.studio.bestroute.routeData;

import com.akexorcist.googledirection.model.Direction;

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
    private boolean isSwitchedOn = true;
    private double fromLat = 0.0;
    private double fromLng = 0.0;
    private double toLat = 0.0;
    private double toLng = 0.0;
    private int alarmRequestCode;
    private transient Direction direction = null;

    public RouteItem(String from, String to, String time) {
        this.from = from;
        this.to = to;
        this.time = time;
        alarmRequestCode = generateCode();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
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

    public boolean isSwitchedOn() {
        return isSwitchedOn;
    }

    public void setIsSwitchedOn(boolean isSwitchedOn) {
        this.isSwitchedOn = isSwitchedOn;
    }

    @Override
    public String toString() {
        return "RouteItem{" +
                "alarmRequestCode=" + alarmRequestCode +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", time='" + time + '\'' +
                ", filePath='" + filePath + '\'' +
                ", isSwitchedOn=" + isSwitchedOn +
                ", fromLat=" + fromLat +
                ", fromLng=" + fromLng +
                ", toLat=" + toLat +
                ", toLng=" + toLng +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteItem)) return false;

        RouteItem routeItem = (RouteItem) o;

        if (isSwitchedOn() != routeItem.isSwitchedOn()) return false;
        if (Double.compare(routeItem.getFromLat(), getFromLat()) != 0) return false;
        if (Double.compare(routeItem.getFromLng(), getFromLng()) != 0) return false;
        if (Double.compare(routeItem.getToLat(), getToLat()) != 0) return false;
        if (Double.compare(routeItem.getToLng(), getToLng()) != 0) return false;
        if (getAlarmRequestCode() != routeItem.getAlarmRequestCode()) return false;
        if (getFrom() != null ? !getFrom().equals(routeItem.getFrom()) : routeItem.getFrom() !=
                null)
            return false;
        if (getTo() != null ? !getTo().equals(routeItem.getTo()) : routeItem.getTo() != null)
            return false;
        if (getTime() != null ? !getTime().equals(routeItem.getTime()) : routeItem.getTime() !=
                null)
            return false;
        //noinspection SimplifiableIfStatement
        if (getFilePath() != null ? !getFilePath().equals(routeItem.getFilePath()) : routeItem
                .getFilePath() != null)
            return false;
        return !(getDirection() != null ? !getDirection().equals(routeItem.getDirection()) :
                routeItem
                        .getDirection() != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getFrom() != null ? getFrom().hashCode() : 0;
        result = 31 * result + (getTo() != null ? getTo().hashCode() : 0);
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getFilePath() != null ? getFilePath().hashCode() : 0);
        result = 31 * result + (isSwitchedOn() ? 1 : 0);
        temp = Double.doubleToLongBits(getFromLat());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getFromLng());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getToLat());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getToLng());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getAlarmRequestCode();
        result = 31 * result + (getDirection() != null ? getDirection().hashCode() : 0);
        return result;
    }
}
