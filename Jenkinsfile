pipeline {

    agent {
        docker {
            image 'gryphonzone/docker-cli'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '1', numToKeepStr: '10')
        disableConcurrentBuilds()
        disableResume()
        timeout(activity: true, time: 20)
        timestamps()
    }

    stages {
        stage('Build and deploy') {
            steps {
                sh './docker.sh'
            }
        }
    }
}
