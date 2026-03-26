pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
        DOCKERHUB_REPO = 'hyoneekim/opt2'
        DOCKER_IMAGE_TAG = "latest"
        DOCKERHUB_CREDENTIALS_ID = 'docker_hub'
    }

    stages {
    stage('check') {
                        steps {
                        git branch: 'main',
                         url: 'https://github.com/hyoneekim/SEP2_w2_assignment.git'
                        }
                }
    stage('build') {
                        steps {
                         sh 'mvn install'
                        }
                }

                    }
                }
    stage('Build Docker image') {
       steps {
         sh '''
         docker build \
           --platform linux/amd64 \
           -t ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} \
           .
         '''
     }
 }

 stage('Push Docker Image') {
     steps {
         withCredentials([usernamePassword(
             credentialsId: "${DOCKERHUB_CREDENTIALS_ID}",
             usernameVariable: 'DOCKER_USER',
             passwordVariable: 'DOCKER_PASS'
         )]) {
             sh '''
                 echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                 docker push ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}
             '''
         }
     }
 }
    }
}