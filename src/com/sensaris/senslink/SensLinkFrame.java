/*     */ package com.sensaris.senslink;
/*     */ 
/*     */ import com.sensaris.common.Formulas;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
          import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SensLinkFrame
/*     */ {
/*  36 */   private HashMap<String, String> _frame_item = null;
/*  37 */   private HashMap<String, String> _frame_value = null;
/*  38 */   private String _frame = null;
/*  39 */   private boolean _bparsed = false;
/*  40 */   private String _last_error = "";
/*     */   public static final String MHEALTH_ITEM_SEP = ",";
/*     */   public static final String MHEALTH_VALUE_SEP = " ";
/*     */   public static final String MHEALTH_KV_SEP = "=";
/*     */ 
/*     */   public SensLinkFrame(String to_parse)
/*     */     throws Exception
/*     */   {
/*  50 */     this._bparsed = parse(to_parse);
/*  51 */     if (!this._bparsed)
/*  52 */       throw new Exception("parsing error: " + this._last_error);
/*     */   }
/*     */ 
/*     */   public SensLinkFrame(String frame_data_type, String frame_data_type_version)
/*     */   {
/*  63 */     this._frame_item = new HashMap();
/*  64 */     this._frame_value = new HashMap();
/*  65 */     add_data_type(frame_data_type);
/*  66 */     add_frame_version(frame_data_type_version);
/*     */   }
/*     */ 
/*     */   public boolean parse(String frame)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       this._frame = frame;
/*  80 */       this._bparsed = true;
/*  81 */       HashMap current = new HashMap();
/*  82 */       this._frame_value = new HashMap();
/*     */ 
/*  84 */       String[] items = this._frame.split(",");
/*     */ 
/*  86 */       boolean b_data = false;
/*  87 */       for (int i = 0; i < items.length; i++) {
/*  88 */         if (!b_data) {
/*  89 */           b_data = items[i].startsWith("data_values");
/*  90 */           if (b_data) {
/*  91 */             this._frame_item = current;
/*  92 */             current = this._frame_value;
/*  93 */             continue;
/*     */           }
/*     */         }
/*  96 */         String[] entry = items[i].split("=");
/*  97 */         current.put(entry[0].trim(), entry[1].trim());
/*     */       }
/*  99 */       return control_parse();
/*     */     } catch (Exception e) {
/* 101 */       this._last_error = e.toString();
/* 102 */       System.err.println("SlF " + e);
/*     */     }
/* 104 */     return false;
/*     */   }
/*     */ 
/*     */   public void set_parsed() {
/* 108 */     this._bparsed = true;
/*     */   }
/*     */ 
/*     */   static String[] remove_false_tokens(String[] tokens, String sep) {
/* 112 */     return tokens;
/*     */   }
/*     */ 
/*     */   public boolean control_parse()
/*     */   {
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */   public void add_epoch_frame(String value)
/*     */   {
/* 129 */     add_header("epoch_frame", value);
/*     */   }
/*     */ 
/*     */   public String get_epoch_frame()
/*     */   {
/* 137 */     return get_headerValueOf("epoch_frame");
/*     */   }
/*     */ 
/*     */   public void add_SensLink_id(String value)
/*     */   {
/* 146 */     add_header("SensLink_id", value);
/*     */   }
/*     */ 
/*     */   public String get_SensLink_id()
/*     */   {
/* 154 */     return get_headerValueOf("SensLink_id");
/*     */   }
/*     */ 
/*     */   public void add_SensLink_addresss(String value)
/*     */   {
/* 163 */     add_header("SensLink_address", value);
/*     */   }
/*     */ 
/*     */   public String get_SensLink_address()
/*     */   {
/* 171 */     return get_headerValueOf("SensLink_address");
/*     */   }
/*     */ 
/*     */   public void add_SensLink_port(String value)
/*     */   {
/* 180 */     add_header("SensLink_port", value);
/*     */   }
/*     */ 
/*     */   public String get_SensLink_port()
/*     */   {
/* 188 */     return get_headerValueOf("SensLink_port");
/*     */   }
/*     */ 
/*     */   public void add_connected(String value)
/*     */   {
/* 197 */     add_header("connected", value);
/*     */   }
/*     */ 
/*     */   public String get_connected()
/*     */   {
/* 205 */     return get_headerValueOf("connected");
/*     */   }
/*     */ 
/*     */   public void add_device_bt_address(String value)
/*     */   {
/* 214 */     add_header("device_bt_address", value);
/*     */   }
/*     */ 
/*     */   public String get_device_bt_address()
/*     */   {
/* 222 */     return get_headerValueOf("device_bt_address");
/*     */   }
/*     */ 
/*     */   public void add_device_name(String value)
/*     */   {
/* 231 */     add_header("device_name", value);
/*     */   }
/*     */ 
/*     */   public String get_device_name()
/*     */   {
/* 239 */     return get_headerValueOf("device_name");
/*     */   }
/*     */ 
/*     */   public void add_frame_type(String value)
/*     */   {
/* 249 */     add_header("frame", value);
/*     */   }
/*     */ 
/*     */   public String get_frame_type()
/*     */   {
/* 257 */     return get_headerValueOf("frame");
/*     */   }
/*     */ 
/*     */   public void add_frame_version(String value)
/*     */   {
/* 267 */     add_header("type_version", value);
/*     */   }
/*     */ 
/*     */   public String get_frame_version()
/*     */   {
/* 275 */     return get_headerValueOf("type_version");
/*     */   }
/*     */ 
/*     */   public void add_data_type(String value)
/*     */   {
/* 285 */     add_header("type", value);
/*     */   }
/*     */ 
/*     */   public String get_data_type()
/*     */   {
/* 293 */     return get_headerValueOf("type");
/*     */   }
/*     */ 
/*     */   public String get_headerValueOf(String key)
/*     */   {
/* 302 */     return (String)this._frame_item.get(key);
/*     */   }
/*     */ 
/*     */   public void add_header(String key, String value)
/*     */   {
/* 311 */     this._frame_item.put(key, value);
/*     */   }
/*     */ 
/*     */   public void add_key_value(String key, String value)
/*     */   {
/* 319 */     this._frame_value.put(key, value);
/*     */   }
/*     */ 
/*     */   public String get_dataValueOf(String key)
/*     */   {
/* 328 */     return (String)this._frame_value.get(key);
/*     */   }
/*     */ 
/*     */   public void add_epoch_measure(String epoch_ISO)
/*     */   {
/* 336 */     this._frame_value.put("epoch_measure", epoch_ISO);
/*     */   }
/*     */ 
/*     */   public String get_epoch_measure()
/*     */   {
/* 344 */     return (String)this._frame_value.get("epoch_measure");
/*     */   }
/*     */ 
/*     */   public String serialise_data()
/*     */   {
/* 356 */     StringBuilder sb = new StringBuilder();
/* 357 */     Set set = this._frame_value.entrySet();
/* 358 */     Iterator ite = set.iterator();
/* 359 */     while (ite.hasNext()) {
/* 360 */       Map.Entry me = (Map.Entry)ite.next();
/* 361 */       sb.append(((String)me.getKey()).trim());
/* 362 */       sb.append("=");
/* 363 */       sb.append(((String)me.getValue()).trim());
/* 364 */       if (ite.hasNext()) {
/* 365 */         sb.append(",");
/*     */       }
/*     */     }
/* 368 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String serialise()
/*     */     throws Exception
/*     */   {
/* 377 */     this._last_error = "";
/*     */     try {
/* 379 */       StringBuilder sb = new StringBuilder();
/* 380 */       Set set = this._frame_item.entrySet();
/* 381 */       Iterator ite = set.iterator();
/* 382 */       while (ite.hasNext()) {
/* 383 */         Map.Entry me = (Map.Entry)ite.next();
/* 384 */         sb.append(((String)me.getKey()).trim());
/* 385 */         sb.append("=");
/* 386 */         sb.append(((String)me.getValue()).trim());
/* 387 */         sb.append(",");
/*     */       }
/* 389 */       sb.append("data_values");
/* 390 */       sb.append("=");
/* 391 */       String data = serialise_data();
/* 392 */       if (data.length() > 0) {
/* 393 */         sb.append(",");
/* 394 */         sb.append(serialise_data());
/*     */       }
/* 396 */       return sb.toString();
/*     */     } catch (Exception e) {
/* 398 */       this._last_error = e.toString();
                throw e;
/* 399 */     }
/*     */   }
/*     */ 
/*     */   public String serialise_rss()
/*     */   {
/* 414 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 416 */     sb.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><rss version=\"2.0\"><channel>");
/*     */ 
/* 418 */     String data_type = get_data_type();
/*     */ 
/* 420 */     build_item_elements(sb, get_device_name(), data_type, get_SensLink_id(), get_epoch_frame());
/*     */ 
/* 422 */     String epoch_measure = get_epoch_measure();
/* 423 */     if (null == epoch_measure)
/* 424 */       epoch_measure = get_epoch_frame();
/* 425 */     String unit = get_dataValueOf("unit");
/* 426 */     if (null == unit)
/* 427 */       unit = "";
/* 428 */     Set set = this._frame_value.entrySet();
/* 429 */     Iterator ite = set.iterator();
/* 430 */     String key = null; String value = null;
/*     */ 
/* 432 */     while (ite.hasNext())
/*     */     {
/* 434 */       Map.Entry me = (Map.Entry)ite.next();
/* 435 */       key = ((String)me.getKey()).trim();
/* 436 */       if ((key.contains(epoch_measure)) || 
/* 438 */         (key.equals("unit")))
/*     */         continue;
/* 440 */       key = data_type + "." + key;
/* 441 */       value = ((String)me.getValue()).trim();
/* 442 */       if ((null != key) && (value != null)) {
/* 443 */         build_item(sb, key, unit, value, epoch_measure);
/*     */       }
/*     */     }
/*     */ 
/* 447 */     sb.append("</channel></rss>");
/* 448 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected void build_item(StringBuffer sb, String title, String comments, String description, String date)
/*     */   {
/* 459 */     sb.append("<item>");
/* 460 */     build_item_elements(sb, title, comments, description, date);
/* 461 */     sb.append("</item>");
/*     */   }
/*     */ 
/*     */   void build_item_elements(StringBuffer sb, String title, String comments, String description, String date)
/*     */   {
/* 467 */     build_element(sb, "title", title);
/* 468 */     build_element(sb, "comments", comments);
/* 469 */     build_element(sb, "description", description);
/* 470 */     build_element(sb, "date", date);
/*     */   }
/*     */ 
/*     */   protected void build_element(StringBuffer sb, String key, String value) {
/* 474 */     sb.append("<" + key + ">");
/* 475 */     sb.append(value);
/* 476 */     sb.append("</" + key + ">");
/*     */   }
/*     */ 
/*     */   public ArrayList<String> retrieve_list_data_types()
/*     */   {
/* 486 */     Set set = this._frame_value.keySet();
/*     */ 
/* 488 */     ArrayList ret = new ArrayList();
/* 489 */     Iterator ite = set.iterator();
/* 490 */     while (ite.hasNext())
/*     */     {
/* 492 */       ret.add((String)ite.next());
/*     */     }
/*     */ 
/* 497 */     return ret;
/*     */   }
/*     */ 
/*     */   public String insert_query_frame()
/*     */   {
/* 568 */     StringBuilder cols = new StringBuilder();
/* 569 */     cols.append(" (`date`");
/* 570 */     StringBuilder values = new StringBuilder();
/* 571 */     values.append(" VALUES ('" + get_epoch_frame() + "'");
/*     */ 
/* 573 */     add_col_val("SensLink_id", cols, get_SensLink_id(), values);
/* 574 */     add_col_val("type", cols, get_data_type(), values);
/* 575 */     add_col_val("device_bt_address", cols, get_device_bt_address(), values);
/* 576 */     add_col_val("device_name", cols, get_device_name(), values);
/* 577 */     add_col_val("type_version", cols, get_frame_version(), values);
/*     */ 
/* 579 */     cols.append(",`");
/* 580 */     cols.append("frame");
/* 581 */     cols.append("`");
/*     */ 
/* 583 */     values.append(",'");
/* 584 */     values.append("$PSEN");
/* 585 */     values.append(",");
/* 586 */     values.append(get_data_type());
/* 587 */     values.append(",");
/* 588 */     values.append(serialise_data());
/* 589 */     values.append("'");
/*     */ 
/* 591 */     cols.append(")");
/* 592 */     values.append(")");
/*     */ 
/* 594 */     return "INSERT INTO frame" + cols + values;
/*     */   }
/*     */ 
/*     */   void add_col_val(String col, StringBuilder cols, String value, StringBuilder values) {
/* 598 */     cols.append(",`");
/* 599 */     cols.append(col);
/* 600 */     cols.append("`");
/*     */ 
/* 602 */     values.append(",'");
/* 603 */     values.append(value);
/* 604 */     values.append("'");
/*     */   }
/*     */ 
/*     */   public String frame_formatting_japet(String id_sensor, String data_type, String key)
/*     */   {
/* 625 */     String type = get_data_type().toLowerCase();
/*     */ 
/* 628 */     String[] ar = key.split(";");
/* 629 */     StringBuilder sb = new StringBuilder();
/* 630 */     for (int i = 0; i < ar.length; i++) {
/* 631 */       if (i > 0)
/* 632 */         sb.append(';');
/* 633 */       sb.append(Formulas.transform_japet(data_type, get_dataValueOf(ar[i]), null, null));
/*     */     }
/*     */ 
/* 636 */     String msg = data_type + "," + id_sensor + "," + sb.toString() + "," + get_epoch_frame();
/*     */ 
/* 638 */     if (data_type.equals("gps"))
/*     */     {
/* 640 */       return msg + "," + get_dataValueOf("status");
/*     */     }
/*     */ 
/* 644 */     return msg;
/*     */   }
/*     */ 
/*     */   public String frame_formatting_mhealth(String id_sensor)
/*     */   {
/* 664 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 667 */     String data_type = get_data_type().toLowerCase().trim();
/* 668 */     sb.append(data_type);
/* 669 */     sb.append(",");
/* 670 */     sb.append(id_sensor.trim());
/* 671 */     sb.append(",");
/* 672 */     String epoch_measure = get_epoch_measure();
/* 673 */     if (null == epoch_measure) {
/* 674 */       epoch_measure = get_epoch_frame();
/*     */     }
/* 676 */     Set set = this._frame_value.entrySet();
/* 677 */     Iterator ite = set.iterator();
/* 678 */     String key = null; String value = null;
/*     */ 
/* 680 */     boolean b_see_key = false;
/* 681 */     while (ite.hasNext())
/*     */     {
/* 683 */       Map.Entry me = (Map.Entry)ite.next();
/* 684 */       key = ((String)me.getKey()).trim();
/* 685 */       if ((key.contains("epoch_measure")) || 
/* 687 */         (key.equals("unit")) || 
/* 689 */         (key.equals("plethysmogram")))
/*     */         continue;
/* 691 */       value = ((String)me.getValue()).trim();
/* 692 */       if ((null != key) && (value != null) && (value.length() > 0))
/*     */       {
/* 694 */         if (b_see_key)
/* 695 */           sb.append(" ");
/*     */         else
/* 697 */           b_see_key = true;
/* 698 */         sb.append(key);
/* 699 */         sb.append("=");
/* 700 */         sb.append(value);
/*     */       }
/*     */     }
/* 703 */     sb.append(",");
/* 704 */     sb.append(epoch_measure);
/* 705 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.SensLinkFrame
 * JD-Core Version:    0.6.0
 */