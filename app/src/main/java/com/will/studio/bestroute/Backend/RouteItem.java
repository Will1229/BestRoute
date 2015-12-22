package com.will.studio.bestroute.Backend;

import android.util.Log;

import com.will.studio.bestroute.Frontend.NewItem.NewItemActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by egaozhi on 2015-12-21.
 */
public class RouteItem implements Serializable {

    private final String EMPTY_STRING = "";

    private String from = EMPTY_STRING;
    private String to = EMPTY_STRING;
    private String time = EMPTY_STRING;

    public RouteItem() {
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

    @Override
    public String toString() {
        return "RouteItem{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public boolean writeToDisc(final String filePath) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(filePath);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(getFrom());
            oos.writeObject(getTo());
            oos.writeObject(getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        Log.d(NewItemActivity.TAG, "save item to " + filePath);
        return true;
    }

    public boolean restoreFromDisc(final String filePath) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(filePath);
            ois = new ObjectInputStream(fis);
            setFrom((String) ois.readObject());
            setTo((String) ois.readObject());
            setTime((String) ois.readObject());
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                ois.close();
                fis.close();
            } catch (final Exception ioe) {
                ioe.printStackTrace();
                return false;
            }
        }
        Log.d(NewItemActivity.TAG, "restore item from " + filePath);
        return true;
    }
}
