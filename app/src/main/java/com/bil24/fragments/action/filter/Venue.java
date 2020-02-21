package com.bil24.fragments.action.filter;

/**
 * Created by SVV on 12.05.2016
 */
public class Venue {
  private long venueId;
  private String venueName;
  private long cityId;
  private String cityName;
  private boolean cityPass;

  public Venue(long venueId, String venueName, long cityId, String cityName, boolean cityPass) {
    this(venueId, venueName, cityId, cityName);
    this.cityPass = cityPass;
  }

  public Venue(long venueId, String venueName, long cityId, String cityName) {
    this.venueId = venueId;
    this.venueName = venueName;
    this.cityId = cityId;
    this.cityName = cityName;
    this.cityPass = false;
  }

  public long getVenueId() {
    return venueId;
  }

  public String getVenueName() {
    return venueName;
  }

  public long getCityId() {
    return cityId;
  }

  public String getCityName() {
    return cityName;
  }

  public boolean isCityPass() {
    return cityPass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Venue)) return false;
    Venue order = (Venue) o;
    return venueId == order.getVenueId();
  }

  @Override
  public int hashCode() {
    return (int) (venueId ^ (venueId >>> 32));
  }
}
