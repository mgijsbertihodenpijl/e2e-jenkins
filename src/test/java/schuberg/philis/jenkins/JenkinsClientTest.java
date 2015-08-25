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
            long wait = getWait(5000);
            Thread.sleep(wait);
            client.build(baseUrl, "jenkins-job-DSL-seed", jobToken);
            List<String> jobs = new ArrayList<String>();
            boolean foundJobs = false;
            int tries = getRetry(5);
            for(int i = 0; i < tries; i++){
                System.out.println("Try to find jobs, attempt " + (i + 1));
                Thread.sleep(wait);
                client.parse(baseUrl);
                jobs = client.getSeededJobNames();
                printJobs(jobs);
                if(jobs.size() > 1){
                    if(expectedSeeds.size() == jobs.size()){
                        foundJobs = true;
                        assertEquals(expectedSeeds, jobs);
                        break;
                    } else {
                        System.out.println("Not all jobs loaded, try again...");
                        foundJobs = false;
                    }
                }
            }
            if(foundJobs == false){
                System.out.print("Expected - ");
                printJobs(expectedSeeds);
                System.out.print("Jenkins - ");
                printJobs(jobs);
                fail("Not all seeded jobs are found after " + tries + " retries ");
            }
        } catch(Exception e){
            e.printStackTrace();
            fail("Exception occured " + e.getMessage() );
        }
    }

    private void printJobs(List<String>jobs){
        StringBuilder b = new StringBuilder();
        for(String job : jobs){
            b.append(job + " ");
        }
        System.out.println("Jobs (" + jobs.size() + ") " + "[" + b.toString() + "]");
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

    private int getRetry(int defaultRetry){
        String retry = System.getProperty("retry");
        try {
            return Integer.parseInt(retry);
        } catch (NumberFormatException e){}
        return defaultRetry;
    }

    private long getWait(long defaultWait){
        String wait = System.getProperty("wait");
        try {
            return Long.parseLong(wait);
        } catch (NumberFormatException e){}
        return defaultWait;

    }
}
