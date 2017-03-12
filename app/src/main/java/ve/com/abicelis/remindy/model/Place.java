package ve.com.abicelis.remindy.model;

/**
 * Created by abice on 8/3/2017.
 */

public class Place {
    private int id;
    private String alias;
    private String address;
    private double latitude;
    private double longitude;
    private float radius;
    private boolean isOneOff;       //Places are one-off when a reminder is created with a Place = Other,
                                    // The place is saved in the database but it isn't a frequent one nor will it appear in saved places

    public Place(String alias, String address, double latitude, double longitude, float radius, boolean isOneOff) {
        this.alias = alias;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.isOneOff = isOneOff;
    }

    public Place(int id, String alias, String address, double latitude, double longitude, float radius, boolean isOneOff) {
        this(alias, address, latitude, longitude, radius, isOneOff);
        this.id = id;
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

    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
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
}
