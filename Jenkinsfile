pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        echo 'compiling....'
        sh 'mvn compile'
      }
    }

    stage('package') {
      parallel {
        stage('package') {
          steps {
            echo 'packaging....'
            sh 'mvn clean package -DskipTests'
          }
        }
      }
    }
  }
  tools {
    maven 'Maven 3.9.13'
  }
  post {
    always {
      echo 'This pipeline is completed.'
    }

  }
}
