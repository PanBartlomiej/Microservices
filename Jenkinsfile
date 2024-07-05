pipeline {
    agent any
    stages {
        stage('Clone repository') {
            steps {
                script {
                    echo 'Clone repository'
                    sh """
                    ssh -i ${SSH_KEY} ${SSH_USER}@${SSH_URL} 'git clone https://github.com/PanBartlomiej/Microservices.git'
                    """
                }
            }
        }
            stage('Deploy to Remote Server') {
                steps {
                    script {
                        echo 'Deploying to remote server' {
                            sh """
                                ssh -i ${SSH_KEY} ${SSH_USER}@${SSH_URL} 'docker-compose -f Microservices/docker-compose.yml up -d'
                            """
                        }
                    }
                }
            }

        stage('Run Postman Tests') {
            steps {
                script {
                    echo 'Running Postman tests'
                    sh """
                       ssh -i ${SSH_KEY} ${SSH_USER}@${SSH_URL} 'newman run ./postman_collection/Gateway.postman_collection.json --environment ./postman_collection/pracaMagisterska.postman_environment.json'
                        """
            }
        }
    }
    post {
        success {
            echo 'Deployment and tests successful!'
        }
        failure {
            echo 'Deployment or tests failed.'
        }
    }
}