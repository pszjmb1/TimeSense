package com.sensaris.common;

public abstract interface SensPodListener
{
  public static final int SENSOR_UNKNOWN = 0;
  public static final int SENSOR_EVT_CONNECTING = 200;
  public static final int SENSOR_EVT_CONNECTED = 201;
  public static final int SENSOR_EVT_DISCONNECTED = 202;
  public static final int DB_EVT_REMOTE_OK = 300;
  public static final int DB_EVT_REMOTE_ER = 301;
  public static final int DB_EVT_LOCAL_OK = 302;
  public static final int DB_EVT_LOCAL_ER = 303;
  public static final int RSS_EVT_LOCAL_OK = 304;
  public static final int RSS_EVT_LOCAL_ER = 305;
  public static final int RSS_EVT_REMOTE_OK = 306;
  public static final int RSS_EVT_REMOTE_ER = 307;
  public static final int SENSOR_HUMIDITY = 1;
  public static final int SENSOR_MIC = 2;
  public static final int SENSOR_TEMPERATURE = 3;
  public static final int SENSOR_CO = 4;
  public static final int SENSOR_NO = 5;
  public static final int SENSOR_OXY = 6;
  public static final int SENSOR_GLUCO = 7;
  public static final int SENSOR_THERMO = 8;
  public static final int SENSOR_BP = 9;
  public static final int SENSOR_RAD5 = 10;
  public static final int SENSOR_RAD60 = 11;
  public static final int SENSOR_SIEVERT = 12;
  public static final int SENSOR_BP_CUFF = 13;
  public static final int SENSOR_CO2 = 14;
  public static final int SENSOR_UV = 15;
  public static final int SENSOR_O3 = 16;
  public static final int SENSOR_BATT = 100;
  public static final int SENSOR_GPS = 101;
  public static final int SENSOR_RTC = 110;
  public static final int HUMIDITY_MAX = 100;
  public static final int HUMIDITY_MIN = 0;
  public static final int TEMPERATURE_MAX = 40;
  public static final int TEMPERATURE_MIN = -10;
  public static final int OXY_MAX = 100;
  public static final int OXY_MIN = 0;
  public static final int PULSE_MAX = 250;
  public static final int PULS_MIN = 0;
  public static final int QUAL_MAX = 100;
  public static final int QUAL_MIN = 0;
}

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.SensPodListener
 * JD-Core Version:    0.6.0
 */