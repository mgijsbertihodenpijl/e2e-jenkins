package schuberg.philis.jenkins;

import java.net.URI;
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JenkinsClient {
    private String userName;
    private String passWord;
    private Document doc;

    public JenkinsClient(String userName, String passWord) {
        this.passWord = passWord;
        this.userName = userName;
    }

    public void parse(String url) throws ClientProtocolException, IOException  {
        URI uri = URI.create(url);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(this.userName, this.passWord));
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        HttpResponse response = httpClient.execute(host, httpGet, localContext);
        this.doc = Jsoup.parse(EntityUtils.toString(response.getEntity())) ;
    }

    private Elements getElements(String selector){
        return this.doc.select(selector);
    }

    private Element getFirstElement(String selector) {
        return this.getElements(selector).first();
    }

    public boolean hasJob(String job){
        return this.getFirstElement("#"+ job) != null ? true: false;
    }

    public String getApiToken(){
        Element input =  this.getFirstElement("#apiToken");
        if(input != null){
            return input.attr("value");
        }
        return "";
    }
}
