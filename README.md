# e2e-jenkins
Java e2e library for writing e2e tests of Jenkins installations.

The library tests if Jenkins installation is correct after the kitchen converge of the Jenkins cookbook of the master.
The test first checks if the seed job is available, bootstraps the seed job and checks if the Groovy based Jobs are
installed by the seed job

# Installation

You need Maven and Java for the tests !

`mvn clean test -Dtest=JenkinsClientTest -DseededJobs=example1,example2,jenkins-job-DSL-seed,Job-DSL-Plugin,wf-1 -Dretry=3 -Dwait=2000 -Dtoken=s22dToken23`

will run the JenkinsClientTest with following parameters:

`seededJobs`
The list of jobs expected to be installed by the seed Job

`retry`
Try to check 3 times. The test polls the Jenkins installation.

`wait`
Wait each attempt 2000 milliseconds. The test waits 2*5=10 seconds after bootstrapping the seed job.

`token`
Job token needed for bootstrap the seed job.

