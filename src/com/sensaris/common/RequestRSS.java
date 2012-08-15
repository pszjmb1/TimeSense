/*    */ package com.sensaris.common;
/*    */ 
/*    */ import com.sensaris.senslink.SensLinkFrame;
/*    */ import com.sensaris.senslink.device.BT_Manager;
/*    */ import com.sensaris.senslink.device.Device;
/*    */ 
/*    */ public class RequestRSS extends Request
/*    */ {
/*    */   RequestRSS(Caller_Closable caller, BT_Manager dmgr, Reply_Handler reply_handler, String req)
/*    */   {
/* 20 */     super(caller, dmgr, reply_handler, req);
/*    */   }
/*    */ 
/*    */   public void data_received_cb(Device device, String type, SensLinkFrame value, boolean is_open)
/*    */   {
/* 34 */     Log.msg(4, "Request.data_received_cb " + value + (is_open ? " \nopen" : " \nclosed"));
/*    */ 
/* 37 */     if (!is_open)
/*    */     {
/* 41 */       state("O");
/* 42 */       reply(null);
/* 43 */       state("C");
/* 44 */       this._b_close = true;
/*    */     } else {
/* 46 */       state("O");
/*    */       try
/*    */       {
/* 49 */         DataSenderRSS.get_instance().post_msg(value.serialise_rss().getBytes()); } catch (Exception e) {
/* 50 */         Log.log(1, "RQrss " + e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.RequestRSS
 * JD-Core Version:    0.6.0
 */