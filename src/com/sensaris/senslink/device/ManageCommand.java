package com.sensaris.senslink.device;

import com.sensaris.common.Command;
import com.sensaris.common.Device_Data_Reader;

public abstract interface ManageCommand
{
  public abstract void put_command(Device_Data_Reader paramDevice_Data_Reader, Command paramCommand);

  public abstract void set_command_completed(Command paramCommand);

  public abstract Command get_command(String paramString);
}

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.ManageCommand
 * JD-Core Version:    0.6.0
 */