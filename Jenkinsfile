def gv
pipeline {
    agent any
    parameters {
        string (name: 'VERSION_NUMBER', defaultValue: '', description: 'Version for the deployment')
        choice (name: 'VERSION', choices: [10,20,30], description: 'Version for the deployment')
        booleanParam (name: 'ExecuteTest', defaultValue: 'true', description: 'choose either true or False')
    }
    /*tools {
        maven 'maven-3.8' //Makes tools (For Example maven) command available in all the Jenkins  stages.
    }*/
    stages {
        stage("initializing.....") {
            when {
                expression {
                    BRANCH_NAME == 'main' //this stage will run only when the git branch name AND there is Code Change But u need to define CODE_CHANGES bcos it is not part of the defaults env variable
                }
            }
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("building jar") {
            when {
                expression {
                    params.ExecuteTest = true
                    BRANCH_NAME == 'main' || BRANCH_NAME == 'dev' //this stage will run only when the git branch name is main OR a master build else skip
                }
            }
            steps {
                script {
                    echo "building jar"
                    //gv.buildJar()
                }
            }
        }
        stage("building image") {
            steps {
                script {
                    echo "building image"
                    //gv.buildImage()
                }
            }
        }
        stage("deploying") {
            steps {
                withCredentials ([usernamePassword(credentialsId: 'server_cred', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    echo "deploying $USER $PASS" 
                    //gv.deployApp()
                }
            }
        } 
    } 
}
