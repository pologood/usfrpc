/*     */ package com.huawei.csc.usf.framework.sr.route;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RouteRuleUtils
/*     */ {
/*  39 */   private static Pattern ROUTE_PATTERN = Pattern.compile("([&!=,]*)\\s*([^&!=,\\s]+)");
/*     */   
/*     */ 
/*  42 */   private static Pattern CONDITION_SEPERATOR = Pattern.compile("(.*)=>(.*)");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RouteRule parse(String rule)
/*     */     throws ParseException
/*     */   {
/* 142 */     if (StringUtils.isBlank(rule))
/*     */     {
/* 144 */       throw new ParseException("Illegal blank route rule.", 0);
/*     */     }
/* 146 */     Matcher matcher = CONDITION_SEPERATOR.matcher(rule);
/* 147 */     if (!matcher.matches())
/*     */     {
/* 149 */       throw new ParseException("condition seperator => not found!", 0);
/*     */     }
/* 151 */     return parse(matcher.group(1), matcher.group(2));
/*     */   }
/*     */   
/*     */   public static RouteRule parse(String whenRule, String thenRule)
/*     */     throws ParseException
/*     */   {
/* 157 */     if ((StringUtils.isBlank(whenRule)) && (StringUtils.isBlank(thenRule)))
/*     */     {
/* 159 */       throw new ParseException("Illegal route rule without when and then express.", 0);
/*     */     }
/*     */     
/* 162 */     Map<String, MatchPair> when = parseRule(whenRule.trim());
/* 163 */     Map<String, MatchPair> then = parseRule(thenRule.trim());
/*     */     
/* 165 */     return new RouteRule(when, then);
/*     */   }
/*     */   
/*     */   public static Map<String, MatchPair> parseRule(String rule)
/*     */     throws ParseException
/*     */   {
/* 171 */     Map<String, MatchPair> condition = new HashMap();
/* 172 */     if (StringUtils.isBlank(rule))
/*     */     {
/* 174 */       return condition;
/*     */     }
/*     */     
/* 177 */     MatchPair pair = null;
/*     */     
/* 179 */     Set<String> values = null;
/* 180 */     Matcher matcher = ROUTE_PATTERN.matcher(rule);
/* 181 */     while (matcher.find())
/*     */     {
/*     */ 
/* 184 */       String separator = matcher.group(1);
/* 185 */       String content = matcher.group(2);
/*     */       
/* 187 */       if ((separator == null) || (separator.length() == 0))
/*     */       {
/* 189 */         pair = createMatchPair(content);
/* 190 */         condition.put(content, pair);
/*     */ 
/*     */       }
/* 193 */       else if ("&".equals(separator))
/*     */       {
/* 195 */         if (condition.get(content) == null)
/*     */         {
/* 197 */           pair = createMatchPair(content);
/* 198 */           condition.put(content, pair);
/*     */         }
/*     */         else
/*     */         {
/* 202 */           condition.put(content, pair);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 207 */       else if ("=".equals(separator))
/*     */       {
/* 209 */         if (pair == null) {
/* 210 */           throw new ParseException("Illegal route rule \"" + rule + "\", The error char '" + separator + "' at index " + matcher.start() + " before \"" + content + "\".", matcher.start());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 215 */         values = pair.getMatches();
/* 216 */         values.add(content);
/*     */ 
/*     */       }
/* 219 */       else if ("!=".equals(separator))
/*     */       {
/* 221 */         if (pair == null) {
/* 222 */           throw new ParseException("Illegal route rule \"" + rule + "\", The error char '" + separator + "' at index " + matcher.start() + " before \"" + content + "\".", matcher.start());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 227 */         values = pair.getUnmatches();
/* 228 */         values.add(content);
/*     */ 
/*     */       }
/* 231 */       else if (",".equals(separator))
/*     */       {
/* 233 */         if ((values == null) || (values.size() == 0)) {
/* 234 */           throw new ParseException("Illegal route rule \"" + rule + "\", The error char '" + separator + "' at index " + matcher.start() + " before \"" + content + "\".", matcher.start());
/*     */         }
/*     */         
/*     */ 
/* 238 */         values.add(content);
/*     */       }
/*     */       else
/*     */       {
/* 242 */         throw new ParseException("Illegal route rule \"" + rule + "\", The error char '" + separator + "' at index " + matcher.start() + " before \"" + content + "\".", matcher.start());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 248 */     return condition;
/*     */   }
/*     */   
/*     */ 
/*     */   private static MatchPair createMatchPair(String content)
/*     */   {
/* 254 */     if (StringUtils.equals(content, "host"))
/*     */     {
/* 256 */       return new HostMatchPair();
/*     */     }
/* 258 */     return new MatchPair();
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean matchRuleWhenCondition(Map<String, String> sample, RouteRule routeRule)
/*     */   {
/* 264 */     Map<String, MatchPair> whenCondition = routeRule.getWhenCondition();
/* 265 */     if (whenCondition == null)
/*     */     {
/* 267 */       return true;
/*     */     }
/* 269 */     boolean matchCondition = matchCondition(sample, whenCondition);
/* 270 */     return matchCondition;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean matchRuleThenCondition(Map<String, String> sample, RouteRule routeRule)
/*     */   {
/* 276 */     Map<String, MatchPair> thenCondition = routeRule.getThenCondition();
/* 277 */     if (thenCondition == null)
/*     */     {
/* 279 */       return true;
/*     */     }
/*     */     
/* 282 */     boolean matchCondition = matchCondition(sample, thenCondition);
/* 283 */     return matchCondition;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean matchCondition(Map<String, String> sample, Map<String, MatchPair> condition)
/*     */   {
/* 289 */     for (Map.Entry<String, String> entry : sample.entrySet())
/*     */     {
/* 291 */       String key = (String)entry.getKey();
/*     */       
/* 293 */       MatchPair pair = (MatchPair)condition.get(key);
/* 294 */       if ((pair != null) && (!pair.pass((String)entry.getValue())))
/*     */       {
/* 296 */         return false;
/*     */       }
/*     */     }
/* 299 */     return true;
/*     */   }
/*     */ }


/* Location:              D:\com.huawei.csc.usf.framework-3.5.20.SPC001.jar!\com\huawei\csc\usf\framework\sr\route\RouteRuleUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */