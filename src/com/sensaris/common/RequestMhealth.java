/*    */ package com.sensaris.common;
/*    */ 
/*    */ import com.sensaris.senslink.SensLinkFrame;
/*    */ import com.sensaris.senslink.device.BT_Manager;
/*    */ import com.sensaris.senslink.device.Device;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class RequestMhealth extends Request
/*    */ {
/*    */   HashMap<String, String> _map_sensor;
/*    */ 
/*    */   RequestMhealth(Caller_Closable caller, BT_Manager dmgr, Reply_Handler reply_handler, String req)
/*    */   {
/* 28 */     super(caller, dmgr, reply_handler, req);
/* 29 */     this._map_sensor = new HashMap();
/*    */   }
/*    */ 
/*    */   public void data_received_cb(Device device, String type, SensLinkFrame frame, boolean is_open)
/*    */   {
/* 45 */     Log.msg(4, "Request.data_received_cb " + frame + (is_open ? " \nopen" : " \nclosed"));
/*    */ 
/* 47 */     if (!is_open)
/*    */     {
/* 51 */       state("O");
/* 52 */       reply(null);
/* 53 */       state("C");
/* 54 */       this._b_close = true;
/*    */     }
/*    */     else
/*    */     {
/* 58 */       state("O");
/*    */       try
/*    */       {
/* 65 */         String device_bt_add = frame.get_device_bt_address();
/* 66 */         String id_sensor = (String)this._map_sensor.get(device_bt_add);
/* 67 */         if (null == id_sensor) {
/* 68 */           id_sensor = DataSenderMhealth.read_id_sensor(device_bt_add, frame.get_device_name());
/* 69 */           if ((null == id_sensor) || (0 == id_sensor.length()))
/*    */           {
/* 71 */             return;
/*    */           }
/*    */           try
/*    */           {
/* 75 */             Integer.parseInt(id_sensor);
/* 76 */             this._map_sensor.put(device_bt_add, id_sensor);
/*    */           }
/*    */           catch (Exception e) {
/* 79 */             return;
/*    */           }
/*    */         }
/*    */         try
/*    */         {
/* 84 */           DataSenderMhealth.instance();
/*    */ 
/* 86 */           String msg = frame.frame_formatting_mhealth(id_sensor);
/*    */ 
/* 88 */           String res = "ER";
/* 89 */           if (null != msg)
/*    */           {
/* 91 */             DataSenderMhealth.instance(); DataSenderMhealth.save_data(msg);
/*    */           }
/*    */           else {
/* 94 */             this._last_error = "error during parsing data";
/*    */ 
/* 96 */             Log.log(1, "RequestMhealth.cb " + this._last_error);
/*    */ 
/* 98 */             return;
/*    */           }
/*    */         }
/*    */         catch (Exception e1) {
/* 102 */           Log.log(1, "RequestMhealth.cb " + e1);
/* 103 */           e1.printStackTrace();
/*    */         }
/* 105 */         return; } catch (Exception e) {
/* 106 */         Log.log(1, "RQd " + e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.RequestMhealth
 * JD-Core Version:    0.6.0
 */