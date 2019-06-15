pipeline {

    options {
        timestamps()
        ansiColor('xterm')
        buildDiscarder logRotator(daysToKeepStr: '30', numToKeepStr: '100')
        disableConcurrentBuilds()
        disableResume()
        timeout(activity: true, time: 20)
        durabilityHint 'PERFORMANCE_OPTIMIZED'
    }

    triggers {
        cron '@daily'
    }

    agent {
        docker {
            image 'gryphonzone/docker-cli'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        DOCKER_CREDENTIALS = credentials('docker')
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
