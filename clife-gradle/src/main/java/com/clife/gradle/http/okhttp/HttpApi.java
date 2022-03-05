package com.clife.gradle.http.okhttp;

import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.http.okhttp.okhttp.HttpsUtils;
import com.clife.gradle.util.Logc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.net.ssl.X509TrustManager;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.internal.platform.Platform;

public class HttpApi {
    public static long READ_TIMEOUT = 15 * 1000;
    public static long WRITE_TIMEOUT = 15 * 1000;
    public static long CONNECT_TIMEOUT = 15 * 1000;
    protected InputStream bksFile;
    protected String password;
    protected InputStream[] certificates;

    public void get() throws IOException {
        String ENDPOINT = "https://api.github.com/repos/square/okhttp/contributors";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ENDPOINT)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        String string = body.string();
        Logc.i(string);
    }

    public String post(String username,String password,String url, Map<String, String> parameterMap) {
        List<String> parameterList = new ArrayList<>();
        FormBody.Builder builder = new FormBody.Builder();
        if (parameterMap.size() > 0) {
            parameterMap.keySet().forEach(parameterName -> {
                String value = parameterMap.get(parameterName);
                builder.add(parameterName, value);
                parameterList.add(parameterName + ":" + value);
            });
        }

        FormBody formBody = builder.build();
        OkHttpClient client = newOkHttpBuilder(username,password).build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                String result = response.body().string();
                Logc.i("code:" + result);
                return result;
            } else {
                Logc.i("code:" + httpCode + "," + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String put(String username,String password, String fileContent,String url) {
        //Logc.e("put username:"+username+",password："+password+",url："+url+",fileContent："+fileContent);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, fileContent);
        OkHttpClient client = newOkHttpBuilder(username,password).build();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            ResponseBody responseBody = response.body();
            Logc.e("httpcode:"+httpCode);
            if (httpCode == 200 && responseBody != null) {
                String result = responseBody.string();
                Logc.i("code:" + result);
                return result;
            } else {
//                Logc.i("code:" + httpCode + "," + response.body().string());
                Logc.i("code:" + httpCode + "," + response.message());
                throw new RuntimeException("code:" + httpCode + "," + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String putFile(String username, String password, File file, String url) {
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, file);
        OkHttpClient client = newOkHttpBuilder(username,password).build();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                String result = response.body().string();
                Logc.i("code:" + result);
                return result;
            } else {
                Logc.i("code:" + httpCode + "," + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String post(String url, Map<String, String> parameterMap) {
        List<String> parameterList = new ArrayList<>();
        FormBody.Builder builder = new FormBody.Builder();
        if (parameterMap.size() > 0) {
            parameterMap.keySet().forEach(parameterName -> {
                String value = parameterMap.get(parameterName);
                builder.add(parameterName, value);
                parameterList.add(parameterName + ":" + value);
            });
        }

        FormBody formBody = builder.build();

        OkHttpClient client = newOkHttpBuilder().build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                String result = response.body().string();
                Logc.i("code:" + result);
                return result;
            } else {
                Logc.i("code:" + httpCode + "," + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String put(String url, Map<String, String> parameterMap) {
        //List<String> parameterList = new ArrayList<>();
        FormBody.Builder builder = new FormBody.Builder();
        if (parameterMap.size() > 0) {
            parameterMap.keySet().forEach(parameterName -> {
                String value = parameterMap.get(parameterName);
                builder.add(parameterName, value);
                //parameterList.add(parameterName + ":" + value);
            });
        }

        FormBody formBody = builder.build();

        OkHttpClient client = newOkHttpBuilder().build();

        Request request = new Request.Builder()
                .url(url)
                .put(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                String result = response.body().string();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String get(String url) {
        OkHttpClient.Builder builder = newOkHttpBuilder();
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();

            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                ResponseBody body = response.body();
                String string = body.string();
                //System.out.println(string);
                return string;
            } else {
                //ResponseBody re = response.body();
                //System.err.println(re.string());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String username,String password,String url) {
        //System.out.println("username:"+username+",password:"+password+",url:"+url);
        System.out.println("url:"+url);
        OkHttpClient.Builder builder = newOkHttpBuilder(username,password);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();

            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                ResponseBody body = response.body();
                String string = body.string();
                //System.out.println(string);
                return string;
            } else {
                //ResponseBody re = response.body();
                //System.err.println(re.string());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getBytes(String username,String password,String url) {
        OkHttpClient.Builder builder = newOkHttpBuilder(username,password);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();

            int httpCode = response.code();
            if (httpCode == 200 && response.body() != null) {
                ResponseBody body = response.body();
                //String string = body.string();
                //System.out.println(string);
                return body.bytes();
            } else {
                //ResponseBody re = response.body();
                //System.err.println(re.string());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OkHttpClient.Builder newOkHttpBuilder() {
        String user = PropertyApi.getApi().getProperty().getSvnUserName();
        String pass = PropertyApi.getApi().getProperty().getSvnPassWord();
        return newOkHttpBuilder(user,pass);
    }
    public OkHttpClient.Builder newOkHttpBuilder(String user,String pass) {

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
//                .addInterceptor(new HttpLoggingInterceptor("gitee", HttpLoggingInterceptor.Level.BASIC));

        if (user != null && pass != null) {
            okBuilder.addInterceptor(new BasicAuthInterceptor(user, pass));
        }
        okBuilder.hostnameVerifier(new HttpsUtils.UnSafeHostnameVerifier());
        //okBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, bksFile, password));


        okBuilder.hostnameVerifier(new HttpsUtils.UnSafeHostnameVerifier());
        X509TrustManager trustManager = Platform.get().platformTrustManager();
        okBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, bksFile, password),trustManager);

        return okBuilder;
    }

    public OkHttpClient buildBasicAuthClient(final String name, final String password) {
        return new OkHttpClient.Builder().authenticator(new okhttp3.Authenticator() {
            @Nullable
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(name, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
    }


    public class BasicAuthInterceptor implements Interceptor {
        private String credentials;


        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }
    }

    /*public OkHttpClient buildBasicAuthClient(final String name, final String password) {
        return new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(name, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
    }*/
}
