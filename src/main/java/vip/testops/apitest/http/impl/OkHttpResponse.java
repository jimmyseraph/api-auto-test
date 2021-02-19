package vip.testops.apitest.http.impl;

import com.google.gson.Gson;
import okhttp3.Response;
import vip.testops.apitest.http.EasyResponse;

import java.io.IOException;
import java.util.Objects;

public class OkHttpResponse implements EasyResponse {
    private Response response;
    private String bodyString;
    private String url;

    OkHttpResponse(String url, Response response) throws IOException {
        this.response = response;
        this.bodyString = Objects.requireNonNull(response.body()).string(); // save the body string as this will be cleared when string method is called in OkHttp
        this.url = url;
    }

    @Override
    public String getBody() {
        return this.bodyString;
    }

    @Override
    public <T> T getBody(Class<T> bodyType) {
        Gson gson = new Gson();
        return gson.fromJson(this.bodyString, bodyType);
    }

    @Override
    public String getHeader(String headerName) {
        return response.header(headerName);
    }

    @Override
    public String getCookie(String cookName) {
        return response.header(cookName);
    }

    @Override
    public int getCode() {
        return response.code();
    }

    @Override
    public String toString() {
        return "OkHttpResponse{" +
                "head=" + response.headers() +
                ", bodyString='" + bodyString + '\'' +
                ", code='" + getCode() + '\'' +
                '}';
    }
}

