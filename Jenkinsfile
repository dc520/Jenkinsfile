pipeline {
  environment {
    APP = ""
  }
  agent any
  stages {
    stage('Checkout') {
      steps {
        dir("src"){
          checkout([$class: 'GitSCM', branches: [[name: params.BRANCH.trim()]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: params.GIT_URL.trim()]]])
        }
      }
    }
    stage('Setup') {
      steps {
        script {
          //Generate a packaged version, this version is associated with the git branch name, git commit id, and the version generation date should be marked at the same time
          env.GIT_COMMIT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
          env.GIT_COMMIT_SHORT = sh(script: "git log -1 --pretty=format:%h", returnStdout: true).trim()
          env.VERSION = "master" + "-" +env.BUILD_NUMBER + "-" + env.GIT_COMMIT_SHORT
          env.IMAGE_URL = 'hrb/xxx/' + env.ServiceName + ':' + env.VERSION
        }
      }
    }
    stage('build code') {
      steps {
        dir("src"){
          script {
            sh "pwd"
            if ("${env.Language}" == "NodeJS") {
              //sh npm install  //The server where the jenkins server is located requires npm to be installed.
              echo "${env.Language}"
            } else if ("${env.Language}" == "Java") {
              //sh "mvn/ant install command"  //The server where the jenkins server is located requires maven/ant to be installed.
              echo "${env.Language}"
            }
          }
        }
      }
    }    
    stage('build image') {
      steps {
        dir("src"){
          script {
            //Get hrb credentials id.
            //withCredentials([Skipdetection(credentialsId: 'hrb', xxVariable: 'xx', xxVariable: 'xx')]) {           
            //sh "docker login -u ${xx} -p ${xx} hrb"
            sh "pwd"
            sh "docker build -t product/${env.ServiceName}:${env.VERSION} ." //Use the version number generated by the setup step to name the tag of docker image.
            //sh "docker tag xxx/${env.ServiceName}:${env.VERSION} hrb/xxx/${env.ServiceName}:${env.VERSION}"
            //sh "docker push hrb/xxx/${env.ServiceName}:${env.VERSION}"
            echo "${env.ServiceName}"
            //}
          }
        }
      }
    }  
    stage('Approval to Dev'){
      steps {
        timeout(time:3600, unit:'SECONDS') {  // DAYS , MINUTES
          input 'Do you approve deployment to Dev?'
        }
      }
    }
    //The approach here is to publish the configuration file by default. You can add job parameters to control whether to publish configuration files.
    stage('Checkout Configfile'){
      steps {
        dir("config"){
          checkout([$class: 'GitSCM', branches: [[name: "master"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: "https://github.com/dc520/config.git"]]])
        }  
      }
    }    
    stage('Deploy To Dev'){
      steps {
        sh 'ansible -m ping dev_app_servers'
        sh "ansible-playbook deployPlaybook.yml -e var_enviro=DEV -e var_image=product/${env.ServiceName}:${env.VERSION} -e var_service_name=${env.ServiceName} -e var_hosts=dev_app_servers -e var_port=${env.PORT}"
      }
    }
    stage('Check Dev Service Up') {
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }
    stage('Approval to Test'){
      steps {
        timeout(time:3600, unit:'SECONDS') {  // DAYS , MINUTES
          input 'Do you approve deployment to Test?'
        }
      }
    }    
    stage('Deploy To Test'){
      //when {
      //    expression { env.ENV == "TEST" }
      //} 
      steps {
        sh 'ansible -m ping test_app_servers'
        sh "ansible-playbook deployPlaybook.yml -e var_enviro=TEST -e var_image=product/${env.ServiceName}:${env.VERSION} -e var_service_name=${env.ServiceName} -e var_hosts=test_app_servers -e var_port=${env.PORT}"
      }
    }
    stage('Check Test Service Up') {
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }
    stage('Approval to Stg'){
      steps {
        timeout(time:3600, unit:'SECONDS') {  // DAYS , MINUTES
          input 'Do you approve deployment to Stg?'
        }
      }
    }   
    stage('Deploy To Stg'){                                                              
      steps {
        //sh 'ansible -m ping stg_app_servers'
        //sh "ansible-playbook deployPlaybook.yml -e var_enviro=STG -e var_image=${env.IMAGE_URL} -e var_service_name=${env.ServiceName} -e var_hosts=stg_app_servers -e var_port=${env.PORT}"
        sh "echo pass"
      }
    }
    stage('Check Stage Service Up') {
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }
    stage('Approval to Prod'){
      steps {
        timeout(time:3600, unit:'SECONDS') {  // DAYS , MINUTES
             input 'Do you approve deployment to Prod?'
        }
      }
    }
    stage('Deploy To Prod'){                       
      steps {
        //sh 'ansible -m ping prod_app_servers'
        //sh "ansible-playbook deployPlaybook.yml -e var_enviro=PROD -e var_image=${env.IMAGE} -e var_service_name=${env.ServiceName} -e var_hosts=prod_app_servers -e var_port=${env.PORT}"
        sh "echo pass"
      }
    }
    stage('Check Prod Service Up') {
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }
  }
  post {
    always {
      echo 'I have finished'
    }
    success {
      echo 'I succeeded!'
    }
    unstable {
      echo 'I am unstable :/'
    }
    failure {
      echo 'I failed :('
    }
    changed {
      echo 'Things are different...'
    }
  }
}
