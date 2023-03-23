package magdalenaramirez.ioc.movemore;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.stream.Collectors;


public class Util {
    public static String getParamsString(Map<String, String> params)  {
        StringBuilder result = new StringBuilder();

        result.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        String resultString = result.toString();
        return resultString.length() > 1
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
/*
public class Util {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        if(params.isEmpty()) return "";//Devuelve nada si esta vacio
        //Devuelve el parametro en un string
        return "?" + params.entrySet().stream()
                .map(entry -> {
                    try {
                        return URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining("&"));
    }

}
*/
