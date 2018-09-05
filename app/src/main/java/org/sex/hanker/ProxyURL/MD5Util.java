package org.sex.hanker.ProxyURL;

import java.security.MessageDigest;

public class MD5Util {
	
	public static void main(String[] args) {
		String abc = "wangdachui123";
		String str;
		try {
			str = MD5Util.GetMD5(abc);
			System.out.println(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * MD5签名
     * 
     * @param paramSrc
     *            the source to be signed
     * @return
     */
    public static String sign(String paramSrc,String key) {
        String origin = paramSrc +key;
        String sign = "";
        try {
        	sign =  GetMD5(origin);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return sign;
    }
	
	public static String GetMD5(String str) throws Exception{
		MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = str.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
	}
}
