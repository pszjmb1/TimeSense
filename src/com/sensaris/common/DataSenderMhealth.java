/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ 
/*     */ public class DataSenderMhealth extends Thread
/*     */ {
/*  50 */   public static String SDB_Writer_MHealth_address = "senspod.com";
/*     */   public static Buffer send_buffer;
/*  59 */   static int default_capacity = 100;
/*  60 */   int capacity = 100;
/*     */   public boolean running_send;
/*  66 */   boolean debug = false;
/*     */   private String _last_error;
/*  71 */   public static DataSenderMhealth myself = null;
/*     */ 
/*     */   public static DataSenderMhealth instance()
/*     */   {
/*  78 */     if (null == myself)
/*  79 */       myself = new DataSenderMhealth(default_capacity);
/*  80 */     return myself;
/*     */   }
/*     */ 
/*     */   public void post_msg(String msg)
/*     */   {
/*  92 */     if ((null != msg) && (msg.length() > 0))
/*  93 */       post_msg(msg.getBytes());
/*     */   }
/*     */ 
/*     */   public void post_msg(byte[] msg)
/*     */   {
/* 102 */     Log.log(4, "post msg " + new String(msg));
/* 103 */     send_buffer.put(msg);
/* 104 */     if (!this.running_send)
/*     */     {
/* 106 */       start();
/*     */       try {
/* 108 */         currentThread(); Thread.sleep(20L);
/*     */       } catch (Exception e) {
/* 110 */         Log.log(1, "DS " + e);
/*     */       }
/*     */ 
/* 113 */       if (!this.running_send)
/* 114 */         return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private DataSenderMhealth(int buff_size)
/*     */   {
/* 122 */     this.capacity = buff_size;
/*     */ 
/* 124 */     send_buffer = new Buffer(this.capacity);
/* 125 */     myself = this;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 196 */     this.running_send = true;
/*     */ 
/* 200 */     byte[] tmp = null;
/*     */     try
/*     */     {
/* 204 */       while (this.running_send)
/*     */         try
/*     */         {
/* 207 */           tmp = (byte[])(byte[])send_buffer.get();
/* 208 */           getPage(tmp.toString(), false);
/* 209 */           Log.log(4, "Msg sent to DbWriter \t" + tmp);
/* 210 */           tmp = null;
/*     */         }
/*     */         catch (Exception e) {
/* 213 */           e.printStackTrace();
/* 214 */           this._last_error = (this._last_error + "\nIOException " + e + " ");
/*     */         }
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 219 */       e1.printStackTrace();
/* 220 */       this._last_error = (this._last_error + "\nException " + e1 + " ");
/* 221 */       Log.log(0, "Exception " + e1);
/*     */     }
/* 223 */     this.running_send = false;
/*     */   }
/*     */ 
/*     */   public String last_error()
/*     */   {
/* 234 */     String st = this._last_error;
/* 235 */     this._last_error = null;
/* 236 */     return st;
/*     */   }
/*     */ 
/*     */   public static String getPage(String url, boolean b_read_response)
/*     */   {
/*     */     try
/*     */     {
/* 319 */       URL my_url = new URL(url);
/*     */ 
/* 321 */       HttpURLConnection cnx = (HttpURLConnection)my_url.openConnection();
/* 322 */       cnx.connect();
/*     */ 
/* 324 */       InputStreamReader isr = new InputStreamReader(cnx.getInputStream());
/* 325 */       BufferedReader rd = new BufferedReader(isr);
/*     */ 
/* 328 */       StringBuilder buffer = new StringBuilder();
/*     */       String line;
/* 329 */       while ((line = rd.readLine()) != null) {
/* 330 */         buffer.append(line + "\n");
/*     */       }
/* 332 */       isr.close();
/* 333 */       cnx.disconnect();
/* 334 */       if ((b_read_response) && 
/* 335 */         (buffer.length() > 0)) {
/* 336 */         return buffer.toString().trim();
/*     */       }
/* 338 */       return "OK";
/*     */     } catch (MalformedURLException ex) {
/*     */     } catch (IOException ex) {
/*     */     }
/* 342 */     return "ER";
/*     */   }
/*     */ 
/*     */   public static String read_id_sensor(String bt_address, String device_name)
/*     */   {
/*     */     try
/*     */     {
/* 355 */       String url = "http://" + SDB_Writer_MHealth_address + "/android/get.php?f=id_sensor&p=" + bt_address;
/*     */ 
/* 357 */       return getPage(url, true); } catch (Exception e) {
/*     */     }
/* 359 */     return null;
/*     */   }
/*     */ 
/*     */   public static void save_data(String value)
/*     */   {
/*     */     try
/*     */     {
/* 370 */       String url = "http://" + SDB_Writer_MHealth_address + "/android/get.php?f=save_data&p=" + URLEncoder.encode(value, "UTF8");
/* 371 */       getPage(url, false);
/*     */     }
/*     */     catch (UnsupportedEncodingException ex) {
/* 374 */       Log.log(0, "Exception " + ex);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.DataSenderMhealth
 * JD-Core Version:    0.6.0
 */