/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.CommandSleep;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.common.Utils;
/*     */ import com.sensaris.senslink.SensLinkFrame;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ 
/*     */ public class BT_Device_COPD extends BT_Device
/*     */ {
/*     */   public static final String TYPE_COPD = "COPd";
/*     */   public static final String frame_version = "1.1";
/*     */   public static final String TYPE_COPD_info = "COPd_INFO";
/*     */   public static final String frame_info_version = "1.1";
/*     */   public static final String COPD_ID = "COPD_ID";
/*     */   public static final String Gender = "Gender";
/*     */   public static final String Age = "Age";
/*     */   public static final String Height = "Height";
/*     */   public static final String RegressionSet = "RegressionSet";
/*     */   public static final String Weight = "Weight";
/*     */   public static final String FEV1Predicted = "FEV1Predicted";
/*     */   public static final String FEV1 = "FEV1";
/*     */   public static final String FEV6Predicted = "FEV6Predicted";
/*     */   public static final String FEV6 = "FEV6";
/*     */   public static final String FEV1FEV6Predicted = "FEV/1FEV6Predicted";
/*     */   public static final String FEV1FEV6 = "FEV1/FEV6";
/*     */   public static final String LungAge = "LungAge";
/*     */   public static final String Valid = "Valid";
/*     */   public static final String SwVersion = "SwVersion";
/*  63 */   private static final byte[] cmd_1rst_part = { 2, 68, 84 };
/*     */   private static final byte ACK = 6;
/*     */   private static final byte NAK = 21;
/*  73 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */   Vector _frame_vect;
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/* 132 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/* 135 */     command_list.put(name, cmd);
/*     */   }
/*     */ 
/*     */   BT_Device_COPD(ServiceRecord service)
/*     */     throws IOException
/*     */   {
/* 142 */     super(service);
/* 143 */     init();
/*     */   }
/*     */ 
/*     */   public BT_Device_COPD()
/*     */   {
/* 150 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/* 157 */     this.STX = new byte[] { 2 };
/* 158 */     this.ETX = new byte[] { 3 };
/*     */ 
/* 160 */     this._frame_vect = new Vector();
/*     */   }
/*     */ 
/*     */   void write_command(Command cmd)
/*     */     throws IOException
/*     */   {
/* 177 */     Log.log(1, "****** write_command: ---'" + cmd._name + "'---");
/* 178 */     int len = cmd_1rst_part.length;
/* 179 */     byte[] commande = new byte[cmd._cmd.length + len + 2];
/* 180 */     System.arraycopy(cmd_1rst_part, 0, commande, 0, len);
/* 181 */     System.arraycopy(cmd._cmd, 0, commande, cmd_1rst_part.length, cmd._cmd.length);
/* 182 */     len += cmd._cmd.length;
/* 183 */     commande[len] = 3;
/* 184 */     len++; commande[len] = Utils.xor_byte_checksum(commande, 0, len);
/* 185 */     Log.log(4, "****** write_command now: '" + Utils.display_hexa(commande) + "'---");
/*     */ 
/* 187 */     for (int i = 0; i < commande.length; i++)
/* 188 */       this.device_in.writeByte(commande[i]);
/*     */   }
/*     */ 
/*     */   public boolean parse_reply(byte[] ar, Vector vframes)
/*     */   {
/* 240 */     String reply = new String(ar);
/* 241 */     boolean b_valid_reply = true;
/*     */     try
/*     */     {
/*     */       String cmd;
/* 245 */       if (null == this._cmd_in_progress)
/* 246 */         cmd = get_cmd(null, reply);
/*     */       else {
/* 248 */         cmd = get_cmd(this._cmd_in_progress._full_cmd, reply);
/*     */       }
/* 250 */       if (null == cmd);
/* 255 */       SensLinkFrame m_frame = get_frame_header("COPd", "1.1");
/*     */ 
/* 257 */       m_frame.add_key_value("COPD_ID", reply.substring(4, 14));
/* 258 */       m_frame.add_key_value("Gender", reply.substring(14, 15));
/* 259 */       m_frame.add_key_value("Age", reply.substring(15, 17));
/* 260 */       m_frame.add_key_value("Height", reply.substring(17, 20));
/*     */ 
/* 262 */       m_frame.add_key_value("RegressionSet", reply.substring(20, 23));
/* 263 */       m_frame.add_key_value("Weight", reply.substring(23, 26));
/* 264 */       m_frame.add_key_value("FEV1Predicted", reply.substring(26, 29));
/* 265 */       m_frame.add_key_value("FEV1", reply.substring(29, 32));
/* 266 */       m_frame.add_key_value("FEV6Predicted", reply.substring(32, 35));
/* 267 */       m_frame.add_key_value("FEV6", reply.substring(35, 38));
/* 268 */       m_frame.add_key_value("FEV/1FEV6Predicted", reply.substring(38, 41));
/* 269 */       m_frame.add_key_value("FEV1/FEV6", reply.substring(41, 44));
/* 270 */       m_frame.add_key_value("LungAge", reply.substring(44, 47));
/* 271 */       m_frame.add_epoch_measure(convert_date_time_tensio_to_iso(reply.substring(47, 53), reply.substring(53, 59)));
/* 272 */       m_frame.add_key_value("Valid", reply.substring(59, 60));
/* 273 */       m_frame.add_key_value("SwVersion", reply.substring(60, 63));
/* 274 */       m_frame.set_parsed();
/*     */ 
/* 276 */       vframes.addElement(m_frame);
/* 277 */       b_valid_reply = true;
/*     */     } catch (Exception e) {
/* 279 */       Log.log(0, "COPD data (data frame) parsing error: " + e);
/* 280 */       b_valid_reply = false;
/*     */     }
/* 282 */     return b_valid_reply;
/*     */   }
/*     */ 
/*     */   String get_cmd(String in_cmd, String reply)
/*     */   {
/* 293 */     String ret_cmd = null;
/* 294 */     if (null == in_cmd)
/*     */     {
/* 296 */       ret_cmd = reply.substring(2, 4);
/*     */     }
/*     */     else {
/* 299 */       ret_cmd = reply.substring(3, 5);
/* 300 */       if (!in_cmd.startsWith(ret_cmd))
/*     */       {
/* 302 */         ret_cmd = in_cmd;
/*     */       }
/*     */     }
/* 304 */     return ret_cmd;
/*     */   }
/*     */ 
/*     */   public boolean parse_info(byte[] ar, Vector vframes)
/*     */   {
/* 310 */     String reply = new String(ar);
/* 311 */     boolean b_valid_reply = true;
/*     */     try {
/* 313 */       SensLinkFrame m_frame = get_frame_header("COPd_INFO", "1.1");
/* 314 */       m_frame.add_key_value("info", "not_yet_parsed");
/* 315 */       vframes.addElement(m_frame);
/*     */     } catch (Exception e) {
/* 317 */       Log.log(0, "COPd data (info frame) parsing error: " + e);
/* 318 */       b_valid_reply = false;
/*     */     }
/* 320 */     return b_valid_reply;
/*     */   }
/*     */ 
/*     */   public static String convert_date_time_tensio_to_iso(String date, String time)
/*     */   {
/* 331 */     StringBuffer ret = new StringBuffer();
/* 332 */     ret.append("20");
/* 333 */     ret.append(date.substring(0, 2));
/* 334 */     ret.append("-");
/* 335 */     ret.append(date.substring(2, 4));
/* 336 */     ret.append("-");
/* 337 */     ret.append(date.substring(4, 6));
/* 338 */     ret.append("T");
/* 339 */     ret.append(time.substring(0, 2));
/* 340 */     ret.append(":");
/* 341 */     ret.append(time.substring(2, 4));
/* 342 */     ret.append(":");
/* 343 */     ret.append(time.substring(4, 6));
/* 344 */     return ret.toString();
/*     */   }
/*     */ 
/*     */   protected void parse_frame(byte[] reply)
/*     */   {
/* 363 */     if ((null == reply) || (0 == reply.length)) {
/* 364 */       return;
/*     */     }
/* 366 */     boolean b_valid_reply = true;
/*     */ 
/* 368 */     int len = reply.length;
/* 369 */     byte by_chksum = reply[(len - 1)];
/* 370 */     byte[] by_tmp = new byte[len - 1];
/* 371 */     System.arraycopy(reply, 0, by_tmp, 0, len - 1);
/* 372 */     if (!is_frame_ends_with_ETX(by_tmp))
/*     */     {
/* 375 */       if (this._part_frame_index + len < 10000) {
/* 376 */         System.arraycopy(reply, 0, this._part_frame, this._part_frame_index, len);
/* 377 */         this._part_frame_index += len;
/*     */       }
/*     */       else {
/* 380 */         Log.log(0, "Frame received too large without ETX, buffer too small (10000) or (huge) garbage ??, reject it.");
/*     */ 
/* 383 */         byte[] arb = Utils.trim_frame(reply, this.STX[0]);
/*     */ 
/* 385 */         if (null != arb)
/*     */         {
/* 387 */           len = arb.length;
/* 388 */           Log.log(0, "Store Frame from seen STX, length " + len);
/* 389 */           System.arraycopy(arb, 0, this._part_frame, 0, len);
/* 390 */           this._part_frame_index = len;
/*     */         } else {
/* 392 */           this._part_frame_index = 0;
/*     */         }
/*     */       }
/* 395 */       return;
/*     */     }
/*     */ 
/* 398 */     if (this._part_frame_index > 0)
/*     */     {
/* 400 */       byte[] ar = new byte[this._part_frame_index + reply.length];
/* 401 */       System.arraycopy(this._part_frame, 0, ar, 0, this._part_frame_index);
/* 402 */       System.arraycopy(reply, 0, ar, this._part_frame_index, reply.length);
/* 403 */       reply = ar;
/*     */     }
/*     */ 
/* 406 */     byte[] arb = Utils.trim_frame(reply, this.STX[0]);
/* 407 */     if (null == arb)
/*     */     {
/* 409 */       return;
/*     */     }
/*     */ 
/* 412 */     byte[] ar = arb;
/*     */ 
/* 414 */     short chksum = (short)Utils.xor_byte_checksum(ar, 0, ar.length);
/* 415 */     if (0 != chksum) {
/* 416 */       Log.log(1, "ERROR COPD - calculated chksum is not equal to received: " + by_chksum + ", calc: " + Utils.xor_byte_checksum(ar, 0, ar.length - 1));
/*     */     }
/*     */ 
/* 419 */     if (null == this._frame_vect)
/*     */     {
/* 421 */       this._frame_vect = new Vector();
/*     */     }
/*     */ 
/* 424 */     this._frame_vect.removeAllElements();
/*     */ 
/* 429 */     b_valid_reply = parse_reply(ar, this._frame_vect);
/*     */ 
/* 431 */     if (!b_valid_reply)
/*     */     {
/* 433 */       Log.log(4, "\n COPD Parsing error\n ???\n");
/* 434 */       reply_command("Error parsing", false);
/* 435 */       return;
/*     */     }
/*     */ 
/* 438 */     int item_nb = this._frame_vect.size();
/*     */ 
/* 440 */     if (item_nb > 0) {
/* 441 */       boolean b_ready = true;
/* 442 */       for (int i = 0; i < item_nb; i++) {
/*     */         try {
/* 444 */           SensLinkFrame m_frame = (SensLinkFrame)this._frame_vect.elementAt(i);
/*     */ 
/* 446 */           add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 447 */           set_last_value(m_frame.get_data_type(), m_frame);
/*     */ 
/* 450 */           reply_command(m_frame.serialise(), false);
/*     */         } catch (Exception e) {
/* 452 */           Log.log(4, "\nParsing error: " + e);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 458 */       Log.log(4, "\n NO MEASURES\n ???\n");
/* 459 */       reply_command("Error no measure", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 465 */     while (this.connected) {
/*     */       try {
/* 467 */         byte[] device_frame = new byte[10000];
/* 468 */         int i = 0;
/* 469 */         int pos = 0;
/* 470 */         this.reading = true;
/* 471 */         byte[] byte_ETX = this.ETX;
/*     */ 
/* 473 */         while (this.reading)
/*     */         {
/* 476 */           if (0 == this.device_out.available())
/*     */             try {
/* 478 */               sleep(10L);
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/*     */             }
/* 483 */           if (0 == this.device_out.available())
/*     */             try {
/* 485 */               sleep(200L);
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/*     */             }
/* 490 */           if (0 == this.device_out.available()) {
/*     */             break;
/*     */           }
/* 493 */           device_frame[i] = this.device_out.readByte();
/*     */ 
/* 495 */           byte c = device_frame[i];
/* 496 */           if (c == byte_ETX[pos])
/* 497 */             pos++;
/*     */           else {
/* 499 */             pos = 0;
/*     */           }
/*     */ 
/* 502 */           if (byte_ETX.length == pos)
/*     */           {
/* 505 */             device_frame[(i++)] = byte_ETX[0];
/* 506 */             if (this.device_out.available() > 0)
/* 507 */               device_frame[i] = this.device_out.readByte();
/* 508 */             byte[] bar = new byte[i + 1];
/* 509 */             System.arraycopy(device_frame, 0, bar, 0, i + 1);
/* 510 */             Log.log(3, "brute device reception hexa: ---'" + Utils.display_hexa(bar) + "'---");
/* 511 */             Log.log(3, "brute device reception text: ---'" + new String(bar) + "'---");
/* 512 */             parse_frame(bar);
/* 513 */             i = 0;
/* 514 */             pos = 0;
/*     */           } else {
/* 516 */             i++;
/*     */           }
/*     */         }
/* 519 */         this.reading = false;
/* 520 */         if (i > 0)
/*     */         {
/* 523 */           byte[] bar = new byte[i + 1];
/* 524 */           System.arraycopy(device_frame, 0, bar, 0, i + 1);
/* 525 */           Log.log(3, "brute device reception hexa: ---'" + Utils.display_hexa(bar) + "'---");
/* 526 */           Log.log(3, "brute device reception text: ---'" + new String(bar) + "'---");
/* 527 */           parse_frame(bar);
/*     */         }
/*     */       } catch (IOException e) {
/* 530 */         Log.log(1, "BT_dev copd disconnected " + this.device_name + " -cnct flag is" + this.connected);
/* 531 */         Log.log(1, "Exception " + e);
/* 532 */         Main.command_manager.bt_Manager.disconnect_device(this.device_name);
/*     */ 
/* 534 */         close_connection();
/*     */       }
/*     */     }
/* 537 */     Log.log(1, "BT device thread exiting " + this.device_name);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  85 */     set_command("DI", new Command(0, "DI", "DI".getBytes(), 0, true, true));
/*     */ 
/*  87 */     set_command("CM", new Command(0, "CM", "CM".getBytes(), 0, true, false));
/*     */ 
/*  89 */     set_command("SZ", new Command(0, "SZ", "SZ".getBytes(), 3, true, false));
/*     */ 
/*  91 */     set_command("GZ", new Command(0, "GZ", "GZ".getBytes(), 0, true, true));
/*     */ 
/*  93 */     set_command("GB", new Command(0, "GB", "GB".getBytes(), 0, true, true));
/*     */ 
/*  97 */     set_command("XR", new Command(0, "XR", "XR".getBytes(), 0, true, false, true));
/*     */ 
/* 100 */     set_command("RD", new Command(0, "RD", "RD".getBytes(), 0, true, false));
/*     */ 
/* 102 */     set_command("GT", new Command(0, "GT", "GT".getBytes(), 0, true, true));
/*     */ 
/* 104 */     set_command("ST", new Command(0, "ST", "ST".getBytes(), 0, true, false));
/*     */ 
/* 106 */     set_command("ID", new Command(0, "ID", "ID".getBytes(), 0, true, false));
/*     */ 
/* 108 */     set_command("VM", new Command(0, "VM", "VM".getBytes(), 0, true, true));
/*     */ 
/* 112 */     Command cmd = new CommandSleep("sleep_5000", 5000L);
/* 113 */     set_command(cmd._name, cmd);
/*     */ 
/* 118 */     ArrayList arl = new ArrayList();
/*     */ 
/* 120 */     cmd = new Command("lstart", arl);
/*     */ 
/* 122 */     arl.add("sleep_5000");
/* 123 */     arl.add("start");
/*     */ 
/* 127 */     set_command("start", new Command(0, "VM", "VM".getBytes(), 0, true, true));
/* 128 */     set_command("stop", new Command(0, "stop", "ACK".getBytes(), 0, false, false, true));
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_COPD
 * JD-Core Version:    0.6.0
 */