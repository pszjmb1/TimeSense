/*      */ package com.sensaris.senslink.device;
/*      */ 
/*      */ import com.sensaris.common.Buffer;
/*      */ import com.sensaris.common.Command;
/*      */ import com.sensaris.common.Device_Data_Reader;
/*      */ import com.sensaris.common.Log;
/*      */ import com.sensaris.common.Utils;
/*      */ import com.sensaris.senslink.SensLinkFrame;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Vector;
/*      */ import javax.bluetooth.RemoteDevice;
/*      */ import javax.bluetooth.ServiceRecord;
/*      */ import javax.microedition.io.Connector;
/*      */ import javax.microedition.io.StreamConnection;
/*      */ 
/*      */ public class BT_Device extends Device
/*      */ {
/*      */   String device_name;
/*      */   String device_bt_address;
/*      */   String device_URL;
/*   50 */   protected boolean connected = false;
/*   51 */   StreamConnection connection_BT = null;
/*   52 */   DataOutputStream device_in = null;
/*   53 */   DataInputStream device_out = null;
/*      */   ServiceRecord service;
/*      */   boolean reading;
/*      */   private HashMap<String, ArrayList> queue_caller;
/*      */   protected ArrayList<String> data_type;
/*      */   protected ArrayList<ArrayList<String>> sub_type_values;
/*   65 */   String header_sensaris_time = "#";
/*   66 */   String type_Day = "Day";
/*   67 */   String type_Hour = "Hour";
/*   68 */   String type_Millisec = "Millisec";
/*      */   public byte[] STX;
/*      */   public byte[] ETX;
/*      */   protected static final int max_frame_length = 10000;
/*      */   protected byte[] _part_frame;
/*      */   protected int _part_frame_index;
/*      */   public float frame_time;
/*      */   public int frame_length;
/*   93 */   public int end_of_frame = 1;
/*      */   public static final String TYPE_UNKNOWN = "TYPE_UNKNOWN";
/*      */   public static final String frame_version = "1.1";
/*      */   public static final int Etx = 1;
/*      */   public static final int Timer = 2;
/*      */   public static final int Length = 3;
/*      */   protected static final byte NL = 10;
/*  117 */   private static HashMap<String, Command> command_list = new HashMap();
/*      */ 
/*      */   public static Command get_command(String name)
/*      */   {
/*  133 */     return (Command)command_list.get(name);
/*      */   }
/*      */   public static void set_command(String name, Command cmd) {
/*  136 */     command_list.put(name, cmd);
/*      */   }
/*      */ 
/*      */   public BT_Device(ServiceRecord service)
/*      */     throws IOException
/*      */   {
/*  146 */     this.service = service;
/*      */ 
/*  150 */     this.device_name = service.getHostDevice().getFriendlyName(false);
/*      */ 
/*  152 */     this.device_name = this.device_name.toUpperCase();
/*  153 */     this.device_name = this.device_name.replace(' ', '_');
/*      */ 
/*  155 */     this.device_bt_address = service.getHostDevice().getBluetoothAddress();
/*      */ 
/*  157 */     this.STX = "$".getBytes();
/*      */ 
/*  159 */     this.ETX = "\n".getBytes();
/*      */ 
/*  161 */     this._part_frame = new byte[10000];
/*  162 */     this._part_frame_index = 0;
/*      */ 
/*  165 */     this.data_type = new ArrayList();
/*      */ 
/*  167 */     this.sub_type_values = new ArrayList();
/*  168 */     this.reading = false;
/*      */   }
/*      */ 
/*      */   public BT_Device()
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean is_reading()
/*      */   {
/*  185 */     return this.reading;
/*      */   }
/*      */ 
/*      */   public ServiceRecord get_service() {
/*  189 */     return this.service;
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*  208 */     Log.log(4, "Start Thread reading incoming data");
/*      */     try
/*      */     {
/*  219 */       Thread.sleep(100L);
/*      */     }
/*      */     catch (Exception e2)
/*      */     {
/*  223 */       Log.log(1, "BT_dev cannot sleep " + e2);
/*  224 */       return;
/*      */     }
/*  226 */     while (this.connected) {
/*      */       try
/*      */       {
/*  229 */         byte[] device_frame = new byte[100000];
/*  230 */         int i = 0;
/*  231 */         int pos = 0;
/*  232 */         this.reading = true;
/*  233 */         byte[] byte_ETX = this.ETX;
/*      */ 
/*  236 */         while (this.reading)
/*      */         {
/*  252 */           if (0 == this.device_out.available())
/*      */             try {
/*  254 */               sleep(10L);
/*      */             }
/*      */             catch (Exception e)
/*      */             {
/*      */             }
/*  259 */           if (0 == this.device_out.available())
/*      */             try
/*      */             {
/*  262 */               sleep(200L);
/*      */             }
/*      */             catch (Exception e)
/*      */             {
/*      */             }
/*  267 */           if (0 == this.device_out.available()) {
/*      */             break;
/*      */           }
/*  270 */           device_frame[i] = this.device_out.readByte();
/*      */ 
/*  272 */           byte c = device_frame[i];
/*  273 */           if (c == byte_ETX[pos])
/*  274 */             pos++;
/*      */           else {
/*  276 */             pos = 0;
/*      */           }
/*  278 */           if ((device_frame[i] == 10) && (i < 16))
/*      */           {
/*  281 */             byte[] deb = new byte[i + 1];
/*  282 */             System.arraycopy(device_frame, 0, deb, 0, i + 1);
/*  283 */             Log.log(2, "skipped NL glitch inside: " + Utils.display_hexa(deb));
/*      */ 
/*  288 */             pos = 0;
/*  289 */             continue;
/*      */           }
/*  291 */           if (byte_ETX.length == pos) {
/*  292 */             byte[] bar = new byte[i + 1];
/*  293 */             System.arraycopy(device_frame, 0, bar, 0, i + 1);
/*  294 */             Log.log(5, "brute device reception hexa: ---'" + Utils.display_hexa(bar) + "'---");
/*  295 */             Log.log(5, "brute device reception text: ---'" + new String(bar) + "'---");
/*  296 */             parse_frame(bar);
/*  297 */             i = 0;
/*  298 */             pos = 0;
/*      */           } else {
/*  300 */             i++;
/*      */           }
/*      */         }
/*  303 */         this.reading = false;
/*  304 */         if (i > 0)
/*      */         {
/*  307 */           byte[] bar = new byte[i + 1];
/*  308 */           System.arraycopy(device_frame, 0, bar, 0, i + 1);
/*  309 */           Log.log(5, "brute device reception hexa: ---'" + Utils.display_hexa(bar) + "'---");
/*  310 */           Log.log(5, "brute device reception text: ---'" + new String(bar) + "'---");
/*  311 */           parse_frame(bar);
/*      */         }
/*      */       }
/*      */       catch (IOException e) {
/*  315 */         Log.log(1, "BT_Dev device disconnected " + this.device_name + " -cnct flag is: " + this.connected);
/*      */ 
/*  317 */         Main.command_manager.bt_Manager.disconnect_device(this.device_name);
/*      */ 
/*  319 */         close_connection();
/*      */       }
/*      */     }
/*  322 */     Log.log(1, "BT_dev thread exiting " + this.device_name);
/*      */   }
/*      */ 
/*      */   protected void parse_frame(byte[] bar)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void parse_frame(String frame)
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void add_type(String type, ArrayList<String> type_val)
/*      */   {
/*  451 */     this.data_type.add(type);
/*      */ 
/*  453 */     this.sub_type_values.add(this.data_type.indexOf(type), type_val);
/*      */   }
/*      */ 
/*      */   public ArrayList<String> sub_type(String type)
/*      */   {
/*  492 */     int index = this.data_type.indexOf(type);
/*  493 */     if (index >= 0)
/*  494 */       return (ArrayList)this.sub_type_values.get(index);
/*  495 */     return null;
/*      */   }
/*      */ 
/*      */   byte[] frame_formatting(String frame)
/*      */   {
/*  501 */     String msg = "frame:&:`SensLink_id`,`device_bt_address`,`frame`:&:'" + Main.SensLink_id + "','" + this.device_bt_address + "','" + frame + "'";
/*      */ 
/*  504 */     return msg.getBytes();
/*      */   }
/*      */ 
/*      */   void create_connection() throws IOException
/*      */   {
/*  509 */     this.connected = false;
/*  510 */     this.device_URL = this.service.getConnectionURL(1, false);
/*      */ 
/*  517 */     this.connection_BT = ((StreamConnection)Connector.open(this.device_URL, 3, true));
/*  518 */     this.device_in = this.connection_BT.openDataOutputStream();
/*  519 */     this.device_out = this.connection_BT.openDataInputStream();
/*      */ 
/*  521 */     this.connected = true;
/*      */ 
/*  534 */     start();
/*      */   }
/*      */ 
/*      */   void close_connection()
/*      */   {
/*  541 */     Log.log(4, "BT device close_connection ");
/*  542 */     this.connected = false;
/*      */ 
/*  546 */     if (null != this._cmd_in_progress)
/*      */     {
/*  548 */       this._cmd_in_progress._b_command_posted_successfully = false;
/*  549 */       Log.log(5, "BT device current cmd set: not posted ");
/*      */     }
/*      */     try
/*      */     {
/*  553 */       this.device_out.close();
/*      */     } catch (IOException e) {
/*  555 */       Log.log(0, "--Error closing output stream of device " + this.device_name + "\nwith message : \n" + e.getMessage());
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  561 */       this.device_in.close();
/*      */     } catch (IOException e) {
/*  563 */       Log.log(0, "--Error closing input stream of device " + this.device_name + "\nwith message : \n" + e.getMessage());
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  569 */       this.connection_BT.close();
/*      */     } catch (IOException e) {
/*  571 */       Log.log(1, "--Error closing connection of device " + this.device_name + "\nwith message : \n" + e.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void put_command(Device_Data_Reader caller, Command cmd)
/*      */   {
/*  619 */     Log.log(4, "BT device put_command named " + cmd._name);
/*  620 */     synchronized (this._command_put_list) {
/*  621 */       cmd._replies = new StringBuilder();
/*  622 */       cmd._posted_time = System.currentTimeMillis();
/*  623 */       cmd._last_reply_time = 0L;
/*  624 */       cmd._caller = caller;
/*  625 */       this._command_put_list.addElement(cmd);
/*      */     }
/*  627 */     send_next_command();
/*      */   }
/*      */ 
/*      */   protected void send_next_command()
/*      */   {
/*  636 */     synchronized (this._command_put_list)
/*      */     {
/*  638 */       if ((!this.connected) || (!this.reading))
/*      */       {
/*  640 */         return;
/*      */       }
/*  642 */       if ((null != this._cmd_in_progress) && (this._cmd_in_progress._b_command_posted_successfully))
/*      */       {
/*  644 */         return;
/*      */       }
/*  646 */       if (this._command_put_list.isEmpty()) {
/*  647 */         return;
/*      */       }
/*  649 */       if ((null == this._cmd_in_progress) && (!this._command_put_list.isEmpty()))
/*  650 */         this._cmd_in_progress = ((Command)this._command_put_list.firstElement());
/*  651 */       if (null == this._cmd_in_progress)
/*  652 */         return;
/*      */       try {
/*  654 */         this._cmd_in_progress._started_time = System.currentTimeMillis();
/*      */ 
/*  657 */         send_command(this._cmd_in_progress);
/*      */       } catch (Exception e) {
/*  659 */         Log.log(0, "BT_d nxt cmd " + e);
/*      */ 
/*  661 */         this._cmd_in_progress._b_command_posted_successfully = false;
/*  662 */         Log.log(5, "current command: set NOT posted.");
/*      */ 
/*  664 */         return;
/*      */       }
/*  666 */       this._cmd_in_progress._b_command_posted_successfully = true;
/*  667 */       Log.log(5, "current command: set posted.");
/*      */       try
/*      */       {
/*  670 */         if (!this._cmd_in_progress._send_back_caller_reply)
/*      */         {
/*  673 */           Log.log(4, "BT device send_next_command immediate void reply and complete " + this._cmd_in_progress._name);
/*  674 */           if (null != this._cmd_in_progress._caller) {
/*  675 */             this._cmd_in_progress._caller.data_received_cb(this, this._cmd_in_progress._full_cmd, (String)null, null != this._cmd_in_progress._next);
/*      */           }
/*  677 */           this._cmd_in_progress._caller = null;
/*  678 */           this._cmd_in_progress._b_completed = true;
/*      */ 
/*  680 */           if (!this._cmd_in_progress._expect_device_reply)
/*      */           {
/*  682 */             Log.log(4, "BT device send_next_command remove " + this._cmd_in_progress._name);
/*  683 */             this._command_put_list.remove(this._cmd_in_progress);
/*  684 */             this._cmd_in_progress = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/*  689 */         System.err.println("error in send_next command " + e);
/*      */       }
/*      */     }
/*  692 */     send_next_command();
/*      */   }
/*      */ 
/*      */   public void set_command_completed(Command cmd)
/*      */   {
/*  704 */     synchronized (this._command_put_list) {
/*  705 */       if (this._cmd_in_progress == cmd)
/*  706 */         this._cmd_in_progress = null;
/*      */       else {
/*  708 */         Log.log(4, "BT device.send_set_command_completed Already completed command " + cmd._name);
/*      */       }
/*  710 */       Log.log(4, "BT device.send_set_command_completed remove " + cmd._name);
/*  711 */       this._command_put_list.remove(cmd);
/*      */     }
/*      */ 
/*  716 */     send_next_command();
/*      */   }
/*      */ 
/*      */   void send_command(String command)
/*      */     throws IOException
/*      */   {
/*  724 */     if (command.equals("end_cmd"))
/*  725 */       return;
/*  726 */     Log.log(1, "******send_command ***senspod command : ---'" + command + "'---");
/*  727 */     if ((this.STX.length > 0) && 
/*  728 */       (!this.STX.toString().equals("$"))) {
/*  729 */       this.device_in.write(this.STX);
/*      */     }
/*      */ 
/*  732 */     this.device_in.write(command.getBytes());
/*  733 */     if (this.ETX.length > 0) {
/*  734 */       this.device_in.write(this.ETX);
/*      */     }
/*      */ 
/*  737 */     this.device_in.flush();
/*  738 */     Log.log(4, "Command " + command + " sent to " + this.device_name);
/*      */   }
/*      */ 
/*      */   void write_command(Command command) throws IOException {
/*  742 */     if (command._name.equals("end_cmd"))
/*  743 */       return;
/*  744 */     this.device_in.write(command.get_full_cmd_byte());
/*  745 */     this.device_in.flush();
/*  746 */     Log.log(4, "Command value: '" + Utils.display_hexa(command.get_full_cmd_byte()) + "' flushed to " + this.device_name);
/*      */   }
/*      */ 
/*      */   void send_command(Command command) throws IOException {
/*  750 */     Log.log(4, "Executing now Command " + command._name);
/*      */ 
/*  752 */     if (command._name.contains("reboot"))
/*  753 */       Log.log(5, "will reboot now");
/*  754 */     if (command.is_device())
/*  755 */       write_command(command);
/*  756 */     else if (command.is_internal())
/*  757 */       command.exec_command(get_friend_name());
/*      */   }
/*      */ 
/*      */   public void reply_command(String value, boolean completed)
/*      */   {
/*  779 */     Log.log(4, "BT_device.reply_command " + value + (completed ? " \ncompleted" : " \nopened ..."));
/*  780 */     synchronized (this._command_put_list)
/*      */     {
/*      */       try
/*      */       {
/*  786 */         if (null == this._cmd_in_progress)
/*      */         {
/*  788 */           return;
/*      */         }
/*  790 */         this._cmd_in_progress._last_reply_time = System.currentTimeMillis();
/*  791 */         if (completed) {
/*  792 */           this._cmd_in_progress._b_completed = true;
/*      */ 
/*  794 */           if ((this._cmd_in_progress._send_back_caller_reply) && (null != this._cmd_in_progress._caller)) {
/*  795 */             this._cmd_in_progress._caller.data_received_cb(this, this._cmd_in_progress._name, this._cmd_in_progress._send_back_caller_reply ? value : null, null != this._cmd_in_progress._next);
/*      */           }
/*      */ 
/*  798 */           this._cmd_in_progress._caller = null;
/*  799 */           Log.log(4, "BT device.reply_command remove " + this._cmd_in_progress._name);
/*  800 */           this._command_put_list.remove(this._cmd_in_progress);
/*  801 */           this._cmd_in_progress = null;
/*      */         }
/*  805 */         else if ((this._cmd_in_progress._send_back_caller_reply) && (null != this._cmd_in_progress._caller))
/*      */         {
/*  807 */           this._cmd_in_progress._caller.data_received_cb(this, this._cmd_in_progress._name, this._cmd_in_progress._send_back_caller_reply ? value : null, true);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  813 */         Log.log(1, "Error while processing a reply of command to device " + this.device_name);
/*      */       }
/*      */     }
/*  816 */     send_next_command();
/*      */   }
/*      */ 
/*      */   public void change_stx_etx(String stx, String etx)
/*      */   {
/*  842 */     this.STX = stx.getBytes();
/*  843 */     if (etx.equalsIgnoreCase("default"))
/*  844 */       this.ETX = "\n".getBytes();
/*      */     else {
/*  846 */       this.ETX = etx.getBytes();
/*      */     }
/*  848 */     this.frame_time = 0.0F;
/*  849 */     this.frame_length = 0;
/*  850 */     this.end_of_frame = 1;
/*  851 */     Log.msg("Frames of " + this.device_name + " are define by STX : " + stx + " and ETX : " + etx);
/*      */   }
/*      */ 
/*      */   public void change_stx_timer(String stx, String timer)
/*      */   {
/*  870 */     this.STX = stx.getBytes();
/*  871 */     this.ETX = null;
/*  872 */     this.frame_time = Integer.valueOf(timer).intValue();
/*  873 */     this.frame_length = 0;
/*  874 */     this.end_of_frame = 2;
/*  875 */     Log.msg("Frames of " + this.device_name + " are define by STX : " + stx + " and timer : " + timer);
/*      */   }
/*      */ 
/*      */   public void change_stx_length(String stx, String length)
/*      */   {
/*  895 */     this.STX = stx.getBytes();
/*  896 */     this.ETX = null;
/*  897 */     this.frame_time = 0.0F;
/*  898 */     this.frame_length = Integer.valueOf(length).intValue();
/*  899 */     this.end_of_frame = 3;
/*  900 */     Log.msg("Frames of " + this.device_name + " are define by STX : " + stx + " and length : " + length);
/*      */   }
/*      */ 
/*      */   protected boolean is_frame_ends_with_ETX(byte[] ar) {
/*  904 */     int e = this.ETX.length - 1; for (int a = ar.length - 1; e >= 0; a--) {
/*  905 */       if (ar[a] != this.ETX[e])
/*  906 */         return false;
/*  904 */       e--;
/*      */     }
/*      */ 
/*  909 */     return true;
/*      */   }
/*      */ 
/*      */   public String state()
/*      */   {
/*  918 */     throw new UnsupportedOperationException("Not supported yet.");
/*      */   }
/*      */ 
/*      */   public String get_friend_name()
/*      */   {
/*  927 */     return this.device_name;
/*      */   }
/*      */ 
/*      */   public String get_address()
/*      */   {
/*  935 */     return this.device_bt_address;
/*      */   }
/*      */ 
/*      */   public SensLinkFrame get_frame_header()
/*      */   {
/*  946 */     SensLinkFrame m_frame = new SensLinkFrame("TYPE_UNKNOWN", "1.1");
/*  947 */     m_frame.add_SensLink_id(Main.SensLink_id);
/*  948 */     m_frame.add_device_name(this.device_name);
/*  949 */     m_frame.add_device_bt_address(this.device_bt_address);
/*  950 */     m_frame.add_epoch_frame(Utils.iso_date(System.currentTimeMillis()));
/*  951 */     return m_frame;
/*      */   }
/*      */ 
/*      */   public SensLinkFrame get_frame_header(String frame_type, String frame_version) {
/*  955 */     SensLinkFrame m_frame = new SensLinkFrame(frame_type, frame_version);
/*  956 */     m_frame.add_SensLink_id(Main.SensLink_id);
/*  957 */     m_frame.add_device_name(this.device_name);
/*  958 */     m_frame.add_device_bt_address(this.device_bt_address);
/*  959 */     m_frame.add_epoch_frame(Utils.iso_date(System.currentTimeMillis()));
/*  960 */     return m_frame;
/*      */   }
/*      */ 
/*      */   public static boolean is_bt_address(String str)
/*      */   {
/*      */     try
/*      */     {
/*  976 */       String val = str.trim();
/*  977 */       if (val.length() != 12)
/*  978 */         return false;
/*  979 */       Long.parseLong(val, 16);
/*  980 */       return true; } catch (Exception e) {
/*      */     }
/*  982 */     return false;
/*      */   }
/*      */ 
/*      */   public static String get_family_part(String name)
/*      */   {
/*  994 */     if (null != Device.map_device_name_to_family)
/*      */     {
/*  996 */       String st = (String)Device.map_device_name_to_family.get(name);
/*  997 */       if (null != st)
/*  998 */         return st;
/*      */     }
/* 1000 */     int index = name.indexOf('_');
/* 1001 */     if (index >= 0)
/* 1002 */       return name.substring(0, index);
/* 1003 */     Log.log(1, "Warning: No family device name for named device: '" + name + "'");
/* 1004 */     return null;
/*      */   }
/*      */ 
/*      */   public static Class get_family_class(String dev_name)
/*      */   {
/* 1019 */     Class d = null;
/* 1020 */     String fam = get_family_part(dev_name);
/*      */     try
/*      */     {
/* 1023 */       String cl_name = BT_Device.class.getPackage().getName() + "." + "BT_Device" + "_" + fam;
/*      */ 
/* 1025 */       d = Class.forName(cl_name);
/*      */     } catch (Exception e) {
/* 1027 */       Log.log(1, "BTd Error: Unsupported device family for device: " + dev_name);
/*      */     }
/*      */ 
/* 1030 */     return d;
/*      */   }
/*      */ 
/*      */   public static BT_Device get_BT_Device(String device_name, ServiceRecord service)
/*      */   {
/*      */     try
/*      */     {
/* 1043 */       String fname = device_name;
/* 1044 */       if (is_bt_address(device_name))
/*      */       {
/* 1046 */         fname = BT_Manager.get_bt_manager_ref().get_name(device_name);
/* 1047 */         if (null == fname) {
/* 1048 */           fname = device_name;
/*      */         }
/*      */       }
/* 1051 */       Class cl = get_family_class(device_name);
/* 1052 */       if (null == cl) {
/* 1053 */         return null;
/*      */       }
/* 1055 */       Object[] initParamArray = { service };
/* 1056 */       Class[] classParamArray = { ServiceRecord.class };
/* 1057 */       Constructor cons = cl.getDeclaredConstructor(classParamArray);
/*      */ 
/* 1059 */       return (BT_Device)cons.newInstance(initParamArray);
/*      */     } catch (Exception e) {
/* 1061 */       Log.log(1, "Fatal: unable to contruct object  " + e);
/*      */     }
/* 1063 */     return null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  121 */     Command cmd = new Command(1, "sleep_5000");
/*  122 */     cmd._in_args = new ArrayList();
/*  123 */     cmd._in_args.add("5000");
/*  124 */     set_command(cmd._name, cmd);
/*  125 */     cmd = new Command(1, "sleep_1000");
/*  126 */     cmd._in_args = new ArrayList();
/*  127 */     cmd._in_args.add("1000");
/*  128 */     set_command(cmd._name, cmd);
/*      */ 
/*  130 */     set_command("end_cmd", new Command(0, "end_cmd", 0, false, false, true));
/*      */   }
/* 1081 */   protected class CheckConnection extends Thread { int _period = 5000;
/* 1082 */     boolean _running = false;
/* 1083 */     String _last_device_name_viewed = null;
/* 1084 */     int _dcpt_init = 2;
/*      */     int _dcpt;
/*      */ 
/* 1089 */     CheckConnection() { init(); }
/*      */ 
/*      */     CheckConnection(int period)
/*      */     {
/* 1093 */       this._period = period;
/* 1094 */       init();
/*      */     }
/*      */ 
/*      */     public boolean is_connected() {
/* 1098 */       return this._running;
/*      */     }
/*      */ 
/*      */     public void close() {
/* 1102 */       this._running = false;
/*      */     }
/*      */ 
/*      */     private void init() {
/* 1106 */       this._dcpt = this._dcpt_init;
/* 1107 */       start();
/*      */     }
/*      */ 
/*      */     public void run() {
/* 1111 */       this._running = true;
/* 1112 */       Log.log(3, "Chck cnt now running (for " + BT_Device.this.device_name + ")");
/*      */       while ((this._running) && (BT_Device.this.connected))
/*      */         {
/*      */           try {
/* 1116 */             Thread.sleep(this._period);
/*      */           }
/*      */           catch (Exception e) {
/* 1119 */             Log.log(5, "Chck_cnt  " + e);
/* 1120 */             this._running = false;
/*      */           }
/* 1122 */           if (!this._running)
/*      */             continue;
/*      */           try
/*      */           {
/* 1126 */             synchronized (Command_Manager.get_command_manager_ref().get_friend_name_end)
/*      */             {
/* 1128 */               Command_Manager.get_command_manager_ref(); Command_Manager.command_buffer.put("get_friend_name " + BT_Device.this.device_name);
/* 1129 */               Command_Manager.get_command_manager_ref().get_friend_name_end.wait();
/* 1130 */               this._last_device_name_viewed = ((String)Command_Manager.get_command_manager_ref().bt_Manager.BT_friend_name_map.get(BT_Device.this.device_name));
/*      */             }
/*      */ 
/* 1134 */             Log.log(5, "Chck_cnt friend name " + this._last_device_name_viewed);
/* 1135 */             this._dcpt = this._dcpt_init;
/*      */           }
/*      */           catch (Exception e) {
/* 1138 */             Log.log(1, "Chck_cnt " + BT_Device.this.device_name + "is disconnected (  friend viewed " + this._last_device_name_viewed + ")\n " + e);
/* 1139 */             this._dcpt -= 1;
/* 1140 */             if (this._dcpt <= 0) {
/* 1120 */             this._running = false;
/*      */             }
/*      */           }
/*      */         }
/* 1146 */       Log.log(1, "Chck cnct exit  (friend viewed " + this._last_device_name_viewed + ")");
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.device.BT_Device
 * JD-Core Version:    0.6.0
 */