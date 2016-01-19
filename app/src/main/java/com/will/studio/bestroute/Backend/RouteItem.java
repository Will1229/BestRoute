package com.will.studio.bestroute.backend;

import android.util.Log;

import com.will.studio.bestroute.frontend.main.NewItemActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by egaozhi on 2015-12-21.
 *
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
                '}';
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

    public RouteItem(String from, String to, String time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }


    public String getFrom() {
        return from;
    }


    public String getTime() {

        return time;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public boolean writeToDisc(final String filePath) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(filePath);
            oos = new ObjectOutputStream(fos);
            setFilePath(filePath);
            oos.writeObject(this);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(NewItemActivity.TAG, "save item to " + filePath);
        return true;
    }

    public static RouteItem restoreFromDisc(final String filePath) {

        RouteItem item = null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            item = (RouteItem) ois.readObject();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (final Exception ioe) {
                ioe.printStackTrace();
            }
        }
        Log.d(NewItemActivity.TAG, "restore item from " + filePath);
        return item;
    }

    public void delete() {
        File file = new File(filePath);
        file.delete();
    }

}
