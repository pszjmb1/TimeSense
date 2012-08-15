/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.CommandDevice;
/*     */ import com.sensaris.common.CommandSleep;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.common.Utils;
/*     */ import com.sensaris.senslink.SensLinkFrame;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ 
/*     */ public class BT_Device_OXY extends BT_Device
/*     */ {
/*     */   public static final String TYPE_OXY = "Oxy";
/*     */   public static final String frame_version = "1.1";
/*  41 */   private static final byte[] cmd_start = { 38, 48, 10 };
/*  42 */   private static final byte[] cmd_stop = { 38, 49, 10 };
/*     */ 
/*  44 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */   Vector _frame_vect;
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/*  70 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/*  73 */     command_list.put(name, cmd);
/*     */   }
/*     */ 
/*     */   BT_Device_OXY(ServiceRecord service)
/*     */     throws IOException
/*     */   {
/*  80 */     super(service);
/*  81 */     init();
/*     */   }
/*     */ 
/*     */   public BT_Device_OXY()
/*     */   {
/*  88 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  94 */     this.STX = new byte[] { -88 };
/*  95 */     this.ETX = new byte[] { -89 };
/*  96 */     this._frame_vect = new Vector();
/*     */   }
/*     */ 
/*     */   public boolean parse_reply(byte[] ar, Vector vframes)
/*     */   {
/* 112 */     if ((ar.length != 46) && (ar.length != 45))
/*     */     {
/* 114 */       Log.log(2, "Error:" + "Oxy" + " frame received has wrong length (expected 45): " + ar.length);
/* 115 */       return false;
/*     */     }
/*     */ 
/* 121 */     String reply = new String(ar);
/* 122 */     boolean b_valid_reply = true;
/*     */     try {
/* 124 */       SensLinkFrame m_frame = get_frame_header("Oxy", "1.1");
/*     */ 
/* 126 */       String time = reply.substring(4, 10);
/* 127 */       String date = reply.substring(10, 16);
/*     */ 
/* 129 */       m_frame.add_key_value("power", reply.substring(16, 17));
/*     */ 
/* 132 */       m_frame.add_epoch_measure(Utils.iso_date(System.currentTimeMillis()));
/* 133 */       m_frame.add_key_value("alarm", reply.substring(17, 18));
/* 134 */       m_frame.add_key_value("spO2", Integer.toString(extract_oxy(reply)));
/* 135 */       m_frame.add_key_value("signal_quality", Integer.toString(extract_quality(reply)));
/* 136 */       m_frame.add_key_value("plethysmogram", extract_plethysmogram(reply));
/*     */ 
/* 139 */       m_frame.add_key_value("heartrate", Integer.toString(extract_heartrate(reply)));
/* 140 */       vframes.addElement(m_frame);
/*     */     } catch (Exception e) {
/* 142 */       Log.log(0, "OXY data parsing error: " + e);
/* 143 */       b_valid_reply = false;
/*     */     }
/* 145 */     return b_valid_reply;
/*     */   }
/*     */ 
/*     */   static int extract_quality(String frame)
/*     */   {
/*     */     try
/*     */     {
/* 152 */       return frame.charAt(21); } catch (Exception e) {
/*     */     }
/* 154 */     return 0;
/*     */   }
/*     */ 
/*     */   static int extract_oxy(String frame)
/*     */   {
/*     */     try
/*     */     {
/* 161 */       return frame.charAt(18); } catch (Exception e) {
/*     */     }
/* 163 */     return 0;
/*     */   }
/*     */ 
/*     */   static int extract_heartrate(String frame)
/*     */   {
/*     */     try
/*     */     {
/* 170 */       return frame.charAt(20); } catch (Exception e) {
/*     */     }
/* 172 */     return 0;
/*     */   }
/*     */ 
/*     */   static int extract_heartrate2(String frame)
/*     */   {
/*     */     try
/*     */     {
/* 179 */       return frame.charAt(19); } catch (Exception e) {
/*     */     }
/* 181 */     return 0;
/*     */   }
/*     */ 
/*     */   static String extract_plethysmogram(String frame)
/*     */   {
/*     */     try
/*     */     {
/* 194 */       return Utils.display_hexa(frame.substring(22, 42), null); } catch (Exception e) {
/*     */     }
/* 196 */     return "";
/*     */   }
/*     */ 
/*     */   public static String convert_date_time_tensio_to_iso(String date, String time)
/*     */   {
/* 209 */     StringBuilder ret = new StringBuilder();
/* 210 */     ret.append("20");
/* 211 */     ret.append(date.substring(0, 2));
/* 212 */     ret.append("-");
/* 213 */     ret.append(date.substring(2, 4));
/* 214 */     ret.append("-");
/* 215 */     ret.append(date.substring(4, 6));
/* 216 */     ret.append("T");
/* 217 */     ret.append(time.substring(0, 2));
/* 218 */     ret.append(":");
/* 219 */     ret.append(time.substring(2, 4));
/* 220 */     ret.append(":");
/* 221 */     ret.append(time.substring(4, 6));
/*     */ 
/* 223 */     return ret.toString();
/*     */   }
/*     */ 
/*     */   protected void parse_frame(byte[] reply)
/*     */   {
/* 262 */     if ((null == reply) || (0 == reply.length)) {
/* 263 */       return;
/*     */     }
/* 265 */     int len = reply.length;
/* 266 */     if (!is_frame_ends_with_ETX(reply))
/*     */     {
/* 269 */       if (this._part_frame_index + len < 10000) {
/* 270 */         System.arraycopy(reply, 0, this._part_frame, this._part_frame_index, len);
/* 271 */         this._part_frame_index += len;
/*     */       }
/*     */       else {
/* 274 */         Log.log(0, "OXY: Frame received too large without ETX, buffer too small (10000) or (huge) garbage ??, reject it.");
/*     */ 
/* 277 */         byte[] arb = Utils.trim_frame(reply, this.STX[0]);
/*     */ 
/* 279 */         if (null != arb)
/*     */         {
/* 281 */           len = arb.length;
/* 282 */           Log.log(0, "Store Frame from seen STX, length " + len);
/* 283 */           System.arraycopy(arb, 0, this._part_frame, 0, len);
/* 284 */           this._part_frame_index = len;
/*     */         } else {
/* 286 */           this._part_frame_index = 0;
/*     */         }
/*     */       }
/* 289 */       return;
/*     */     }
/*     */ 
/* 292 */     if (this._part_frame_index > 0)
/*     */     {
/* 294 */       byte[] ar = new byte[this._part_frame_index + reply.length];
/* 295 */       System.arraycopy(this._part_frame, 0, ar, 0, this._part_frame_index);
/* 296 */       System.arraycopy(reply, 0, ar, this._part_frame_index, reply.length);
/* 297 */       reply = ar;
/*     */     }
/* 299 */     Boolean[] boolar = new Boolean[1];
/* 300 */     byte[] arb = Utils.trim_frame(reply, this.STX[0], boolar);
/* 301 */     if (null == arb)
/*     */     {
/* 303 */       return;
/*     */     }
/* 305 */     byte[] ar = Utils.destuffa9(arb);
/* 306 */     Log.log(4, "Frame length to parse size after destuffing: " + ar.length);
/* 307 */     short chksum = Utils.CRC16(ar, 1, ar.length - 2);
/* 308 */     if (0 != chksum) {
/* 309 */       Log.log(1, "ERROR OXY - calculated chksum is not 0: " + chksum);
/*     */     }
/*     */ 
/* 312 */     if (null == this._frame_vect) {
/* 313 */       this._frame_vect = new Vector();
/*     */     }
/*     */ 
/* 316 */     this._frame_vect.removeAllElements();
/*     */ 
/* 320 */     parse_reply(ar, this._frame_vect);
/*     */ 
/* 322 */     int item_nb = this._frame_vect.size();
/*     */ 
/* 324 */     if (item_nb > 0) {
/* 325 */       for (int i = 0; i < item_nb; i++) {
/*     */         try {
/* 327 */           SensLinkFrame m_frame = (SensLinkFrame)this._frame_vect.elementAt(i);
/* 328 */           add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 329 */           set_last_value(m_frame.get_data_type(), m_frame);
/*     */ 
/* 332 */           reply_command(m_frame.serialise(), false);
/*     */         } catch (Exception e) {
/* 334 */           Log.log(4, "\nOXY Parsing error: " + e);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 340 */       Log.log(4, "OXY: NO MEASURES\n ???\n");
/* 341 */       reply_command("Error no measure", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  50 */     set_command("start", new Command(0, "start", cmd_start, 0, true, true));
/*  51 */     set_command("stop", new Command(0, "stop", cmd_stop, 0, false, false, true));
/*     */ 
/*  54 */     Command cmd = new CommandSleep("sleep_5000", 5000L);
/*  55 */     set_command(cmd._name, cmd);
/*  56 */     cmd = new CommandSleep("sleep_1000", 1000L);
/*  57 */     set_command(cmd._name, cmd);
/*  58 */     cmd = new CommandDevice("dcn", "disconnect");
/*  59 */     set_command(cmd._name, cmd);
/*  60 */     ArrayList arl = new ArrayList();
/*     */ 
/*  62 */     cmd = new Command("open", arl);
/*  63 */     arl.add("start");
/*  64 */     arl.add("sleep_1000");
/*  65 */     arl.add("start");
/*     */ 
/*  67 */     set_command(cmd._name, cmd);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_OXY
 * JD-Core Version:    0.6.0
 */