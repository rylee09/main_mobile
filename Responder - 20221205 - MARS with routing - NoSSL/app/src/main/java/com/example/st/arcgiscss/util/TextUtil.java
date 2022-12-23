package com.example.st.arcgiscss.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class TextUtil {
    /**
     */
    public static String transMapToString(Map map){
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (java.util.Map.Entry)iterator.next();
            sb.append(entry.getKey().toString()).append( "'" ).append(null==entry.getValue()?"":
                    entry.getValue().toString()).append(iterator.hasNext() ? "^" : "");


        }
        return sb.toString();
    }


    /**
     */
    public static Map transStringToMap(String mapString){
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : ""))
            items = new StringTokenizer(entrys.nextToken(), "'");
        return map;
    }
}

