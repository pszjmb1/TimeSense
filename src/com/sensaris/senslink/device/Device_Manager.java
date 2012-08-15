package com.sensaris.senslink.device;

import java.util.ArrayList;

public abstract interface Device_Manager
{
  public static final String DEVICE_STATE_NOT_AVAILABLE = "n";
  public static final String DEVICE_STATE_AVAILABLE = "a";
  public static final String DEVICE_STATE_CONNECTED = "c";

  public abstract void connect_device(Device_State_Caller paramDevice_State_Caller, String paramString, boolean paramBoolean);

  public abstract void disconnect_device(Device_State_Caller paramDevice_State_Caller, String paramString);

  public abstract void list_available_device(Device_State_Caller paramDevice_State_Caller, boolean paramBoolean);

  public abstract void monitor_connected_devices(Device_State_Caller paramDevice_State_Caller, boolean paramBoolean);

  public abstract void send_command_device(Device_State_Caller paramDevice_State_Caller, String paramString1, String paramString2);

  public abstract ArrayList get_connected_device();

  public abstract Device get_device_connected(String paramString);

  public abstract boolean is_device_availaible(String paramString);
}

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.Device_Manager
 * JD-Core Version:    0.6.0
 */