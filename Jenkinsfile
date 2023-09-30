def gv

pipeline {
    agent any
    parameters {
        string (name: 'VERSION_NUMBER', defaultValue: '', description: 'Version for the deployment')
        choice (name: 'VERSION', defaultValue: '20', choice: [10,20,30], description: 'Version for the deployment')
        booleanParam (name: 'ExecuteTest', defaultValue: 'true', description: 'choose either true or False')
    }
    tools {
        maven 'maven-3.9' //Makes tools (For Example maven) command available in all the Jenkins  stages
    }
    stages {
        stage("init") {
            when {
                expression {
                    BRANCH_NAME == 'main' && CODE_CHANGES == true //this stage will run only when the git branch name AND there is Code Change But u need to define CODE_CHANGES bcos it is not part of the defaults env variable
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
                withCredentials ([usernamePassword(credentials: 'server-credentials', usernameVariable: USER, passwordVariable: PASS)]) // To sue this CMD you must have "CREDENTIALS BINDING PLUGIN" and the CREDENTIALS PLUGIN" installed in the JEnkins
                script {
                    echo "deploying ${USER} ${PASS} ${params.VERSION}" 
                    //gv.deployApp()
                }
            }
        } 
    } 
   /* post {
        always {
            //
        }
        failure {
          //
        }
        success {
            // 
        }
    }
}
}
*/
