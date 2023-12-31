#!/usr/bin/env groovy

// @Library('jenkins-shared-lib') //"NOTE: if there is nothing before pipeline use @Library('jenkins-shared-lib')_" // Also note that either of this is used when you have configured Jenkins shared lib on the 
// Jenkins global with a name "jenkins-shared-lib". To do this, go to Setting > Managed Jenkins > configuration > global library and add the Jenkins-share-lib repository there with the Git username and password
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
                    sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit' // This will increment the verion on the pom.xml file
                    // we need to retrieve the new incremented version number of the app from the pom.xml file and use it as our image tag instead of using the BUILD_NUMBER. To do this we need to read the pom.xml file..
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
            input {
                message "selete the environment"
                ok "Apply"
                parameters {
                    choice (name: 'Env', choices: ['dev', 'stage', 'prod'], description: '')
                }
            }
            when {
                expression {
                    BRANCH_NAME == 'main' && Env =="prod" 
                }
            }
            
            steps {
                script {
                    echo "pushing to ACR........."
                    deployImage("nedumacr.azurecr.io/demo-app:jma-$IMAGE_NAME", env.BRANCH_NAME)  // Note this "deployImage" is linked to a Jenkins shared Lib....
                
                }
            }
        }
        // Note: That even after we have incremented the version number in the code and push to ACR, it is not commited to the git hub pom.xml and thus when 
        // programmer push another code to the git hub, the job triggers the same version increase. Therefore we need to commit the new increment to Github
        stage('commit version update......') {
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

                    // Because my Github Password contain special character @, I will need to encode it else it wont work with Jenkins.
                        def encodedPassword = URLEncoder.encode(PASS, "UTF-8")

                        // Set the Git remote URL with the encoded password
                        sh "git remote set-url origin https://${USER}:${encodedPassword}@github.com/EzeChinedumUchenna/Java-maven-app001.git"
                        sh 'git add .'
                        sh 'git commit -m "ci:version increase"'
                        sh 'git push origin HEAD:refs/heads/main' //here I want to push to main branch. Selete any branch you want to push to Eg sh 'git push origin HEAD:refs/heads/bug-fix'
                }
                    // Note: After you have implemeted the above, Jenkins will ended up commiting the version change into the Github repo but remember we have a webhook configured between Jenkins
                    // and the Github, thus whenever Jenkins commit changes to Github another build will be trigered thus causing an endless loop. 

                    // To achieve the above we need to install a Jenkins plugin called IGNORE COMMITTER STRATEGY >>> Go to the Pipeline Configuration >> Branch Sources >> under behaviour 
                    // look for BUILD STRTEGIES, click on it an add Jenkins email address or username. Eg nedum_jenkins@gmail.com
            }
        }
        }
        stage ("deploying to Production Server") {
            // First you need to SSH into the server to run some cmd.
            // To SSH into the server, you need a SSH Agent plugin installed in Jenkins
            // Since Jenkins will connect to the Production server, we need to create a credential in Jenkin that will has the Production Server Username and the Server Key in .pem. Best Practice is to create the Credential from the pipeline page (pipelne scope); the SSH credential will only be used by the pipeline.
            // From the pipeline blade, click on the pipeline syntax, And look for SSH agent to know how to use SSH plugin. 
            steps {
                script {
                    //withCredentials([usernamePassword(credentialsId: 'azure_acr_cred', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        // First go to the Server and install Azure CLI sudo apt install azure-cli and login to Azure platform using your username and password
                        // Open a terminal on your server and run the following command to log in to your Azure account. It is best to do that on he server first or use
                        //sh 'az acr login --name NedumACR -u $USER --password-stdin' // Make sure you open port 22 on the Production Server NSG and NOTE on this line we will use (') and not (") not even when there is a String value
                   // }
                    sshagent(['Production_Server_SSH-KEY']) {
                        def dockerCmd = "docker run -p 8080:8080 -d nedumacr.azurecr.io/demo-app:jma-$IMAGE_NAME" // Make sure you have docker and az CLI installed on both the Production Server and Jenkins server and that Java-maven port uns on port 8080
                        // Make sure you have SSH Agent Plugin Installed......
                        sh "ssh -o StrictHostKeyChecking=no chinedumeze@20.26.114.46 ${dockerCmd}" // -o StrictHostKeyChecking=no" - this flag is used to override any pop up that comes when you SSH into a server. Note that this is not an interactive mode 
                        
                    } 
                }
            }
        }
    } 
}

