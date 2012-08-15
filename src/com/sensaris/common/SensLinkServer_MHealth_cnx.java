/*     */ package com.sensaris.common;
/*     */ 
/*     */ import com.sensaris.senslink.device.BT_Manager;
/*     */ import com.sensaris.senslink.device.Main;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class SensLinkServer_MHealth_cnx
/*     */   implements Runnable, Caller_Closable
/*     */ {
/*  22 */   private static SensLinkServer_MHealth_cnx myself = null;
/*     */   String _server_adress;
/*     */   int _server_port;
/*     */   Publisher _publisher;
/*     */   Request_Handler _ah;
/*     */   BT_Manager _bt;
/*     */ 
/*     */   public static SensLinkServer_MHealth_cnx instance()
/*     */   {
/*  30 */     if ((null == myself) && (Main.b_SensLinkServer_mhealth)) {
/*  31 */       myself = new SensLinkServer_MHealth_cnx(Main.SensLinkServer_MHealth_address, Main.SensLinkServer_MHealth_port);
/*     */     }
/*     */ 
/*  34 */     return myself;
/*     */   }
/*     */ 
/*     */   private SensLinkServer_MHealth_cnx(String address, int port) {
/*  38 */     this._server_adress = address;
/*  39 */     this._server_port = port;
/*  40 */     this._ah = null;
/*  41 */     new Thread(this).start();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  46 */     synchronized (this) {
/*  47 */       Socket socket = null;
/*  48 */       boolean _connected = false;
/*  49 */       this._bt = BT_Manager.get_bt_manager_ref();
/*  50 */       while (Main.b_SensLinkServer_mhealth)
/*     */       {
/*     */         try {
/*  53 */           if ((null == this._ah) || (this._ah.is_closed())) {
/*  54 */             socket = new Socket(this._server_adress, this._server_port);
/*  55 */             this._ah = new Request_Handler(this, socket);
/*  56 */             this._ah.start();
/*  57 */             System.out.println("cnx accepted on " + this._server_adress + ":" + this._server_port);
/*  58 */             if (null != this._publisher)
/*  59 */               this._publisher.close();
/*  60 */             this._publisher = new Publisher(this._ah);
/*     */           }
/*     */         }
/*     */         catch (Exception e) {
/*  64 */           Log.msg(1, " " + e);
/*  65 */           socket = null;
/*     */         }
/*     */         try {
/*  68 */           Thread.sleep(20000L);
/*  69 */           this._bt.device_search();
/*     */         }
/*     */         catch (Exception e) {
/*  72 */           Log.msg(1, " " + e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  77 */     System.err.println(" slink_mhealth exited");
/*  78 */     this._publisher._b_publish = false;
/*     */   }
/*     */ 
/*     */   public void close() {
/*  82 */     if (null != this._ah) {
/*  83 */       this._ah.close();
/*  84 */       this._ah = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close(boolean b_propagate)
/*     */   {
/*  90 */     if (null != this._ah) {
/*  91 */       this._ah.close();
/*  92 */       this._ah = null;
/*     */     }
/*     */   }
/*     */   protected class Publisher implements Runnable {
/*     */     boolean _b_publish;
/*     */     Request_Handler _ah;
/* 105 */     int periode = 10000;
/*     */     static final String VISIBLE = " V";
/*     */     static final String CONNECTED = " C";
/*     */     static final String CMD_SET_SENSPOD_STATUS = "set_device_status";
/*     */ 
/* 108 */     Publisher(Request_Handler ah) { this._ah = ah;
/* 109 */       new Thread(this).start(); }
/*     */ 
/*     */     void close()
/*     */     {
/* 113 */       this._b_publish = false;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 120 */       this._b_publish = true;
/*     */ 
/* 123 */       while (this._b_publish) {
/* 124 */         write_out(SensLinkServer_MHealth_cnx.this._bt.discovered_list(), " V");
/* 125 */         write_out2(SensLinkServer_MHealth_cnx.this._bt.connected_list(), " C");
/*     */         try {
/* 127 */           Thread.sleep(this.periode);
/*     */         } catch (Exception e) {
/*     */         }
/*     */       }
/* 131 */       this._b_publish = false;
/*     */     }
/*     */ 
/*     */     private void write_out(ArrayList<String> ar, String str) {
/*     */       try {
/* 136 */         if (null != ar) {
/* 137 */           int len = ar.size();
/*     */ 
/* 139 */           for (int i = 0; i < len; i += 2) {
/* 140 */             StringBuilder sb = new StringBuilder();
/* 141 */             sb.append("set_device_status");
/* 142 */             sb.append(" ");
/* 143 */             sb.append((String)ar.get(i));
/* 144 */             sb.append(" ");
/* 145 */             sb.append((String)ar.get(i + 1));
/* 146 */             sb.append(" ");
/* 147 */             sb.append(str);
/* 148 */             this._ah.reply(sb.toString());
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/*     */       }
/*     */     }
/*     */ 
/*     */     private void write_out2(ArrayList<String> ar, String str) {
/*     */       try {
/* 156 */         if (null != ar) {
/* 157 */           int len = ar.size();
/* 158 */           for (int i = 0; i < len; i++) {
/* 159 */             StringBuilder sb = new StringBuilder();
/* 160 */             sb.append("set_device_status");
/* 161 */             sb.append(" ");
/* 162 */             sb.append((String)ar.get(i));
/* 163 */             sb.append(" ");
/* 164 */             sb.append(str);
/* 165 */             this._ah.reply(sb.toString());
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.SensLinkServer_MHealth_cnx
 * JD-Core Version:    0.6.0
 */