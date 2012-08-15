/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.common.Utils;
/*     */ import com.sensaris.senslink.SensLinkFrame;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ 
/*     */ public class BT_Device_ECG extends BT_Device
/*     */ {
/*     */   public static final String TYPE_ECG_DATA = "ECG";
/*     */   public static final String frame_data_version = "1.1";
/*     */   public static final String TYPE_ECG_INFO = "ECG_INFO";
/*     */   public static final String frame_info_version = "1.1";
/*  50 */   private static final byte[] cmd_start = { 38, 48, 10 };
/*  51 */   private static final byte[] cmd_stop = { 38, 49, 10 };
/*     */   private static final String Device_type_name = "ECG";
/*  90 */   boolean b_system_frame = false;
/*     */ 
/*  92 */   public static boolean _bsimu = false;
/*     */ 
/*  94 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/* 105 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/* 108 */     command_list.put(name, cmd);
/*     */   }
/*     */ 
/*     */   BT_Device_ECG(ServiceRecord service) throws IOException {
/* 112 */     super(service);
/* 113 */     init();
/*     */   }
/*     */ 
/*     */   public BT_Device_ECG()
/*     */   {
/* 121 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/* 129 */     this.STX = new byte[] { -88 };
/* 130 */     this.ETX = new byte[] { -89 };
/*     */   }
/*     */ 
/*     */   protected void parse_frame(byte[] reply)
/*     */   {
/* 146 */     if ((null == reply) || (0 == reply.length)) {
/* 147 */       return;
/*     */     }
/* 149 */     int len = reply.length;
/* 150 */     if (!is_frame_ends_with_ETX(reply))
/*     */     {
/* 153 */       if (this._part_frame_index + len < 10000)
/*     */       {
/* 155 */         System.arraycopy(reply, 0, this._part_frame, this._part_frame_index, len);
/* 156 */         this._part_frame_index += len;
/*     */       }
/*     */       else
/*     */       {
/* 160 */         Log.log(0, "ECG: Frame received too large without ETX, buffer too small (10000) or (huge) garbage ??, reject it.");
/* 161 */         byte[] arb = Utils.trim_frame(reply, this.STX[0]);
/* 162 */         if (null != arb)
/*     */         {
/* 164 */           len = arb.length;
/* 165 */           Log.log(0, "ECG: Store Frame from seen STX, length " + len);
/* 166 */           System.arraycopy(arb, 0, this._part_frame, 0, len);
/* 167 */           this._part_frame_index = len;
/*     */         }
/*     */         else {
/* 170 */           this._part_frame_index = 0;
/*     */         }
/*     */       }
/* 173 */       return;
/*     */     }
/*     */ 
/* 176 */     if (this._part_frame_index > 0)
/*     */     {
/* 178 */       byte[] ar = new byte[this._part_frame_index + reply.length];
/* 179 */       System.arraycopy(this._part_frame, 0, ar, 0, this._part_frame_index);
/* 180 */       System.arraycopy(reply, 0, ar, this._part_frame_index, reply.length);
/* 181 */       reply = ar;
/*     */     }
/*     */ 
/* 188 */     Boolean[] boolar = new Boolean[1];
/* 189 */     byte[] arb = Utils.trim_frame(reply, this.STX[0], boolar);
/*     */ 
/* 193 */     if ((null == arb) || (0 == arb.length))
/* 194 */       return;
/* 195 */     this.b_system_frame = boolar[0].booleanValue();
/* 196 */     byte[] ar = Utils.destuffa9(arb);
/* 197 */     Log.log(4, "ECG Frame-to-parse size after destuffing: " + ar.length);
/* 198 */     short calc_chksum = Utils.CRC16(ar, 1, ar.length - 4);
/*     */     try {
/* 200 */       short cks = Utils.extract_checksum(ar, this.ETX);
/* 201 */       if (cks != calc_chksum)
/*     */       {
/* 203 */         Log.log(0, "ECG: ERROR calculated chksum is: " + calc_chksum + " read: " + cks);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 207 */       Log.log(0, "ECG: ERROR cannot calculated chksum, too short frame ? " + ar.length);
/*     */     }
/*     */ 
/* 210 */     if (_bsimu) {
/* 211 */       parse_data_SIMULATOR(ar);
/*     */     }
/* 214 */     else if (this.b_system_frame)
/* 215 */       parse_system_frame(ar);
/*     */     else
/* 217 */       parse_data_frame(ar);
/*     */   }
/*     */ 
/*     */   private void parse_data_SIMULATOR(byte[] value)
/*     */   {
/*     */     try
/*     */     {
/* 280 */       SensLinkFrame m_frame = get_frame_header("ECG", "1.1");
/*     */ 
/* 283 */       short v = (short)(int)Utils.extract_long(value, 1, 2);
/*     */ 
/* 286 */       m_frame.add_key_value("data_0", String.valueOf(v));
/* 287 */       reply_command(m_frame.serialise(), false);
/* 288 */       add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 289 */       set_last_value(m_frame.get_data_type(), m_frame);
/* 290 */       Log.log(4, "Parsing reply " + v);
/*     */     }
/*     */     catch (Exception e) {
/* 293 */       Log.log(1, "Error parsing reply " + e);
/* 294 */       reply_command("error parsing reply " + e, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parse_data_frame(byte[] value)
/*     */   {
/*     */     try
/*     */     {
/* 303 */       SensLinkFrame m_frame = get_frame_header("ECG", "1.1");
/*     */ 
/* 305 */       String id_ecg = String.valueOf((char)value[1]);
/* 306 */       m_frame.add_key_value("ECG_ID", id_ecg);
/*     */ 
/* 309 */       String packet_number = String.valueOf(Utils.extract_unsigned_long(value, 2, 3));
/* 310 */       m_frame.add_key_value("packet_number", packet_number);
/*     */ 
/* 312 */       String pulse = String.valueOf(Utils.byte_to_unsign_int(value[5]));
/* 313 */       m_frame.add_key_value("pulse", pulse);
/*     */ 
/* 316 */       int itmp = Utils.extract_bits(value[6], (byte)-64);
/* 317 */       m_frame.add_key_value("bat", String.valueOf(itmp));
/*     */ 
/* 319 */       itmp = Utils.extract_bits(value[6], (byte)32);
/* 320 */       m_frame.add_key_value("contact_v6", String.valueOf(itmp));
/*     */ 
/* 322 */       itmp = Utils.extract_bits(value[6], (byte)16);
/* 323 */       m_frame.add_key_value("contact_v5", String.valueOf(itmp));
/*     */ 
/* 325 */       itmp = Utils.extract_bits(value[6], (byte)8);
/* 326 */       m_frame.add_key_value("contact_v4", String.valueOf(itmp));
/*     */ 
/* 328 */       itmp = Utils.extract_bits(value[6], (byte)4);
/* 329 */       m_frame.add_key_value("contact_v3", String.valueOf(itmp));
/*     */ 
/* 331 */       itmp = Utils.extract_bits(value[6], (byte)2);
/* 332 */       m_frame.add_key_value("contact_v2", String.valueOf(itmp));
/*     */ 
/* 334 */       itmp = Utils.extract_bits(value[6], (byte)1);
/* 335 */       m_frame.add_key_value("contact_v1", String.valueOf(itmp));
/*     */ 
/* 338 */       itmp = Utils.extract_bits(value[7], (byte)-128);
/* 339 */       m_frame.add_key_value("contact_l", String.valueOf(itmp));
/*     */ 
/* 341 */       itmp = Utils.extract_bits(value[7], (byte)64);
/* 342 */       m_frame.add_key_value("contact_r", String.valueOf(itmp));
/*     */ 
/* 344 */       itmp = Utils.extract_bits(value[7], (byte)32);
/* 345 */       m_frame.add_key_value("contact_f", String.valueOf(itmp));
/*     */ 
/* 347 */       itmp = Utils.extract_bits(value[7], (byte)16);
/* 348 */       m_frame.add_key_value("contact_n", String.valueOf(itmp));
/*     */ 
/* 350 */       itmp = Utils.extract_bits(value[7], (byte)12);
/* 351 */       m_frame.add_key_value("diff_mode", String.valueOf(itmp));
/*     */ 
/* 356 */       itmp = Utils.extract_bits(value[7], (byte)2);
/* 357 */       m_frame.add_key_value("bimodal", String.valueOf(itmp));
/*     */ 
/* 362 */       itmp = Utils.extract_bits(value[7], (byte)1);
/* 363 */       m_frame.add_key_value("packet_type", String.valueOf(itmp));
/* 364 */       int packet_type = itmp;
/*     */ 
/* 370 */       itmp = Utils.extract_bits(value[8], (byte)1);
/* 371 */       m_frame.add_key_value("Err_ADC_0", String.valueOf(itmp));
/*     */ 
/* 373 */       itmp = Utils.extract_bits(value[8], (byte)2);
/* 374 */       m_frame.add_key_value("Err_ADC_1", String.valueOf(itmp));
/*     */ 
/* 376 */       itmp = Utils.extract_bits(value[8], (byte)4);
/* 377 */       m_frame.add_key_value("Err_PLD_0", String.valueOf(itmp));
/*     */ 
/* 379 */       itmp = Utils.extract_bits(value[8], (byte)8);
/* 380 */       m_frame.add_key_value("Err_PLD_1", String.valueOf(itmp));
/*     */ 
/* 388 */       for (int i = 0; i < 12; i++)
/*     */       {
/* 390 */         String tmp = String.valueOf(Utils.extract_short(value, 8 + i * 2, 2));
/* 391 */         m_frame.add_key_value("v_" + (i < 10 ? "0" : "") + i, tmp);
/*     */       }
/*     */ 
/* 394 */       String nb = String.valueOf(Utils.extract_unsigned_long(value, 2, 2));
/* 395 */       m_frame.add_key_value("packet_nb", nb);
/*     */ 
/* 424 */       reply_command(m_frame.serialise(), false);
/* 425 */       add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 426 */       set_last_value(m_frame.get_data_type(), m_frame);
/*     */     }
/*     */     catch (Exception e) {
/* 429 */       Log.log(1, "ECG: Error parsing reply " + e);
/* 430 */       reply_command("error parsing reply " + e, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   void parse_system_frame(byte[] value)
/*     */   {
/*     */     try
/*     */     {
/* 443 */       SensLinkFrame m_frame = get_frame_header("ECG_INFO", "1.1");
/* 444 */       reply_command(m_frame.serialise(), false);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 448 */       Log.log(1, "ECG: Error parsing reply " + e);
/* 449 */       reply_command("error parsing reply " + e, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   static short get_payload_value(byte msb, byte lsb) {
/* 454 */     boolean m_pos = (msb & 0x80) == 0;
/* 455 */     msb = (byte)(msb & 0x7E);
/* 456 */     msb = (byte)(msb >> 1);
/* 457 */     int v = msb;
/* 458 */     v |= (m_pos ? 0 : 128);
/* 459 */     v <<= 8;
/* 460 */     v |= Utils.byte_to_unsign_int(lsb);
/* 461 */     return (short)v;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  99 */     set_command("start", new Command(0, "start", cmd_start, 0, true, true));
/*     */ 
/* 102 */     set_command("stop", new Command(0, "stop", cmd_stop, 0, true, false, true));
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_ECG
 * JD-Core Version:    0.6.0
 */