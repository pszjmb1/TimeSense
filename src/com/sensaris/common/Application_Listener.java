/*    */ package com.sensaris.common;
/*    */ 
/*    */ import java.io.InputStreamReader;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class Application_Listener extends Thread
/*    */ {
/*    */   private int _first_listen_port;
/*    */   private int _range_listen_port;
/*    */   private boolean _b_to_stop;
/*    */ 
/*    */   Application_Listener(int first_listen_port, int range_listen_port)
/*    */   {
/* 31 */     this._first_listen_port = first_listen_port;
/* 32 */     this._range_listen_port = range_listen_port;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 44 */     this._b_to_stop = false;
/* 45 */     InputStreamReader in = null;
/* 46 */     ServerSocket so = null;
/* 47 */     Vector ah_list = new Vector();
/* 48 */     while (!this._b_to_stop)
/*    */     {
/* 51 */       if (null == so) {
/* 52 */         for (int i = 0; i < this._range_listen_port; i++)
/*    */         {
/*    */           try
/*    */           {
/* 56 */             so = new ServerSocket(this._first_listen_port + i);
/*    */           }
/*    */           catch (Exception e)
/*    */           {
/* 60 */             Log.log(0, "App listener " + e);
/*    */             try
/*    */             {
/* 66 */               Socket socket = so.accept();
/* 67 */               Application_handler ah = new Application_handler(socket);
/* 68 */               ah.start();
/* 69 */               ah_list.add(ah);
/*    */             }
/*    */             catch (Exception ex) {
/* 72 */               Log.log(0, "App listener " + ex);
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/* 78 */     for (int i = 0; i < ah_list.size(); i++)
/*    */     {
/* 80 */       Application_handler ah = (Application_handler)ah_list.elementAt(i);
/* 81 */       ah.disconnect();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 92 */     this._b_to_stop = true;
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Application_Listener
 * JD-Core Version:    0.6.0
 */