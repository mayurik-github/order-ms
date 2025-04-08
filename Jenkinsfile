pipeline {
    agent any
    tools {
        jdk 'JDK-17' 
        maven 'maven'
    }
    
    environment {
        DOCKER_USER = 'mayurikulkarni2024' 
        IMAGE_NAME = 'order-ms'
        KUBE_CONFIG = "/home/ubuntu/.kube/config"
        AWS_REGION = "us-west-1"
        EKS_CLUSTER_NAME = "order-cluster"
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
        
        stage('Deploy to EKS') {
	    	steps {
		        script {
		            withCredentials([[
		                $class: 'AmazonWebServicesCredentialsBinding',
		                credentialsId: 'aws-creds',  // Jenkins credential ID for AWS keys
		                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
		                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
		            ]]) {
		            	env.PATH = "/usr/local/bin:$PATH"
		                sh '''
		                    # Configure kubectl for EKS
		                    aws eks update-kubeconfig \
		                      --region ${AWS_REGION} \
		                      --name ${EKS_CLUSTER_NAME}
		
		                    # Apply manifests
		                    kubectl apply -f k8s/configmap.yaml
		                    kubectl apply -f k8s-manifests/deployment.yaml
		                    kubectl apply -f k8s-manifests/service.yaml
		                '''
		            }
	        	}
	    	}
	    }
	}
}