package schuberg.philis.jenkins;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

/**
 *  This factory creates a httpClient connection and HttpClientContext with credentials stored in an AuthCache object.
 *  This object is used to preemptively authenticate against known hosts.
 *
 */
public class ClientFactory {

    private URI uri;
    private String userName;
    private String passWord;
    private HttpHost host;
    private HttpClient httpClient;
    private HttpClientContext httpClientContext;


    private ClientFactory( String url, String userName, String passWord) {
        this.passWord = passWord;
        this.uri = URI.create(url);
        this.userName = userName;
    }

    public static ClientFactory create(String url, String userName, String passWord){
        return new ClientFactory(url,userName,passWord).build();
    }

    public ClientFactory build(){
        this.host = new HttpHost(this.uri.getHost(), this.uri.getPort(), this.uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(this.userName, this.passWord));
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        this.httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        this.httpClientContext  = HttpClientContext.create();
        this.httpClientContext.setAuthCache(authCache);
        return this;
    }

    public URI getUri() {
        return uri;
    }

    public HttpHost getHost() {
        return host;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpClientContext getHttpClientContext() {
        return httpClientContext;
    }
}
