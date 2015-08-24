package schuberg.philis.jenkins;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SeededJobsTest {
    String baseUrl = "http://localhost:8080";
    JenkinsClient client;
    List<String> expected = new ArrayList<String>();

    @Before
    public void setUp(){
        client = new JenkinsClient("admin","admin");
        expected = getExpectedSeededJobs();
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

    @Test
    public void testSeededJobs(){
        try {
            client.parse(baseUrl);
            List<String> jobs = client.getSeededJobNames();
            assertEquals(expected, jobs);
        } catch(Exception e){
            e.printStackTrace();
            fail("Exception occured " + e.getMessage() );
        }

    }
}
