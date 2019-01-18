package cn.csu.sise.computerscience.myapplication.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * 重要说明：
 * <p>
 * 本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 * （包括OrderInfoUtil2_0）。
 * <p>
 * 在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 * 否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 */
public class PayDemoActivity {
    public static final String TAG = "Pay";
    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016092500591323";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088102177171654";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "";

    /**
     * pkcs8 格式的商户私钥。
     * <p>
     * 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * RSA2_PRIVATE。
     * <p>
     * 建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDPSIPiRu5KJ0YU0qeoHIAgly0ji3qUZdxTgvOqeWr8arVp6c3BOB7+FAN3kYwQr6P5da9ByPqVqqgdGXo7ieSGMAz9BUSkUxol8UsD0Cae9ppn7WleSC6rdy6x3dPmZsqFQ2GmjeC28fRd/fFgeaL8pgUN3o9iHEuH4OVIFY6QuOQAeE+93U8smJndjBBHOXEvLkutVSEfxkJ19eu6U6mMdU7AaEHdT3sErGoii1jaq80p/obPuvAfNeshD8w/Db/4rREnwrNECSQKjuczAxDJQI1ZMQTYCNt4fyJbb6FfXPB+IrnzUTCvOvI/2QUBjOrkOFe3HNvTwAzv6bqkF6vtAgMBAAECggEANvhgIU32O6wWpccHt0l3oSz2R4rhwyEQQQVlYTw55x7VZXUfn7ImZfMgyEDNCE/MCIfxJl0KN06+rpjnFYsIE54Ck8cl4P443P8UibpVw6paYF1RqDM0TTyaIYlWfK+d3aF08BqIEXww9gw2SwN2+dF9XaQ8GiSu0yYNKASvfilPy0Oc7gQYI2xSH62P84WlRGGRwDumskDvoHy8QezXhj/qJUm1DPtSJ5A/b6CO+ArQCcqVZCI1j2b4ZoTr2+Nnv4wjtAUKmpy+x/tTECTF/Ogncrve6cJ1maXGoCM0V7QzZIKMGVhr1s4laSf/Rd9HfjBPDwDXnbX4G7IAgpeDAQKBgQD0FT14ytu7X0HNZXFo2kNlo8tmD1Ytio2Wm+TF/dAKSKEjbarlwahGsZHPYZ3o7Qj2hGwxA3O7L51qKqxQPRmvC40MzMtJ3pnUT5JTqt59PDR37B2WtO38pjyUzV+UlaYSE6x5SIjzS7yMj2L1NHnZXUrmLo1Am8rJzzLhSUjB0QKBgQDZZ1IM9A02IQ4hMZVi4z7bQrLCPVloJ3ZFqqHmEMBJYB4qL6Blfs6iKi3isdgwOo6g2HA7xmRnwjiHXNP7uwbOt7AVUktj+xjMgjegYkLFIqsKP3uw/EHxUfHy3cCP5bFI7f9235/N7TcpbRjJqSfYtUyUAsJL4H/87nwavsrTXQKBgBOb1TlsCqUN7QpkOO00g6+ZrhB/8OttsxxDX+isEFeTKCbhe5ZDrZIoV89yRy5cRJkRYGO7pEOIerv5u6gikHqokedSWI6RrIgK8HV6R6+F860isenWlTvz2vAVY7VbJGFVZUjFFAN/tSERr+CFG7jjrs25Na3YFj2KQMsqiQbRAoGAL+NWa4wGrPmzBxTI9zNfUSTsA2VIIG0w3D+t0eO2t3L4sgozpqe8FHR+DwVlvZnoeP20GzakP0bE9Ow5taWI5lgH7WpnOOrCl6nHXwjF9G6ihmtdb6rhS6rlRL4cAJZz6sCiGSlBJjcug22InEMqh83HxIKnEdet2aUeqTWFr/ECgYEAtQKkIOFSlyZxRC/Uptt7cSc3qX4iFtSzTeYEdbwyaUBainaXCbY8ATkBLYxAr9rfaCQ5MMcUEhKqpmJB/TE+xhmDXTqu8jN2P4eGlvcIVNZclX+X4NMJ45D60vBqdXyGVHZzQap6x9gyhQlR2XZtwTbU+WBgkyb3Qq7FE4CHWUE=";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//					showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
                        Log.d(TAG, "handleMessage: 支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//					showAlert(PayDemoActivity.this, getString(R.string.pay_failed) + payResult);
                        Log.d(TAG, "handleMessage: 支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    public static void payV2(final Activity activity, final Handler handler) {

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}