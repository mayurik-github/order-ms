pipeline {
    agent any
    tools {
        jdk 'JDK-17' 
        maven 'maven'
    }
    
    environment {
        DOCKER_USER = 'mayurikulkarni2024' 
        IMAGE_NAME = 'order-ms'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/mayurik-github/order-ms.git'
            }
        }
        
        stage('Build Spring Boot Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build -t $DOCKER_USER/$IMAGE_NAME .
                    docker tag $DOCKER_USER/$IMAGE_NAME $DOCKER_USER/$IMAGE_NAME:latest 
                '''
            }
        }
        
        stage('Push Docker Image') {
            steps {
                withDockerRegistry([credentialsId: 'dockerhub-credentials', url: 'https://index.docker.io/v1/']) {
                    sh 'docker push $DOCKER_USER/$IMAGE_NAME:latest'
                }
            }
        }
    }
}