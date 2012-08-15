/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
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
/*     */ public class BT_Device_BP extends BT_Device
/*     */ {
/*     */   public static final String TYPE_BP = "Bp";
/*     */   public static final String frame_version = "1.1";
/*     */   public static final String TYPE_BP_info = "Bp_INFO";
/*     */   public static final String frame_info_version = "1.1";
/*     */   public static final String BP_ID = "BP_ID";
/*     */   public static final String PatientNumber = "PatientNumber";
/*     */   public static final String Power = "Power";
/*     */   public static final String Status = "Status";
/*     */   public static final String Systolic = "Systolic";
/*     */   public static final String Diastolic = "Diastolic";
/*     */   public static final String Average = "Average";
/*     */   public static final String HeartRate = "HeartRate";
/*  49 */   private static final byte[] cmd_start = { 38, 48, 10 };
/*  50 */   private static final byte[] cmd_last = { 38, 53, 10 };
/*  51 */   private static final byte[] cmd_stop = { 38, 49, 10 };
/*     */ 
/*  55 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */   Vector _frame_vect;
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/*  85 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/*  88 */     command_list.put(name, cmd);
/*     */   }
/*     */ 
/*     */   BT_Device_BP(ServiceRecord service)
/*     */     throws IOException
/*     */   {
/*  95 */     super(service);
/*  96 */     init();
/*     */   }
/*     */ 
/*     */   public BT_Device_BP()
/*     */   {
/* 103 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/* 110 */     this.STX = new byte[] { -88 };
/* 111 */     this.ETX = new byte[] { -89 };
/*     */ 
/* 113 */     this._frame_vect = new Vector();
/*     */   }
/*     */ 
/*     */   public boolean parse_reply(byte[] ar, Vector vframes)
/*     */   {
/* 126 */     String reply = new String(ar);
/* 127 */     boolean b_valid_reply = true;
/*     */     try
/*     */     {
/* 153 */       SensLinkFrame m_frame = get_frame_header("Bp", "1.1");
/*     */ 
/* 155 */       m_frame.add_key_value("BP_ID", reply.substring(1, 2));
/* 156 */       m_frame.add_key_value("PatientNumber", reply.substring(2, 4));
/*     */ 
/* 158 */       m_frame.add_epoch_measure(Utils.iso_date(System.currentTimeMillis()));
/* 159 */       m_frame.add_key_value("Power", reply.substring(16, 17));
/* 160 */       m_frame.add_key_value("Status", reply.substring(17, 18));
/*     */ 
/* 162 */       m_frame.add_key_value("Systolic", reply.substring(18, 21));
/* 163 */       m_frame.add_key_value("Diastolic", reply.substring(21, 24));
/* 164 */       m_frame.add_key_value("Average", reply.substring(24, 27));
/* 165 */       m_frame.add_key_value("HeartRate", reply.substring(27, 30));
/*     */ 
/* 167 */       vframes.addElement(m_frame);
/*     */     } catch (Exception e) {
/* 169 */       Log.log(0, "BP data (data frame) parsing error: " + e);
/* 170 */       b_valid_reply = false;
/*     */     }
/* 172 */     return b_valid_reply;
/*     */   }
/*     */ 
/*     */   public boolean parse_info(byte[] ar, Vector vframes)
/*     */   {
/* 178 */     String reply = new String(ar);
/* 179 */     boolean b_valid_reply = true;
/*     */     try {
/* 181 */       SensLinkFrame m_frame = get_frame_header("Bp_INFO", "1.1");
/* 182 */       m_frame.add_key_value("info", "not_yet_parsed");
/* 183 */       vframes.addElement(m_frame);
/*     */     } catch (Exception e) {
/* 185 */       Log.log(0, "Bp data (info frame) parsing error: " + e);
/* 186 */       b_valid_reply = false;
/*     */     }
/* 188 */     return b_valid_reply;
/*     */   }
/*     */ 
/*     */   public static String convert_date_time_tensio_to_iso(String date, String time)
/*     */   {
/* 199 */     StringBuffer ret = new StringBuffer();
/* 200 */     ret.append("20");
/* 201 */     ret.append(date.substring(0, 2));
/* 202 */     ret.append("-");
/* 203 */     ret.append(date.substring(2, 4));
/* 204 */     ret.append("-");
/* 205 */     ret.append(date.substring(4, 6));
/* 206 */     ret.append("T");
/* 207 */     ret.append(time.substring(0, 2));
/* 208 */     ret.append(":");
/* 209 */     ret.append(time.substring(2, 4));
/* 210 */     ret.append(":");
/* 211 */     ret.append(time.substring(4, 6));
/* 212 */     return ret.toString();
/*     */   }
/*     */ 
/*     */   protected void parse_frame(byte[] reply)
/*     */   {
/* 231 */     if ((null == reply) || (0 == reply.length)) {
/* 232 */       return;
/*     */     }
/* 234 */     boolean b_valid_reply = true;
/*     */ 
/* 236 */     int len = reply.length;
/* 237 */     if (!is_frame_ends_with_ETX(reply))
/*     */     {
/* 240 */       if (this._part_frame_index + len < 10000) {
/* 241 */         System.arraycopy(reply, 0, this._part_frame, this._part_frame_index, len);
/* 242 */         this._part_frame_index += len;
/*     */       }
/*     */       else {
/* 245 */         Log.log(0, "Frame received too large without ETX, buffer too small (10000) or (huge) garbage ??, reject it.");
/*     */ 
/* 248 */         byte[] arb = Utils.trim_frame(reply, this.STX[0]);
/*     */ 
/* 250 */         if (null != arb)
/*     */         {
/* 252 */           len = arb.length;
/* 253 */           Log.log(0, "Store Frame from seen STX, length " + len);
/* 254 */           System.arraycopy(arb, 0, this._part_frame, 0, len);
/* 255 */           this._part_frame_index = len;
/*     */         } else {
/* 257 */           this._part_frame_index = 0;
/*     */         }
/*     */       }
/* 260 */       return;
/*     */     }
/*     */ 
/* 263 */     if (this._part_frame_index > 0)
/*     */     {
/* 265 */       byte[] ar = new byte[this._part_frame_index + reply.length];
/* 266 */       System.arraycopy(this._part_frame, 0, ar, 0, this._part_frame_index);
/* 267 */       System.arraycopy(reply, 0, ar, this._part_frame_index, reply.length);
/* 268 */       reply = ar;
/*     */     }
/* 270 */     Boolean[] boolar = new Boolean[1];
/* 271 */     byte[] arb = Utils.trim_frame(reply, this.STX[0], boolar);
/* 272 */     if (null == arb)
/*     */     {
/* 274 */       return;
/*     */     }
/* 276 */     byte[] ar = Utils.destuffa9(arb);
/* 277 */     Log.log(4, "Frame to parse size after destuffing: " + ar.length);
/* 278 */     short chksum = Utils.CRC16(ar, 1, ar.length - 2);
/* 279 */     if (0 != chksum) {
/* 280 */       Log.log(1, "ERROR BP - calculated chksum is not 0: " + chksum);
/*     */     }
/*     */ 
/* 283 */     if (null == this._frame_vect) {
/* 284 */       this._frame_vect = new Vector();
/*     */     }
/*     */ 
/* 287 */     this._frame_vect.removeAllElements();
/* 288 */     if (boolar[0].booleanValue())
/* 289 */       parse_info(ar, this._frame_vect);
/*     */     else {
/* 291 */       b_valid_reply = parse_reply(ar, this._frame_vect);
/*     */     }
/* 293 */     if (!b_valid_reply)
/*     */     {
/* 295 */       Log.log(4, "\n BP Parsing error\n ???\n");
/* 296 */       reply_command("Error parsing", true);
/* 297 */       return;
/*     */     }
/*     */ 
/* 300 */     int item_nb = this._frame_vect.size();
/*     */ 
/* 302 */     if (item_nb > 0) {
/* 303 */       boolean b_ready = true;
/* 304 */       for (int i = 0; i < item_nb; i++) {
/*     */         try {
/* 306 */           SensLinkFrame m_frame = (SensLinkFrame)this._frame_vect.elementAt(i);
/*     */ 
/* 308 */           add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 309 */           set_last_value(m_frame.get_data_type(), m_frame);
/*     */ 
/* 311 */           reply_command(m_frame.serialise(), item_nb == i + 1);
/*     */         }
/*     */         catch (Exception e) {
/* 314 */           Log.log(4, "\nParsing error: " + e);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 320 */       Log.log(4, "\n NO MEASURES\n ???\n");
/* 321 */       reply_command("Error no measure", true);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  61 */     set_command("start", new Command(0, "start", cmd_start, 0, true, true));
/*     */ 
/*  65 */     set_command("stop", new Command(0, "stop", cmd_stop, 0, true, false, true));
/*     */ 
/*  67 */     set_command("last", new Command(0, "last", cmd_last, 0, true, true));
/*     */ 
/*  69 */     Command cmd = new CommandSleep("sleep_5000", 5000L);
/*  70 */     set_command(cmd._name, cmd);
/*     */ 
/*  75 */     ArrayList arl = new ArrayList();
/*     */ 
/*  77 */     cmd = new Command("lstart", arl);
/*     */ 
/*  79 */     arl.add("sleep_5000");
/*  80 */     arl.add("start");
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_BP
 * JD-Core Version:    0.6.0
 */