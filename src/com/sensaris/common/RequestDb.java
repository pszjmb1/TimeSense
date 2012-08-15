/*    */ package com.sensaris.common;
/*    */ 
/*    */ import com.sensaris.senslink.SensLinkFrame;
/*    */ import com.sensaris.senslink.device.BT_Manager;
/*    */ import com.sensaris.senslink.device.Device;
/*    */ 
/*    */ public class RequestDb extends Request
/*    */ {
/*    */   RequestDb(Caller_Closable caller, BT_Manager dmgr, Reply_Handler reply_handler, String req)
/*    */   {
/* 19 */     super(caller, dmgr, reply_handler, req);
/*    */   }
/*    */ 
/*    */   public void data_received_cb(Device device, String type, SensLinkFrame value, boolean is_open)
/*    */   {
/* 33 */     Log.msg(4, "Request.data_received_cb " + value + (is_open ? " \nopen" : " \nclosed"));
/*    */ 
/* 36 */     if (!is_open)
/*    */     {
/* 40 */       state("O");
/* 41 */       reply(null);
/* 42 */       state("C");
/* 43 */       this._b_close = true;
/*    */     } else {
/* 45 */       state("O");
/*    */       try
/*    */       {
/* 48 */         DataSender.get_instance().post_msg(value.serialise().getBytes()); } catch (Exception e) {
/* 49 */         Log.log(1, "RQd " + e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.RequestDb
 * JD-Core Version:    0.6.0
 */