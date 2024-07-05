pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS_ID = 'docker-credentials' // Nazwa danych uwierzytelniających do Dockera
        DOCKER_IMAGE_TAG = 'latest' // Tag obrazu Docker
    }
    stages {
        stage('Clone repository') {
            steps {
                git branch: 'master', url: 'https://github.com/PanBartlomiej/Microservices'
            }
        }
        stage('Build and Push Docker Images') {
            steps {
                script {
                    def services = ['ewidencja', 'graf', 'monitoring', 'gateway', 'config-server', 'frontend']

                    services.each { service ->
                        dir("${service}") {
                            echo "Building Docker image for ${service}"
                            def imageName = "panbartlomiej/${service}:latest"
                            sh "docker build -t ${imageName} ."
                            sh "docker login -u ${DOCKER_HUB_CREDENTIALS_USR} -p ${DOCKER_HUB_CREDENTIALS_PSW}"
                            sh "docker push ${imageName}" // Tylko jeśli używasz Docker Hub
                        }
                        
                    }
                }
            }
        }
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo 'Deploying with Docker Compose'
                    sh 'docker-compose down'
                    sh 'docker-compose up -d --build'
                }
            }
        }
        stage('Run Postman Tests') {
            steps {
                script {
                    echo 'Running Postman tests'
                    sh '''
                        docker run --rm -v ${WORKSPACE}/postman_collection:/etc/newman -t postman/newman run "/etc/newman/Ewidencja urządzeń sieciowych.postman_collection.json" -e "/etc/newman/pracaMagisterska.postman_environment.json"
                    '''
                    sh '''
                        docker run --rm -v ${WORKSPACE}/postman_collection:/etc/newman -t postman/newman run "/etc/newman/Graf połączeń logicznych.postman_collection.json" -e "/etc/newman/pracaMagisterska.postman_environment.json"
                    '''
                }
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
