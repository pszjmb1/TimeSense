/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.common.Utils;
/*     */ import com.sensaris.senslink.SensLinkFrame;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ 
/*     */ public class BT_Device_GLUCO extends BT_Device
/*     */ {
/*     */   public static final String TYPE_GLUCO = "Gluco";
/*     */   public static final String frame_version = "1.1";
/*     */   public static final String TYPE_TEMPERATURE = "Temperature";
/*     */   public static final String temperature_frame_version = "1.1";
/*  47 */   private static final byte[] cmd_start_gluco = { 38, 49, 10 };
/*  48 */   private static final byte[] cmd_start_thermo = { 38, 48, 10 };
/*  49 */   private static final byte[] cmd_raz_gluco = { 38, 50, 10 };
/*     */ 
/*  52 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/* 105 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/* 108 */     command_list.put(name, cmd);
/*     */   }
/*     */   BT_Device_GLUCO(ServiceRecord service) throws IOException {
/* 111 */     super(service);
/* 112 */     init();
/*     */   }
/*     */ 
/*     */   public BT_Device_GLUCO()
/*     */   {
/* 120 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/* 126 */     this.STX = "P ".getBytes();
/* 127 */     this.ETX = new byte[] { 19, 10 };
/*     */   }
/*     */ 
/*     */   protected void parse_frame(byte[] rep)
/*     */   {
/* 142 */     if ((null == rep) || (rep.length < 3))
/* 143 */       return;
/* 144 */     String reply = new String(rep);
/* 145 */     boolean b_valid_reply = true;
/*     */ 
/* 147 */     Log.log(4, "\nGluco: now parsing: '" + reply.toString() + "'\n");
/* 148 */     StringTokenizer stk = new StringTokenizer(reply.toString(), ",");
/* 149 */     int item_nb = stk.countTokens();
/* 150 */     Log.log(4, "item nb: " + item_nb + "\n");
/* 151 */     if (item_nb > 0)
/*     */     {
/* 153 */       String start = stk.nextToken();
/* 154 */       Log.log(4, "start: '" + start + "'\n");
/* 155 */       if (start.startsWith("P"))
/*     */       {
/* 157 */         parse_gluco(stk, start);
/* 158 */       } else if (start.startsWith("$PSEN"))
/*     */       {
/* 160 */         parse_thermo(stk, start);
/*     */       }
/*     */       else
/*     */       {
/* 164 */         Log.log(4, "\nError Reply is not starting with P nor $PSEN (unsupported model ?)\n");
/* 165 */         reply_command("error cannot parse", true);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 170 */       Log.log(4, "\nError NO comma separated strings found - change battery ?\n");
/* 171 */       reply_command("error cannot parse", true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parse_thermo(StringTokenizer stk, String start)
/*     */   {
/*     */     try
/*     */     {
/* 186 */       String type = stk.nextToken().trim();
/* 187 */       String key = stk.nextToken().trim();
/* 188 */       String value = stk.nextToken().trim();
/* 189 */       int index = value.indexOf(' ');
/* 190 */       if (index != -1)
/*     */       {
/* 192 */         value = value.substring(0, index).trim();
/*     */       }
/*     */ 
/* 195 */       SensLinkFrame m_frame = get_frame_header(type, "1.1");
/* 196 */       m_frame.add_epoch_measure(Utils.iso_date(System.currentTimeMillis()));
/* 197 */       m_frame.add_key_value(key, value);
/* 198 */       m_frame.add_key_value("unit", "°C");
/* 199 */       String parsed_reply = m_frame.serialise();
/*     */ 
/* 202 */       add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 203 */       set_last_value(m_frame.get_data_type(), m_frame);
/* 204 */       reply_command(parsed_reply, true);
/*     */     }
/*     */     catch (Exception e) {
/* 207 */       String str = "Error parsing temperature frame ";
/* 208 */       Log.log(1, str + e);
/* 209 */       reply_command(str, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parse_gluco(StringTokenizer stk, String start)
/*     */   {
/* 221 */     String tmp = start.substring(2).trim();
/*     */ 
/* 223 */     int measure_nb = 0;
/*     */     try {
/* 225 */       measure_nb = Integer.parseInt(tmp);
/*     */     }
/*     */     catch (Exception eparse)
/*     */     {
/* 229 */       return;
/*     */     }
/* 231 */     if (measure_nb > 0)
/*     */     {
/* 234 */       tmp = stk.nextToken();
/* 235 */       Log.log(4, "gluco_meter id: '" + tmp + "'\n");
/* 236 */       String gluco_meter = tmp.replace('"', ' ').trim();
/* 237 */       tmp = stk.nextToken();
/*     */ 
/* 239 */       Log.log(4, "unit ... : '" + tmp + "'\n");
/* 240 */       int index = tmp.indexOf("\"", 1);
/* 241 */       String unit = tmp.substring(1, index).replace('"', ' ').trim();
/* 242 */       Log.log(4, "unit parsed: '" + unit + "'\n");
/*     */ 
/* 264 */       for (int i = 0; i < measure_nb; i++) {
/*     */         try
/*     */         {
/* 267 */           SensLinkFrame m_frame = get_frame_header("Gluco", "1.1");
/*     */ 
/* 274 */           tmp = stk.nextToken();
/* 275 */           Log.log(4, "date : '" + tmp + "'\n");
/* 276 */           String date = tmp.replace('"', ' ').trim();
/*     */ 
/* 280 */           tmp = stk.nextToken();
/* 281 */           Log.log(4, "time : '" + tmp + "'\n");
/* 282 */           String time = tmp.replace('"', ' ').trim();
/* 283 */           String epoch_measure = Utils.convert_date_time_usa_to_iso(date, time);
/*     */ 
/* 286 */           tmp = stk.nextToken();
/*     */ 
/* 288 */           Log.log(4, "measure ... : '" + tmp + "'\n");
/*     */ 
/* 290 */           index = tmp.indexOf('"', 2);
/* 291 */           String measure = tmp.substring(1, index).trim();
/* 292 */           tmp = tmp.replace('"', ' ').trim();
/* 293 */           Log.log(4, "measure: '" + measure + "'\n");
/*     */ 
/* 297 */           tmp = stk.nextToken();
/* 298 */           if (tmp.startsWith("\""))
/*     */           {
/* 300 */             tmp = stk.nextToken();
/* 301 */             tmp = stk.nextToken();
/*     */           }
/* 303 */           Log.log(4, "checksum : '" + tmp + "'\n");
/*     */ 
/* 307 */           m_frame.add_key_value(unit, measure);
/*     */ 
/* 311 */           m_frame.add_epoch_measure(epoch_measure);
/*     */ 
/* 317 */           String parsed_reply = m_frame.serialise();
/*     */ 
/* 322 */           add_type(m_frame.get_data_type(), m_frame.retrieve_list_data_types());
/* 323 */           set_last_value(m_frame.get_data_type(), m_frame);
/* 324 */           reply_command(parsed_reply, measure_nb == i + 1);
/*     */         }
/*     */         catch (Exception e) {
/* 327 */           Log.log(4, "\nGluco: Parsing error: " + e);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 335 */       Log.log(4, "\n NO MEASURES\n Please proceed\n");
/* 336 */       reply_command("error no measure", true);
/*     */     }
/*     */   }
/*     */ 
/*     */   void write_command(Command cmd)
/*     */     throws IOException
/*     */   {
/* 348 */     Log.log(1, "****** write_command: ---'" + cmd._name + "'---");
/* 349 */     int max = 2;
/*     */ 
/* 351 */     boolean b_ultra2_compat = false;
/*     */     try
/*     */     {
/* 369 */       byte[] commande = cmd.get_full_cmd_byte();
/*     */ 
/* 381 */       for (int i = 0; i < commande.length; i++) {
/* 382 */         this.device_in.writeByte(commande[i]);
/* 383 */         this.device_in.flush();
/* 384 */         Thread.sleep(5L);
/*     */       }
/*     */ 
/* 403 */       Log.log(1, "Command " + cmd + " (value; " + Utils.display_hexa(commande) + ") sent to " + this.device_name);
/*     */     }
/*     */     catch (Exception e) {
/* 406 */       Log.log(1, "error: " + e + "\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  95 */     set_command("start_thermo", new Command(0, "start_thermo", cmd_start_thermo, 0, true, true));
/*  96 */     set_command("start_gluco", new Command(0, "start_gluco", cmd_start_gluco, 0, true, true));
/*  97 */     set_command("raz_gluco", new Command(0, "raz_gluco", cmd_raz_gluco, 0, true, false));
/*     */ 
/* 100 */     set_command("DMP", new Command(0, "DMP", cmd_start_gluco, 0, true, true));
/* 101 */     set_command("DMZ", new Command(0, "DMZ", cmd_raz_gluco, 0, true, false));
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device_GLUCO
 * JD-Core Version:    0.6.0
 */