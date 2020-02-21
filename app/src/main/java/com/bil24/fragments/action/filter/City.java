package com.bil24.fragments.action.filter;

import android.support.annotation.NonNull;
import server.net.obj.extra.*;

import java.util.*;

/**
 * Created by SVV on 12.05.2016
 */
public class City {
  private long cityId;
  private String cityName;

  public City(long cityId, String cityName) {
    this.cityId = cityId;
    this.cityName = cityName;
  }

  public long getCityId() {
    return cityId;
  }

  public String getCityName() {
    return cityName;
  }

  public static List<City> convertFromExtraCity(@NonNull List<ExtraCity> extraCityList) {
    List<City> cityList = new ArrayList<>();
    for (ExtraCity extraCity : extraCityList) {
      cityList.add(new City(extraCity.getCityId(), extraCity.getCityName()));
    }
    return cityList;
  }

  public static List<City> convertFromExtraFilterCity(@NonNull List<ExtraCityFilter> extraCityList) {
    List<City> cityList = new ArrayList<>();
    for (ExtraCityFilter extraCity : extraCityList) {
      cityList.add(new City(extraCity.getCityId(), extraCity.getCityName()));
    }
    return cityList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof City)) return false;
    City order = (City) o;
    return cityId == order.getCityId();
  }

  @Override
  public int hashCode() {
    return (int) (cityId ^ (cityId >>> 32));
  }
}
