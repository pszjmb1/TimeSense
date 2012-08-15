/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Reply_Handler extends Thread
/*     */ {
/*     */   Buffer queue_reply;
/*     */   public boolean _b_close_request;
/*     */   private boolean _b_close;
/*     */   BufferedWriter _out;
/*     */   Caller_Closable _caller;
/*     */ 
/*     */   public Reply_Handler(BufferedWriter out, Caller_Closable caller)
/*     */   {
/*  30 */     this.queue_reply = new Buffer(100);
/*  31 */     this._out = out;
/*  32 */     this._caller = caller;
/*  33 */     this._b_close_request = false;
/*  34 */     this._b_close = false;
/*  35 */     start();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  43 */     while (!this._b_close)
/*     */     {
/*  47 */       String msg = (String)this.queue_reply.get();
/*     */       try {
/*  49 */         this._out.write(msg);
/*  50 */         this._out.newLine();
/*  51 */         this._out.flush();
/*     */       } catch (Exception e) {
/*  53 */         Log.log(1, "Reply_Handler: error writing reply " + e);
/*     */         try {
/*  55 */           if (null != this._out) {
/*  56 */             this._out.close();
/*  57 */             this._out = null;
/*     */           }
/*     */         } catch (Exception e2) {
/*  60 */           System.err.println("RH " + e);
/*     */         }
/*  62 */         this._out = null;
/*  63 */         this._b_close_request = true;
/*  64 */         this._b_close = true;
/*     */       }
/*     */     }
/*  67 */     if (null != this._caller)
/*  68 */       this._caller.close();
/*     */     try
/*     */     {
/*  71 */       if (null != this._out) {
/*  72 */         this._out.close();
/*  73 */         this._out = null;
/*     */       }
/*     */     } catch (Exception e2) {
/*  76 */       System.err.println("RH " + e2);
/*     */     }
/*  78 */     this._b_close_request = true;
/*  79 */     this._b_close = true;
/*  80 */     Log.log(1, "Reply_Handler thread exiting now");
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  92 */     this._b_close = true;
/*     */   }
/*     */ 
/*     */   public boolean is_closed()
/*     */   {
/*  97 */     return this._b_close;
/*     */   }
/*     */ 
/*     */   public void reply(String msg)
/*     */   {
/* 105 */     this.queue_reply.put(msg);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Reply_Handler
 * JD-Core Version:    0.6.0
 */