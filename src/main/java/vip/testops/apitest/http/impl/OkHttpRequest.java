package vip.testops.apitest.http.impl;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.testops.apitest.http.EasyRequest;
import vip.testops.apitest.http.EasyResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OkHttpRequest  implements EasyRequest {
    private final Logger logger = LoggerFactory.getLogger(OkHttpRequest.class);

    private final OkHttpClient newClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    private String url;
    private String method;
    private HashMap<String, String> headers;
    private HashMap<String, String> cookies;
    private HashMap<String, String> requestParam;
    private RequestBody requestBody;

    public OkHttpRequest(){
        headers = new HashMap<>();
        cookies = new HashMap<>();
        requestParam = new HashMap<>();
        this.method = "GET";
        requestBody = null;
    }

    @Override
    public EasyRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public EasyRequest addQueryParam(String paramKey, String paramValue) {
        this.requestParam.put(paramKey, paramValue);
        return this;
    }

    @Override
    public String getQueryParam(String paramKey) {
        return this.requestParam.get(paramKey);
    }

    @Override
    public EasyRequest setRequestParam(HashMap<String, String> paramMap) {
        this.requestParam = paramMap;
        return this;
    }

    @Override
    public EasyRequest addHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
        return this;
    }

    @Override
    public EasyRequest setHeader(HashMap<String, String> headerMap) {
        this.headers = headerMap;
        return this;
    }

    @Override
    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    @Override
    public EasyRequest addCookie(String cookName, String cookValue) {
        this.cookies.put(cookName, cookValue);
        return this;
    }

    @Override
    public EasyRequest setCookie(HashMap<String, String> cookieMap) {
        this.cookies = cookieMap;
        return this;
    }

    @Override
    public EasyRequest setBody(String mimeType, String content) {
        MediaType mediaType = MediaType.parse(mimeType);
        this.requestBody = RequestBody.create(content, mediaType);
        logger.info("response body：{}", content);
        return this;
    }

    @Override
    public EasyRequest setMethod(String method) {
        this.method = method.toUpperCase();
        return this;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public EasyResponse execute() throws IOException {
        Request.Builder newBuilder = new Request.Builder();
        //设置url
        this.url = expandUrl(url);
        logger.info("target url -> {}：", url);
        newBuilder.url(url);
        //设置headers
        if (headers != null){
            headers.forEach(newBuilder::addHeader);
        }
        //设置cookie
        if (cookies != null){
            StringBuffer cookieString = new StringBuffer();
            cookies.forEach((k,v) ->cookieString
                    .append(k).append("=").append(v).append(";"));
            newBuilder.addHeader("Cookie",cookieString.toString());
        }
        // 设置方法与请求体
        newBuilder.method(this.method, this.requestBody);
        Request request = newBuilder.build();
        Response response = newClient.newCall(request).execute();
        EasyResponse easyResponse = new OkHttpResponse(this.url, response);
        logger.info("Get response: {}", easyResponse);
        return easyResponse;
    }


    //私有方法
    private String expandUrl(String url) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (requestParam != null) {
            logger.info("Query params：{}", requestParam);
            requestParam.forEach(urlBuilder::setQueryParameter);
        }
        return urlBuilder.build().toString();
    }

}

