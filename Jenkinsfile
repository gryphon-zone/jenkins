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
        buildDiscarder logRotator(daysToKeepStr: '1', numToKeepStr: '10')
        disableConcurrentBuilds()
        disableResume()
        timeout(activity: true, time: 20)
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        ansiColor('xterm')
        timestamps()
    }

    triggers {
        cron '@daily'
    }

    stages {
        stage ('Docker login') {
            steps {
                sh "echo \"${DOCKER_CREDENTIALS_PSW}\" | docker login -u \"${DOCKER_CREDENTIALS_USR}\" --password-stdin"
            }
        }

        stage('Build and deploy Docker image') {
            steps {
                sh './docker.sh'
            }
        }
    }
}
