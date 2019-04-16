package tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

class StringUtil {

    public static String BlobToString(Blob blob) throws SQLException, IOException {

        String reString;
        InputStream is = blob.getBinaryStream();
        ByteArrayInputStream bais = (ByteArrayInputStream) is;
        byte[] byte_data = new byte[bais.available()]; //bais.available()返回此输入流的字节数
        bais.read(byte_data, 0, byte_data.length); //将输入流中的内容读到指定的数组
        reString = new String(byte_data, StandardCharsets.UTF_8); //再转为String，并使用指定的编码方式
        is.close();

        return reString;
    }


    public static String toURLEncoded(String paramString) {
        String str=paramString;
        if(paramString==null||paramString.equals("")){ return "";}
        try {
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
