#!/usr/bin/env groovy

// @Library('jenkins-shared-lib') //"NOTE: if there is nothing before pipeline use @Library('jenkins-shared-lib')_" // Also note that this is for global scoped.
library identifier: 'jenkins-shared-lib@main', retriever: modernSCM([$class: 'GitSCMSource', remote: 'https://github.com/EzeChinedumUchenna/Jenkins_Shared_Lib.git', credentialId: 'My-Github-cred']) 
// The above is for project scope

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
        stage("incrementing app version.....") {
            steps {
                script {
                    echo 'incrementing app version ...'
                    sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit' // This will increment the 
                    
                    // we need to retrieve the version number of the app from the pom.xml file and use it as our image tag instead of using the BUILD_NUMBER. To do this we need to read the pom.xml file..
                    def reader = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = reader[0][1]  //this will read the first version in the pom.xml file and the 1 column. Asuming that [0][0]=<version>, [0][1]=1.0.0 and [0][2]=</version>
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }

        // stage("incrementing app version.....") {
        //     steps {
        //         script {
        //             echo 'incrementing app version ...'
        //             sh 'mvn build-helper:parse-version versions:set \
        //             -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit'
        //             //sh 'mvn build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion} -DgenerateBackupPoms=false'

        //                 // we need to retrieve the version number of the app from the pom.xml file and use it as our image tag instead of using the BUILD_NUMBER. To do this we need to read the pom.xml file..
        //             def mavenPom = readMavenPom 'pom.xml'
        //             // def version = reader[0][1]  //this will read the first version in the pom.xml file and the 1 column. Assuming that [0][0]=<version>, [0][1]=1.0.0 and [0][2]=</version>
        //             // add this pluggin "Pipeline Utility StepsVersion"
                    
        //             env.IMAGE_NAME = "${mavenPom.version}-$BUILD_NUMBER"
        //         }
        //     }
        // }
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
                    echo "pushing to ACR........."
                    deployImage("nedumacr.azurecr.io/demo-app:jma-$IMAGE_NAME", env.BRANCH_NAME)
                
                }
            }
        }
        // Note: That even after we have incremented the version number in the code and push to ACR, it is not commited to the git hub pom.xml and thus when 
        // programmer push another code to the git hub, the job triggers the same version increase. Therefore we need to commit the new increment to Github
        stage ('commit version update......') {
            steps {
                script {
                    // We need access to github
                    withCredentials([usernamePassword(credentialsId: 'My-Github-cred', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                    // First we are going to attach a metadata to our commit. Like email and username, else Jenkins will complain. This is very important and a must-have at first commit but can be remove aftr that.
                        sh 'git config user.email "nedum_jenkins@gmail.com"' 
                        sh 'git config user.name "jenkins"'
                    // Note can set the above globally for all the project by adding '--global'
                    // sh 'git config --global user.email "nedum_jenkins@gmail.com"' 
                    // sh 'git config --global user.name "nedum_jenkins"' 
                    // we want git to print out the following information
                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'

                    //
                        
                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/EzeChinedumUchenna/Java-maven-app001.git" //here will are setting the Origin value to https://github.com/EzeChinedumUchenna/Java-maven-app001.git before the git push origin cmd
                        sh 'git add .'
                        sh 'git commit -m "ci:version increase"' 
                        sh 'git push origin HEAD:jenkins-jobs'
                }
            }
        }
    } 
}
}
