package com.igorronner.interstitialsample.requests;

import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

public class MyTrustManager implements X509TrustManager {


        MyTrustManager() {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            try {
                chain[0].checkValidity();
            } catch (Exception e) {
                try {

                    throw new CertificateException("Certificado não está válido");
                } catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }