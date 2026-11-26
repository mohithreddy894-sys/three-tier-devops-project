pipeline {
    agent any

    environment {
        DH_USER = 'mohithvreddy'
        TAG     = "build-${BUILD_NUMBER}"
    }

    stages {
        stage('Build Images') {
            steps {
                sh 'docker build -t $DH_USER/releaseboard-backend:$TAG ./backend'
                sh 'docker build -t $DH_USER/releaseboard-frontend:$TAG ./frontend'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN')]) {
                    sh 'echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh 'docker push $DH_USER/releaseboard-backend:$TAG'
                    sh 'docker push $DH_USER/releaseboard-frontend:$TAG'
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout'
        }
    }
}
