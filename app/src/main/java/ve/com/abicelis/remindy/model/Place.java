package ve.com.abicelis.remindy.model;

import java.io.Serializable;

/**
 * Created by abice on 8/3/2017.
 */

public class Place implements Serializable {

    private static final int DEFAULT_RADIUS = 500;  //Default radius 500m

    private int id;
    private String alias;
    private String address;
    private double latitude;
    private double longitude;
    private int radius;
    private boolean isOneOff;       //Places are one-off when a reminder is created with a Place = Other,
                                    // The place is saved in the database but it isn't a frequent one nor will it appear in saved places

    public Place(String alias, String address, double latitude, double longitude, int radius, boolean isOneOff) {
        this.alias = alias;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.isOneOff = isOneOff;
    }

    public Place(int id, String alias, String address, double latitude, double longitude, int radius, boolean isOneOff) {
        this(alias, address, latitude, longitude, radius, isOneOff);
        this.id = id;
    }

    public Place(Place place) {
        this(place.getId(), place.getAlias(), place.getAddress(), place.getLatitude(), place.getLongitude(), place.getRadius(), place.isOneOff());
    }

    public Place() {
        radius = DEFAULT_RADIUS;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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

    public boolean isOneOff() {
        return isOneOff;
    }
    public void setOneOff(boolean oneOff) {
        isOneOff = oneOff;
    }

    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " alias=" + alias + "\r\n" +
                " address=" + address + "\r\n" +
                " latitude=" + latitude + "\r\n" +
                " longitude=" + longitude + "\r\n" +
                " radius=" + radius + "\r\n" +
                " isOneOff=" + isOneOff;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        if(obj instanceof Place) {
            Place that = (Place) obj;
            return (this.getId() == that.getId() &&
                    this.getAlias().equals(that.getAlias()) &&
                    this.getLatitude() == that.getLatitude() &&
                    this.getLongitude() == that.getLongitude() &&
                    this.getRadius() == that.getRadius());
        }
        return false;
    }
}
