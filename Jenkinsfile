pipeline {

    agent {
        docker {
            image 'gryphonzone/docker-cli'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        DOCKER_CREDENTIALS = credentials('docker')
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
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
                    sh "echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin"
                    sh './docker.sh'
                }
            }
        }
    }
}
