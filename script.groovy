#!/usr/bin/env groovy

def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
} 

def buildImage() {
    echo "building the docker image..."
    sh 'docker build -t nedumacr.azurecr.io/demo-app:jma-${BUILD_NUMBER} .'

    }

def deployImage() {
    echo "deploying image to ACR ...."
    withCredentials([usernamePassword(credentialsId: 'azure_acr_cred', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "echo $PASS | docker login -u $USER --password-stdin nedumacr.azurecr.io"
        sh 'docker push nedumacr.azurecr.io/demo-app:jma-${BUILD_NUMBER}'
    }
}
def deployApp() {
    echo 'deploying the application...'
} 

return this
