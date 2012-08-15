/*     */ package com.sensaris.common;
/*     */ 
/*     */ import com.sensaris.senslink.device.Device;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class Command
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int CMD_TYPE_DEVICE = 0;
/*     */   public static final int CMD_TYPE_INTERNAL = 1;
/*     */   public static final int CMD_TYPE_COMPOSITE = 2;
/*     */   public static final String REQ_CMD_I_ONE = "one";
/*     */   public static final String REQ_CMD_I_ZERO = "zero";
/*     */   public static final String REQ_CMD_I_REPLY = "reply_caller";
/*     */   public static final String REQ_CMD_I_FILTER_LOG_FILE = "filter_log";
/*     */   public static final String REQ_CMD_I_FILTER_RECENT_FILE = "filter_recent";
/*     */   public static final String REQ_CMD_I_FILTER_FILE_NAME = "filter_file_name";
/*     */   public static final String REQ_CMD_I_SPLIT_DEVICE_REPLY = "split_device_reply";
/*     */   public static final String REQ_CMD_LG_GET_LAST_LOG = "last_log";
/*     */   public static final String REQ_CMD_LG_GET_FIRST_LOG = "first_log";
/*     */   public static final String REQ_CMD_LG_GET_LOGS_AND_PURGE = "logs_and_purge";
/*     */   public static final String REQ_CMD_LG_SET_PERIOD = "set_period";
/*     */   public static final String REQ_CMD_END_CMD = "end_cmd";
/* 128 */   private final Object sync_obj = new Object();
/* 129 */   private static boolean b_init = false;
/*     */ 
/* 131 */   public static long delay_of_end_of_reply = 20000L;
/*     */   public int _type_cmd;
/*     */   public String _name;
/*     */   public byte[] _cmd;
/*     */   public boolean _b_implemented;
/*     */   public int _in_arg_nb;
/*     */   public int _out_arg_nb;
/*     */   public boolean _expect_device_reply;
/*     */   public boolean _send_back_caller_reply;
/*     */   public int _cmd_state;
/*     */   public boolean _b_accept_interruptible_command;
/*     */   public boolean _b_must_interrupt;
/*     */   public boolean _b_completed;
/*     */   public Object _completion_rules;
/*     */   public boolean _b_command_posted_successfully;
/*     */   public Device_Data_Reader _caller;
/*     */   public String _full_cmd;
/*     */   public ArrayList<String> _out_args;
/*     */   public ArrayList<String> _in_args;
/*     */   public long _posted_time;
/*     */   public long _started_time;
/*     */   public long _last_reply_time;
/*     */   public StringBuilder _replies;
/*     */   public ArrayList<String> _sub_command_list;
/*     */   public Command _next;
/*     */   public boolean _b_session_mode;
/*     */   public Device _device;
/*     */ 
/*     */   public static void init()
/*     */   {
/* 231 */     b_init = true;
/*     */   }
/*     */ 
/*     */   public static Command build_cmd(Device_Data_Reader caller, Command cmd)
/*     */   {
/* 242 */     Command ret = null;
/*     */     try
/*     */     {
/* 245 */       ret = (Command)cmd.clone();
/*     */     } catch (Exception e) {
/* 247 */       Log.log(1, "Command clone error " + e);
/* 248 */       return null;
/*     */     }
/* 250 */     ret._caller = caller;
/* 251 */     ret._b_completed = false;
/* 252 */     return ret;
/*     */   }
/*     */ 
/*     */   public static Command build_composite_cmd(Device_Data_Reader caller, Device dev, Command cmd)
/*     */   {
/* 264 */     bcx(null, caller, dev, cmd, cmd);
/* 265 */     return cmd._next;
/*     */   }
/*     */ 
/*     */   private static Command bcx(ArrayList<Command> arl, Device_Data_Reader caller, Device dev, Command cmd, Command prev) {
/* 269 */     if (!cmd.is_composite())
/*     */     {
/* 271 */       Command cc = build_cmd(caller, cmd);
/* 272 */       if (null != prev)
/* 273 */         prev._next = cc;
/* 274 */       return cc;
/*     */     }
/* 276 */     for (int i = 0; i < cmd._sub_command_list.size(); i++)
/*     */     {
/* 278 */       prev = bcx(arl, caller, dev, Device.get_command((String)cmd._sub_command_list.get(i)), prev);
/*     */     }
/* 280 */     return prev;
/*     */   }
/*     */ 
/*     */   public Command(int cmd_type, String name, int in_min_args, boolean b_expected_reply, boolean b_reply_to_caller)
/*     */   {
/* 306 */     init(cmd_type, name, in_min_args, b_expected_reply, b_reply_to_caller);
/*     */   }
/*     */ 
/*     */   public Command(int cmd_type, String name, int in_min_args, boolean b_expected_reply, boolean b_reply_to_caller, boolean b_must_interrupt)
/*     */   {
/* 326 */     init(cmd_type, name, in_min_args, b_expected_reply, b_reply_to_caller, b_must_interrupt);
/*     */   }
/*     */ 
/*     */   public Command(int cmd_type, String name, byte[] cmd, int in_min_args, boolean b_expected_reply, boolean b_reply_to_caller)
/*     */   {
/* 342 */     this._cmd = cmd;
/* 343 */     init(cmd_type, name, in_min_args, b_expected_reply, b_reply_to_caller);
/*     */   }
/*     */ 
/*     */   public Command(int cmd_type, String name, byte[] cmd, int in_min_args, boolean b_expected_reply, boolean b_reply_to_caller, boolean b_must_interrupt)
/*     */   {
/* 348 */     this._cmd = cmd;
/* 349 */     init(cmd_type, name, in_min_args, b_expected_reply, b_reply_to_caller, b_must_interrupt);
/*     */   }
/*     */ 
/*     */   public Command(int cmd_type, String name)
/*     */   {
/* 363 */     this._type_cmd = cmd_type;
/* 364 */     this._name = name;
/* 365 */     this._sub_command_list = null;
/* 366 */     this._b_command_posted_successfully = false;
/*     */   }
/*     */ 
/*     */   public Command(String name, ArrayList<String> sub_cmd_list)
/*     */   {
/* 382 */     this._type_cmd = 2;
/* 383 */     this._name = name;
/* 384 */     this._sub_command_list = sub_cmd_list;
/* 385 */     this._b_implemented = true;
/* 386 */     this._b_command_posted_successfully = false;
/* 387 */     this._b_completed = false;
/* 388 */     this._b_must_interrupt = false;
/* 389 */     this._device = null;
/*     */   }
/*     */ 
/*     */   protected void init(int cmd_type, String name, int in_min_args, boolean b_expected_reply, boolean b_reply_to_caller)
/*     */   {
/* 395 */     this._type_cmd = cmd_type;
/* 396 */     this._name = name;
/* 397 */     this._in_arg_nb = in_min_args;
/* 398 */     this._expect_device_reply = b_expected_reply;
/* 399 */     this._send_back_caller_reply = b_reply_to_caller;
/* 400 */     this._sub_command_list = null;
/* 401 */     this._b_implemented = true;
/* 402 */     this._b_command_posted_successfully = false;
/*     */ 
/* 404 */     this._completion_rules = new Object();
/* 405 */     this._b_must_interrupt = false;
/* 406 */     this._device = null;
/*     */   }
/*     */ 
/*     */   protected void init(int cmd_type, String name, int in_min_args, boolean b_expected_reply, boolean b_reply_to_caller, boolean b_must_interrupt)
/*     */   {
/* 411 */     this._type_cmd = cmd_type;
/* 412 */     this._name = name;
/* 413 */     this._in_arg_nb = in_min_args;
/* 414 */     this._expect_device_reply = b_expected_reply;
/* 415 */     this._send_back_caller_reply = b_reply_to_caller;
/* 416 */     this._sub_command_list = null;
/* 417 */     this._b_implemented = true;
/* 418 */     this._b_command_posted_successfully = false;
/*     */ 
/* 420 */     this._completion_rules = new Object();
/* 421 */     this._b_must_interrupt = b_must_interrupt;
/* 422 */     this._device = null;
/*     */   }
/*     */ 
/*     */   public void exec_command(Object obf)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void set_device(Device dev)
/*     */   {
/* 444 */     this._device = dev;
/*     */   }
/*     */ 
/*     */   public Device get_device() {
/* 448 */     return this._device;
/*     */   }
/*     */ 
/*     */   public void set_in_args(ArrayList<String> in_args)
/*     */   {
/* 455 */     this._in_args = in_args;
/*     */   }
/*     */ 
/*     */   public void add_arg(String arg) {
/* 459 */     if (null == this._in_args) {
/* 460 */       this._in_args = new ArrayList();
/*     */     }
/* 462 */     this._in_args.add(arg);
/*     */   }
/*     */ 
/*     */   public String get_full_cmd()
/*     */   {
/* 477 */     if (null == this._in_args)
/* 478 */       return this._name;
/* 479 */     StringBuilder sb = new StringBuilder();
/* 480 */     sb.append(this._name);
/* 481 */     for (int i = 0; i < this._in_args.size(); i++)
/*     */     {
/* 483 */       sb.append(" ");
/* 484 */       sb.append((String)this._in_args.get(i));
/*     */     }
/* 486 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public byte[] get_full_cmd_byte() {
/* 490 */     if (null == this._in_args)
/*     */     {
/* 492 */       if (null == this._cmd) {
/* 493 */         return this._name.getBytes();
/*     */       }
/* 495 */       return this._cmd;
/*     */     }
/* 497 */     StringBuilder sb = new StringBuilder();
/* 498 */     sb.append(this._name);
/* 499 */     for (int i = 0; i < this._in_args.size(); i++)
/*     */     {
/* 501 */       sb.append(" ");
/* 502 */       sb.append((String)this._in_args.get(i));
/*     */     }
/* 504 */     return sb.toString().getBytes();
/*     */   }
/*     */ 
/*     */   public void timestamp_device_last_reply()
/*     */   {
/* 510 */     synchronized (this.sync_obj) {
/* 511 */       this._last_reply_time = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean is_device_reply_completed_now()
/*     */   {
/* 522 */     synchronized (this.sync_obj)
/*     */     {
/* 524 */       return this._b_completed;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean chain_next_command()
/*     */   {
/* 579 */     return false;
/*     */   }
/*     */ 
/*     */   public ArrayList<String> filter_log(ArrayList<String> list)
/*     */   {
/* 589 */     return filter_file_type(".LOG", list);
/*     */   }
/*     */ 
/*     */   protected String get_last_first_log(boolean b_recent)
/*     */   {
/* 602 */     String[] ar = this._replies.toString().split("\n");
/* 603 */     if (null == ar) {
/* 604 */       return null;
/*     */     }
/* 606 */     TreeMap tree = new TreeMap();
/* 607 */     int len = ar.length;
/* 608 */     for (int i = 0; i < len; i++) {
/* 609 */       String[] arentry = ar[i].split(" +", 0);
/* 610 */       if (!arentry[0].endsWith(".LOG"))
/*     */       {
/*     */         continue;
/*     */       }
/* 614 */       tree.put(arentry[2], arentry[0].trim());
/*     */     }
/* 616 */     if (tree.size() > 0) {
/* 617 */       if (b_recent) {
/* 618 */         return (String)tree.lastEntry().getValue();
/*     */       }
/* 620 */       return (String)tree.firstEntry().getValue();
/*     */     }
/*     */ 
/* 623 */     return null;
/*     */   }
/*     */ 
/*     */   public ArrayList<String> filter_file_type(String type, ArrayList<String> value)
/*     */   {
/* 636 */     if ((null == value) || (0 == value.size())) {
/* 637 */       return null;
/*     */     }
/* 639 */     int len = value.size();
/* 640 */     ArrayList ret = new ArrayList();
/* 641 */     for (int i = 0; i < len; i++) {
/* 642 */       String[] arentry = ((String)value.get(i)).split(" +", 0);
/* 643 */       if (!arentry[0].endsWith(type)) {
/*     */         continue;
/*     */       }
/* 646 */       for (int j = 0; j < 3; j++) {
/* 647 */         ret.add(arentry[j]);
/*     */       }
/*     */     }
/* 650 */     return ret;
/*     */   }
/*     */ 
/*     */   public ArrayList<String> split_device_reply(String value)
/*     */   {
/* 659 */     if (null == value) {
/* 660 */       return null;
/*     */     }
/* 662 */     String[] ar = value.split("\n+");
/* 663 */     if ((null == ar) || (0 == ar.length)) {
/* 664 */       return null;
/*     */     }
/* 666 */     ArrayList aa = new ArrayList();
/* 667 */     int len = ar.length;
/* 668 */     for (int i = 0; i < len; i++) {
/* 669 */       aa.add(ar[i]);
/*     */     }
/* 671 */     return aa;
/*     */   }
/*     */ 
/*     */   public ArrayList<String> filter_last(ArrayList<String> value)
/*     */   {
/* 680 */     return filter_first_last(true, value);
/*     */   }
/*     */ 
/*     */   public ArrayList<String> filter_first(ArrayList<String> value)
/*     */   {
/* 689 */     return filter_first_last(false, value);
/*     */   }
/*     */ 
/*     */   protected ArrayList<String> filter_first_last(boolean b_recent, ArrayList<String> value)
/*     */   {
/* 700 */     if (null == value) {
/* 701 */       return null;
/*     */     }
/* 703 */     int len = value.size();
/* 704 */     if (0 == len) {
/* 705 */       return null;
/*     */     }
/* 707 */     TreeMap tree = new TreeMap();
/*     */ 
/* 709 */     for (int i = 0; i < len; i += 3) {
/* 710 */       tree.put(value.get(i + 2), value.get(i));
/*     */     }
/*     */ 
/* 713 */     if (tree.size() > 0) {
/* 714 */       ArrayList ret = new ArrayList();
/* 715 */       if (b_recent)
/* 716 */         ret.add(tree.lastEntry().getValue());
/*     */       else {
/* 718 */         ret.add(tree.firstEntry().getValue());
/*     */       }
/* 720 */       return ret;
/*     */     }
/* 722 */     return null;
/*     */   }
/*     */ 
/*     */   protected ArrayList<String> one(ArrayList<String> value)
/*     */   {
/* 731 */     return return_constant("1");
/*     */   }
/*     */ 
/*     */   protected ArrayList<String> zero(ArrayList<String> value)
/*     */   {
/* 740 */     return return_constant("0");
/*     */   }
/*     */ 
/*     */   protected ArrayList<String> return_constant(String value)
/*     */   {
/* 749 */     ArrayList ret = new ArrayList();
/* 750 */     ret.add(value);
/* 751 */     return ret;
/*     */   }
/*     */ 
/*     */   public boolean is_composite() {
/* 755 */     return 2 == this._type_cmd;
/*     */   }
/*     */ 
/*     */   public boolean is_internal() {
/* 759 */     return 1 == this._type_cmd;
/*     */   }
/*     */ 
/*     */   public boolean is_device() {
/* 763 */     return 0 == this._type_cmd;
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Command
 * JD-Core Version:    0.6.0
 */