package com.sensaris.common;

import com.sensaris.senslink.device.Command_Manager;
import java.io.BufferedWriter;
import java.net.Socket;

class Application_handler extends Thread
{
  private static Command_Manager _cmd_manager;
  private Socket _socket;
  private BufferedWriter _bout;

  Application_handler(Socket so)
  {
  }

  public void run()
  {
  }

  public void disconnect()
  {
  }
}

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Application_handler
 * JD-Core Version:    0.6.0
 */