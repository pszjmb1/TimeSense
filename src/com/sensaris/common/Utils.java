/*     */ package com.sensaris.common;
/*     */ 
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Utils
/*     */ {
/*  34 */   public static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
/*     */ 
/*  36 */   public static DecimalFormat decimal_format_0 = new DecimalFormat("0", dfs);
/*  37 */   public static DecimalFormat decimal_format_1 = new DecimalFormat("0.0", dfs);
/*  38 */   public static DecimalFormat decimal_format_2 = new DecimalFormat("0.00", dfs);
/*  39 */   public static DecimalFormat decimal_format_3 = new DecimalFormat("0.000", dfs);
/*  40 */   static DecimalFormat decimal_format_7 = new DecimalFormat("0.0000000", dfs);
/*     */ 
/* 180 */   static final String[] month_name = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
/* 181 */   static final String[] month_id = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
/*     */ 
/* 185 */   public static final HashMap<String, String> hmonth = new HashMap();
/*     */ 
/*     */   public static String decimal_number(float fl, int decimal)
/*     */   {
/*  52 */     switch (decimal) {
/*     */     case 0:
/*  54 */       return decimal_format_0.format(fl);
/*     */     case 1:
/*  56 */       return decimal_format_1.format(fl);
/*     */     case 2:
/*  58 */       return decimal_format_2.format(fl);
/*     */     case 3:
/*     */     }
/*  61 */     return decimal_format_3.format(fl);
/*     */   }
/*     */ 
/*     */   public static String convert_date_time_usa_to_iso(String date, String time)
/*     */   {
/* 117 */     StringBuilder ret = new StringBuilder();
/* 118 */     ret.append("20");
/* 119 */     ret.append(date.substring(6, 8));
/* 120 */     ret.append("-");
/* 121 */     ret.append(date.substring(0, 2));
/* 122 */     ret.append("-");
/* 123 */     ret.append(date.substring(3, 5));
/* 124 */     ret.append("T");
/* 125 */     ret.append(time);
/* 126 */     return ret.toString();
/*     */   }
/*     */ 
/*     */   public static String convert_date_time_sens_to_iso(String date, String time, String milli)
/*     */   {
/* 135 */     StringBuilder ret = new StringBuilder();
/* 136 */     ret.append("20");
/* 137 */     ret.append(date.substring(4, 6));
/* 138 */     ret.append("-");
/* 139 */     ret.append(date.substring(2, 4));
/* 140 */     ret.append("-");
/* 141 */     ret.append(date.substring(0, 2));
/* 142 */     ret.append("T");
/* 143 */     ret.append(time.substring(0, 2));
/* 144 */     ret.append(":");
/* 145 */     ret.append(time.substring(2, 4));
/* 146 */     ret.append(":");
/* 147 */     ret.append(time.substring(4, 6));
/* 148 */     if ((null != milli) && (milli.length() > 0))
/*     */     {
/* 150 */       ret.append(".");
/* 151 */       for (int i = 0; i < milli.length() - 3; i++)
/*     */       {
/* 153 */         ret.append("0");
/*     */       }
/* 155 */       ret.append(milli);
/*     */     }
/* 157 */     return ret.toString();
/*     */   }
/*     */ 
/*     */   public static String iso_date(long java_time)
/*     */   {
/* 167 */     Date date = new Date(java_time);
/* 168 */     StringBuilder ret = new StringBuilder();
/* 169 */     String strdate = date.toString();
/* 170 */     String[] dt = strdate.split(" ");
/* 171 */     ret.append(dt[5]);
/* 172 */     ret.append("-");
/* 173 */     ret.append(month_id(dt[1]));
/* 174 */     ret.append("-");
/* 175 */     ret.append(dt[2]);
/* 176 */     ret.append("T");
/* 177 */     ret.append(dt[3]);
/* 178 */     return ret.toString();
/*     */   }
/*     */ 
/*     */   public static String month_id(String month_name_abr)
/*     */   {
/* 199 */     return (String)hmonth.get(month_name_abr);
/*     */   }
/*     */ 
/*     */   public static String display_hexa(byte[] text, String sep)
/*     */   {
/* 245 */     StringBuilder buff = new StringBuilder(text.length);
/* 246 */     for (int i = 0; i < text.length; i++) {
/* 247 */       int c = text[i] & 0xFF;
/*     */ 
/* 250 */       if (c < 16)
/* 251 */         buff.append("0");
/* 252 */       buff.append(Integer.toHexString(c).toUpperCase());
/* 253 */       if (null != sep)
/* 254 */         buff.append(sep);
/*     */     }
/* 256 */     return buff.toString();
/*     */   }
/*     */ 
/*     */   public static String display_hexa(byte[] text)
/*     */   {
/* 261 */     return display_hexa(text, " ");
/*     */   }
/*     */ 
/*     */   public static String display_hexa(String text) {
/* 265 */     return display_hexa(text.getBytes());
/*     */   }
/*     */ 
/*     */   public static String display_hexa(String text, String sep) {
/* 269 */     return display_hexa(text.getBytes(), sep);
/*     */   }
/*     */ 
/*     */   public static byte[] destuffa9(byte[] ar) {
/* 273 */     return destuf(ar, (byte)-87); 
/*     */   }
/*     */ 
/*     */   public static byte[] destuf(byte[] ar, byte stuf)
/*     */   {
/* 306 */     if (null == ar)
/* 307 */       return null;
/* 308 */     int len = ar.length;
/* 309 */     int j = 0;
/* 310 */     for (int i = 0; i < len; j++)
/*     */     {
/* 312 */       if (i != j)
/* 313 */         ar[j] = ar[i];
/* 314 */       if (stuf == ar[i])
/*     */       {
/* 316 */         i++;
/* 317 */         if (len == i)
/*     */         {
/* 320 */           Log.log(1, "Destuf error, last byte is mapped!");
/* 321 */           break;
/*     */         }
/* 323 */         ar[j] = ar[i];
/*     */         int tmp69_68 = j; ar[tmp69_68] = (byte)(ar[tmp69_68] | 0x20);
/*     */       }
/* 310 */       i++;
/*     */     }
/*     */ 
/* 328 */     byte[] ar2 = new byte[j];
/* 329 */     System.arraycopy(ar, 0, ar2, 0, j);
/* 330 */     return ar2;
/*     */   }
/*     */ 
/*     */   public static short CRC16(byte[] ar, int index, int len)
/*     */   {
/* 342 */     return crc_calculate_crc((short)-1, ar, index, len);
/*     */   }
/*     */ 
/*     */   public static short crc_calculate_crc(short initial_crc, byte[] buffer, int index, int length)
/*     */   {
/* 348 */     short crc = initial_crc;
/* 349 */     if (null != buffer)
/*     */     {
/* 351 */       for (; index < length; index++)
/*     */       {
/* 353 */         crc = (short)((byte)(crc >> 8) | (short)(crc << 8));
/* 354 */         crc = (short)(crc ^ buffer[index]);
/* 355 */         crc = (short)(crc ^ (byte)(crc & 0xFF) >> 4);
/* 356 */         crc = (short)(crc ^ (short)((short)(crc << 8) << 4));
/* 357 */         crc = (short)(crc ^ (short)((short)((crc & 0xFF) << 4) << 1));
/*     */       }
/*     */     }
/* 360 */     return crc;
/*     */   }
/*     */ 
/*     */   public static byte xor_byte_checksum(byte[] buffer, int index, int length)
/*     */   {
/* 365 */     byte crc = 0;
/* 366 */     if (null != buffer)
/*     */     {
/* 368 */       for (; index < length; index++)
/*     */       {
/* 370 */         crc = (byte)(crc ^ buffer[index]);
/*     */       }
/*     */     }
/* 373 */     return crc;
/*     */   }
/*     */ 
/*     */   public static byte[] trim_frame(byte[] arb, byte b_start)
/*     */   {
/* 394 */     return trim_frame(arb, b_start, null);
/*     */   }
/*     */ 
/*     */   public static byte[] trim_frame(byte[] arb, byte b_start, Object[] retb)
/*     */   {
/* 399 */     int last_index = -1;
/* 400 */     int prev_index = -1;
/* 401 */     for (int i = 0; i < arb.length; i++)
/*     */     {
/* 403 */       if (b_start != arb[i])
/*     */         continue;
/* 405 */       prev_index = last_index;
/* 406 */       last_index = i;
/*     */     }
/*     */ 
/* 409 */     if (-1 == last_index)
/* 410 */       return null;
/*     */     byte[] ret;
/* 412 */     if (0 != last_index)
/*     */     {
/* 414 */       ret = new byte[arb.length - last_index];
/* 415 */       System.arraycopy(arb, last_index, ret, 0, arb.length - last_index);
/*     */     }
/*     */     else {
/* 418 */       ret = arb;
/*     */     }
/* 420 */     if (null != retb)
/* 421 */       retb[0] = new Boolean((prev_index != -1) && (1 == prev_index - last_index));
/* 422 */     return ret;
/*     */   }
/*     */ 
/*     */   public static short extract_checksum(byte[] ar, byte[] ETX)
/*     */     throws Exception
/*     */   {
/* 433 */     return (short)(int)extract_long(ar, ar.length - ETX.length - 2, 2);
/*     */   }
/*     */ 
/*     */   public static long extract_long(byte[] ar, int index, int len)
/*     */   {
/* 454 */     byte b = ar[(index++)];
/* 455 */     long ret = 0L;
/* 456 */     long lsign = is_negative(b) ? 128L : 0L;
/* 457 */     ret = b & 0x7F;
/* 458 */     len--;
/*     */     do
/*     */     {
/* 461 */       ret <<= 8;
/* 462 */       lsign <<= 8;
/* 463 */       ret |= byte_to_unsign_int(ar[(index++)]);
/* 464 */       len--; } while (len > 0);
/* 465 */     ret |= lsign;
/* 466 */     return ret;
/*     */   }
/*     */ 
/*     */   public static short extract_short(byte[] ar, int index, int len) {
/* 470 */     short sh = (short)ar[(index++)];
/* 471 */     sh = (short)(sh << 8);
/* 472 */     sh = (short)(sh | ar[(index++)]);
/* 473 */     return sh;
/*     */   }
/*     */ 
/*     */   public static long extract_unsigned_long(byte[] ar, int index, int len)
/*     */   {
/* 509 */     long ret = 0L;
/*     */     do
/*     */     {
/* 512 */       ret <<= 8;
/* 513 */       ret |= byte_to_unsign_int(ar[(index++)]);
/* 514 */       len--; } while (len > 0);
/* 515 */     return ret;
/*     */   }
/*     */ 
/*     */   public static int extract_bits(byte b, byte mask)
/*     */   {
/* 533 */     int v = byte_to_unsign_int(b);
/* 534 */     v &= mask;
/* 535 */     int mm = byte_to_unsign_int(mask);
/* 536 */     return Integer.rotateRight(v, Integer.numberOfTrailingZeros(mm));
/*     */   }
/*     */ 
/*     */   public static int byte_to_unsign_int(byte msb)
/*     */   {
/* 546 */     int v = msb & 0x7F;
/* 547 */     v |= (msb < 0 ? 128 : 0);
/* 548 */     return v;
/*     */   }
/*     */ 
/*     */   public static int byte_to_unsign_int_old(byte msb) {
/* 552 */     boolean m_pos = (msb & 0x80) == 0;
/* 553 */     int v = msb & 0x7F;
/* 554 */     v |= (m_pos ? 0 : 128);
/* 555 */     return v;
/*     */   }
/*     */ 
/*     */   public static boolean is_negative(byte b)
/*     */   {
/* 566 */     return (b & 0x80) != 0;
/*     */   }
/*     */ 
/*     */   public static Object build_object(Class clazz, String cl_name)
/*     */   {
/*     */     try
/*     */     {
/* 577 */       ClassLoader cld = clazz.getClassLoader();
/* 578 */       return cld.loadClass(cl_name).newInstance();
/*     */     }
/*     */     catch (Exception e1) {
/* 581 */       Log.log(0, "Error retrieving object by Class&name (" + cl_name + ")\n " + e1);
/* 582 */     }return null;
/*     */   }
/*     */ 
/*     */   public static Class build_class(String cl_name)
/*     */   {
/*     */     try
/*     */     {
/* 592 */       return Class.forName(cl_name);
/*     */     }
/*     */     catch (Exception e1)
/*     */     {
/* 596 */       Log.log(0, "Error retrieving class by name (" + cl_name + ")\n " + e1);
/* 597 */     }return null;
/*     */   }
/*     */ 
/*     */   public static String byte_to_string(byte[] ar)
/*     */   {
/* 637 */     StringBuilder sb = new StringBuilder();
/* 638 */     for (int i = 0; i < ar.length; i++)
/* 639 */       sb.append((char)ar[i]);
/* 640 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String sexag_to_dec(String sexag)
/*     */   {
/*     */     try
/*     */     {
/* 658 */       double db = Double.valueOf(sexag).doubleValue() / 100.0D;
/* 659 */       double intpart = Math.floor(db);
/*     */ 
/* 662 */       double res = intpart + (db - intpart) / 0.6D;
/* 663 */       res *= 10000000.0D;
/* 664 */       res = Math.round(res);
/* 665 */       res /= 10000000.0D;
/*     */ 
/* 667 */       return decimal_format_7.format(res);
/*     */     } catch (Exception e) {
/* 669 */       Log.log(1, "sexag_to_dec " + e);
/* 670 */     }return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 186 */     for (int i = 0; i < month_name.length; i++)
/* 187 */       hmonth.put(month_name[i], month_id[i]);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Utils
 * JD-Core Version:    0.6.0
 */