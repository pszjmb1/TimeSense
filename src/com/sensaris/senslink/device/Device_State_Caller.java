package com.sensaris.senslink.device;

public abstract interface Device_State_Caller
{
  public abstract Device_Manager device_manager_ref();

  public abstract void device_manager_connect_device_cb(String paramString, Device paramDevice, boolean paramBoolean);
}

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.Device_State_Caller
 * JD-Core Version:    0.6.0
 */