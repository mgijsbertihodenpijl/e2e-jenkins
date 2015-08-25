node {
    stage 'checkout'
    git 'https://github.schubergphilis.com/broden/sbp_jenkins_wrapper'
    stage 'converge'
    sh 'kitchen converge master.*71 || true'
    stage 'verify'
    sh 'kitchen verify master.*71'
    stage 'e2e'
    dir('e2e') {
        def jobs = 'example1,example2,jenkins-job-DSL-seed,Job-DSL-Plugin,wf-1'
        def retry = '3'
        def wait = '2000'
        git 'https://github.com/mgijsbertihodenpijl/e2e-jenkins'
        sh 'mvn clean test -Dtest=JenkinsClientTest -DseededJobs=${jobs} -Dretry=${retry} -Dwait=${wait}'
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
    }
//    stage 'destroy'
//    sh 'kitchen destroy master.*71'
}