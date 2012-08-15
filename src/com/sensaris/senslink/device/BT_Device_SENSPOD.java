/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.common.Utils;
/*     */ import com.sensaris.senslink.SensLinkFrame;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ 
/*     */ public class BT_Device_SENSPOD extends BT_Device
/*     */ {
/*     */   public static final String TYPE_GPS = "GPS";
/*     */   public static final String TYPE_INFO = "TYPE_INFO";
/*     */   public static final String frame_version = "1.1";
/*     */   public static final String REQ_CMD_SP_SUSPEND = "suspend";
/*     */   public static final String REQ_CMD_SP_SUSPEND_POD = "suspend\n";
/*     */   public static final String REQ_CMD_SP_RESET = "reset";
/*     */   public static final String REQ_CMD_SP_RESET_POB = "reset\n";
/*     */   public static final String REQ_CMD_SP_REBOOT = "reboot";
/*     */   public static final String REQ_CMD_SP_REBOOT_POD = "reboot\n";
/*     */   public static final String REQ_CMD_SP_SHUTDOWN = "shutdown";
/*     */   public static final String REQ_CMD_SP_SHUTDOWN_POD = "shutdown\n";
/*     */   public static final String REQ_CMD_SP_LIST = "LIST";
/*     */   public static final String REQ_CMD_SP_LIST_POD = "LIST\n";
/*     */   public static final String REQ_CMD_SP_GET = "GET";
/*     */   public static final String REQ_CMD_SP_GET_POD = "GET\n";
/*     */   public static final String REQ_CMD_SP_PUT = "PUT";
/*     */   public static final String REQ_CMD_SP_PUT_POD = "PUT\n";
/*     */   public static final String REQ_CMD_SP_DEL = "DEL";
/*     */   public static final String REQ_CMD_SP_DEL_POD = "DEL\n";
/*     */   public static final String REQ_CMD_SP_SETECHO = "setecho";
/*     */   public static final String REQ_CMD_SP_SETECHO_POD = "setecho\n";
/*     */   public static final String REQ_CMD_SP_SETTIME = "settime";
/*     */   public static final String REQ_CMD_SP_SETTIME_POD = "settime\n";
/*     */   static final String header_sensaris_value = "$PSEN";
/*     */   static final String header_sensaris_info = "$PINF";
/*     */   static final String header_sensaris_gprmc = "$GPRMC";
/* 109 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */ 
/*     */   BT_Device_SENSPOD(ServiceRecord service) throws IOException
/*     */   {
/* 113 */     super(service);
/* 114 */     init();
/*     */   }
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/* 139 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/* 142 */     command_list.put(name, cmd);
/*     */   }
/*     */ 
/*     */   public BT_Device_SENSPOD()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 152 */     this.STX = "$".getBytes();
/*     */ 
/* 154 */     this.ETX = "\n".getBytes();
/*     */   }
/*     */ 
/*     */   protected void parse_frame(byte[] frame)
/*     */   {
/* 161 */     StringBuilder sb = new StringBuilder();
/* 162 */     for (byte b : frame)
/* 163 */       sb.append((char)b);
/* 164 */     parse_frame(sb.toString());
/*     */   }
/*     */ 
/*     */   protected void parse_frame(String frame)
/*     */   {
/* 273 */     String[] frame_arr = frame.split(",");
/* 274 */     String type = "";
/* 275 */     String time = "";
/* 276 */     String value = "";
/* 277 */     int len = frame_arr.length;
/*     */ 
/* 279 */     int sensaris_frame_value = frame.indexOf("$PSEN");
/* 280 */     int sensaris_frame_info = frame.indexOf("$PINF");
/* 281 */     int sensaris_frame_gps = frame.indexOf("$GPRMC");
/* 282 */     boolean bheader_time = frame.startsWith(this.header_sensaris_time);
/* 283 */     String epoch_measure = null;
/* 284 */     if ((sensaris_frame_value >= 0) || (sensaris_frame_info >= 0) || (sensaris_frame_gps >= 0)) {
/* 285 */       if (bheader_time) {
/*     */         try {
/* 287 */           String[] time_arr = frame_arr[0].split(":");
/* 288 */           time = time + "," + this.type_Day + "," + time_arr[0].substring(1);
/* 289 */           time = time + "," + this.type_Hour + "," + time_arr[1];
/* 290 */           time = time + "," + this.type_Millisec + "," + time_arr[2];
/* 291 */           epoch_measure = Utils.convert_date_time_sens_to_iso(time_arr[0].substring(1), time_arr[1], time_arr[2]);
/*     */         } catch (Exception etime) {
/* 293 */           time = ",time_err,0";
/*     */         }
/*     */       }
/*     */       else {
/* 297 */         epoch_measure = Utils.iso_date(System.currentTimeMillis());
/*     */       }
/*     */ 
/* 300 */       if (sensaris_frame_gps >= 0) {
/* 301 */         type = "GPS";
/*     */ 
/* 303 */         value = frame.substring(sensaris_frame_gps + "$GPRMC".length());
/* 304 */         value = value.substring(1, value.length() - 2);
/* 305 */         value = value + time;
/* 306 */         set_last_value(type, value);
/*     */ 
/* 308 */         type = "GPS";
/* 309 */         SensLinkFrame m_frame = get_frame_header(type, "1.1");
/*     */ 
/* 314 */         String[] ar = frame.split(",");
/* 315 */         String data_type = ar[0].substring(1);
/*     */ 
/* 321 */         int lenh = bheader_time ? ar[0].length() + 1 : 0;
/*     */ 
/* 324 */         value = frame.substring(lenh, frame.length() - 2);
/*     */ 
/* 326 */         boolean b_parse_err = false;
/* 327 */         if (data_type.equals("GPRMC"))
/*     */         {
/* 330 */           String[] coord = parse_gprmc(value);
/*     */ 
/* 333 */           if (null != coord) {
/* 334 */             m_frame.add_key_value("point", coord[0] + " " + coord[1]);
/*     */ 
/* 338 */             m_frame.add_key_value("status", coord[2]);
/*     */ 
/* 341 */             m_frame.set_parsed();
/*     */           } else {
/* 343 */             Log.log(1, "GPRMC frame: Unable to find coord or locked byte (" + value + ")");
/*     */ 
/* 348 */             b_parse_err = true;
/*     */           }
/* 350 */         } else if (!data_type.equals("GPZDA")) {
/* 351 */           data_type = "Err_GP";
/* 352 */         }value = value.replace(',', '!');
/*     */ 
/* 360 */         m_frame.add_data_type(data_type);
/* 361 */         m_frame.add_frame_type(type);
/* 362 */         m_frame.add_key_value(data_type, value);
/*     */ 
/* 368 */         m_frame.add_key_value("unit", data_type);
/* 369 */         m_frame.add_key_value("note", "Comas are replaced by exclamation marks in std " + type + " frame.");
/*     */ 
/* 373 */         set_last_value(type, m_frame);
/*     */       }
/* 375 */       else if (sensaris_frame_info >= 0) {
/* 376 */         type = "TYPE_INFO";
/* 377 */         value = frame.substring(sensaris_frame_info + "$PINF".length());
/* 378 */         value = value.substring(1, value.length() - 2);
/* 379 */         value = value + time;
/* 380 */         set_last_info("info", value);
/*     */       }
/* 383 */       else if (sensaris_frame_value >= 0)
/*     */       {
/* 385 */         type = "TYPE_UNKNOWN";
/*     */         try
/*     */         {
/* 388 */           value = frame.substring(sensaris_frame_value + "$PSEN".length());
/* 389 */           value = value.substring(1, value.length() - 2);
/* 390 */           String[] val_ar = value.split(",");
/* 391 */           if (val_ar.length > 1)
/*     */           {
/* 393 */             type = val_ar[0];
/* 394 */             type = type.trim();
/* 395 */             SensLinkFrame m_frame = get_frame_header(type, "1.1");
/* 396 */             int index = this.data_type.indexOf(type);
/*     */ 
/* 400 */             value = value.substring(type.length() + 1);
/* 401 */             for (int i = 1; i < val_ar.length; i += 2)
/*     */             {
/* 404 */               m_frame.add_key_value(val_ar[i].trim(), val_ar[(i + 1)].trim());
/*     */             }
/* 406 */             m_frame.add_epoch_measure(epoch_measure);
/* 407 */             if (-1 == index) {
/* 408 */               ArrayList type_val = new ArrayList();
/* 409 */               int nb_sub_type = (val_ar.length - 1) / 2;
/* 410 */               for (int j = 0; j < nb_sub_type; j++) {
/* 411 */                 type_val.add(j, val_ar[(1 + 2 * j)]);
/*     */               }
/* 413 */               add_type(type, type_val);
/*     */             }
/* 415 */             set_last_value(type, m_frame);
/*     */           } else {
/* 417 */             value = frame.substring(sensaris_frame_value + "$PSEN".length());
/* 418 */             value = value.substring(1, value.length() - 2);
/* 419 */             value = value + time;
/* 420 */             set_last_value(type, value);
/*     */           }
/*     */         }
/*     */         catch (Exception etype) {
/* 424 */           value = frame.substring(sensaris_frame_value + "$PSEN".length());
/* 425 */           value = value.substring(0, value.length() - 2);
/* 426 */           value = value + time;
/* 427 */           set_last_value(type, value);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 436 */       if (null != this._cmd_in_progress)
/*     */       {
/* 438 */         if (this._cmd_in_progress._name.contains("reboot"))
/*     */         {
/* 440 */           if (frame.contains("reboot"))
/*     */           {
/* 442 */             reply_command(frame, true);
/* 443 */             return;
/*     */           }
/*     */         }
/* 446 */         else if (this._cmd_in_progress._name.contains("suspend"))
/*     */         {
/* 448 */           if (frame.contains("suspend"))
/*     */           {
/* 450 */             reply_command(frame, true);
/* 451 */             return;
/*     */           }
/*     */         }
/* 454 */         else if (this._cmd_in_progress._name.contains("reset"))
/*     */         {
/* 456 */           if (frame.contains("reset"))
/*     */           {
/* 458 */             reply_command(frame, true);
/* 459 */             return;
/*     */           }
/*     */         }
/*     */       }
/* 463 */       reply_command(frame, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public SensLinkFrame get_frame_header()
/*     */   {
/* 484 */     SensLinkFrame m_frame = new SensLinkFrame("TYPE_UNKNOWN", "1.1");
/* 485 */     m_frame.add_SensLink_id(Main.SensLink_id);
/* 486 */     m_frame.add_device_name(this.device_name);
/* 487 */     m_frame.add_device_bt_address(this.device_bt_address);
/* 488 */     return m_frame;
/*     */   }
/*     */ 
/*     */   static String[] parse_gprmc(String to_parse)
/*     */   {
/*     */     try
/*     */     {
/* 501 */       String[] ar = to_parse.split(",");
/* 502 */       if (ar.length != 13) {
/* 503 */         Log.log(1, "parse_gprmc length != 13");
/*     */ 
/* 505 */         return null;
/*     */       }
/* 507 */       String tmp = ar[3];
/* 508 */       if ((null == tmp) || (tmp.length() == 0))
/*     */       {
/*     */         String tmp44_42 = "0"; ar[1] = tmp44_42; ar[0] = tmp44_42;
/*     */ 
/* 512 */         ar[2] = "V";
/* 513 */         return ar;
/*     */       }
/* 515 */       tmp = Utils.sexag_to_dec(tmp);
/* 516 */       if (null == tmp)
/* 517 */         return null;
/* 518 */       if (ar[4].equalsIgnoreCase("S"))
/* 519 */         tmp = "-" + tmp;
/* 520 */       ar[0] = tmp;
/* 521 */       tmp = ar[5];
/* 522 */       tmp = Utils.sexag_to_dec(tmp);
/* 523 */       if (null == tmp)
/* 524 */         return null;
/* 525 */       if (ar[6].equalsIgnoreCase("W"))
/* 526 */         tmp = "-" + tmp;
/* 527 */       ar[1] = tmp;
/* 528 */       tmp = ar[2].trim();
/* 529 */       ar[2] = tmp;
/* 530 */       return ar;
/*     */     } catch (Exception e) {
/* 532 */       Log.log(1, "parse_gprmc " + e);
/* 533 */     }return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 121 */     set_command("suspend", new Command(0, "suspend\n", 0, true, false));
/*     */ 
/* 123 */     set_command("reboot", new Command(0, "reboot\n", 0, true, false));
/* 124 */     set_command("shutdown", new Command(0, "shutdown\n", 0, true, false));
/* 125 */     set_command("LIST", new Command(0, "LIST\n", 0, true, true));
/*     */ 
/* 127 */     set_command("GET", new Command(0, "GET\n", 1, true, true));
/* 128 */     set_command("PUT", new Command(0, "PUT\n", 4, true, false));
/* 129 */     set_command("DEL", new Command(0, "DEL\n", 1, true, false));
/*     */ 
/* 131 */     set_command("setecho", new Command(0, "setecho\n", 1, true, false));
/* 132 */     set_command("settime", new Command(0, "settime\n", 1, true, false));
/*     */ 
/* 136 */     get_command("DEL")._b_implemented = false;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_SENSPOD
 * JD-Core Version:    0.6.0
 */