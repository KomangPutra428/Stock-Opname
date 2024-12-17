package com.tvip.stockopnamegudang;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsTrustManager {
    public static void allowAllSSL() {
        TrustManager[] victimizedManager = {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                System.out.println("Auth = " + authType);
                if (chain == null || chain.length == 0) {
                    throw new IllegalArgumentException("Certificate is null or empty");
                } else if (authType == null || authType.length() == 0) {
                    throw new IllegalArgumentException("Authtype is null or empty");
                } else if (authType.equalsIgnoreCase("ECDHE_RSA") || authType.equalsIgnoreCase("ECDHE_ECDSA") || authType.equalsIgnoreCase("RSA") || authType.equalsIgnoreCase("GENERIC") || authType.equalsIgnoreCase("ECDSA")) {
                    try {
                        chain[0].checkValidity();
                    } catch (Exception e) {
                        throw new CertificateException("Certificate is not valid or trusted");
                    }
                } else {
                    throw new CertificateException("Certificate is not trust");
                }
            }
        }};
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init((KeyManager[]) null, victimizedManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    if (hostname.equals("hrd.tvip.co.id") || hostname.equals("assessment.tvip.co.id") || hostname.equals("apisec.tvip.co.id")) {
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
        }
    }
}
