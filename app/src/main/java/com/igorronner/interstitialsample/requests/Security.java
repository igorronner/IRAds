package com.igorronner.interstitialsample.requests;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class Security extends SSLSocketFactory {

    SSLContext sslContext;


    public Security(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super();
        this.sslContext = SSLContext.getInstance("TLS");
        this.sslContext.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
    }

    @Override
    public Socket createSocket() throws IOException {
        return super.createSocket();
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        SSLSocket sslSocket = (SSLSocket) this.sslContext.getSocketFactory().createSocket();
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        return sslSocket;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        SSLSocket sslSocket = (SSLSocket) this.sslContext.getSocketFactory().createSocket(s, host, port, autoClose);
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        return sslSocket;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    public SSLContext getSslContext() {
        return sslContext;
    }
}

