/*     */ package com.sensaris.common;
/*     */ 
/*     */ import com.sensaris.senslink.device.Main;
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ 
/*     */ public class DataSender extends Thread
/*     */ {
/*  33 */   public static String SDB_Writer_address = "sensaris.com";
/*     */ 
/*  38 */   public static int SDB_Writer_port = 33334;
/*     */   public static DatagramSocket socket;
/*     */   public static Buffer send_buffer;
/*  52 */   static int default_capacity = 100;
/*  53 */   int capacity = 100;
/*     */   public boolean running_send;
/*  60 */   boolean debug = false;
/*     */   private String _last_error;
/*  66 */   public static DataSender myself = null;
/*     */ 
/*     */   public static DataSender get_instance()
/*     */   {
/*  74 */     if (null == myself)
/*     */     {
/*  76 */       myself = new DataSender(default_capacity);
/*     */     }
/*  78 */     return myself;
/*     */   }
/*     */ 
/*     */   public void post_msg(String msg)
/*     */   {
/*  91 */     if ((null != msg) && (msg.length() > 0))
/*  92 */       post_msg(msg.getBytes());
/*     */   }
/*     */ 
/*     */   public void post_msg(byte[] msg)
/*     */   {
/* 100 */     Log.log(4, "post msg " + new String(msg));
/* 101 */     send_buffer.put(msg);
/* 102 */     if (!this.running_send)
/*     */     {
/* 104 */       start();
/*     */       try { currentThread(); Thread.sleep(20L); } catch (Exception e) {
/* 106 */         Log.log(1, "DS " + e);
/*     */       }
/*     */ 
/* 109 */       if (!this.running_send)
/* 110 */         return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private DataSender(int buff_size)
/*     */   {
/* 117 */     this.capacity = buff_size;
/*     */ 
/* 119 */     send_buffer = new Buffer(this.capacity);
/* 120 */     myself = this;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 129 */       Main.SDB_Writer_open_port();
/*     */     }
/*     */     catch (Exception e) {
/* 132 */       Log.log(0, "Unable to open socket " + e);
/* 133 */       this._last_error = ("\nUnable to open socket " + e);
/* 134 */       return;
/*     */     }
/*     */ 
/* 137 */     this.running_send = true;
/*     */ 
/* 142 */     byte[] tmp = null;
/*     */     try
/*     */     {
/* 145 */       InetAddress address = InetAddress.getByName(SDB_Writer_address);
/*     */ 
/* 148 */       while (this.running_send)
/*     */       {
/*     */         try
/*     */         {
/* 152 */           tmp = null;
/*     */ 
/* 154 */           tmp = (byte[])(byte[])send_buffer.get();
/*     */ 
/* 157 */           DatagramPacket dgram = new DatagramPacket(tmp, tmp.length, address, SDB_Writer_port);
/*     */ 
/* 159 */           socket.send(dgram);
/*     */ 
/* 161 */           Log.log(4, "Msg sent to DbWriter \t" + tmp);
/* 162 */           tmp = null;
/*     */         }
/*     */         catch (IOException e) {
/* 165 */           e.printStackTrace();
/* 166 */           this._last_error = (this._last_error + "\nIOException " + e + " ");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (UnknownHostException e1) {
/* 171 */       e1.printStackTrace();
/* 172 */       this._last_error = (this._last_error + "\nUnknownHostException " + e1 + " ");
/* 173 */       Log.log(0, "UnknownHostException " + e1);
/*     */     }
/*     */     catch (Exception e2)
/*     */     {
/* 177 */       e2.printStackTrace();
/* 178 */       this._last_error = (this._last_error + "\nException " + e2 + " ");
/* 179 */       Log.log(0, "Exception " + e2);
/*     */     }
/* 181 */     this.running_send = false;
/* 182 */     if (null != tmp)
/*     */     {
/* 184 */       Log.log(4, "Re-post msg .. ");
/* 185 */       post_msg(tmp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String last_error()
/*     */   {
/* 197 */     String st = this._last_error;
/* 198 */     this._last_error = null;
/* 199 */     return st;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.DataSender
 * JD-Core Version:    0.6.0
 */