#!/usr/bin/env groovy

// @Library('jenkins-shared-lib') //"NOTE: if there is nothing before pipeline use @Library('jenkins-shared-lib')_" // Also note that this is for global scoped.....

library identifier: 'jenkins-shared-lib@main', retriever: modernSCM([$class: 'GitSCMSource', remote: 'https://github.com/EzeChinedumUchenna/Jenkins_Shared_Lib.git', credentialId: 'My-Github-cred', branchName: env.BRANCH_NAME])

def gv
def branchName = env.BRANCH_NAME
pipeline {
    agent any
     tools {
        maven 'maven-3.9' 
    }
    parameters {
        string (name: 'VERSION_NUMBER', defaultValue: '', description: 'Version for the deployment')
        choice (name: 'VERSION', choices: [10,20,30], description: 'Version for the deployment')
        booleanParam (name: 'ExecuteTest', defaultValue: 'true', description: 'choose either true or False')
    }    
    stages {
        stage("initializing.....") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("building jar") {
            steps {
                script {
                    echo "building jar"
                    gv.buildJar()
                }
            }
        }
        stage("building image") {
            steps {
                script {
                    echo "building image"
                    gv.buildImage()
                }
            }
        }
        stage("deploying to ACR") {
            // when {
            //     expression {
            //         BRANCH_NAME == 'main'
            //     }
            // }
            input {
                message "selete the environment"
                ok "Done"
                parameters {
                    choice (name: 'ENV', choices: ['dev', 'stage', 'prod'], description: '')
                }
            }
            steps {
                script {
                    echo "pushing to ACR in "
                    // deployImage "nedumacr.azurecr.io/demo-app:jma-${BUILD_NUMBER}, branchName"
                    //deployImage(imageName: "nedumacr.azurecr.io/demo-app:jma-${BUILD_NUMBER}", branchName: env.BRANCH_NAME)
                    deployImage("nedumacr.azurecr.io/demo-app:jma-${BUILD_NUMBER}", env.BRANCH_NAME)
                
                }
            }
        } 
    } 
}
