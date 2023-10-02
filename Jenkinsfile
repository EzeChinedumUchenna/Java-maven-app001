def gv
pipeline {
    agent any
     tools {
        maven 'maven-3.9' 
    }
    // parameters {
    //     string (name: 'VERSION_NUMBER', defaultValue: '', description: 'Version for the deployment')
    //     choice (name: 'VERSION', choices: [10,20,30], description: 'Version for the deployment')
    //     booleanParam (name: 'ExecuteTest', defaultValue: 'true', description: 'choose either true or False')
    // }    
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
            when {
                expression {
                    BRANCH_NAME = "main"
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
                    gv.deployImage()
                
                }
            }
        } 
    } 
}
