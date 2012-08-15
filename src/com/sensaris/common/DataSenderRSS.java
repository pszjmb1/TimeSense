/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class DataSenderRSS extends Thread
/*     */ {
/*  35 */   public static String SDB_Writer_address = "localhost";
/*     */ 
/*  40 */   public static int SDB_Writer_port = 1337;
/*     */   SocketClient socket;
/*     */   public static Buffer send_buffer;
/*  55 */   static int default_capacity = 100;
/*  56 */   int capacity = 100;
/*     */   public boolean running_send;
/*  63 */   boolean debug = false;
/*     */   private String _last_error;
/*  66 */   int delay = 100000;
/*     */ 
/*  70 */   public static DataSenderRSS myself = null;
/*     */ 
/*     */   public static DataSenderRSS get_instance()
/*     */   {
/*  78 */     if (null == myself)
/*     */     {
/*  80 */       myself = new DataSenderRSS(default_capacity);
/*     */     }
/*  82 */     return myself;
/*     */   }
/*     */ 
/*     */   public void post_msg(String msg)
/*     */   {
/*  95 */     if ((null != msg) && (msg.length() > 0))
/*  96 */       post_msg(msg.getBytes());
/*     */   }
/*     */ 
/*     */   public void post_msg(byte[] msg)
/*     */   {
/* 104 */     Log.log(4, "post msg " + new String(msg));
/* 105 */     send_buffer.put(msg);
/* 106 */     if (!this.running_send)
/*     */     {
/* 108 */       start();
/*     */       try { currentThread(); Thread.sleep(20L); } catch (Exception e) {
/* 110 */         Log.log(1, "DS " + e);
/*     */       }
/*     */ 
/* 113 */       if (!this.running_send)
/* 114 */         return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private DataSenderRSS(int buff_size)
/*     */   {
/* 120 */     this.capacity = buff_size;
/*     */ 
/* 122 */     send_buffer = new Buffer(this.capacity);
/* 123 */     myself = this;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 133 */       Log.log(1, "Open socket on server " + SDB_Writer_address + ":" + SDB_Writer_port);
/* 134 */       this.socket = new SocketClient();
/* 135 */       this.socket.open(SDB_Writer_address, SDB_Writer_port, this.delay);
/*     */     }
/*     */     catch (Exception e) {
/* 138 */       Log.log(0, "Unable to open socket " + e);
/* 139 */       this._last_error = ("\nUnable to open socket " + e);
/* 140 */       return;
/*     */     }
/*     */ 
/* 143 */     this.running_send = true;
/*     */ 
/* 145 */     String tmp = null;
/*     */ 
/* 148 */     while (this.running_send) {
/*     */       try
/*     */       {
/* 151 */         byte[] ar = (byte[])(byte[])send_buffer.get();
/* 152 */         tmp = Utils.byte_to_string(ar);
/*     */ 
/* 154 */         this.socket.send(tmp);
/*     */ 
/* 156 */         Log.log(4, "Msg sent \t" + tmp);
/*     */       }
/*     */       catch (IOException e) {
/* 159 */         e.printStackTrace();
/* 160 */         this._last_error = (this._last_error + "\nIOException " + e + " ");
/*     */       }
/*     */     }
/* 163 */     this.running_send = false;
/* 164 */     if (null != tmp)
/*     */     {
/* 166 */       Log.log(4, "Re-post msg .. ");
/* 167 */       post_msg(tmp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String last_error()
/*     */   {
/* 178 */     String st = this._last_error;
/* 179 */     this._last_error = null;
/* 180 */     return st;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.DataSenderRSS
 * JD-Core Version:    0.6.0
 */