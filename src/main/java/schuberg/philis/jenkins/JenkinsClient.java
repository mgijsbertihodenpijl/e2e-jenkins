package schuberg.philis.jenkins;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class takes care of parsing Jenkins pages and submitting jobs via the REST API of Jenkins.
 *
 * @author mgijsbertihodenpijl
 */
public class JenkinsClient {
    private String userName;
    private String passWord;
    private Document doc;

    public JenkinsClient(String userName, String passWord) {
        this.passWord = passWord;
        this.userName = userName;
    }

    /**
     * This method stores a page of Jenkins into the Jsoup Document. Now it possible to search on the page for
     * characteristics like a Job status.
     *
     * @param url, the url of the Jenkins page
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void parse(String url) throws ClientProtocolException, IOException  {
        ClientFactory clientFactory =  ClientFactory.create(url, this.userName, this.passWord);
        HttpGet httpGet = new HttpGet(clientFactory.getUri());
        HttpClient httpClient = clientFactory.getHttpClient();
        HttpClientContext localContext = clientFactory.getHttpClientContext();
        HttpResponse response = httpClient.execute(clientFactory.getHost(), httpGet, localContext);
        this.doc = Jsoup.parse(EntityUtils.toString(response.getEntity())) ;
    }

    private Elements getElements(String selector){
        return this.doc.select(selector);
    }

    private Element getFirstElement(String selector) {
        return this.getElements(selector).first();
    }

    /**
     * This method checks if a Jenkins job is available. This is used to check if the Jenkins jobs are loaded
     * into the Jenkins installation after a kitchen converge.
     *
     * @param job
     * @return true if Job is available
     */
    public boolean hasJob(String job){
        return this.getFirstElement("#"+ job) != null ? true: false;
    }

    /**
     * Gets API token of the Jenkins installation. The token is generated during the installation of Jenkins.
     *
     * @return API token or empty if not available
     */
    public String getApiToken(){
        Element input =  this.getFirstElement("#apiToken");
        if(input != null){
            return input.attr("value");
        }
        return "";
    }

    /**
     * This method builds a Jenkins job. The job should have a Job token configured.
     * See https://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API.
     *
     * @param baseUrl, the baseUrl of the Jenkins installation
     * @param job, name of the Jenkins job
     * @param jobToken, the token of the Job
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public int build(String baseUrl, String job, String jobToken)throws ClientProtocolException, IOException, URISyntaxException{
        String jobUrl = baseUrl + "/job/" + job + "/build/token=" + jobToken;
        System.out.println(jobUrl);
        ClientFactory clientFactory =  ClientFactory.create(jobUrl, this.userName, this.passWord);
        HttpUriRequest build = RequestBuilder.post()
                .setUri(clientFactory.getUri())
                .addParameter("token", jobToken)
                .build();
        HttpClient httpClient = clientFactory.getHttpClient();
        HttpResponse response = httpClient.execute(clientFactory.getHost(), build, clientFactory.getHttpClientContext());
        int statusCode  = response.getStatusLine().getStatusCode();
        System.out.println("Job build [" + jobUrl +" ] Status  [" + response.getStatusLine() + "]");
        return statusCode;
    }
}
