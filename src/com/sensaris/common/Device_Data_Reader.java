package com.sensaris.common;

import com.sensaris.senslink.SensLinkFrame;
import com.sensaris.senslink.device.Device;

public abstract interface Device_Data_Reader
{
  public abstract void data_received_cb(Device paramDevice, String paramString1, String paramString2, boolean paramBoolean);

  public abstract void data_received_cb(Device paramDevice, String paramString, SensLinkFrame paramSensLinkFrame, boolean paramBoolean);
}

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Device_Data_Reader
 * JD-Core Version:    0.6.0
 */