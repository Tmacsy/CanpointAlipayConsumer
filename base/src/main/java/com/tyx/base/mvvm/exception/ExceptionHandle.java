package com.tyx.base.mvvm.exception;

import android.net.ParseException;

import com.sam.system.log.L;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * Created by Allen on 2017/7/20.
 * 保留所有版权，未经允许请不要分享到互联网和其他人
 */
public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401; //token失败
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED: {
                    L.e("登录信息过期!");
                    ex.code = UNAUTHORIZED;
                    ex.msg = "登录信息过期!";
                }
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.msg = "网络错误";
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponseThrowable(resultException, resultException.msg, resultException.code);
            ex.msg = resultException.msg;
            return ex;
        } else if (e instanceof com.alibaba.fastjson.JSONException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ResponseThrowable(e, "解析错误", ERROR.PARSE_ERROR);
            ex.msg = "解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, "连接失败", ERROR.NETWORD_ERROR);
            ex.msg = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponseThrowable(e, "证书验证失败", ERROR.SSL_ERROR);
            ex.msg = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponseThrowable(e, "连接超时", ERROR.TIMEOUT_ERROR);
            ex.msg = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponseThrowable(e, "连接超时", ERROR.TIMEOUT_ERROR);
            ex.msg = "连接超时";
            return ex;
        } else {
            ex = new ResponseThrowable(e, "未知错误", ERROR.UNKNOWN);
            ex.msg = "未知错误";
            return ex;
        }
    }


    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }

    public static class ResponseThrowable extends Exception {
        public int code;
        public String msg;

        public ResponseThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }

        public ResponseThrowable(Throwable throwable, String msg, int code) {
            super(msg, throwable);
            this.code = code;
            this.msg = msg;
        }

        public ResponseThrowable(String msg, int code) {
            super(msg);
            this.code = code;
            this.msg = msg;
        }
    }

    public static class ServerException extends RuntimeException {
        public int code;
        public String msg;

        public ServerException() {
        }

        public ServerException(int code, String msg) {
            super(msg);
            this.code = code;
            this.msg = msg;
        }
    }
}

