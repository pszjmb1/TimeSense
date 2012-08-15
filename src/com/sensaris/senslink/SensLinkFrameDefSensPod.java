/*     */ package com.sensaris.senslink;
/*     */ 
/*     */ import com.sensaris.common.frame.Frame;
/*     */ import com.sensaris.common.frame.Sensor;
/*     */ 
/*     */ public class SensLinkFrameDefSensPod extends SensLinkFrameDef
/*     */ {
/*     */   public static final String header_Sensaris_DATA = "$PSEN";
/*     */   public static final String header_Sensaris_INFO = "$INFO";
/*     */   public static final String header_Sensaris_GPRMC = "$GPRMC";
/*     */   public static final String header_Sensaris_TIME = "#";
/*     */   public static final String TYPE_UNKNOWN = "TYPE_UNKNOWN";
/*     */   public static final String TYPE_INFO = "TYPE_INFO";
/*     */   public static final String HUM_FRAME_TYPE = "Hum";
/*     */   public static final String COX_FRAME_TYPE = "COx";
/*     */   public static final String NOX_FRAME_TYPE = "NOx";
/*     */   public static final String NOISE_FRAME_TYPE = "Noise";
/*     */   public static final String BATT_FRAME_TYPE = "Batt";
/*     */   public static final String GPS_FRAME_TYPE = "GPS";
/*     */   public static final String RTC_FRAME_TYPE = "RTC";
/*     */   public static final String CO2_FRAME_TYPE = "CO2";
/*     */   public static final String RAD5_FRAME_TYPE = "Rad5";
/*     */   public static final String RAD60_FRAME_TYPE = "Rad60";
/*     */   public static final String SIEVERT_FRAME_TYPE = "uSv";
/*     */   public static final String UV_FRAME_TYPE = "UV";
/*     */   public static final String O3_FRAME_TYPE = "O3";
/*     */   public static final String SENSOR_HUMIDITY = "H";
/*     */   public static final String SENSOR_HUMIDITY_UNIT = "%";
/*     */   public static final int SENSOR_HUM_DECIMAL = 1;
/*     */   public static final String DATA_VALUE_HUM = "hum";
/*     */   public static final String SENSOR_TEMPERATURE = "T";
/*     */   public static final String SENSOR_TEMPERATURE_UNIT = "°C";
/*     */   public static final int SENSOR_TEMP_DECIMAL = 1;
/*     */   public static final String DATA_VALUE_TEMP = "temp";
/*     */   public static final String SENSOR_COX = "V";
/*     */   public static final String SENSOR_COX_UNIT = "ppm";
/*     */   public static final int SENSOR_COX_DECIMAL = 2;
/*     */   public static final String DATA_VALUE_COX = "cox";
/*     */   public static final String SENSOR_NOISE = "dB";
/*     */   public static final String SENSOR_NOISE_UNIT = "dB";
/*     */   public static final int SENSOR_NOISE_DECIMAL = 0;
/*     */   public static final String DATA_VALUE_NOISE = "noise";
/*     */   public static final String SENSOR_NOX = "V";
/*     */   public static final String SENSOR_NOX_UNIT = "ppb";
/*     */   public static final int SENSOR_NOX_DECIMAL = 2;
/*     */   public static final String DATA_VALUE_NOX = "nox";
/*     */   public static final String SENSOR_BATT = "V";
/*     */   public static final String SENSOR_BATT_UNIT = "V";
/*     */   public static final int SENSOR_BATT_DECIMAL = 2;
/*     */   public static final String DATA_VALUE_BATT = "batt";
/*     */   public static final String SENSOR_GPS_POINT = "point";
/*     */   public static final String SENSOR_GPS_STATUS = "status";
/*     */   public static final String DATA_VALUE_GPS = "gps";
/*     */   public static final String SENSOR_GPS_STATUS_ON = "A";
/*     */   public static final String SENSOR_GPS_STATUS_OFF = "V";
/*     */   public static final String SENSOR_RAD5 = "c";
/*     */   public static final String SENSOR_RAD5_UNIT = "c";
/*     */   public static final int SENSOR_RAD5_DECIMAL = 0;
/*     */   public static final String DATA_VALUE_RAD5 = "rad5";
/*     */   public static final String SENSOR_RAD60 = "cpm";
/*     */   public static final String SENSOR_RAD60_UNIT = "cpm";
/*     */   public static final int SENSOR_RAD60_DECIMAL = 0;
/*     */   public static final String DATA_VALUE_RAD60 = "rad60";
/*     */   public static final String SENSOR_SIEVERT = "µSv/h";
/*     */   public static final String SENSOR_SIEVERT_UNIT = "µSv/h";
/*     */   public static final int SENSOR_SIEVERT_DECIMAL = 0;
/*     */   public static final String DATA_VALUE_SIEVERT = "sievert";
/*     */   public static final String SENSOR_CO2 = "ppm";
/*     */   public static final String SENSOR_CO2_UNIT = "ppm";
/*     */   public static final int SENSOR_CO2_DECIMAL = 0;
/*     */   public static final String DATA_VALUE_CO2 = "co2";
/*     */   public static final String SENSOR_UV = "V";
/*     */   public static final String SENSOR_UV_UNIT = "µW/m2";
/*     */   public static final int SENSOR_UV_DECIMAL = 3;
/*     */   public static final String DATA_VALUE_UV = "uv";
/*     */   public static final String SENSOR_O3 = "V";
/*     */   public static final String SENSOR_O3_UNIT = "ppb";
/*     */   public static final int SENSOR_O3_DECIMAL = 3;
/*     */   public static final String DATA_VALUE_O3 = "o3";
/*     */   public static final String SENSOR_RTC_TIME = "Time";
/*     */   public static final String SENSOR_RTC_DATE = "Date";
/*     */   public static final String SENSOR_RTC_EPOCH = "epoch";
/*     */   public static final String DATA_VALUE_RTC = "rtc";
/*     */   static Frame frame;
/*     */   static Sensor sensor;
/*     */ 
/*     */   public static void init()
/*     */   {
/* 183 */     frame = new Frame("Hum");
/* 184 */     frame.setFrameItemNb(6);
/* 185 */     frame.setFrameLen(25);
/*     */ 
/* 188 */     sensor = new Sensor("Hum", "H");
/* 189 */     sensor.setUnitName("%");
/* 190 */     sensor.setDataType(Float.class);
/* 191 */     sensor.setMaximumRange(new Float(100.0F));
/* 192 */     sensor.setMinimumRange(new Float(0.0F));
/* 193 */     sensor.setResolution(new Float(Math.pow(10.0D, -2.0D)));
/*     */ 
/* 195 */     sensor.setJapetFrameValueKey("H");
/* 196 */     sensor.setDbType("hum");
/*     */ 
/* 198 */     frame.addSensor(sensor);
/*     */ 
/* 200 */     sensor = new Sensor("Hum", "T");
/* 201 */     sensor.setUnitName("°C");
/* 202 */     sensor.setDataType(Float.class);
/* 203 */     sensor.setMaximumRange(new Float(120.0F));
/* 204 */     sensor.setMinimumRange(new Float(-20.0F));
/* 205 */     sensor.setResolution(new Float(Math.pow(10.0D, -2.0D)));
/*     */ 
/* 207 */     sensor.setJapetFrameValueKey("T");
/* 208 */     sensor.setDbType("temp");
/*     */ 
/* 210 */     frame.addSensor(sensor);
/*     */ 
/* 212 */     frame = new Frame("COx");
/* 213 */     frame.setFrameItemNb(4);
/* 214 */     frame.setFrameLen(17);
/*     */ 
/* 216 */     sensor = new Sensor("COx", "V");
/* 217 */     sensor.setUnitName("ppm");
/* 218 */     sensor.setDataType(Float.class);
/* 219 */     sensor.setMaximumRange(new Float(500.0F));
/* 220 */     sensor.setMinimumRange(new Float(0.0F));
/* 221 */     sensor.setResolution(new Float(Math.pow(10.0D, -2.0D)));
/*     */ 
/* 223 */     sensor.setJapetFrameValueKey("V");
/* 224 */     sensor.setDbType("cox");
/*     */ 
/* 226 */     frame.addSensor(sensor);
/*     */ 
/* 228 */     frame = new Frame("Noise");
/* 229 */     frame.setFrameItemNb(4);
/* 230 */     frame.setFrameLen(18);
/*     */ 
/* 232 */     sensor = new Sensor("Noise", "V");
/* 233 */     sensor.setUnitName("dB");
/* 234 */     sensor.setDataType(Integer.class);
/* 235 */     sensor.setMaximumRange(new Integer(140));
/* 236 */     sensor.setMinimumRange(new Integer(0));
/* 237 */     sensor.setResolution(new Float(Math.pow(10.0D, 0.0D)));
/*     */ 
/* 239 */     sensor.setJapetFrameValueKey("dB");
/* 240 */     sensor.setDbType("noise");
/*     */ 
/* 242 */     frame.addSensor(sensor);
/*     */ 
/* 244 */     frame = new Frame("NOx");
/* 245 */     frame.setFrameItemNb(4);
/* 246 */     frame.setFrameLen(17);
/*     */ 
/* 248 */     sensor = new Sensor("NOx", "V");
/* 249 */     sensor.setUnitName("dB");
/* 250 */     sensor.setDataType(Float.class);
/* 251 */     sensor.setMaximumRange(new Float(500.0F));
/* 252 */     sensor.setMinimumRange(new Float(0.0F));
/* 253 */     sensor.setResolution(new Float(Math.pow(10.0D, -2.0D)));
/*     */ 
/* 255 */     sensor.setJapetFrameValueKey("V");
/* 256 */     sensor.setDbType("nox");
/*     */ 
/* 258 */     frame.addSensor(sensor);
/*     */ 
/* 261 */     frame = new Frame("Batt");
/* 262 */     frame.setFrameItemNb(4);
/* 263 */     frame.setFrameLen(17);
/*     */ 
/* 265 */     sensor = new Sensor("Batt", "V");
/* 266 */     sensor.setUnitName("V");
/* 267 */     sensor.setDataType(Float.class);
/* 268 */     sensor.setMaximumRange(new Float(4.5D));
/* 269 */     sensor.setMinimumRange(new Float(3.0D));
/* 270 */     sensor.setResolution(new Float(Math.pow(10.0D, -1.0D)));
/*     */ 
/* 272 */     sensor.setJapetFrameValueKey("V");
/* 273 */     sensor.setDbType("batt");
/*     */ 
/* 275 */     frame.addSensor(sensor);
/*     */ 
/* 277 */     frame = new Frame("RTC");
/* 278 */     frame.setFrameItemNb(6);
/* 279 */     frame.setFrameLen(33);
/*     */ 
/* 282 */     sensor = new Sensor("RTC", "rtc");
/* 283 */     sensor.setUnitName("epoch");
/* 284 */     sensor.setDataType(String.class);
/* 285 */     sensor.setMaximumRange("");
/* 286 */     sensor.setMinimumRange("");
/* 287 */     sensor.setResolution(new Float(Math.pow(10.0D, 0.0D)));
/*     */ 
/* 290 */     sensor.setJapetFrameValueKey("Date;Time");
/* 291 */     sensor.setDbType("rtc");
/*     */ 
/* 293 */     frame.addSensor(sensor);
/*     */ 
/* 296 */     frame = new Frame("UV");
/* 297 */     frame.setFrameItemNb(4);
/* 298 */     frame.setFrameLen(16);
/*     */ 
/* 300 */     sensor = new Sensor("UV", "V");
/* 301 */     sensor.setUnitName("µW/m2");
/* 302 */     sensor.setDataType(Float.class);
/* 303 */     sensor.setMaximumRange(new Float(4.5D));
/* 304 */     sensor.setMinimumRange(new Float(0.0D));
/* 305 */     sensor.setResolution(new Float(Math.pow(10.0D, -3.0D)));
/*     */ 
/* 307 */     sensor.setJapetFrameValueKey("V");
/* 308 */     sensor.setDbType("uv");
/*     */ 
/* 310 */     frame.addSensor(sensor);
/*     */ 
/* 312 */     frame = new Frame("O3");
/* 313 */     frame.setFrameItemNb(4);
/* 314 */     frame.setFrameLen(16);
/*     */ 
/* 316 */     sensor = new Sensor("O3", "V");
/* 317 */     sensor.setUnitName("ppb");
/* 318 */     sensor.setDataType(Float.class);
/* 319 */     sensor.setMaximumRange(new Float(4.5D));
/* 320 */     sensor.setMinimumRange(new Float(0.0D));
/* 321 */     sensor.setResolution(new Float(Math.pow(10.0D, -3.0D)));
/*     */ 
/* 323 */     sensor.setJapetFrameValueKey("V");
/* 324 */     sensor.setDbType("o3");
/*     */ 
/* 326 */     frame.addSensor(sensor);
/*     */ 
/* 328 */     frame = new Frame("Rad5");
/* 329 */     frame.setFrameItemNb(4);
/* 330 */     frame.setFrameLen(18);
/*     */ 
/* 332 */     sensor = new Sensor("Rad5", "cp");
/* 333 */     sensor.setUnitName("c");
/* 334 */     sensor.setDataType(Float.class);
/* 335 */     sensor.setMaximumRange(new Float(1000.0F));
/* 336 */     sensor.setMinimumRange(new Float(0.0D));
/* 337 */     sensor.setResolution(new Float(Math.pow(10.0D, 0.0D)));
/*     */ 
/* 339 */     sensor.setJapetFrameValueKey("cp");
/* 340 */     sensor.setDbType("rad5");
/*     */ 
/* 342 */     frame.addSensor(sensor);
/*     */ 
/* 344 */     frame = new Frame("Rad60");
/* 345 */     frame.setFrameItemNb(4);
/* 346 */     frame.setFrameLen(30);
/*     */ 
/* 348 */     sensor = new Sensor("Rad60", "cpm");
/* 349 */     sensor.setUnitName("cpm");
/* 350 */     sensor.setDataType(Float.class);
/* 351 */     sensor.setMaximumRange(new Float(1000.0F));
/* 352 */     sensor.setMinimumRange(new Float(0.0D));
/* 353 */     sensor.setResolution(new Float(Math.pow(10.0D, 0.0D)));
/*     */ 
/* 355 */     sensor.setJapetFrameValueKey("cpm");
/* 356 */     sensor.setDbType("rad60");
/*     */ 
/* 358 */     frame.addSensor(sensor);
/*     */ 
/* 360 */     frame = new Frame("uSv");
/* 361 */     frame.setFrameItemNb(4);
/* 362 */     frame.setFrameLen(20);
/*     */ 
/* 364 */     sensor = new Sensor("uSv", "uSv/h");
/* 365 */     sensor.setUnitName("µSv/h");
/* 366 */     sensor.setDataType(Float.class);
/* 367 */     sensor.setMaximumRange(new Float(1000.0F));
/* 368 */     sensor.setMinimumRange(new Float(0.0D));
/* 369 */     sensor.setResolution(new Float(Math.pow(10.0D, -2.0D)));
/*     */ 
/* 371 */     sensor.setJapetFrameValueKey("uSv/h");
/* 372 */     sensor.setDbType("sievert");
/*     */ 
/* 374 */     frame.addSensor(sensor);
/*     */ 
/* 376 */     frame = new Frame("CO2");
/* 377 */     frame.setFrameItemNb(4);
/* 378 */     frame.setFrameLen(18);
/*     */ 
/* 380 */     sensor = new Sensor("CO2", "ppm");
/* 381 */     sensor.setUnitName("ppm");
/* 382 */     sensor.setDataType(Float.class);
/* 383 */     sensor.setMaximumRange(new Float(2000.0F));
/* 384 */     sensor.setMinimumRange(new Float(0.0D));
/* 385 */     sensor.setResolution(new Float(Math.pow(10.0D, 0.0D)));
/*     */ 
/* 387 */     sensor.setJapetFrameValueKey("ppm");
/* 388 */     sensor.setDbType("co2");
/*     */ 
/* 390 */     frame.addSensor(sensor);
/*     */ 
/* 392 */     frame = new Frame("GPS");
/* 393 */     frame.setFrameItemNb(6);
/* 394 */     frame.setFrameLen(33);
/*     */ 
/* 396 */     sensor = new Sensor("GPS", "point");
/* 397 */     sensor.setUnitName("point");
/* 398 */     sensor.setDataType(String.class);
/* 399 */     sensor.setMaximumRange("");
/* 400 */     sensor.setMinimumRange("");
/* 401 */     sensor.setResolution(new Float(Math.pow(10.0D, 0.0D)));
/*     */ 
/* 403 */     sensor.setJapetFrameValueKey("point");
/* 404 */     sensor.setDbType("gps");
/*     */ 
/* 406 */     frame.addSensor(sensor);
/*     */   }
/*     */ }

/* Location:           /home/martin/sensdecomp/
 * Qualified Name:     com.sensaris.senslink.SensLinkFrameDefSensPod
 * JD-Core Version:    0.6.0
 */