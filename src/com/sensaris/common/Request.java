/*      */ package com.sensaris.common;
/*      */ 
/*      */ import com.sensaris.senslink.SensLinkFrame;
/*      */ import com.sensaris.senslink.device.BT_Device;
/*      */ import com.sensaris.senslink.device.BT_Manager;
/*      */ import com.sensaris.senslink.device.Command_Manager;
/*      */ import com.sensaris.senslink.device.Device;
/*      */ import com.sensaris.senslink.device.Device_Manager;
/*      */ import com.sensaris.senslink.device.Device_State_Caller;
/*      */ import com.sensaris.senslink.device.Main;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ 
/*      */ public class Request extends Thread
/*      */   implements Device_Data_Reader, Device_State_Caller
/*      */ {
/*      */   public static final String REQ_STATE_OPENNED_UNKNOWN = "OU";
/*      */   public static final String REQ_STATE_OPENNED = "O";
/*      */   public static final String REQ_STATE_CLOSED = "C";
/*      */   public static final String REQ_STATE_CLOSED_ERROR = "CE";
/*      */   public static final String REQ_SEP_LIST_BEGIN = "@begin";
/*      */   public static final String REQ_SEP_LIST_END = "@end";
/*      */   public static final String REQ_CMD_SYS_HELP = "help";
/*      */   public static final String REQ_CMD_SYS_SNAP = "snap";
/*      */   public static final String REQ_CMD_SYS_VIEW = "view";
/*      */   public static final String REQ_CMD_CNCT = "cn";
/*      */   public static final String REQ_CMD_DCNCT = "dcn";
/*      */   public static final String REQ_CMD_GET = "get";
/*      */   public static final String REQ_CMD_MON = "mon";
/*      */   public static final String REQ_CMD_SEND = "send";
/*      */   public static final String REQ_CMD_INFO = "info";
/*      */   public static final String REQ_CMD_DEBUG = "debug";
/*      */   public static final String REQ_CMD_CLOSE = "close";
/*      */   public static final String REQ_CMD_EXIT = "exit";
/*      */   public static final String REQ_CMD_HALT = "halt";
/*      */   public static final String REQ_CMD_CLOSE_ALL = "close_all";
/*      */   public static final String REQ_CMD_UPDATE_DEVICE_LIST = "update_list";
/*      */   public static final String REQ_CMD_STOP_DEVICE = "stop";
/*      */   public static final String REQ_CMD_BROADCAST = "db";
/*      */   public static final String REQ_CMD_SENDRSS = "drss";
/*      */   public static final String REQ_CMD_SENDMHEALTH = "dmh";
/*      */   public static final String REQ_CMD_SEND_JAPET = "dj";
/*      */   public static final String DEVICE_ALL = "*";
/*      */   public static final String DEVICE_ALL_CONNECTED = "+";
/*      */   public static final String DEVICE_ONE_CONNECTED = "?";
/*      */   public static final String SERVICE_ALL = "*";
/*      */   public static final String SERVICE_DEVICE_STATE = "state";
/*      */   public static final String SERVICE_DEVICE_STATS = "stats";
/*      */   public static final String SERVICE_DEVICE_LIST = "list";
/*      */   public static final String REP_SEP_SERVICE_VALUE = " ";
/*      */   public static final String DEV_FAMILY_NAME_SENSPOD = "SENSPOD";
/*      */   public static final String DEV_FAMILY_NAME_GLUCO = "GLUCO";
/*      */   public static final String DEV_FAMILY_NAME_OXY = "OXY";
/*      */   public static final String DEV_FAMILY_NAME_ECG = "ECG";
/*      */   public static final String DEV_FAMILY_NAME_BP = "BP";
/*      */   public static final String ERR_UNKOWN_DEVICE_TYPE = "Error device type unsupported ";
/*      */   public static final String ERR_UNINTERPRETABLE_CMD = "Uninterpretable command";
/*      */   public static final String ERR_UNIMPLEMENTED_CMD = "Unimplemented command";
/*      */   public static final String ERR_UNINTERPRETABLE_SYS_CMD = "Uninterpretable device command";
/*      */   public static final String ERR_UNKNOWN_SYS_CMD = "Uknown device command";
/*      */   public static final String ERR_UNIMPLEMENTED_SYS_CMD = "Unimplemented device command";
/*      */   public static final String ERR_INSUFFISANT_ARGS_NB_SYS_CMD = "Insufficient device command arguments, required: ";
/*      */   public static final String ERR_INTEGER_CONVERSION_FAILED_SYS_CMD = "Argument error, cannot interprete this as a integer: ";
/*  251 */   static HashMap<String, Request> openned_req_list = new HashMap();
/*  252 */   static HashMap<String, Request> closed_req_list = new HashMap();
/*  253 */   static long req_id_index = 1L;
/*      */ 
/*  258 */   protected boolean _b_close = true;
/*      */   private BT_Manager _device_mng;
/*      */   private Reply_Handler _reply_handler;
/*      */   private String _RID;
/*      */   private String _state;
/*      */   private Command _current_command;
/*      */   String _cmd;
/*      */   private boolean _is_cmd_mode_monitor;
/*      */   private boolean _is_cmd_type_data;
/*      */   private boolean _is_cmd_to_device;
/*      */   String _full_req;
/*      */   int _req_args_nb;
/*      */   private long _begin_epoch;
/*      */   private long _end_epoch;
/*      */   int _reply_nb;
/*      */   private Caller_Closable _caller;
/*      */   private String[] _query_args;
/*      */   private HashMap<String, Device> _device_list;
/*      */   private HashMap<String, Command> _type_list;
/*      */   String _last_error;
/*      */   Command_Manager _cmd_mgr;
/*      */   protected int _periodicity;
/*      */ 
/*      */   public static Request get(String RID)
/*      */   {
/*  294 */     return (Request)openned_req_list.get(RID);
/*      */   }
/*      */ 
/*      */   private void exit(boolean berror) {
/*  298 */     close_all(this._caller, berror);
/*      */ 
/*  300 */     this._caller.close();
/*      */   }
/*      */ 
/*      */   public static void close_all(Caller_Closable caller, boolean berror)
/*      */   {
/*  314 */     Log.log(1, "close_all");
/*  315 */     Iterator ite = openned_req_list.values().iterator();
/*  316 */     while (ite.hasNext())
/*      */       try {
/*  318 */         Request req = (Request)ite.next();
/*  319 */         if (caller.equals(req.caller()))
/*  320 */           req.close(berror);
/*      */       }
/*      */       catch (Exception e) {
/*  323 */         Log.log(1, "RQ " + e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void stop_device(Caller_Closable caller, boolean berror) {
/*  328 */     Log.log(1, "stop_device");
/*  329 */     Iterator ite1 = this._device_list.keySet().iterator();
/*  330 */     while (ite1.hasNext())
/*      */     {
/*  332 */       String device_name = (String)ite1.next();
/*      */ 
/*  334 */       Iterator ite = openned_req_list.values().iterator();
/*  335 */       while (ite.hasNext())
/*      */         try {
/*  337 */           Request req = (Request)ite.next();
/*  338 */           if (caller.equals(req.caller())) {
/*  339 */             if (req._b_close)
/*      */               continue;
/*  341 */             if (req._device_list.containsKey(device_name))
/*  342 */               req.close(berror);
/*      */           }
/*      */         } catch (Exception e) {
/*  345 */           Log.log(1, "RQ " + e);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void close(Caller_Closable caller, boolean berror, String reqid)
/*      */   {
/*  352 */     Log.log(1, "close");
/*  353 */     Request req = (Request)openned_req_list.get(reqid.trim());
/*  354 */     if ((null != req) && (caller.equals(req.caller())))
/*  355 */       req.close(berror);
/*      */     else
/*  357 */       Log.log(1, "NO REQ found to close for this ID: " + reqid + " and listener.");
/*      */   }
/*      */ 
/*      */   Request(Caller_Closable caller, BT_Manager dmgr, Reply_Handler reply_handler, String req)
/*      */   {
/*  365 */     this._reply_handler = reply_handler;
/*      */ 
/*  367 */     this._device_mng = dmgr;
/*  368 */     this._cmd_mgr = Command_Manager.get_command_manager_ref();
/*  369 */     this._last_error = "";
/*  370 */     this._caller = caller;
/*  371 */     this._full_req = req;
/*  372 */     this._periodicity = 0;
/*  373 */     this._RID = "";
/*  374 */     if ((null != req) && (req.length() > 0) && (parse_request(req))) {
/*  375 */       init_open_request();
/*      */     }
/*      */     else
/*      */     {
/*  389 */       this._b_close = true;
/*  390 */       this._RID = "0";
/*      */ 
/*  392 */       String reply = ("CE".equals(this._state) ? "\"error: " + this._last_error + "\"" : "") + " - End of request.\"";
/*      */ 
/*  395 */       reply(reply);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void init_open_request()
/*      */   {
/*  402 */     this._b_close = false;
/*      */ 
/*  404 */     this._begin_epoch = System.currentTimeMillis();
/*  405 */     this._RID = String.valueOf(req_id_index++);
/*  406 */     this._state = "OU";
/*  407 */     openned_req_list.put(this._RID, this);
/*      */ 
/*  409 */     reply("");
/*  410 */     start();
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*      */     try
/*      */     {
/*  422 */       while (!this._b_close) {
/*  423 */         if (cnct_devices())
/*      */         {
/*  425 */           if ((this._is_cmd_type_data) || (this._is_cmd_to_device))
/*  426 */             cnct_services();
/*      */           else
/*  428 */             cnct_infos();
/*      */         }
/*      */         else
/*  431 */           handle_system_request();
/*      */         try
/*      */         {
/*  434 */           sleep(1000L);
/*      */         } catch (Exception e) {
/*  436 */           Log.log(1, "Request " + e);
/*  437 */           e.printStackTrace(System.err);
/*      */         }
/*      */       }
/*      */     } catch (Exception e2) {
/*  441 */       Log.log(1, "Request thread exception " + e2);
/*  442 */       e2.printStackTrace(System.err);
/*      */     }
/*      */ 
/*  445 */     my_close();
/*  446 */     Log.log(1, "Request " + this._RID + " exit Thread now ********");
/*      */   }
/*      */ 
/*      */   void handle_system_request()
/*      */   {
/*  455 */     if (this._cmd.equals("update_list"))
/*  456 */       command_update_list();
/*      */   }
/*      */ 
/*      */   private void command_update_list()
/*      */   {
/*      */     do {
/*  462 */       synchronized (this._cmd_mgr.search_end) {
/*  463 */         Command_Manager.command_buffer.put("update_list");
/*      */         try {
/*  465 */           this._state = "O";
/*  466 */           reply("searching...");
/*  467 */           this._cmd_mgr.search_end.wait();
/*  468 */           if (this._b_close) {
/*  469 */             return;
/*      */           }
/*  471 */           ArrayList ar = this._device_mng.discovered_list();
/*  472 */           if ((null == ar) || (0 == ar.size())) {
/*  473 */             reply("No device available found.");
/*      */           } else {
/*  475 */             int len = ar.size();
/*      */ 
/*  490 */             StringBuilder sb = new StringBuilder();
/*  491 */             for (int i = 0; i < len; i += 2) {
/*  492 */               sb.append((String)ar.get(i));
/*  493 */               sb.append(" ");
/*  494 */               sb.append((String)ar.get(i + 1));
/*  495 */               sb.append("\n");
/*      */             }
/*  497 */             reply(sb.toString());
/*      */           }
/*      */         } catch (Exception e) {
/*  500 */           Log.log(1, "RQ " + e);
/*  501 */           close(false);
/*      */         }
/*  503 */         if (this._periodicity > 0)
/*      */           try {
/*  505 */             sleep(this._periodicity * 1000);
/*      */           } catch (Exception e) {
/*  507 */             Log.log(1, "RQ " + e);
/*  508 */             close(true);
/*      */           }
/*      */         else
/*  511 */           close(true);
/*      */       }
/*      */     }
/*  514 */     while (!this._b_close);
/*      */   }
/*      */ 
/*      */   private boolean cnct_devices()
/*      */   {
/*  537 */     boolean ret = false;
/*  538 */     if (null == this._device_list) {
/*  539 */       return ret;
/*      */     }
/*  541 */     Iterator ite = this._device_list.keySet().iterator();
/*  542 */     while (ite.hasNext()) {
/*  543 */       String dev_name = (String)ite.next();
/*      */ 
/*  545 */       Device dev = this._device_mng.get_device_connected(dev_name);
/*      */ 
/*  557 */       this._device_list.put(dev_name, dev);
/*      */ 
/*  560 */       if (null == dev)
/*      */       {
/*  563 */         synchronized (this._cmd_mgr.connection_end) {
/*  564 */           Log.log(1, "Rq: ask/wait to connect " + dev_name);
/*  565 */           Command_Manager.command_buffer.put("connect " + dev_name);
/*      */           try {
/*  567 */             this._cmd_mgr.connection_end.wait();
/*  568 */             Log.log(1, "Rq: end waiting to connect " + dev_name);
/*  569 */             dev = this._device_mng.get_device_connected(dev_name);
/*  570 */             if (null != dev) {
/*  571 */               this._device_list.put(dev_name, dev);
/*      */ 
/*  573 */               Log.log(1, "Rq: " + dev_name + " connected");
/*  574 */               ret = true;
/*      */             }
/*      */           } catch (Exception e) {
/*  577 */             Log.log(1, "Rq: Err. end waiting search end " + e);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  582 */         ret = true;
/*      */       }
/*      */     }
/*  585 */     return ret;
/*      */   }
/*      */ 
/*      */   void cnct_services() {
/*  589 */     my_un_cnct_services(true);
/*      */   }
/*      */ 
/*      */   void un_cnct_services() {
/*  593 */     my_un_cnct_services(false);
/*      */   }
/*      */ 
/*      */   void my_un_cnct_services(boolean b_to_connect)
/*      */   {
/*  605 */     if ((null == this._device_list) || (null == this._type_list)) {
/*  606 */       return;
/*      */     }
/*      */ 
/*  610 */     Iterator ite = this._device_list.values().iterator();
/*  611 */     Collection col_services = this._type_list.keySet();
/*  612 */     while (ite.hasNext()) {
/*  613 */       Device dev = (Device)ite.next();
/*  614 */       if (null != dev)
/*      */       {
/*  616 */         Iterator ite_serv = col_services.iterator();
/*  617 */         while (ite_serv.hasNext()) {
/*  618 */           String service = (String)ite_serv.next();
/*      */ 
/*  620 */           if (!this._is_cmd_type_data)
/*      */           {
/*  622 */             if (b_to_connect)
/*      */             {
/*  624 */               Command cmd = (Command)this._type_list.get(service);
/*      */ 
/*  626 */               if ((null == cmd._caller) && (!cmd._b_completed))
/*      */               {
/*  628 */                 if (cmd.is_composite())
/*      */                 {
/*  630 */                   Log.log(4, "Rq: Remove original descriptor: " + service);
/*  631 */                   this._type_list.remove(service);
/*  632 */                   cmd = Command.build_composite_cmd(this, dev, cmd);
/*      */ 
/*  635 */                   this._type_list.put(service, cmd);
/*  636 */                   while (null != cmd) {
/*  637 */                     dev.put_command(this, cmd);
/*  638 */                     cmd.set_device(dev);
/*  639 */                     cmd = cmd._next;
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*  645 */                 if (cmd._b_must_interrupt)
/*      */                 {
/*  647 */                   dev.reply_command("closed", true);
/*      */                 }
/*      */ 
/*  650 */                 if (!cmd._name.equals("end_cmd"))
/*      */                 {
/*  658 */                   if (dev.is_reading()) {
/*  659 */                     cmd = Command.build_cmd(this, cmd);
/*  660 */                     this._type_list.put(service, cmd);
/*  661 */                     dev.put_command(this, cmd);
/*  662 */                     cmd.set_device(dev);
/*      */                   }
/*      */                 }
/*      */                 else {
/*  666 */                   cmd._b_command_posted_successfully = true;
/*  667 */                   cmd._b_completed = true;
/*      */                 }
/*      */ 
/*      */               }
/*  672 */               else if (cmd.is_device_reply_completed_now())
/*      */               {
/*  674 */                 if (cmd._send_back_caller_reply)
/*      */                 {
/*  676 */                   if (null != cmd._caller) {
/*  677 */                     cmd._caller.data_received_cb(dev, service, null == cmd._replies ? "void" : cmd._replies.toString(), null != cmd._next);
/*      */                   }
/*      */ 
/*  680 */                   Log.log(1, "Rq: End of command " + service + " reply len " + (null == cmd._replies ? 0 : cmd._replies.length()));
/*      */                 }
/*      */                 else {
/*  683 */                   if (null != cmd._caller) {
/*  684 */                     cmd._caller.data_received_cb(dev, service, (String)null, null != cmd._next);
/*      */                   }
/*  686 */                   Log.log(1, "Rq: End of command " + service);
/*      */                 }
/*      */ 
/*  689 */                 if (null == cmd._next)
/*      */                 {
/*  691 */                   Log.log(4, "Rq: Remove original entry " + service);
/*  692 */                   this._type_list.remove(service);
/*      */ 
/*  694 */                   this._b_close = true;
/*      */                 }
/*  696 */                 Log.log(4, "Rq: command completed: " + service);
/*  697 */                 dev.set_command_completed(cmd);
/*      */               }
/*      */               else
/*      */               {
/*  701 */                 Device a_dev = cmd.get_device();
/*  702 */                 if ((null != a_dev) && (dev != a_dev)) {
/*  703 */                   cmd._b_command_posted_successfully = false;
/*      */                 }
/*  705 */                 if (!cmd._b_command_posted_successfully)
/*      */                 {
/*  709 */                   dev.put_command(this, cmd);
/*  710 */                   cmd.set_device(dev);
/*      */                 }
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/*  716 */               Command cmd = (Command)this._type_list.get(service);
/*  717 */               if (!cmd._b_completed)
/*      */               {
/*  719 */                 dev.set_command_completed(cmd);
/*      */               }
/*  721 */               if (null != cmd._caller)
/*      */               {
/*  723 */                 cmd._caller.data_received_cb(dev, service, "", false);
/*      */               }
/*  725 */               this._type_list.remove(service);
/*  726 */               this._current_command = null;
/*      */             }
/*      */ 
/*      */           }
/*  730 */           else if (this._is_cmd_mode_monitor) {
/*  731 */             boolean bon = dev.is_mon_function(this, service);
/*  732 */             if ((!bon) && (b_to_connect)) {
/*  733 */               Log.log(1, "Rq: device not mon " + service + ": ask now");
/*  734 */               dev.mon_next_values(this, service);
/*      */             }
/*  736 */             else if ((!b_to_connect) && (bon)) {
/*  737 */               Log.log(1, "Rq: device is mon " + service + ": ask stop now");
/*  738 */               dev.un_mon_next_values(this, service);
/*      */             }
/*      */           } else {
/*  741 */             boolean bon = dev.is_get_function(this, service);
/*  742 */             if ((!bon) && (b_to_connect))
/*  743 */               dev.get_next_value(this, service);
/*  744 */             else if ((!b_to_connect) && (bon))
/*  745 */               dev.un_get_next_value(this, service);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void cnct_infos()
/*      */   {
/*  815 */     my_cnct_infos(true);
/*      */   }
/*      */ 
/*      */   void un_cnct_infos() {
/*  819 */     my_cnct_infos(false);
/*      */   }
/*      */ 
/*      */   void my_cnct_infos(boolean b_to_connect) {
/*  823 */     if (null == this._device_list) {
/*  824 */       return;
/*      */     }
/*  826 */     Iterator ite = this._device_list.values().iterator();
/*      */ 
/*  828 */     String service = "*";
/*  829 */     while (ite.hasNext()) {
/*  830 */       Device dev = (Device)ite.next();
/*  831 */       if ((null != dev) && 
/*  832 */         (this._is_cmd_mode_monitor)) {
/*  833 */         boolean bon = dev.is_mon_info(this, service);
/*  834 */         if ((!bon) && (b_to_connect)) {
/*  835 */           Log.log(1, "device not mon for infos: ask now");
/*  836 */           dev.mon_next_infos(this, service);
/*      */         }
/*  838 */         else if ((!b_to_connect) && (bon)) {
/*  839 */           Log.log(1, "device is mon infos: ask stop now");
/*  840 */           dev.un_mon_next_infos(this, service);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void reply(String reply)
/*      */   {
/*  857 */     this._reply_handler.reply(this._RID + " " + this._state + " " + reply);
/*      */   }
/*      */ 
/*      */   private boolean parse_request(String req)
/*      */   {
/*  870 */     boolean bret = true;
/*  871 */     this._query_args = req.trim().split(" +", 0);
/*  872 */     this._req_args_nb = this._query_args.length;
/*  873 */     if (this._req_args_nb <= 0) {
/*  874 */       bret = false;
/*  875 */       this._last_error = "Uninterpretable command";
/*  876 */       this._state = "CE";
/*  877 */       return bret;
/*      */     }
/*  879 */     this._cmd = this._query_args[0];
/*  880 */     Log.log(1, this._RID + " request: " + req);
/*  881 */     this._is_cmd_to_device = false;
/*  882 */     if ((this._req_args_nb > 2) && ((this._cmd.equals("mon")) || (this._cmd.equals("get")))) {
/*  883 */       this._is_cmd_type_data = true;
/*  884 */       bret &= extract_device_list();
/*  885 */       if (bret)
/*  886 */         bret &= extract_service_list();
/*      */     }
/*  888 */     else if (this._cmd.equals("snap")) {
/*  889 */       bret = false;
/*  890 */       this._state = "CE";
/*  891 */       this._last_error = "Unimplemented command";
/*  892 */       this._is_cmd_type_data = false;
/*  893 */     } else if (this._cmd.equals("view")) {
/*  894 */       bret = false;
/*  895 */       this._state = "CE";
/*  896 */       this._last_error = "Unimplemented command";
/*  897 */       this._is_cmd_type_data = false;
/*  898 */     } else if (this._cmd.equals("info")) {
/*  899 */       bret &= extract_device_list();
/*  900 */       this._is_cmd_type_data = false;
/*  901 */     } else if (this._cmd.equals("debug")) {
/*  902 */       bret = false;
/*  903 */       this._last_error = "err";
/*  904 */       this._state = "CE";
/*  905 */       if ((this._req_args_nb == 2) && 
/*  906 */         (null != this._query_args))
/*      */         try {
/*  908 */           int level = Integer.parseInt(this._query_args[1]);
/*  909 */           if (level >= -1)
/*      */           {
/*  911 */             Log.level(level);
/*  912 */             this._last_error = "done";
/*  913 */             this._state = "C";
/*      */           }
/*      */         }
/*      */         catch (Exception e) {
/*      */         }
/*      */     }
/*  919 */     else if (this._cmd.equals("close_all")) {
/*  920 */       close_all(this._caller, false);
/*  921 */       this._state = "C";
/*  922 */       bret = false;
/*  923 */     } else if (this._cmd.equals("stop")) {
/*  924 */       bret &= extract_device_list();
/*      */ 
/*  926 */       stop_device(this._caller, false);
/*  927 */       this._state = "C";
/*  928 */       bret = false;
/*      */     }
/*  931 */     else if ((this._cmd.equals("close")) && (2 == this._req_args_nb)) {
/*  932 */       close(this._caller, false, this._query_args[1]);
/*  933 */       this._state = "C";
/*  934 */       bret = false;
/*  935 */     } else if (this._cmd.equals("exit")) {
/*  936 */       exit(false);
/*  937 */     } else if (this._cmd.equals("send")) {
/*  938 */       this._is_cmd_type_data = false;
/*  939 */       this._is_cmd_to_device = true;
/*  940 */       bret &= extract_device_list();
/*  941 */       if (bret)
/*  942 */         bret &= extract_command();
/*      */     }
/*  944 */     else if (this._cmd.equals("dcn"))
/*      */     {
/*  950 */       bret &= extract_device_list();
/*  951 */       if (bret)
/*      */       {
/*  954 */         Command_Manager.command_buffer.put("disconnect " + (String)this._device_list.keySet().iterator().next());
/*      */       }
/*  956 */       this._state = "C";
/*  957 */       bret = false;
/*  958 */     } else if (this._cmd.equals("cn")) {
/*  959 */       bret &= extract_device_list();
/*  960 */       if (bret)
/*      */       {
/*  963 */         Command_Manager.command_buffer.put("connect " + (String)this._device_list.keySet().iterator().next());
/*      */       }
/*  965 */       this._state = "C";
/*  966 */       bret = false;
/*  967 */     } else if (this._cmd.equals("list")) {
/*  968 */       this._RID = "0";
/*  969 */       this._state = "O";
/*  970 */       ArrayList ar = this._device_mng.discovered_list();
/*  971 */       if ((null == ar) || (0 == ar.size())) {
/*  972 */         reply("No device available found.");
/*      */       } else {
/*  974 */         int len = ar.size();
/*  975 */         for (int i = 0; i < len; i += 2) {
/*  976 */           reply((String)ar.get(i) + " " + (String)ar.get(i + 1));
/*      */         }
/*      */       }
/*  979 */       bret = false;
/*  980 */       this._state = "C";
/*  981 */     } else if (this._cmd.equals("help")) {
/*  982 */       reply(help_msg());
/*  983 */       bret = false;
/*      */     }
/*  985 */     else if (this._cmd.equals("update_list")) {
/*  986 */       this._is_cmd_type_data = false;
/*  987 */       this._is_cmd_to_device = false;
/*  988 */       if (this._req_args_nb > 1)
/*      */         try {
/*  990 */           this._periodicity = Integer.parseInt(this._query_args[1]);
/*  991 */           bret = true;
/*      */         } catch (Exception e) {
/*  993 */           this._last_error = "Argument error, cannot interprete this as a integer: ";
/*  994 */           this._state = "CE";
/*  995 */           bret = false;
/*      */         }
/*      */     }
/*      */     else {
/*  999 */       bret = false;
/* 1000 */       this._last_error = "Uninterpretable command";
/* 1001 */       this._state = "CE";
/*      */     }
/*      */ 
/* 1004 */     this._is_cmd_mode_monitor = ((this._cmd.equals("mon")) || (this._cmd.equals("info")));
/*      */ 
/* 1006 */     return bret;
/*      */   }
/*      */ 
/*      */   String help_msg() {
/* 1010 */     StringBuffer sb = new StringBuffer();
/* 1011 */     sb.append("help:\n");
/* 1012 */     sb.append("\n-------------------------------------\n\n");
/* 1013 */     sb.append("  " + Main.name() + " server Commands\n");
/* 1014 */     sb.append("\n");
/* 1015 */     sb.append(" Version " + Main.version());
/* 1016 */     sb.append("\n\n");
/* 1017 */     sb.append("   Commands for measures\n");
/* 1018 */     sb.append("<req> <devices> <services><enter>");
/* 1019 */     sb.append("\n");
/* 1020 */     sb.append("where:\n - <req> = mon or get:\n   - mon = monitoring, retrieving continuously until close.\n   - get = get once, than request is closed.\n - <devices> = list of one or more devices\n    - * = all available (1) devices\n    - <devices> = <device_name>[,<device_name>]* (one or more)\n       <device_name> = 'BT friend' name of a device. ex: 'SENSPOD_0023'\n       ex.: SENSPOD_0023,SENSPOD_0045 (without space in this list)\n - <services> = list of one or more services\n    - * = all services of <devices>\n    - <services> =  <service_name>[,<service_name>]*    - <service_name> = name of a service provided by devices\n -      ex.: Hum COx NOx RTC GPS Gluco Oxy ...\n\n   \n\n   Commands to devices (passed to <devices>) \n\n  syntax: send <devices> <command><enter>\n    example: send GLUCO_030 DMP<enter>  pass DMP to device named GLUCO_030\n\n   \n\n   Other commands \n\n    - db <devices> <services><enter>\n      tell server to broadcast data from services and devices to Db server\n      Db server (usually DbWriter.jar) access is set in config file " + Main.config_file + ". \n" + "      Default configuration is DbWriter_address=sensaris.com DbWriter_port=33334\n" + "\n" + "    - close <RID><enter>\n" + "         close request having this request ID (<RID>)\n\n" + "    - close_all<enter>\n" + "         close all requests of the current session \n\n" + "    - exit<enter>\n" + "         close the current session and all requests\n" + "\n" + "\n" + " NOTE: all texts are case sensitive.\n" + "");
/*      */ 
/* 1058 */     sb.append("\n-------------------------------------\n\n");
/* 1059 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   boolean extract_device_list()
/*      */   {
/* 1077 */     if (this._query_args.length < 2) {
/* 1078 */       this._last_error = "Uninterpretable device command";
/* 1079 */       return false;
/*      */     }
/* 1081 */     this._device_list = new HashMap();
/* 1082 */     String list = this._query_args[1];
/* 1083 */     String[] val = list.split(",");
/* 1084 */     int len = val.length;
/* 1085 */     if (1 == len)
/*      */     {
/* 1087 */       if (list.equals("*"))
/*      */       {
/* 1091 */         this._device_mng.list_available_device(this, true);
/* 1092 */       } else if (!list.equals("+"))
/*      */       {
/* 1094 */         if (!list.equals("?"))
/*      */         {
/* 1097 */           list = list.trim();
/*      */ 
/* 1099 */           if (!BT_Device.is_bt_address(list))
/*      */           {
/* 1101 */             String bt = this._device_mng.get_bt(list);
/* 1102 */             if (null != bt)
/* 1103 */               list = bt;
/*      */           }
/* 1105 */           this._device_list.put(list, null);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1112 */       for (int i = 0; i < len; i++) {
/* 1113 */         String dev_name = val[i].trim();
/* 1114 */         if (!BT_Device.is_bt_address(dev_name))
/*      */         {
/* 1116 */           String bt = this._device_mng.get_bt(dev_name);
/* 1117 */           if (null != bt)
/* 1118 */             dev_name = bt;
/*      */         }
/* 1120 */         this._device_list.put(dev_name, null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1126 */     return true;
/*      */   }
/*      */ 
/*      */   boolean extract_service_list() {
/* 1130 */     this._type_list = new HashMap();
/* 1131 */     String list = this._query_args[2];
/* 1132 */     String[] val = list.split(",");
/* 1133 */     int len = val.length;
/* 1134 */     int i = 0;
/* 1135 */     for (; i < len; i++) {
/* 1136 */       String serv = val[i].trim();
/* 1137 */       this._type_list.put(serv, null);
/*      */     }
/* 1139 */     return true;
/*      */   }
/*      */ 
/*      */   boolean extract_command()
/*      */   {
/* 1153 */     if (this._req_args_nb < 3) {
/* 1154 */       this._last_error = "Uninterpretable device command";
/* 1155 */       return false;
/*      */     }
/* 1157 */     String cmd = this._query_args[2];
/*      */ 
/* 1159 */     Command c = null;
/* 1160 */     if (null == c) {
/*      */       try
/*      */       {
/* 1163 */         Iterator ite = this._device_list.keySet().iterator();
/* 1164 */         String dev_name = (String)ite.next();
/* 1165 */         if (null != dev_name)
/*      */         {
/* 1167 */           String fname = dev_name;
/*      */ 
/* 1169 */           if (BT_Device.is_bt_address(dev_name))
/*      */           {
/* 1171 */             fname = BT_Manager.get_bt_manager_ref().get_name(dev_name);
/* 1172 */             if (null == fname)
/* 1173 */               fname = dev_name;
/*      */           }
/*      */           try {
/* 1176 */             Class d = BT_Device.get_family_class(fname);
/* 1177 */             if (null == d) {
/* 1178 */               this._last_error = ("Error device type unsupported " + BT_Device.get_family_part(fname));
/* 1179 */               return false;
/*      */             }
/*      */ 
/* 1189 */             Object[] initParamArray = { new String(cmd) };
/* 1190 */             Method m = null;
/* 1191 */             Method[] met = d.getMethods();
/* 1192 */             for (int i = 0; i < met.length; i++) {
/* 1193 */               if (met[i].getName().equals("get_command")) {
/* 1194 */                 m = met[i];
/* 1195 */                 break;
/*      */               }
/*      */             }
/* 1198 */             if (null != m)
/*      */             {
/* 1200 */               c = (Command)m.invoke(null, initParamArray);
/*      */             } else {
/* 1202 */               this._last_error = "Unimplemented command";
/* 1203 */               return false;
/*      */             }
/*      */           }
/*      */           catch (Exception e1) {
/* 1207 */             Log.log(4, "Error retrieving command " + e1);
/* 1208 */             this._last_error = ("Error device type unsupported " + BT_Device.get_family_part(fname));
/* 1209 */             Log.log(0, this._last_error);
/* 1210 */             return false;
/*      */           }
/*      */         }
/*      */       } catch (Exception e) {
/* 1214 */         Log.log(4, "Error retrieving device " + e);
/* 1215 */         this._last_error = "Error retrieving device in passed request";
/* 1216 */         return false;
/*      */       }
/*      */     }
/* 1219 */     if (null == c) {
/* 1220 */       this._last_error = "Unimplemented command";
/* 1221 */       return false;
/*      */     }
/* 1223 */     if (!c._b_implemented) {
/* 1224 */       this._last_error = "Unimplemented command";
/* 1225 */       return false;
/*      */     }
/* 1227 */     if (this._req_args_nb - 3 < c._in_arg_nb) {
/* 1228 */       this._last_error = ("Insufficient device command arguments, required: " + c._in_arg_nb);
/* 1229 */       return false;
/*      */     }
/* 1231 */     this._type_list = new HashMap();
/*      */ 
/* 1240 */     for (int i = 3; i < this._query_args.length - 3; i++) {
/* 1241 */       c.add_arg(this._query_args[i]);
/*      */     }
/*      */ 
/* 1244 */     this._type_list.put(cmd, c);
/* 1245 */     return true;
/*      */   }
/*      */ 
/*      */   public String state()
/*      */   {
/* 1254 */     return this._state;
/*      */   }
/*      */ 
/*      */   public String state(String state)
/*      */   {
/* 1264 */     String prev = this._state;
/* 1265 */     this._state = state;
/* 1266 */     return prev;
/*      */   }
/*      */ 
/*      */   public String RID()
/*      */   {
/* 1274 */     return this._RID;
/*      */   }
/*      */ 
/*      */   protected Caller_Closable caller()
/*      */   {
/* 1282 */     return this._caller;
/*      */   }
/*      */ 
/*      */   public void close(boolean berror)
/*      */   {
/* 1290 */     this._state = (berror ? "CE" : "C");
/* 1291 */     this._b_close = true;
/*      */   }
/*      */ 
/*      */   private void my_close()
/*      */   {
/* 1299 */     this._end_epoch = System.currentTimeMillis();
/* 1300 */     this._caller = null;
/*      */ 
/* 1303 */     un_cnct_services();
/* 1304 */     un_cnct_infos();
/*      */ 
/* 1307 */     this._state = "C";
/* 1308 */     reply("");
/* 1309 */     synchronized (openned_req_list) {
/* 1310 */       closed_req_list.put(this._RID, openned_req_list.remove(this._RID));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void data_received_cb(Device device, String type, String value, boolean is_open)
/*      */   {
/* 1324 */     Log.log(4, "Request.data_received_cb " + value + (is_open ? " \nopen" : " \nclosed"));
/* 1325 */     StringBuilder sb = new StringBuilder();
/* 1326 */     sb.append(device.get_friend_name());
/* 1327 */     sb.append(" ");
/* 1328 */     sb.append(type);
/* 1329 */     sb.append(" ");
/* 1330 */     sb.append(value);
/* 1331 */     if (!is_open)
/*      */     {
/* 1335 */       state("O");
/* 1336 */       reply(sb.toString());
/* 1337 */       state("C");
/* 1338 */       this._b_close = true;
/*      */     } else {
/* 1340 */       state("O");
/* 1341 */       reply(sb.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void data_received_cb(Device device, String type, SensLinkFrame value, boolean is_open)
/*      */   {
/* 1353 */     Log.log(4, "Request.data_received_cb " + value + (is_open ? " \nopen" : " \nclosed"));
/* 1354 */     StringBuilder sb = new StringBuilder();
/* 1355 */     sb.append(device.get_friend_name());
/* 1356 */     sb.append(" ");
/* 1357 */     sb.append(type);
/* 1358 */     if (null != value) {
/* 1359 */       sb.append(" ");
/* 1360 */       sb.append(value.serialise_data());
/*      */     }
/* 1362 */     if (!is_open)
/*      */     {
/* 1366 */       state("O");
/* 1367 */       reply(sb.toString());
/* 1368 */       state("C");
/* 1369 */       this._b_close = true;
/*      */     } else {
/* 1371 */       state("O");
/* 1372 */       reply(sb.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Device_Manager device_manager_ref()
/*      */   {
/* 1381 */     throw new UnsupportedOperationException("Not supported yet.");
/*      */   }
/*      */ 
/*      */   public void device_manager_connect_device_cb(String dev_name, Device device, boolean connected)
/*      */   {
/* 1391 */     if ((connected) && (("get".equals(this._cmd)) || ("mon".equals(this._cmd))))
/*      */     {
/* 1394 */       if (connected) {
/* 1395 */         if (this._device_list.containsKey(dev_name)) {
/* 1396 */           if (this._device_list.containsValue(device)) {
/* 1397 */             return;
/*      */           }
/* 1399 */           this._device_list.put(dev_name, device);
/*      */         }
/*      */ 
/*      */       }
/* 1406 */       else if (this._device_list.containsKey(dev_name)) {
/* 1407 */         if (!this._device_list.containsValue(device)) {
/* 1408 */           return;
/*      */         }
/* 1410 */         this._device_list.put(dev_name, null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void device_manager_connected_devices(ArrayList list)
/*      */   {
/* 1449 */     throw new UnsupportedOperationException("Not supported yet.");
/*      */   }
/*      */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Request
 * JD-Core Version:    0.6.0
 */