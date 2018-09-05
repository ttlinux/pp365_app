package org.sex.hanker.ProxyURL;

import org.sex.hanker.Utils.LogTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Model for Http GET request.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class GetRequest {

    private static final Pattern RANGE_HEADER_PATTERN = Pattern.compile("[R,r]ange:[ ]?bytes=(\\d*)-");
    private static final Pattern URL_PATTERN = Pattern.compile("GET /(.*) HTTP");

    public final String uri;
    public static String RequestUrl;
    public final long rangeOffset;
    public final boolean partial;//true 为断点续传
    public String FileName;
    public String FileSuffix;
    private long ContentLength;


    public long getContentLength() {
        return ContentLength;
    }

    public void setContentLength(long contentLength) {
        ContentLength = contentLength;
    }

    public static String getRequestUrl() {
        return RequestUrl;
    }

    public static void setRequestUrl(String requestUrl) {
        RequestUrl = requestUrl;
    }

    public GetRequest(String request) {
        
        long offset = findRangeOffset(request);
        this.rangeOffset = Math.max(0, offset);
        this.partial = offset >= 0;
        this.uri = findUri(request);
        this.FileName=uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
        this.FileSuffix=uri.substring(uri.lastIndexOf(".")+1,uri.length());
    }

    public static GetRequest read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder stringRequest = new StringBuilder();
        String line;
        while ((line = reader.readLine()).length()>0) { // until new line (headers ending)
            stringRequest.append(line).append('\n');
        }
        LogTools.e("RequestDetail",stringRequest.toString());
        return new GetRequest(stringRequest.toString());
    }

    private long findRangeOffset(String request) {
        Matcher matcher = RANGE_HEADER_PATTERN.matcher(request);
        if (matcher.find()) {
            String rangeValue = matcher.group(1);
            return Long.parseLong(rangeValue);
        }
        return -1;
    }

    private String findUri(String request) {
        Matcher matcher = URL_PATTERN.matcher(request);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid request `" + request + "`: url not found!");
    }

    @Override
    public String toString() {
        return "GetRequest{" +
                "rangeOffset=" + rangeOffset +
                ", partial=" + partial +
                ", uri='" + uri + '\'' +
                '}';
    }
}
