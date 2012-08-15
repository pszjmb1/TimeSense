/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Buffer;
/*     */ import com.sensaris.common.Log;
/*     */ import javax.bluetooth.DiscoveryAgent;
/*     */ 
/*     */ public class Command_Manager extends Thread
/*     */ {
/*     */   BT_Manager bt_Manager;
/*     */   boolean stop_SensLink;
/*     */   public static Buffer command_buffer;
/*  34 */   private static Command_Manager myself = null;
/*     */ 
/*  38 */   public final Object search_end = new Object();
/*     */ 
/*  42 */   public final Object connection_end = new Object();
/*  43 */   public final Object get_friend_name_end = new Object();
/*     */ 
/*     */   public static Command_Manager get_command_manager_ref()
/*     */   {
/*  52 */     if (null == myself)
/*     */     {
/*  54 */       new Command_Manager().start();
/*     */     }
/*  56 */     return myself;
/*     */   }
/*     */ 
/*     */   Command_Manager() {
/*  60 */     myself = this;
/*  61 */     this.bt_Manager = BT_Manager.get_bt_manager_ref();
/*  62 */     command_buffer = new Buffer(10);
/*  63 */     this.stop_SensLink = false;
/*     */   }
/*     */ 
/*     */   void manage_command(String msg)
/*     */   {
/*  68 */     Log.log(4, "CMgr: Command received " + msg);
/*  69 */     String[] msg_array = msg.split(" ");
/*  70 */     if (msg_array[0].equalsIgnoreCase("search")) {
/*  71 */       this.bt_Manager.device_search();
/*     */     }
/*  73 */     else if (msg_array[0].equalsIgnoreCase("update_list")) {
/*  74 */       this.bt_Manager.update_device_list();
/*     */     }
/*  76 */     else if (msg_array[0].equalsIgnoreCase("list")) {
/*  77 */       this.bt_Manager.discovered_list();
/*     */     }
/*  79 */     else if (msg_array[0].equalsIgnoreCase("stop_search")) {
/*  80 */       this.bt_Manager.local_agent.cancelInquiry(this.bt_Manager.listener);
/*     */     }
/*  82 */     else if (msg_array[0].equalsIgnoreCase("pair"))
/*     */     {
/*  86 */       Log.log(1, "--CMgr: error " + msg + " : Pair command is no more available");
/*     */     }
/*  88 */     else if (msg_array[0].equalsIgnoreCase("frame")) {
/*  89 */       if (msg_array.length == 5)
/*     */       {
/*  91 */         if (msg_array[1].equalsIgnoreCase("stx_etx"))
/*  92 */           this.bt_Manager.change_stx_etx(msg_array[2], msg_array[3], msg_array[4]);
/*  93 */         else if (msg_array[1].equalsIgnoreCase("stx_timer"))
/*  94 */           this.bt_Manager.change_stx_timer(msg_array[2], msg_array[3], msg_array[4]);
/*  95 */         else if (msg_array[1].equalsIgnoreCase("stx_length"))
/*  96 */           this.bt_Manager.change_stx_length(msg_array[2], msg_array[3], msg_array[4]);
/*  97 */         else Log.log(1, "--Command error " + msg + " instead of : \n[frame (stx_etx|stx_timer|stx_length) (device_name|mac_address) stx (etx|timer|length)]"); 
/*     */       }
/*     */       else
/*  99 */         Log.log(1, "--Command error " + msg + " instead of : \n[frame (stx_etx|stx_timer|stx_length) (device_name|mac_address) stx (etx|timer|length)]");
/*     */     }
/* 101 */     else if (msg_array[0].equalsIgnoreCase("disconnect")) {
/* 102 */       if (msg_array.length == 2)
/* 103 */         this.bt_Manager.disconnect_device(msg_array[1]);
/* 104 */       else Log.log(1, "--Command error " + msg + " instead of : \n[disconnect (device_name|mac_address)]");
/*     */     }
/* 106 */     else if (msg_array[0].equalsIgnoreCase("connect")) {
/* 107 */       if (msg_array.length == 2)
/* 108 */         this.bt_Manager.connect_device(msg_array[1]);
/* 109 */       else Log.log(1, "--Command error " + msg + " instead of : \n[connect (device_name|mac_address)]");
/*     */     }
/* 111 */     else if (msg_array[0].equalsIgnoreCase("get_friend_name")) {
/* 112 */       if (msg_array.length == 2)
/* 113 */         this.bt_Manager.get_friend_name(msg_array[1]);
/* 114 */       else Log.log(1, "--Command error " + msg + " instead of : \n[get_friend_name (device_name|mac_address)]");
/*     */     }
/* 116 */     else if (msg_array[0].equalsIgnoreCase("send_command")) {
/* 117 */       if (msg_array.length >= 2)
/*     */       {
/* 119 */         String to_send = "";
/* 120 */         for (int i = 2; i < msg_array.length; i++) {
/* 121 */           if (to_send.length() != 0) {
/* 122 */             to_send = to_send + " ";
/*     */           }
/* 124 */           to_send = to_send + msg_array[i];
/*     */         }
/* 126 */         this.bt_Manager.send_command(msg_array[1], to_send); } else {
/* 127 */         Log.log(1, "--Command error " + msg + " instead of : \n[send_command (device_name|mac_address) la_commande]");
/*     */       }
/* 129 */     } else if (msg_array[0].equalsIgnoreCase("stop_SensLink"))
/*     */     {
/* 131 */       this.stop_SensLink = true;
/*     */     }
/* 133 */     else Log.log(1, "-- CMgr: Unknown command : " + msg);
/*     */ 
/* 135 */     Log.log(4, "CMgr: " + msg + " processed");
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 144 */     while (!this.stop_SensLink) {
/* 145 */       manage_command((String)command_buffer.get());
/*     */     }
/*     */ 
/* 148 */     Log.log(2, "CM: exit");
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.Command_Manager
 * JD-Core Version:    0.6.0
 */