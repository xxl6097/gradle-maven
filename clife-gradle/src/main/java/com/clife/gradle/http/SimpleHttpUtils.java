package com.clife.gradle.http;


import com.clife.gradle.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleHttpUtils {

    private static final int TEXT_REQUEST_MAX_LENGTH = 5 * 1024 * 1024;

    private static final Map<String, String> DEFAULT_REQUEST_HEADERS = new HashMap<String, String>();

    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();

    /**
     * User-Agent PC: Windows10 IE 11
     */
    private static final String USER_AGENT_FOR_PC = "Mozilla 0.0 Mozilla/5.0 (Windows NT 10.0; Trident/7.0; rv:11.0) like Gecko";

    private static final String USER_AGENT_FOR_MOBILE = "Chrome Mozilla/5.0 (Linux; Android 7.0; Nexus 6 Build/NBD92D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.132 Mobile Safari/537.36";

    private static int CONNECT_TIME_OUT = 15 * 1000;

    private static int READ_TIME_OUT = 0;

    static {
        CookieHandler.setDefault(new CookieManager());

        setMobileBrowserModel(false);
    }

    public static void setMobileBrowserModel(boolean isMobileBrowser) {
        setDefaultRequestHeader("User-Agent", isMobileBrowser ? USER_AGENT_FOR_MOBILE : USER_AGENT_FOR_PC);
    }

    public static void setTimeOut(int connectTimeOut, int readTimeOut) {
        if (connectTimeOut < 0 || readTimeOut < 0) {
            IllegalArgumentException e = new IllegalArgumentException("timeout can not be negative");
            e.printStackTrace();
            throw e;
        }
        RW_LOCK.writeLock().lock();
        try {
            CONNECT_TIME_OUT = connectTimeOut;
            READ_TIME_OUT = readTimeOut;
        } finally {
            RW_LOCK.writeLock().unlock();
        }
    }

    /**
     *
     * @param key key
     * @param value value
     */
    public static void setDefaultRequestHeader(String key, String value) {
        RW_LOCK.writeLock().lock();
        try {
            DEFAULT_REQUEST_HEADERS.put(key, value);
        } finally {
            RW_LOCK.writeLock().unlock();
        }
    }

    /**
     * removeDefaultRequestHeader
     * @param key key
     */
    public static void removeDefaultRequestHeader(String key) {
        RW_LOCK.writeLock().lock();
        try {
            DEFAULT_REQUEST_HEADERS.remove(key);
        } finally {
            RW_LOCK.writeLock().unlock();
        }
    }

    public static String get(String url) throws Exception {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> headers) throws Exception {
        return get(url, headers, null);
    }

    public static String get(String url, File saveToFile) throws Exception {
        return get(url, null, saveToFile);
    }

    public static String get(String url, Map<String, String> headers, File saveToFile) throws Exception {
        return sendRequest(url, "GET", headers, null, saveToFile);
    }

    public static String downFile(String url, Map<String, String> headers, String saveToFile) throws Exception {
        return downFile(url, "GET", headers, null, saveToFile);
    }

    public static String downFile(String url, String method, Map<String, String> headers, InputStream bodyStream, String filePath) throws Exception {
        String fileName = url.substring(url.lastIndexOf('/')+1);
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sendRequest(url, "GET", headers, null, file);
    }

    public static String post(String url, byte[] body) throws Exception {
        return post(url, null, body);
    }

    public static String post(String url, Map<String, String> headers, byte[] body) throws Exception {
        InputStream in = null;
        if (body != null && body.length > 0) {
            in = new ByteArrayInputStream(body);
        }
        return post(url, headers, in);
    }

    public static String post(String url, File bodyFile) throws Exception {
        return post(url, null, bodyFile);
    }

    public static String post(String url, Map<String, String> headers, File bodyFile) throws Exception {
        InputStream in = null;
        if (bodyFile != null && bodyFile.exists() && bodyFile.isFile() && bodyFile.length() > 0) {
            in = new FileInputStream(bodyFile);
        }
        return post(url, headers, in);
    }

    public static String post(String url, InputStream bodyStream) throws Exception {
        return post(url, null, bodyStream);
    }

    public static String post(String url, Map<String, String> headers, InputStream bodyStream) throws Exception {
        return sendRequest(url, "POST", headers, bodyStream, null);
    }


    private static boolean setContentType(HttpURLConnection conn, Map<String, String> headers) {
        if (headers == null || conn == null)
            return false;
        String clientId = headers.get("mqtt-clientid");
        if (clientId != null && !clientId.equals("")) {
            // ?:
//            String boundary = "---------------------------823928434";
            String boundary = UUID.randomUUID().toString();
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            return true;
        }
        return false;
    }

    private static void setDisposition(String boundary, String key, String fileName) {
        // next line
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        StringBuilder sb = new StringBuilder();
        sb.append(boundaryPrefix);
        sb.append(boundary);
        sb.append(newLine);

        sb.append("Content-Disposition: form-data;name=\"" + key + "\";filename=\"" + fileName + "\"" + newLine);
        sb.append("Content-Type:application/octet-stream");
        // ???��??
        sb.append(newLine);
        sb.append(newLine);
    }

    public static String sendRequest(String url, String method, Map<String, String> headers, InputStream bodyStream, File saveToFile) throws Exception {
        assertUrlValid(url);

        HttpURLConnection conn = null;
        try {
            //
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();

            // ?
            setDefaultProperties(conn);

            // ?
            if (method != null && method.length() > 0) {
                conn.setRequestMethod(method);
            }


            // ?
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }

            }

            if (headers != null) {
                final String username = headers.get("username");//"admin";
                final String password = headers.get("password");//"public";

                if (username != null && password != null) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password.toCharArray());
                        }
                    });
                }
            }


            //
            if (bodyStream != null) {
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                copyStreamAndClose(bodyStream, out);
            }

            // code
            int code = conn.getResponseCode();

            // ?
            if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = conn.getHeaderField("Location");
                if (location != null) {
                    closeStream(bodyStream);
                    // ?? GET
                    return sendRequest(location, "GET", headers, null, saveToFile);
                }
            }

            // ?
            long contentLength = conn.getContentLengthLong();
            // 
            String contentType = conn.getContentType();

            // 
            InputStream in = conn.getInputStream();

            // ??, ?
            if (code != HttpURLConnection.HTTP_OK) {
                IOException e= new IOException("Http Error: " + code + "; Desc: " + handleResponseBodyToString(in, contentType));
                e.printStackTrace();
                throw e;
            }

            if (saveToFile != null) {
                handleResponseBodyToFile(in, saveToFile);
                return saveToFile.getPath();
            }

            // ????, 
            if (contentLength > TEXT_REQUEST_MAX_LENGTH) {
                IOException e= new IOException("Response content length too large: " + contentLength);
                e.printStackTrace();
                throw e;
            }
            return handleResponseBodyToString(in, contentType);

        } finally {
            closeConnection(conn);
        }
    }

    private static void assertUrlValid(String url) throws IllegalAccessException {
        boolean isValid = false;
        if (url != null) {
            url = url.toLowerCase();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                isValid = true;
            }
        }
        if (!isValid) {
            IllegalAccessException e= new IllegalAccessException("Only support http or https url: " + url);
            e.printStackTrace();
            throw e;
        }
    }

    private static void setDefaultProperties(HttpURLConnection conn) {
        RW_LOCK.readLock().lock();
        try {
            // ???
            conn.setConnectTimeout(CONNECT_TIME_OUT);

            // ???
            conn.setReadTimeout(READ_TIME_OUT);

            //
            if (DEFAULT_REQUEST_HEADERS.size() > 0) {
                for (Map.Entry<String, String> entry : DEFAULT_REQUEST_HEADERS.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        } finally {
            RW_LOCK.readLock().unlock();
        }
    }

    private static void handleResponseBodyToFile(InputStream in, File saveToFile) throws Exception {
        OutputStream out = null;
        try {
            out = new FileOutputStream(saveToFile);
            copyStreamAndClose(in, out);
        } finally {
            closeStream(out);
        }
    }

    private static String handleResponseBodyToString(InputStream in, String contentType) throws Exception {
        ByteArrayOutputStream bytesOut = null;

        try {
            bytesOut = new ByteArrayOutputStream();

            // 
            copyStreamAndClose(in, bytesOut);

            // ??
            byte[] contentBytes = bytesOut.toByteArray();

            // ???
            String charset = parseCharset(contentType);
            if (charset == null) {
                charset = parseCharsetFromHtml(contentBytes);
                if (charset == null) {
                    charset = "utf-8";
                }
            }

            // 
            String content = null;
            try {
                content = new String(contentBytes, charset);
            } catch (UnsupportedEncodingException e) {
                content = new String(contentBytes);
            }

            return content;

        } finally {
            closeStream(bytesOut);
        }
    }

    private static void copyStreamAndClose(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }


    private static String parseCharsetFromHtml(byte[] htmlBytes) {
        if (htmlBytes == null || htmlBytes.length == 0) {
            return null;
        }
        String html = null;
        try {
            // ???? ISO-8859-1 ??
            html = new String(htmlBytes, "ISO-8859-1");
            return parseCharsetFromHtml(html);
        } catch (UnsupportedEncodingException e) {
            html = new String(htmlBytes);
        }
        return parseCharsetFromHtml(html);
    }

    private static String parseCharsetFromHtml(String html) {
        if (html == null || html.length() == 0) {
            return null;
        }
        html = html.toLowerCase();
        Pattern p = Pattern.compile("<meta [^>]+>");
        Matcher m = p.matcher(html);
        String meta = null;
        String charset = null;
        while (m.find()) {
            meta = m.group();
            charset = parseCharset(meta);
            if (charset != null) {
                break;
            }
        }
        return charset;
    }

    private static String parseCharset(String content) {
        // text/html; charset=iso-8859-1
        // <meta charset="utf-8">
        // <meta charset='utf-8'>
        // <meta http-equiv="Content-Type" content="text/html; charset=gbk" />
        // <meta http-equiv="Content-Type" content='text/html; charset=gbk' />
        // <meta http-equiv=content-type content=text/html;charset=utf-8>
        if (content == null) {
            return null;
        }
        content = content.trim().toLowerCase();
        Pattern p = Pattern.compile("(?<=((charset=)|(charset=')|(charset=\")))[^'\"/> ]+(?=($|'|\"|/|>| ))");
        Matcher m = p.matcher(content);
        String charset = null;
        while (m.find()) {
            charset = m.group();
            if (charset != null) {
                break;
            }
        }
        return charset;
    }

    private static void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                // nothing
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                // nothing
            }
        }
    }


    public static String uploadFile(String host, Map<String, String> headers, File file, String key) throws Exception {
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        final String BOUNDARY = UUID.randomUUID().toString();
        String target = host;
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(target);
            urlConn = (HttpURLConnection) url.openConnection(); // ?HTTP
            urlConn.setRequestMethod("POST"); // ??POST?
            urlConn.setDoInput(true); // ��
            urlConn.setDoOutput(true); // ��
            urlConn.setUseCaches(false); // ?
            urlConn.setInstanceFollowRedirects(true); // ??HTTP?
            // ?
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConn.setRequestProperty(entry.getKey(), entry.getValue());
                }

            }
            urlConn.setRequestProperty("connection", "Keep-Alive");
            urlConn.setRequestProperty("Charset", "UTF-8");
            urlConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY); //
            DataOutputStream out = new DataOutputStream(
                    urlConn.getOutputStream()); // 

            // ??
            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);

            sb.append("Content-Disposition: form-data;name=\"" + key + "\";filename=\"" + file.getName() + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // ???��??
            sb.append(newLine);
            sb.append(newLine);
//            System.out.println(sb.toString());
            out.write(sb.toString().getBytes());
            DataInputStream in = new DataInputStream(
                    new FileInputStream(file));
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write(newLine.getBytes());
            in.close();

            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY
                    + boundaryPrefix + newLine).getBytes();
            out.write(end_data);
            out.flush(); //
            out.close(); // ?
            // code
            int code = urlConn.getResponseCode();
            System.out.println(" getResponseCode:" + code);
            if (code == HttpURLConnection.HTTP_OK) {


//                InputStreamReader in1 = new InputStreamReader(
//                        urlConn.getInputStream(), "utf-8"); // ??utf-8??
//                BufferedReader buffer = new BufferedReader(in1); // 
//                String inputLine = null;
//                System.out.println("inputLine:" + buffer.readLine());
//                while ((inputLine = buffer.readLine()) != null) {
//                    System.out.println(inputLine + "\n");
//                }


                // ?
                long contentLength = urlConn.getContentLengthLong();
                // 
                String contentType = urlConn.getContentType();

                // 
                InputStream inn = urlConn.getInputStream();

                if (code != HttpURLConnection.HTTP_OK) {
                    IOException e= new IOException("Http Error: " + code + "; Desc: " + handleResponseBodyToString(inn, contentType));
                    e.printStackTrace();
                    throw e;
                }

                if (contentLength > TEXT_REQUEST_MAX_LENGTH) {
                    IOException e= new IOException("Response content length too large: " + contentLength);
                    e.printStackTrace();
                    throw e;
                }


                // 
                String content = handleResponseBodyToString(inn, contentType);
                return content;
            }
        } finally {
            closeConnection(urlConn);
        }
        return null;
    }


    public static void downLoad(String fileUrl, Map<String, String> headers) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            int contentLength = getConnection(fileUrl, null).getContentLength();
            System.out.println("文件的大小是:" + contentLength);
            if (contentLength > 32) {
                InputStream is = getConnection(fileUrl, null).getInputStream();
                bis = new BufferedInputStream(is);
                FileOutputStream fos = new FileOutputStream("D:/test/美女.jpg");
                bos = new BufferedOutputStream(fos);
                int b = 0;
                byte[] byArr = new byte[1024];
                while ((b = bis.read(byArr)) != -1) {
                    bos.write(byArr, 0, b);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HttpURLConnection getConnection(String httpUrl, Map<String, String> headers) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

        }

        if (headers != null) {
            final String username = headers.get("username");//"admin";
            final String password = headers.get("password");//"public";

            if (username != null && password != null) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });
            }
        }

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.connect();
        return connection;

    }
}
