/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.intel.bluetooth.RemoteDeviceHelper;
/*     */ import com.sensaris.common.Buffer;
/*     */ import com.sensaris.common.Log;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import javax.bluetooth.BluetoothStateException;
/*     */ import javax.bluetooth.DiscoveryAgent;
/*     */ import javax.bluetooth.LocalDevice;
/*     */ import javax.bluetooth.RemoteDevice;
/*     */ import javax.bluetooth.ServiceRecord;
/*     */ import javax.bluetooth.UUID;
/*     */ 
/*     */ public class BT_Manager
/*     */ {
/*     */   public DiscoveryAgent local_agent;
/*     */   public BT_Listener listener;
/*     */   HashMap<String, BT_Device> BT_connected_device_map;
/*     */   HashMap<String, String> BT_friend_name_map;
/*     */   HashMap<String, String> friend_name_BT_map;
/*  55 */   static final Object BT_connected_device_sync = new Object();
/*     */   public static ArrayList<String> BT_last_discovered_list;
/*     */   public boolean is_inquiring;
/*     */   HashMap<String, ArrayList<Device_State_Caller>> caller_connected_device_monitor_list;
/*     */   HashMap<String, ArrayList<Device_State_Caller>> caller_connected_device_get_list;
/*     */   ArrayList<Device_State_Caller> caller_connected_monitor_list;
/*     */   HashMap<String, ArrayList<Device_State_Caller>> caller_available_device_monitor_list;
/*     */   HashMap<String, ArrayList<Device_State_Caller>> caller_available_device_get_list;
/*     */   HashMap<String, RemoteDevice> BT_last_discovered_hash;
/*  78 */   public static final Object discovered_update_list = new Object();
/*     */ 
/*  82 */   public static BT_Manager myself = null;
/*     */ 
/*     */   public static BT_Manager get_bt_manager_ref()
/*     */   {
/*  89 */     if (null != myself) {
/*  90 */       return myself;
/*     */     }
/*  92 */     new BT_Manager();
/*  93 */     return myself;
/*     */   }
/*     */ 
/*     */   private BT_Manager()
/*     */   {
/*  98 */     this.caller_connected_device_monitor_list = new HashMap();
/*  99 */     this.caller_connected_device_get_list = new HashMap();
/* 100 */     this.caller_available_device_monitor_list = new HashMap();
/* 101 */     this.caller_available_device_get_list = new HashMap();
/* 102 */     BT_last_discovered_list = new ArrayList();
/* 103 */     this.BT_last_discovered_hash = new HashMap();
/* 104 */     this.BT_friend_name_map = new HashMap();
/* 105 */     this.friend_name_BT_map = new HashMap();
/*     */     try
/*     */     {
/* 108 */       this.listener = new BT_Listener();
/*     */     } catch (Exception e) {
/* 110 */       Log.log(0, "--BTMgr: Error Listener unreachable :  with message : " + e.getMessage());
/*     */     }
/*     */ 
/* 116 */     this.BT_connected_device_map = new HashMap();
/*     */     try {
/* 118 */       this.local_agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
/* 119 */       myself = this;
/*     */     }
/*     */     catch (BluetoothStateException e) {
/* 122 */       Log.log(0, "-- Error, cannot access devices. Bluetooth unavailable ?");
/* 123 */       Log.log(0, "--Error local device unreachable:  with message: " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void device_search()
/*     */   {
/*     */     try
/*     */     {
/* 177 */       synchronized (this.listener.evnt_inquiry_end) {
/* 178 */         Log.log(2, "BTMgr: Start device search");
/* 179 */         this.listener.BT_device_discovered.removeAllElements();
/* 180 */         this.is_inquiring = true;
/* 181 */         this.local_agent.startInquiry(10390323, this.listener);
/* 182 */         this.listener.evnt_inquiry_end.wait();
/* 183 */         this.is_inquiring = false;
/*     */         try {
/* 185 */           store_discovered_list();
/*     */         }
/*     */         catch (Exception e) {
/* 188 */           Log.log(0, "BTMgr: ERROR BT device_search: " + e);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 205 */       Log.log(1, "--Error BT device search interrupted  with message : " + e.getMessage());
/*     */     }
/*     */     catch (BluetoothStateException e)
/*     */     {
/* 209 */       Log.log(0, "--Error another program is already using BT detection with message : " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void store_discovered_list()
/*     */   {
/* 222 */     if (null == this.listener.BT_device_discovered) {
/* 223 */       synchronized (discovered_update_list) {
/* 224 */         BT_last_discovered_list = null;
/* 225 */         this.BT_last_discovered_hash = null;
/*     */       }
/* 227 */       return;
/*     */     }
/* 229 */     ArrayList ar = new ArrayList();
/* 230 */     HashMap hm = new HashMap();
/* 231 */     int len = this.listener.BT_device_discovered.size();
/* 232 */     for (int i = 0; i < len; i++) {
/*     */       try {
/* 234 */         RemoteDevice device = (RemoteDevice)this.listener.BT_device_discovered.elementAt(i);
/* 235 */         String device_bt_address = device.getBluetoothAddress();
/*     */         try {
/* 237 */           String device_name = device.getFriendlyName(false);
/* 238 */           if (null == device_name)
/* 239 */             device_name = device.getFriendlyName(true);
/* 240 */           if (null != device_name)
/*     */           {
/* 242 */             device_name = device_name.toUpperCase();
/*     */ 
/* 245 */             device_name = device_name.trim();
/* 246 */             device_name = device_name.replace(' ', '_');
/* 247 */             this.BT_friend_name_map.put(device_bt_address, device_name);
/* 248 */             this.friend_name_BT_map.put(device_name, device_bt_address);
/*     */           }
/*     */           else {
/* 251 */             continue;
/* 252 */           }ar.add(device_bt_address);
/* 253 */           ar.add(device_name);
/*     */ 
/* 255 */           hm.put(device_bt_address, device);
/*     */         }
/*     */         catch (Exception e1) {
/* 258 */           ar.add("-");
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 262 */         Log.log(0, " Err.: store_discovered_list: " + e);
/*     */       }
/*     */     }
/* 265 */     synchronized (discovered_update_list) {
/* 266 */       BT_last_discovered_list = ar;
/* 267 */       this.BT_last_discovered_hash = hm;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ArrayList<String> discovered_list()
/*     */   {
/* 279 */     synchronized (discovered_update_list)
/*     */     {
/* 281 */       if (null == BT_last_discovered_list) {
/* 282 */         return null;
/*     */       }
/* 284 */       return (ArrayList)BT_last_discovered_list.clone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ArrayList<String> connected_list()
/*     */   {
/* 307 */     synchronized (BT_connected_device_sync) {
/* 308 */       if (this.BT_connected_device_map.isEmpty()) {
/* 309 */         return null;
/*     */       }
/* 311 */       Iterator ite = this.BT_connected_device_map.entrySet().iterator();
/* 312 */       ArrayList ar = new ArrayList();
/* 313 */       while (ite.hasNext()) {
/* 314 */         Map.Entry me = (Map.Entry)ite.next();
/*     */ 
/* 316 */         ar.add((String)me.getKey() + " " + ((BT_Device)me.getValue()).device_name);
/*     */       }
/* 318 */       return ar;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void update_device_list()
/*     */   {
/* 388 */     device_search();
/*     */ 
/* 390 */     synchronized (Command_Manager.get_command_manager_ref().search_end) {
/* 391 */       Command_Manager.get_command_manager_ref().search_end.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public RemoteDevice device_pair(String dev, String pin)
/*     */   {
/*     */     try
/*     */     {
/* 433 */       RemoteDevice device = find_discoverable_device(dev);
/*     */ 
/* 435 */       boolean is_auth = device.isAuthenticated();
/* 436 */       Log.log(4, "BTMgr " + dev + " authenticated is " + is_auth);
/* 437 */       boolean is_trusted = device.isTrustedDevice();
/* 438 */       Log.log(4, "BTMgr " + dev + " trusted is " + is_trusted);
/*     */ 
/* 443 */       boolean reponse = RemoteDeviceHelper.authenticate(device, "1111");
/* 444 */       if (reponse) {
/* 445 */         Log.log(4, "BTMgr " + dev + " paired");
/* 446 */         return device;
/*     */       }
/* 448 */       Log.log(4, "BTMgr " + dev + " not paired");
/*     */     }
/*     */     catch (IOException e) {
/* 451 */       Log.log(0, "BTMgr  --Error while pairing\nwith message : \n" + e.getMessage());
/*     */     }
/*     */ 
/* 455 */     return null;
/*     */   }
/*     */ 
/*     */   void get_friend_name(String dev)
/*     */   {
/* 468 */     Log.log(5, "BTMgr: trying TO get friend name Device " + dev + " begin");
/* 469 */     BT_Device bt_device = find_connected_device(dev);
/* 470 */     if (null == bt_device) {
/* 471 */       Log.log(1, "BTMgr: get_friend_name " + dev + " not connected ");
/*     */ 
/* 474 */       this.BT_friend_name_map.put(dev, "");
/*     */     }
/*     */     else
/*     */     {
/*     */       try {
/* 479 */         String tmp = bt_device.get_service().getHostDevice().getFriendlyName(true);
/* 480 */         tmp = tmp.replace(' ', '_');
/*     */ 
/* 482 */         Log.log(5, "BTMgr: got friend name " + tmp);
/* 483 */         this.BT_friend_name_map.put(dev, tmp);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 487 */         this.BT_friend_name_map.put(dev, "");
/* 488 */         Log.log(1, "BTMgr: Chck_cnt " + dev + "is disconnected " + ")\n " + e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 494 */     synchronized (Command_Manager.get_command_manager_ref().get_friend_name_end) {
/* 495 */       Command_Manager.get_command_manager_ref().get_friend_name_end.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String get_name(String bt)
/*     */   {
/* 506 */     return (String)this.BT_friend_name_map.get(bt);
/*     */   }
/*     */ 
/*     */   public String get_bt(String name)
/*     */   {
/* 515 */     return (String)this.friend_name_BT_map.get(name);
/*     */   }
/*     */ 
/*     */   void connect_device(String dev)
/*     */   {
/* 524 */     Log.log(4, "BTMgr: trying TO connect Device " + dev + " begin");
/*     */ 
/* 526 */     BT_Device bt_device = find_connected_device(dev);
/* 527 */     if (null == bt_device) {
/* 528 */       Log.log(4, "BTMgr: " + dev + " not already connected, look for discoverable ");
/* 529 */       RemoteDevice device = find_discoverable_device(dev);
/* 530 */       if (device != null) {
/* 531 */         boolean is_auth = device.isAuthenticated();
/* 532 */         if (!is_auth) {
/* 533 */           is_auth = null != device_pair(dev, "1111");
/*     */         }
/* 535 */         if (!is_auth) {
/* 536 */           Log.log(1, "BTMgr: arrg! device " + dev + " not paired ?");
/*     */         }
/*     */         try
/*     */         {
/* 540 */           search_service(device);
/* 541 */           Log.log(4, "BTMgr: get service now ");
/* 542 */           if ((null == this.listener.temp_BT_service) || (0 == this.listener.temp_BT_service.size())) {
/* 543 */             Log.log(4, "BTMgr: arrg! list of listener.temp_BT_service is void !?!");
/*     */           }
/* 545 */           ServiceRecord service = (ServiceRecord)this.listener.temp_BT_service.firstElement();
/* 546 */           Log.log(4, "BTMgr: service is " + service);
/*     */ 
/* 551 */           String fname = device.getFriendlyName(false);
/* 552 */           if (null == fname) {
/* 553 */             fname = device.getFriendlyName(true);
/*     */           }
/* 555 */           if (null == fname) {
/* 556 */             Log.log(1, "WARNING no 'friend name' found for device " + device.getBluetoothAddress());
/* 557 */             bt_device = new BT_Device(service);
/*     */           }
/*     */           else {
/* 560 */             fname = fname.toUpperCase();
/* 561 */             fname = fname.replace(' ', '_');
/*     */ 
/* 563 */             bt_device = BT_Device.get_BT_Device(fname, service);
/* 564 */             if (null == bt_device) {
/* 565 */               Log.log(0, "--BTMgr: Fatal, unable to create object device for " + fname);
/* 566 */               Log.log(0, "--BTMgr: Fatal, possible cause: unkown family  " + BT_Device.get_family_part(fname));
/* 567 */               return;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 591 */           bt_device.create_connection();
/*     */         } catch (IOException e) {
/* 593 */           Log.log(0, "--BTMgr: Error while creating BT_device connection with message : " + e.getMessage());
/*     */ 
/* 595 */           bt_device = null;
/*     */         } catch (NoSuchElementException e) {
/* 597 */           Log.log(0, "--BTMgr: Error this device " + dev + " is not paired or have not Serial Profile (SPP)" + " with message : " + e.getMessage());
/*     */ 
/* 600 */           bt_device = null;
/*     */         }
/*     */       }
/*     */       else {
/* 604 */         Log.log(4, "BTMgr device " + dev + " not available launch update_list");
/*     */ 
/* 606 */         Command_Manager.get_command_manager_ref(); Command_Manager.command_buffer.put("update_list");
/* 607 */         synchronized (Command_Manager.get_command_manager_ref().connection_end) {
/* 608 */           Command_Manager.get_command_manager_ref().connection_end.notifyAll();
/*     */         }
/* 610 */         return;
/*     */       }
/* 612 */       if (null != bt_device)
/*     */       {
/* 614 */         synchronized (BT_connected_device_sync) {
/* 615 */           this.BT_connected_device_map.put(dev, bt_device);
/*     */         }
/*     */ 
/* 618 */         Log.log(4, "BTMgr: Device " + dev + " is connected - propagate ");
/*     */       }
/*     */     }
/* 621 */     Log.log(4, "BTMgr (connecting Device) " + dev + " completed");
/*     */ 
/* 623 */     synchronized (Command_Manager.get_command_manager_ref().connection_end) {
/* 624 */       Command_Manager.get_command_manager_ref().connection_end.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void search_service(RemoteDevice device)
/*     */   {
/* 630 */     UUID SERIAL_PORT_UUID = new UUID(4353L);
/* 631 */     UUID[] searchUuidSet = { SERIAL_PORT_UUID };
/* 632 */     int[] attrIDs = null;
/*     */     try
/*     */     {
/* 637 */       synchronized (this.listener.evnt_service_search_end) {
/* 638 */         Log.log(4, "BTMgr: Service search started");
/* 639 */         this.listener.temp_BT_service.removeAllElements();
/* 640 */         this.local_agent.searchServices(attrIDs, searchUuidSet, device, this.listener);
/* 641 */         this.listener.evnt_service_search_end.wait();
/* 642 */         Log.log(4, "BTMgr: Service search ended");
/*     */       }
/*     */     } catch (Exception e) {
/* 645 */       Log.log(0, "BTMgr: Service search interrupted   with message : " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   void disconnect_device(String dev)
/*     */   {
/* 665 */     Log.log(1, "BTMgr: now discncts " + dev);
/* 666 */     synchronized (BT_connected_device_sync) {
/* 667 */       BT_Device bt_device = (BT_Device)this.BT_connected_device_map.remove(dev);
/* 668 */       if (bt_device != null)
/* 669 */         bt_device.close_connection();
/*     */     }
/*     */   }
/*     */ 
/*     */   void disconnect_all_devices()
/*     */   {
/* 689 */     synchronized (BT_connected_device_sync) {
/* 690 */       if (this.BT_connected_device_map.isEmpty()) {
/* 691 */         return;
/*     */       }
/* 693 */       Iterator ite = this.BT_connected_device_map.entrySet().iterator();
/* 694 */       while (ite.hasNext()) {
/* 695 */         Map.Entry me = (Map.Entry)ite.next();
/* 696 */         ((BT_Device)me.getValue()).close_connection();
/*     */       }
/* 698 */       this.BT_connected_device_map.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void change_stx_etx(String dev, String stx, String etx)
/*     */   {
/* 710 */     Log.log(4, "Change STX and ETX for " + dev);
/* 711 */     BT_Device device = find_connected_device(dev);
/* 712 */     if (device != null)
/* 713 */       device.change_stx_etx(stx, etx);
/*     */     else
/* 715 */       Log.log(1, "--Error device " + dev + " have to be connected before changing STX and ETX");
/*     */   }
/*     */ 
/*     */   public void change_stx_timer(String dev, String stx, String timer)
/*     */   {
/* 726 */     Log.log(4, "Change STX and timer for " + dev);
/* 727 */     BT_Device device = find_connected_device(dev);
/* 728 */     if (device != null)
/* 729 */       device.change_stx_timer(stx, timer);
/*     */     else
/* 731 */       Log.log(1, "--Error device " + dev + " have to be connected before changing STX and timer");
/*     */   }
/*     */ 
/*     */   public void change_stx_length(String dev, String stx, String length)
/*     */   {
/* 743 */     Log.log(4, "Change STX and length for " + dev);
/* 744 */     BT_Device device = find_connected_device(dev);
/* 745 */     if (device != null)
/* 746 */       device.change_stx_length(stx, length);
/*     */     else
/* 748 */       Log.log(1, "--Error device " + dev + " have to be connected before changing STX and length");
/*     */   }
/*     */ 
/*     */   void send_command(String dev, String command)
/*     */   {
/* 755 */     Log.log(4, "BTMgr: Send command " + command + " to " + dev);
/* 756 */     BT_Device BT_device = find_connected_device(dev);
/* 757 */     if (BT_device != null)
/*     */       try {
/* 759 */         BT_device.send_command(command);
/*     */       } catch (IOException e) {
/* 761 */         Log.log(0, "BTMgr: --Error sending command to " + dev + " with message : " + e.getMessage());
/*     */       }
/*     */   }
/*     */ 
/*     */   private RemoteDevice find_discoverable_device(String dev)
/*     */   {
/* 807 */     synchronized (discovered_update_list) {
/* 808 */       return (RemoteDevice)this.BT_last_discovered_hash.get(dev);
/*     */     }
/*     */   }
/*     */ 
/*     */   public BT_Device find_connected_device(String dev)
/*     */   {
/* 835 */     synchronized (BT_connected_device_sync) {
/* 836 */       return (BT_Device)this.BT_connected_device_map.get(dev);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disconnect_device(Device_State_Caller caller, String device)
/*     */   {
/* 856 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   public void list_available_device(Device_State_Caller caller, boolean b_monitor)
/*     */   {
/* 865 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   public void monitor_connected_devices(Device_State_Caller caller, boolean b_monitor)
/*     */   {
/* 874 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   public Device get_device_connected(String dev)
/*     */   {
/* 896 */     return find_connected_device(dev);
/*     */   }
/*     */ 
/*     */   public boolean is_device_availaible(String dev)
/*     */   {
/* 905 */     return null != find_discoverable_device(dev);
/*     */   }
/*     */ 
/*     */   public void send_command_device(Device_State_Caller caller, String device, String cmd)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Manager
 * JD-Core Version:    0.6.0
 */