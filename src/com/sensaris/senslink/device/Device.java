/*     */ package com.sensaris.senslink.device;
/*     */ 
/*     */ import com.sensaris.common.Command;
/*     */ import com.sensaris.common.Device_Data_Reader;
/*     */ import com.sensaris.common.Log;
/*     */ import com.sensaris.senslink.SensLinkFrame;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public abstract class Device extends Thread
/*     */ {
/*     */   private HashMap<Device_Data_Reader, ArrayList<String>> _caller_monitor_list;
/*     */   private HashMap<Device_Data_Reader, ArrayList<String>> _caller_get_list;
/*     */   private HashMap<String, ArrayList<Device_Data_Reader>> _function_monitor_list;
/*     */   private HashMap<String, ArrayList<Device_Data_Reader>> _function_get_list;
/*     */   private HashMap<Device_Data_Reader, ArrayList<String>> _caller_monitor_info_list;
/*     */   private HashMap<String, ArrayList<Device_Data_Reader>> _function_monitor_info_list;
/*     */   private final Object _synchro_object;
/*     */   private HashMap<String, String> _function_last_value;
/*     */   private HashMap<String, String> _function_last_info_value;
/*     */   protected Vector<Command> _command_put_list;
/*  55 */   protected Command _cmd_in_progress = null;
/*     */ 
/*  61 */   private static HashMap<String, Command> command_list = new HashMap();
/*     */ 
/*  72 */   protected static HashMap<String, String> map_device_name_to_family = new HashMap();
/*     */ 
/*     */   public static Command get_command(String name)
/*     */   {
/*  78 */     return (Command)command_list.get(name);
/*     */   }
/*     */   public static void set_command(String name, Command cmd) {
/*  81 */     command_list.put(name, cmd);
/*     */   }
/*     */ 
/*     */   Device() {
/*  85 */     this._synchro_object = new Object();
/*  86 */     this._caller_monitor_list = new HashMap();
/*  87 */     this._caller_get_list = new HashMap();
/*  88 */     this._function_monitor_list = new HashMap();
/*  89 */     this._function_get_list = new HashMap();
/*  90 */     this._function_last_value = new HashMap();
/*  91 */     this._function_last_info_value = new HashMap();
/*  92 */     this._command_put_list = new Vector();
/*     */ 
/*  95 */     this._caller_monitor_info_list = new HashMap();
/*  96 */     this._function_monitor_info_list = new HashMap();
/*     */   }
/*     */ 
/*     */   public abstract String state();
/*     */ 
/*     */   public abstract boolean is_reading();
/*     */ 
/*     */   public abstract String get_friend_name();
/*     */ 
/*     */   public void mon_next_values(Device_Data_Reader caller, String function)
/*     */   {
/* 120 */     register_caller(this._caller_monitor_list, this._function_monitor_list, caller, function);
/*     */   }
/*     */ 
/*     */   public void un_mon_next_values(Device_Data_Reader caller, String function)
/*     */   {
/* 129 */     un_register_caller(this._caller_monitor_list, this._function_monitor_list, caller, function);
/*     */   }
/*     */ 
/*     */   public void mon_next_infos(Device_Data_Reader caller, String function)
/*     */   {
/* 138 */     register_caller(this._caller_monitor_info_list, this._function_monitor_info_list, caller, function);
/*     */   }
/*     */ 
/*     */   public void un_mon_next_infos(Device_Data_Reader caller, String function)
/*     */   {
/* 147 */     un_register_caller(this._caller_monitor_info_list, this._function_monitor_info_list, caller, function);
/*     */   }
/*     */ 
/*     */   public void get_next_value(Device_Data_Reader caller, String function)
/*     */   {
/* 157 */     register_caller(this._caller_get_list, this._function_get_list, caller, function);
/*     */   }
/*     */ 
/*     */   public void un_get_next_value(Device_Data_Reader caller, String function)
/*     */   {
/* 166 */     un_register_caller(this._caller_get_list, this._function_get_list, caller, function);
/*     */   }
/*     */ 
/*     */   public String get_current_value(String function)
/*     */   {
/* 175 */     synchronized (this._function_last_value) {
/* 176 */       return (String)this._function_last_value.get(function);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String get_current_info_value(String function)
/*     */   {
/* 186 */     synchronized (this._function_last_info_value) {
/* 187 */       return (String)this._function_last_info_value.get(function);
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void put_command(Device_Data_Reader paramDevice_Data_Reader, Command paramCommand);
/*     */ 
/*     */   public abstract void set_command_completed(Command paramCommand);
/*     */ 
/*     */   public abstract void reply_command(String paramString, boolean paramBoolean);
/*     */ 
/*     */   public void set_last_value(String function, String value)
/*     */   {
/* 233 */     synchronized (this._synchro_object) {
/* 234 */       this._function_last_value.put(function, value);
/*     */ 
/* 237 */       distribute((ArrayList)this._function_monitor_list.get(function), function, true, value);
/* 238 */       distribute((ArrayList)this._function_monitor_list.get("*"), function, true, value);
/*     */ 
/* 241 */       distribute((ArrayList)this._function_get_list.get(function), function, false, value);
/* 242 */       distribute((ArrayList)this._function_get_list.get("*"), function, false, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void set_last_value(String function, SensLinkFrame value)
/*     */   {
/* 253 */     synchronized (this._synchro_object) {
/* 254 */       this._function_last_value.put(function, value.serialise_data());
/*     */ 
/* 257 */       distribute((ArrayList)this._function_monitor_list.get(function), function, true, value);
/* 258 */       distribute((ArrayList)this._function_monitor_list.get("*"), function, true, value);
/*     */ 
/* 261 */       distribute((ArrayList)this._function_get_list.get(function), function, false, value);
/* 262 */       distribute((ArrayList)this._function_get_list.get("*"), function, false, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void set_last_info(String function, String value)
/*     */   {
/* 277 */     synchronized (this._synchro_object) {
/* 278 */       this._function_last_info_value.put(function, value);
/*     */ 
/* 281 */       distribute((ArrayList)this._function_monitor_info_list.get("*"), function, true, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void distribute(ArrayList<Device_Data_Reader> list, String function, boolean bmon, String value)
/*     */   {
/* 291 */     if (null != list) {
/* 292 */       int len = list.size();
/* 293 */       for (int i = 0; i < len; i++)
/*     */         try {
/* 295 */           ((Device_Data_Reader)list.get(i)).data_received_cb(this, function, value, bmon);
/*     */         }
/*     */         catch (Exception e) {
/* 298 */           Log.log(1, "Dev " + e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void distribute(ArrayList<Device_Data_Reader> list, String function, boolean bmon, SensLinkFrame value)
/*     */   {
/* 314 */     if (null != list) {
/* 315 */       int len = list.size();
/* 316 */       for (int i = 0; i < len; i++)
/*     */         try {
/* 318 */           ((Device_Data_Reader)list.get(i)).data_received_cb(this, function, value, bmon);
/*     */         }
/*     */         catch (Exception e) {
/* 321 */           Log.log(1, "Dev " + e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean is_mon_function(Device_Data_Reader caller, String function)
/*     */   {
/* 333 */     synchronized (this._synchro_object) {
/* 334 */       ArrayList list = (ArrayList)this._function_monitor_list.get(function);
/* 335 */       if (null != list) {
/* 336 */         return list.contains(caller);
/*     */       }
/* 338 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean is_get_function(Device_Data_Reader caller, String function)
/*     */   {
/* 349 */     synchronized (this._synchro_object) {
/* 350 */       ArrayList list = (ArrayList)this._function_get_list.get(function);
/* 351 */       if (null != list) {
/* 352 */         return list.contains(caller);
/*     */       }
/* 354 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean is_mon_info(Device_Data_Reader caller, String function)
/*     */   {
/* 365 */     synchronized (this._synchro_object) {
/* 366 */       ArrayList list = (ArrayList)this._function_monitor_info_list.get(function);
/* 367 */       if (null != list) {
/* 368 */         return list.contains(caller);
/*     */       }
/* 370 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */   }
/*     */ 
/*     */   private void register_caller(HashMap<Device_Data_Reader, ArrayList<String>> caller_list, HashMap<String, ArrayList<Device_Data_Reader>> function_list, Device_Data_Reader caller, String function)
/*     */   {
/* 386 */     synchronized (this._synchro_object)
/*     */     {
/* 388 */       ArrayList ar = (ArrayList)caller_list.get(caller);
/* 389 */       if (null == ar) {
/* 390 */         ar = new ArrayList();
/*     */       }
/* 392 */       else if (ar.contains(function)) {
/* 393 */         return;
/*     */       }
/*     */ 
/* 396 */       ar.add(function);
/*     */ 
/* 398 */       ArrayList arf = (ArrayList)function_list.get(function);
/* 399 */       if (null == arf) {
/* 400 */         arf = new ArrayList();
/* 401 */         function_list.put(function, arf);
/*     */       }
/* 403 */       else if (arf.contains(caller)) {
/* 404 */         return;
/*     */       }
/*     */ 
/* 408 */       arf.add(caller);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void un_register_caller(HashMap<Device_Data_Reader, ArrayList<String>> caller_list, HashMap<String, ArrayList<Device_Data_Reader>> function_list, Device_Data_Reader caller, String function)
/*     */   {
/* 416 */     synchronized (this._synchro_object) {
/* 417 */       ArrayList ar = (ArrayList)caller_list.get(caller);
/* 418 */       if (null != ar) {
/* 419 */         ar.remove(function);
/* 420 */         if (0 == ar.size())
/*     */         {
/* 422 */           caller_list.remove(caller);
/*     */         }
/*     */       }
/* 425 */       ArrayList arf = (ArrayList)function_list.get(function);
/* 426 */       if ((null != arf) && 
/* 427 */         (arf.remove(caller)) && 
/* 428 */         (0 == arf.size()))
/* 429 */         function_list.remove(function);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     map_device_name_to_family.put("WT12-A", "BP");
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.Device
 * JD-Core Version:    0.6.0
 */