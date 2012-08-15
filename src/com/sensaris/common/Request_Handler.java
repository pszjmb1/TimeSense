/*     */ package com.sensaris.common;
/*     */ 
/*     */ import com.sensaris.senslink.device.BT_Manager;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.Socket;
/*     */ 
/*     */ class Request_Handler extends Thread
/*     */   implements Caller_Closable
/*     */ {
/*     */   private BT_Manager _dev_manager;
/*     */   private Socket _socket;
/*     */   private BufferedWriter _bout;
/*     */   private boolean _b_to_close;
/*     */   Reply_Handler _rep_h;
/*     */   private Caller_Closable _caller;
/*     */ 
/*     */   Request_Handler(Caller_Closable caller, Socket so)
/*     */   {
/*  59 */     this._socket = so;
/*  60 */     this._caller = caller;
/*  61 */     this._dev_manager = BT_Manager.get_bt_manager_ref();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  68 */     BufferedReader buf = null;
/*  69 */     this._bout = null;
/*     */     try
/*     */     {
/*  73 */       buf = new BufferedReader(new InputStreamReader(this._socket.getInputStream()));
/*  74 */       this._bout = new BufferedWriter(new OutputStreamWriter(this._socket.getOutputStream()));
/*     */ 
/*  76 */       this._rep_h = new Reply_Handler(this._bout, this);
/*     */ 
/*  78 */       this._b_to_close = false;
/*  79 */       while (!this._b_to_close)
/*     */       {
/*  81 */         String msg = buf.readLine();
/*     */ 
/*  87 */         if ((null != msg) && (msg.startsWith("db")))
/*     */         {
/*  89 */           String req = msg.replaceFirst("db", "mon");
/*  90 */           new RequestDb(this, this._dev_manager, this._rep_h, req);
/*  91 */           continue;
/*  92 */         }if ((null != msg) && (msg.startsWith("drss")))
/*     */         {
/*  94 */           String req = msg.replaceFirst("drss", "mon");
/*  95 */           new RequestRSS(this, this._dev_manager, this._rep_h, req);
/*  96 */           continue;
/*  97 */         }if ((null != msg) && (msg.startsWith("dmh")))
/*     */         {
/*  99 */           String req = msg.replaceFirst("dmh", "mon");
/* 100 */           new RequestMhealth(this, this._dev_manager, this._rep_h, req);
/* 101 */           continue;
/* 102 */         }if ((null != msg) && (msg.startsWith("dj")))
/*     */         {
/* 104 */           String req = msg.replaceFirst("dj", "mon");
/* 105 */           new RequestJapet(this, this._dev_manager, this._rep_h, req);
/* 106 */           continue;
/*     */         }
/* 108 */         new Request(this, this._dev_manager, this._rep_h, msg);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 119 */       Log.log(1, "request handler " + e);
/* 120 */       e.printStackTrace(System.out);
/*     */     }
/*     */ 
/* 123 */     Request.close_all(this, true);
/* 124 */     this._rep_h.close();
/*     */     try {
/* 126 */       if (null != buf)
/* 127 */         buf.close();
/* 128 */       buf = null; } catch (Exception e) {
/*     */     }
/*     */     try {
/* 131 */       if (null != this._bout)
/* 132 */         this._bout.close();
/* 133 */       this._bout = null; } catch (Exception e) {
/*     */     }
/* 135 */     if (null != this._caller)
/* 136 */       this._caller.close();
/* 137 */     Log.log(1, "RH: exited");
/*     */   }
/*     */ 
/*     */   public void disconnect()
/*     */   {
/* 142 */     this._b_to_close = true;
/*     */   }
/*     */ 
/*     */   public synchronized void reply(String reply)
/*     */   {
/*     */     try
/*     */     {
/* 154 */       this._rep_h.reply(reply);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 159 */       Log.log(0, "RH " + e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 166 */     this._b_to_close = true;
/*     */   }
/*     */ 
/*     */   public void close(boolean b_propagate) {
/* 170 */     this._b_to_close = true;
/* 171 */     this._caller.close(b_propagate);
/*     */   }
/*     */ 
/*     */   public boolean is_closed() {
/* 175 */     return this._b_to_close;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Request_Handler
 * JD-Core Version:    0.6.0
 */