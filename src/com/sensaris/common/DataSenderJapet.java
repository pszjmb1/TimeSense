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
/*     */ public class DataSenderJapet extends Thread
/*     */ {
/*  62 */   public static String SDB_Writer_JAPET_address = "sensdots.com";
/*     */   public static Buffer send_buffer;
/*  69 */   static int default_capacity = 100;
/*  70 */   int capacity = 100;
/*     */   public boolean running_send;
/*  76 */   boolean debug = false;
/*     */   private String _last_error;
/*  81 */   public static DataSenderJapet myself = null;
/*     */ 
/*     */   public static DataSenderJapet instance()
/*     */   {
/*  88 */     if (null == myself)
/*  89 */       myself = new DataSenderJapet(default_capacity);
/*  90 */     return myself;
/*     */   }
/*     */ 
/*     */   public void post_msg(String msg)
/*     */   {
/* 102 */     if ((null != msg) && (msg.length() > 0))
/* 103 */       post_msg(msg.getBytes());
/*     */   }
/*     */ 
/*     */   public void post_msg(byte[] msg)
/*     */   {
/* 112 */     Log.log(4, "post msg " + new String(msg));
/* 113 */     send_buffer.put(msg);
/* 114 */     if (!this.running_send)
/*     */     {
/* 116 */       start();
/*     */       try {
/* 118 */         currentThread(); Thread.sleep(20L);
/*     */       } catch (Exception e) {
/* 120 */         Log.log(1, "DS " + e);
/*     */       }
/*     */ 
/* 123 */       if (!this.running_send)
/* 124 */         return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private DataSenderJapet(int buff_size)
/*     */   {
/* 131 */     this.capacity = buff_size;
/*     */ 
/* 133 */     send_buffer = new Buffer(this.capacity);
/* 134 */     myself = this;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 142 */     this.running_send = true;
/*     */ 
/* 146 */     byte[] tmp = null;
/*     */     try
/*     */     {
/* 150 */       while (this.running_send)
/*     */         try
/*     */         {
/* 153 */           tmp = (byte[])(byte[])send_buffer.get();
/* 154 */           getPage(tmp.toString(), false);
/* 155 */           Log.log(4, "Msg sent to DbWriter \t" + tmp);
/* 156 */           tmp = null;
/*     */         }
/*     */         catch (Exception e) {
/* 159 */           e.printStackTrace();
/* 160 */           this._last_error = (this._last_error + "\nIOException " + e + " ");
/*     */         }
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 165 */       e1.printStackTrace();
/* 166 */       this._last_error = (this._last_error + "\nException " + e1 + " ");
/* 167 */       Log.log(0, "Exception " + e1);
/*     */     }
/* 169 */     this.running_send = false;
/*     */   }
/*     */ 
/*     */   public String last_error()
/*     */   {
/* 180 */     String st = this._last_error;
/* 181 */     this._last_error = null;
/* 182 */     return st;
/*     */   }
/*     */ 
/*     */   public static String getPage(String url, boolean b_read_response)
/*     */   {
/*     */     try
/*     */     {
/* 265 */       URL my_url = new URL(url);
/*     */ 
/* 267 */       HttpURLConnection cnx = (HttpURLConnection)my_url.openConnection();
/* 268 */       cnx.connect();
/*     */ 
/* 270 */       InputStreamReader isr = new InputStreamReader(cnx.getInputStream());
/* 271 */       BufferedReader rd = new BufferedReader(isr);
/*     */ 
/* 274 */       StringBuilder buffer = new StringBuilder();
/*     */       String line;
/* 275 */       while ((line = rd.readLine()) != null) {
/* 276 */         buffer.append(line + "\n");
/*     */       }
/* 278 */       isr.close();
/* 279 */       cnx.disconnect();
/* 280 */       if ((b_read_response) && 
/* 281 */         (buffer.length() > 0)) {
/* 282 */         return buffer.toString().trim();
/*     */       }
/* 284 */       return "OK";
/*     */     } catch (MalformedURLException ex) {
/*     */     } catch (IOException ex) {
/*     */     }
/* 288 */     return "ER";
/*     */   }
/*     */ 
/*     */   public static String read_id_sensor(String bt_address, String device_name)
/*     */   {
/*     */     try
/*     */     {
/* 301 */       String url = "http://" + SDB_Writer_JAPET_address + "/android/get.php?f=id_sensor&p=" + bt_address;
/*     */ 
/* 303 */       return getPage(url, true); } catch (Exception e) {
/*     */     }
/* 305 */     return null;
/*     */   }
/*     */ 
/*     */   public static void save_data(String value)
/*     */   {
/*     */     try
/*     */     {
/* 316 */       String url = "http://" + SDB_Writer_JAPET_address + "/android/get.php?f=save_data&p=" + URLEncoder.encode(value, "UTF8");
/* 317 */       getPage(url, false);
/*     */     }
/*     */     catch (UnsupportedEncodingException ex) {
/* 320 */       Log.log(0, "Exception " + ex);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.DataSenderJapet
 * JD-Core Version:    0.6.0
 */