/*     */ package com.sensaris.common;
/*     */ 
/*     */ public class Formulas
/*     */ {
/* 195 */   private static double aO3 = 0.77D;
/* 196 */   private static double bO3 = 1.367D;
/*     */ 
/* 198 */   private static double aRhO3 = -0.031D;
/* 199 */   private static double bRhO3 = 0.165D;
/*     */ 
/* 201 */   private static double aTO3 = -0.152D;
/* 202 */   private static double bTO3 = 0.3664D;
/*     */ 
/* 204 */   private static double RlO3 = 300000.0D;
/*     */ 
/* 237 */   private static double alphaTNOx = 707.0D;
/* 238 */   private static double betaTNOx = -2.03D;
/*     */ 
/* 240 */   private static double RlNOx = 30000.0D;
/*     */ 
/*     */   public static float transform(int type, float value, float temp, float hum)
/*     */   {
/*  30 */     switch (type) {
/*     */     case 4:
/*  32 */       return value * 10.0F / 3.0F;
/*     */     case 5:
/*  35 */       return -90.0F * value + 327.0F;
/*     */     }
/*     */ 
/*  41 */     return value;
/*     */   }
/*     */ 
/*     */   public static String transform_japet(String type, String value, String temp, String hum)
/*     */   {
/*     */     int decimal;
/*     */     try
/*     */     {
/*  49 */       float fvalue = Float.parseFloat(value);
/*  50 */       float ftemp = (0.0F / 0.0F); float fhum = (0.0F / 0.0F);
/*     */ 
/*  52 */       if (null != temp)
/*  53 */         ftemp = Float.parseFloat(temp);
/*  54 */       if (null != hum)
/*  55 */         ftemp = Float.parseFloat(hum);
/*  57 */       if (type.equalsIgnoreCase("cox")) {
/*  58 */         int itype = 4;
/*  59 */         decimal = 2;
/*     */       }
/*     */       else
/*     */       {
/*  60 */         if (type.equalsIgnoreCase("nox")) {
/*  61 */           int itype = 5;
/*  62 */           decimal = 2;
/*     */         }
/*     */         else
/*     */         {
/*  63 */           if (type.equalsIgnoreCase("hum")) {
/*  64 */             int itype = 1;
/*  65 */             decimal = 1;
/*     */           }
/*     */           else
/*     */           {
/*  66 */             if (type.equalsIgnoreCase("temp")) {
/*  67 */               int itype = 3;
/*  68 */               decimal = 1;
/*     */             }
/*     */             else
/*     */             {
/*  69 */               if (type.equalsIgnoreCase("batt")) {
/*  70 */                 int itype = 100;
/*  71 */                 decimal = 2;
/*     */               }
/*     */               else
/*     */               {
/*  72 */                 if (type.equalsIgnoreCase("noice")) {
/*  73 */                   int itype = 2;
/*  74 */                   decimal = 0;
/*     */                 }
/*     */                 else
/*     */                 {
/*  75 */                   if (type.equalsIgnoreCase("co2")) {
/*  76 */                     int itype = 14;
/*  77 */                     decimal = 0;
/*     */                   }
/*     */                   else
/*     */                   {
/*  78 */                     if (type.equalsIgnoreCase("c")) {
/*  79 */                       int itype = 10;
/*  80 */                       decimal = 0;
/*     */                     }
/*     */                     else
/*     */                     {
/*  81 */                       if (type.equalsIgnoreCase("cpm")) {
/*  82 */                         int itype = 11;
/*  83 */                         decimal = 0;
/*     */                       }
/*     */                       else
/*     */                       {
/*  84 */                         if (type.equalsIgnoreCase("µSv/h"))
/*     */                         {
/*  86 */                           int itype = 12;
/*  87 */                           decimal = 0;
/*     */                         }
/*     */                         else
/*     */                         {
/*  88 */                           if (type.equalsIgnoreCase("V")) {
/*  89 */                             int itype = 15;
/*  90 */                             decimal = 3;
/*     */                           }
/*     */                           else
/*     */                           {
/*  91 */                             if (type.equalsIgnoreCase("V")) {
/*  92 */                               int itype = 16;
/*  93 */                               decimal = 3;
/*     */                             } else {
/*  95 */                               return value;
/*     */                             }
/*     */                           }
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       int itype=0;//JMB made it 0 as it was undefined before.
/*  96 */       return Utils.decimal_number(transform(itype, fvalue, ftemp, fhum), decimal);
/*     */     } catch (Exception e) {
/*     */     }
/*  99 */     return value;
/*     */   }
/*     */ 
/*     */   private static float calc_uv(float value, float temp, float hum)
/*     */   {
/* 105 */     return value * value * -200.0F + 1565.0F * value - 42.0F;
/*     */   }
/*     */ 
/*     */   private static float calc_O3(float value, float temp, float hum)
/*     */   {
/*     */     try
/*     */     {
/* 208 */       double dvalue = value;
/* 209 */       double A2 = aRhO3 * hum + bRhO3;
/* 210 */       double B2 = aTO3 * temp + bTO3;
/* 211 */       double x = Math.log(RlO3 * dvalue / (3.3D - dvalue));
/* 212 */       double y = (x - A2 - B2 - bO3) / aO3;
/* 213 */       return (float)Math.exp(y);
/*     */     } catch (Exception e) {
/* 215 */       e.printStackTrace();
/*     */ 
/* 217 */       Log.log(1, "calc_O3 " + e.toString());
/* 218 */     }return (0.0F / 0.0F);
/*     */   }
/*     */ 
/*     */   private static float calc_NOx(float value, float temp, float hum)
/*     */   {
/*     */     try
/*     */     {
/* 244 */       double dvalue = value;
/* 245 */       double A2 = aRhO3 * hum + bRhO3;
/* 246 */       double B2 = aTO3 * temp + bTO3;
/* 247 */       double x = Math.log(RlO3 * dvalue) / (3.3D - dvalue);
/* 248 */       return (float)Math.exp((x - A2 - B2 - bO3) / aO3);
/*     */     } catch (Exception e) {
/* 250 */       e.printStackTrace();
/*     */ 
/* 252 */       Log.log(1, "calc_O3 " + e.toString());
/* 253 */     }return (0.0F / 0.0F);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.common.Formulas
 * JD-Core Version:    0.6.0
 */