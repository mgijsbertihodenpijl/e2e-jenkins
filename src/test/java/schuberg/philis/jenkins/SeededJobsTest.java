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

    @Before
    public void setUp(){
        client = new JenkinsClient("admin","admin");
    }

    @Test
    public void testSeededJobs(){
        try {
            client.parse(baseUrl);
            List<String> expected = new ArrayList<String>();
            expected.add("example1");
            expected.add("example2");
            expected.add("jenkins-job-DSL-seed");
            expected.add("Job-DSL-Plugin");
            expected.add("wf-1");
            List<String> jobs = client.getSeededJobNames();
            assertEquals(expected, jobs);
        } catch(Exception e){
            e.printStackTrace();
            fail("Exception occured " + e.getMessage() );
        }

    }
}
