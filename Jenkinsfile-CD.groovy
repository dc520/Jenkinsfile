pipeline {
  environment {
    APP = ""
  }

  agent any

  stages {

    stage('Setup') {
      steps {
        script {
          if (env.ENV == 'DEV') {
            
          } 

        }
      }
    }

    stage('Deploy To Dev'){
      when {
          expression { env.ENV == "DEV" }
      }
      steps {
        sh 'ansible -m ping dev_app_servers'
        sh "ansible-playbook deployPlaybook.yml -e var_enviro=DEV -e var_image_name=${env.IMAGE} -e var_service_name=${env.ServiceName} -e var_hosts=dev_app_servers -e var_port=${env.PORT}"
      }
    }

    stage('Check Dev Service Up') {
      when {
          expression { env.ENV == "DEV" }
      }
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }
    
    stage('Deploy To Test'){
      when {
          expression { env.ENV == "TEST" }
      }
      steps {
        sh 'ansible -m ping test_app_servers'
        sh "ansible-playbook deployPlaybook.yml -e var_enviro=TEST -e var_image_name=${env.IMAGE} -e var_service_name=${env.ServiceName} -e var_hosts=test_app_servers -e var_port=${env.PORT}"
      }
    }

    stage('Check Test Service Up') {
      when {
          expression { env.BRANCH_NAME.contains('VINBOM') }
      }
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }

    stage('Deploy To Stg'){
      when {
          expression { (env.ENV == "STG" }
      }
      steps {
        sh 'ansible -m ping stg_app_servers'
        sh "ansible-playbook deployPlaybook.yml -e var_enviro=STG -e var_image_name=${env.IMAGE} -e var_service_name=${env.ServiceName} -e var_hosts=stg_app_servers -e var_port=${env.PORT}"
      }
    }

    stage('Check Stage Service Up') {
      when {
          expression { (env.ENV == "STG" }
      }
      steps {
        //sh 'xx' //Send http request to the already deployed service for health check.
        echo "pass"
      }
    }

    stage('Approval to Prod'){
      when {
          expression { env.BRANCH_NAME == 'master' && env.COUNTRY == 'cn' }
      }
      steps {
        timeout(time:3600, unit:'SECONDS') {  // DAYS , MINUTES
             input 'Do you approve deployment to Staging?'
        }
      }
    }


    stage('Deploy To Prod'){
      when {
          expression { (env.ENV == "PROD" }
      }
      steps {
        sh 'ansible -m ping prod_app_servers'
        sh "ansible-playbook deployPlaybook.yml -e var_enviro=PROD -e var_image_name=${env.IMAGE} -e var_service_name=${env.ServiceName} -e var_hosts=prod_app_servers -e var_port=${env.PORT}"
      }
    }

    stage('Check Prod Service Up') {
      when {
          expression expression { (env.ENV == "PROD" }
      }
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
