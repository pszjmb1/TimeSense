/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Log;
/*     */ import java.util.Vector;
/*     */ import javax.bluetooth.DeviceClass;
/*     */ import javax.bluetooth.DiscoveryListener;
/*     */ import javax.bluetooth.RemoteDevice;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ 
/*     */ public class BT_Listener
/*     */   implements DiscoveryListener
/*     */ {
/*  44 */   final Object evnt_new_device = new Object();
/*     */ 
/*  46 */   final Object evnt_inquiry_end = new Object();
/*  47 */   final Object evnt_service_search_end = new Object();
/*     */   public Vector<RemoteDevice> BT_device_discovered;
/*     */   public Vector<ServiceRecord> temp_BT_service;
/*     */ 
/*     */   BT_Listener()
/*     */   {
/*  60 */     this.BT_device_discovered = new Vector();
/*  61 */     this.temp_BT_service = new Vector();
/*     */   }
/*     */ 
/*     */   public void deviceDiscovered(RemoteDevice device, DeviceClass device_class)
/*     */   {
/*  74 */     synchronized (this.evnt_new_device) {
/*  75 */       this.BT_device_discovered.addElement(device);
/*  76 */       this.evnt_new_device.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void inquiryCompleted(int end_search_state)
/*     */   {
/*  85 */     synchronized (this.evnt_inquiry_end) {
/*  86 */       this.evnt_inquiry_end.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void servicesDiscovered(int transID, ServiceRecord[] servRecord)
/*     */   {
/*  96 */     for (int i = 0; i < servRecord.length; i++) {
/*  97 */       this.temp_BT_service.addElement(servRecord[i]);
/*  98 */       Log.log(2, "Service found " + servRecord[i].getConnectionURL(1, false));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void serviceSearchCompleted(int transID, int respCode)
/*     */   {
/* 109 */     synchronized (this.evnt_service_search_end) {
/* 110 */       this.evnt_service_search_end.notifyAll();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Listener
 * JD-Core Version:    0.6.0
 */