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

        stage('') {
          steps {
            script {
              docker.withRegistry('https://index.docker.io/v1/', 'dockerlogin') {
                def commitHash = env.GIT_COMMIT.take(7)
                def dockerImage = docker.build("ductuanbl2000/ridehub-app:${commitHash}", "./")
                dockerImage.push()
                dockerImage.push("latest")
                dockerImage.push("dev")
              }
            }

          }
        }

      }
    }

  }
  tools {
    maven 'Maven 3.9.14'
  }
  post {
    always {
      echo 'This pipeline is completed.'
    }

  }
}