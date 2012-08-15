/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Request_Listener extends Thread
/*     */   implements Caller_Closable
/*     */ {
/*     */   private int _first_listen_port;
/*     */   private int _range_listen_port;
/*     */   private boolean _b_to_stop;
/*     */   private Caller_Closable _caller;
/*     */ 
/*     */   public Request_Listener(Caller_Closable caller, int first_listen_port, int range_listen_port)
/*     */   {
/*  38 */     this._caller = caller;
/*  39 */     this._first_listen_port = first_listen_port;
/*  40 */     this._range_listen_port = range_listen_port;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  52 */     this._b_to_stop = false;
/*  53 */     InputStreamReader in = null;
/*  54 */     ServerSocket so = null;
/*  55 */     Vector ah_list = new Vector();
/*  56 */     while (!this._b_to_stop)
/*     */     {
/*  59 */       if (null == so)
/*  60 */         for (int i = 0; i < this._range_listen_port; i++)
/*     */         {
/*     */           try
/*     */           {
/*  64 */             so = new ServerSocket(this._first_listen_port + i);
/*     */           }
/*     */           catch (Exception e) {
/*  67 */             Log.log(0, "RL: " + e);
/*     */           }
/*     */         }
/*     */       try
/*     */       {
/*  72 */         Log.msg("listening port: " + so.getLocalPort());
/*  73 */         Socket socket = so.accept();
/*     */ 
/*  75 */         Request_Handler ah = new Request_Handler(this, socket);
/*  76 */         ah.start();
/*  77 */         ah_list.add(ah);
/*     */       }
/*     */       catch (Exception e) {
/*  80 */         Log.msg(1, "request listener " + e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  85 */     for (int i = 0; i < ah_list.size(); i++)
/*     */     {
/*  87 */       Request_Handler ah = (Request_Handler)ah_list.elementAt(i);
/*  88 */       ah.disconnect();
/*     */     }
/*  90 */     Log.msg(1, "request listener exited");
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  98 */     Log.msg(1, "request listener asked to close");
/*  99 */     this._b_to_stop = true;
/*     */   }
/*     */ 
/*     */   public void close(boolean b_propagate)
/*     */   {
/* 107 */     Log.msg(1, "request listener asked to close");
/* 108 */     this._b_to_stop = true;
/* 109 */     if (null != this._caller)
/* 110 */       this._caller.close(b_propagate);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Request_Listener
 * JD-Core Version:    0.6.0
 */