def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9' # Makes maven commnad available in all the stages
    }
    stages {
        stage("init") {
            when {
                expression {
                    BRANCH_NAME == 'main' && CODE_CHANGES == true #this stage will run only when the git branch name AND there is Code Change But u need to define CODE_CHANGES bcos it is not part of the defaults env variable
                }
            }
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("build jar") {
            when {
                expression {
                    BRANCH_NAME == 'main' || BRANCH_NAME == 'dev' #this stage will run only when the git branch name is main OR a master build else skip
                }
            }
            steps {
                script {
                    echo "building jar"
                    //gv.buildJar()
                }
            }
        }
        stage("build image") {
            steps {
                script {
                    echo "building image"
                    //gv.buildImage()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    echo "deploying"
                    //gv.deployApp()
                }
            }
        }
    }   
}
