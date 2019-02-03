package com.helloingob.gifter.data.connection;

import java.net.URI;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

//stackoverflow.com/questions/3272681/httpurlconnection-thread-safety
public class HTTPConnection {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);

    private HttpClient httpClient;
    private RequestConfig requestConfig;
    private HttpClientContext httpClientContext;
    private String userAgent;

    public HTTPConnection(String userAgent) {
        this.userAgent = userAgent;
        httpClient = HttpClientBuilder.create().build();
        httpClientContext = HttpClientContext.create();
        requestConfig = RequestConfig.custom().setSocketTimeout(CentralSettings.General.TIMEOUT).setConnectTimeout(CentralSettings.General.TIMEOUT).setConnectionRequestTimeout(CentralSettings.General.TIMEOUT).build();
    }

    public HttpResponse doGet(String url) {
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        if (userAgent != null) {
            request.setHeader("User-Agent", userAgent);
        }
        try {
            return httpClient.execute(request);
        } catch (Exception e) {
            new ErrorLogHandler().error(e.toString(), "'" + url + "'", logger);
        }
        return null;
    }

    public HttpResponse doGet(String url, String PHPSESSID) {
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        if (userAgent != null) {
            request.setHeader("User-Agent", userAgent);
        }
        request.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
        try {
            return httpClient.execute(request);
        } catch (Exception e) {
            new ErrorLogHandler().error(e.toString(), "'" + url + "'", logger);
        }
        return null;
    }

    public HttpResponse doLogin(String url, String PHPSESSID) {
        HttpGet request = new HttpGet(url);
        HttpClientContext context = HttpClientContext.create();
        request.setConfig(requestConfig);
        if (userAgent != null) {
            request.setHeader("User-Agent", userAgent);
        }
        request.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
        try {
            HttpResponse httpResponse = httpClient.execute(request, context);
            //Suspension Check
            if (httpResponse != null && context.getRedirectLocations() != null) {
                List<URI> redirectLocations = context.getRedirectLocations();
                for (URI uri : redirectLocations) {
                    if (uri.toString().equals(CentralSettings.Url.SUSPENSION_URL)) {
                        httpResponse.setStatusCode(301);
                        break;
                    }
                }
            }
            return httpResponse;
        } catch (Exception e) {
            new ErrorLogHandler().error(e, "'" + url + "'", logger);
        }
        return null;
    }

    public HttpResponse doPost(String url, List<NameValuePair> nameValuePairs, String PHPSESSID) {
        HttpEntity requestEntity;
        try {
            requestEntity = new UrlEncodedFormEntity(nameValuePairs);
            HttpPost request = new HttpPost(url);
            request.setEntity(requestEntity);
            request.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
            return httpClient.execute(request);
        } catch (Exception e) {
            new ErrorLogHandler().error(e.toString(), "'" + url + "'", logger);
        }
        return null;
    }

    public String getRedirectLocation(String url) {
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        if (userAgent != null) {
            request.setHeader("User-Agent", userAgent);
        }
        try {
            httpClient.execute(request, httpClientContext);
            return httpClientContext.getRedirectLocations().get(1).toString();
        } catch (Exception e) {
            new ErrorLogHandler().error(e.toString(), "'" + url + "'", logger);
        }
        return null;
    }

}
