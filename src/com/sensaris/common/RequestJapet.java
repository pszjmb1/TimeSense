/*    */ package com.sensaris.common;
/*    */ 
/*    */ import com.sensaris.common.frame.Frame;
/*    */ import com.sensaris.common.frame.Sensor;
/*    */ import com.sensaris.senslink.SensLinkFrame;
/*    */ import com.sensaris.senslink.device.BT_Manager;
/*    */ import com.sensaris.senslink.device.Device;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class RequestJapet extends Request
/*    */ {
/*    */   HashMap<String, String> _map_sensor;
/*    */ 
/*    */   RequestJapet(Caller_Closable caller, BT_Manager dmgr, Reply_Handler reply_handler, String req)
/*    */   {
/* 21 */     super(caller, dmgr, reply_handler, req);
/* 22 */     this._map_sensor = new HashMap();
/*    */   }
/*    */ 
/*    */   public void data_received_cb(Device device, String type, SensLinkFrame frame, boolean is_open)
/*    */   {
/* 37 */     Log.msg(4, "Request.data_received_cb " + frame + (is_open ? " \nopen" : " \nclosed"));
/*    */ 
/* 39 */     if (!is_open)
/*    */     {
/* 43 */       state("O");
/* 44 */       reply(null);
/* 45 */       state("C");
/* 46 */       this._b_close = true;
/*    */     } else {
/* 48 */       state("O");
/*    */       try
/*    */       {
/* 55 */         String device_bt_add = frame.get_device_bt_address();
/* 56 */         String id_sensor = (String)this._map_sensor.get(device_bt_add);
/* 57 */         if (null == id_sensor) {
/* 58 */           id_sensor = DataSenderJapet.read_id_sensor(device_bt_add, frame.get_device_name());
/* 59 */           if ((null == id_sensor) || (0 == id_sensor.length()))
/*    */           {
/* 61 */             return;
/*    */           }
/*    */           try
/*    */           {
/* 65 */             Integer.parseInt(id_sensor);
/* 66 */             this._map_sensor.put(device_bt_add, id_sensor);
/*    */           }
/*    */           catch (Exception e) {
/* 69 */             return;
/*    */           }
/*    */         }
/*    */         try
/*    */         {
/* 74 */           DataSenderJapet.instance();
/*    */ 
/* 77 */           Frame st_frame = Frame.getFrame(type);
/* 78 */           if (null == st_frame) {
/* 79 */             return;
/*    */           }
/*    */ 
/* 82 */           for (Sensor sensor : st_frame.listSensor()) {
/* 83 */             String sensor_key = sensor.getJapetFrameValueKey();
/* 84 */             if (null == sensor_key)
/*    */             {
/* 86 */               return;
/*    */             }
/* 88 */             String msg = frame.frame_formatting_japet(id_sensor, sensor.getDbType(), sensor.getJapetFrameValueKey());
/*    */ 
/* 90 */             String res = "ER";
/* 91 */             if (null != msg) {
/* 92 */               DataSenderJapet.save_data(msg);
/*    */             } else {
/* 94 */               this._last_error = "error during parsing data";
/*    */ 
/* 96 */               Log.log(1, "DataSenderJapet.cb " + this._last_error);
/*    */ 
/* 98 */               return;
/*    */             }
/*    */           }
/*    */         }
/*    */         catch (Exception e1) {
/* 103 */           Log.log(1, "DataSenderJapet.cb " + e1);
/* 104 */           e1.printStackTrace();
/*    */         }
/* 106 */         return;
/*    */       } catch (Exception e) {
/* 108 */         Log.log(1, "RQj " + e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.RequestJapet
 * JD-Core Version:    0.6.0
 */