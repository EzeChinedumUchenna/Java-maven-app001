#!/usr/bin/env groovy

// @Library('jenkins-shared-lib') //"NOTE: if there is nothing before pipeline use @Library('jenkins-shared-lib')_" // Also note that this is for global scoped..

library identifier: 'jenkins-shared-lib@main', retriever: modernSCM([$class: 'GitSCMSource', remote: 'https://github.com/EzeChinedumUchenna/Jenkins_Shared_Lib.git', credentialId: 'My-Github-cred'])

def gv

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
        // stage("incrementing app version.....") {
        //     steps {
        //         script {
        //             echo 'incrementing app version ...'
        //             sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion.\\\${parsedVersion.nextIncrementalVersion} version:commit'
                    
        //             // we need to retrieve the version number of the app from the pom.xml file and use it as our image tag instead of using the BUILD_NUMBER. To do this we need to read the pom.xml file..
        //             def reader = readFile('pom.xml') =~ '<version>(.+)</version>'
        //             def version = reader[0][1]  //this will read the first version in the pom.xml file and the 1 column. Asuming that [0][0]=<version>, [0][1]=1.0.0 and [0][2]=</version>
        //             env.IMAGE_NAME = "$version-$BUILD_NUMBER"
        //         }
        //     }
        // }

        stage("incrementing app version.....") {
            steps {
                script {
                    echo 'incrementing app version ...'
                    sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} version:commit'
            
                        // we need to retrieve the version number of the app from the pom.xml file and use it as our image tag instead of using the BUILD_NUMBER. To do this we need to read the pom.xml file..
                    def reader = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = reader[0][1]  //this will read the first version in the pom.xml file and the 1 column. Assuming that [0][0]=<version>, [0][1]=1.0.0 and [0][2]=</version>
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }
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
            when {
                expression {
                    BRANCH_NAME == 'main'
                }
            }
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
                    deployImage("nedumacr.azurecr.io/demo-app:jma-$IMAGE_NAME ", env.BRANCH_NAME)
                
                }
            }
        } 
    } 
}
