package schuberg.philis.jenkins;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

public class JenkinsClientTest {

    String baseUrl = "http://localhost:8080";
    JenkinsClient client;

    @Before
    public void setUp(){
        client = new JenkinsClient("admin","admin");
    }

    @Test
    public void testHasJob(){
        try {
            client.parse(baseUrl);
            assertTrue(client.hasJob("job_jenkins-job-DSL-seed"));
        } catch(Exception e){
            e.printStackTrace();
            fail("Exception occured " + e.getMessage() );
        }
    }

    @Test
    public void testGetApiToken(){
        try {
            client.parse(baseUrl + "/user/admin/configure");
            String apiToken = client.getApiToken();
            Pattern pattern = Pattern.compile("\\w+");
            Matcher matcher = pattern.matcher(apiToken);
            System.out.println("apiToken {" + apiToken + "}");
            assertTrue(matcher.matches());
        } catch(Exception e){
            e.printStackTrace();
            fail("Exception occured " + e.getMessage() );
        }
    }
}
