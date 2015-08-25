#!/usr/bin/env bash
mvn clean test -Dtest=JenkinsClientTest -Dtoken=s22dToken23 -DseededJobs=example1,example2,jenkins-job-DSL-seed,Job-DSL-Plugin,wf-1 -Dretry=150 -Dwait=2000
