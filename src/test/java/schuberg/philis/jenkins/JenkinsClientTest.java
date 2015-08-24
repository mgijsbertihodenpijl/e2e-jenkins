package schuberg.philis.jenkins;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JenkinsClientTest {

    String baseUrl = "http://localhost:8080";
    JenkinsClient client;
    List<String> expectedSeeds = new ArrayList<String>();

    @Before
    public void setUp(){
        client = new JenkinsClient("admin","admin");
        expectedSeeds = getExpectedSeededJobs();
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

    @Test
    public void testBuild(){
        try {
            String jobToken = "s22dToken23";
            client.build(baseUrl, "jenkins-job-DSL-seed", jobToken);
            Thread.sleep(10000);
            client.build(baseUrl, "jenkins-job-DSL-seed", jobToken);
            boolean foundJobs = false;
            for(int i = 0; i < 5 ; i++){
                Thread.sleep(5000);
                client.parse(baseUrl);
                List<String> jobs = client.getSeededJobNames();
                if(jobs.size() > 1){
                    foundJobs = true;
                    assertEquals(expectedSeeds, jobs);
                }
            }
            if(foundJobs == false){
                fail("Seed jobs are not found after 5 retries");
            }
        } catch(Exception e){
            e.printStackTrace();
            fail("Exception occured " + e.getMessage() );
        }
    }

    private List<String> getExpectedSeededJobs(){
        List<String>names = new ArrayList<String>();
        String seeds = System.getProperty("seededJobs");
        if(seeds != null && seeds.trim().length() > 0 ){
            if (seeds.contains(",")){
                String[] jobsArr = seeds.split(",");
                for(String job : jobsArr){
                    names.add(job.trim());
                }
            } else {
                names.add(seeds.trim());
            }
        } else {
            names.add("example1");
            names.add("example2");
            names.add("jenkins-job-DSL-seed");
            names.add("Job-DSL-Plugin");
            names.add("wf-1");
        }
        return names;
    }
}
