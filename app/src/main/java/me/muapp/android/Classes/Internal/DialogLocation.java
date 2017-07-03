package me.muapp.android.Classes.Internal;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 03/07/17.
 */

public class DialogLocation implements Parcelable {
    double latitude;
    double longitude;
    int radius;

    public DialogLocation() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    protected DialogLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        radius = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(radius);
    }

    public Location getLocation() {
        try {
            Location l = new Location("DIALOG_LOCATION");
            l.setLatitude(latitude);
            l.setLongitude(longitude);
            return l;
        } catch (Exception x) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DialogLocation> CREATOR = new Parcelable.Creator<DialogLocation>() {
        @Override
        public DialogLocation createFromParcel(Parcel in) {
            return new DialogLocation(in);
        }

        @Override
        public DialogLocation[] newArray(int size) {
            return new DialogLocation[size];
        }
    };

    @Override
    public String toString() {
        return "DialogLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                '}';
    }
}