pipeline {
 environment {
   jobBaseName = "${env.JOB_NAME}".split('/').first()
 }
 agent {
        docker {
            image 'maven:3-alpine'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
 options {
      timeout(time: 10, unit: 'MINUTES') 
  }  
 stages {
   stage('Build') {
     steps {
       echo "Building Chatter"
       sh 'mvn -f Project_src/Chatter/pom.xml install'
       echo "Building Prattle"
       sh 'mvn -f Project_src/Prattle/pom.xml compile'
     }
   }

   stage('SonarQube') {
    steps {
      withSonarQubeEnv('SonarQube') {
        sh 'mvn -f Project_src/Prattle/pom.xml clean install'
        sh 'mvn -f Project_src/Prattle/pom.xml sonar:sonar -Dsonar.projectKey=${jobBaseName} -Dsonar.projectName=${jobBaseName}'
      }

      sh 'sleep 30'
      timeout(time: 10, unit: 'SECONDS') {
       retry(5) {
        script {
          def qg = waitForQualityGate()
          if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
          }
        }
      }
    }
  }
}
}


 post {
     always {
            archive 'target/**/*.jar'
            }       
    success {
           slackSend (baseUrl: "https://cs5500.slack.com/services/hooks/jenkins-ci/", token: "84SFK4SF1EC4Pqdd2NzjXhCi", channel: "#cs5500-team-203-f18", color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME}")
            }
    failure {  
           slackSend (baseUrl: "https://cs5500.slack.com/services/hooks/jenkins-ci/", token: "84SFK4SF1EC4Pqdd2NzjXhCi", channel: "#cs5500-team-203-f18", color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME}")
            }
       }
}
