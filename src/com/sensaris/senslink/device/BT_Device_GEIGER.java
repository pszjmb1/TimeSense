/*    */ package com.sensaris.senslink.device;
/*    */ 
/*    */ import com.sensaris.common.Command;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import javax.bluetooth.ServiceRecord;
/*    */ 
/*    */ public class BT_Device_GEIGER extends BT_Device_SENSPOD
/*    */ {
/* 21 */   private static HashMap<String, Command> command_list = new HashMap();
/*    */ 
/*    */   BT_Device_GEIGER(ServiceRecord service) throws IOException {
/* 24 */     super(service);
/* 25 */     init();
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 32 */     BT_Device_SENSPOD.set_command("suspend", new Command(0, "suspend\n", 0, true, false));
/*    */ 
/* 34 */     BT_Device_SENSPOD.set_command("reboot", new Command(0, "reboot\n", 0, true, false));
/* 35 */     BT_Device_SENSPOD.set_command("shutdown", new Command(0, "shutdown\n", 0, true, false));
/* 36 */     BT_Device_SENSPOD.set_command("LIST", new Command(0, "LIST\n", 0, true, true));
/*    */ 
/* 38 */     BT_Device_SENSPOD.set_command("GET", new Command(0, "GET\n", 1, true, true));
/* 39 */     BT_Device_SENSPOD.set_command("PUT", new Command(0, "PUT\n", 4, true, false));
/* 40 */     BT_Device_SENSPOD.set_command("DEL", new Command(0, "DEL\n", 1, true, false));
/*    */ 
/* 42 */     BT_Device_SENSPOD.set_command("setecho", new Command(0, "setecho\n", 1, true, false));
/* 43 */     BT_Device_SENSPOD.set_command("settime", new Command(0, "settime\n", 1, true, false));
/*    */ 
/* 47 */     BT_Device_SENSPOD.get_command("DEL")._b_implemented = false;
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_GEIGER
 * JD-Core Version:    0.6.0
 */