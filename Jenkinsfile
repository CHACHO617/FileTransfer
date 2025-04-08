pipeline {
    agent any

    stages {
        stage('Clonar repositorio') {
            steps {
                git 'https://github.com/CHACHO617/FileTransfer.git'
            }
        }

        stage('Verificar estructura del proyecto') {
            steps {
                echo "📁 Listando archivos en el proyecto"
                bat 'dir'
            }
        }

        stage('Step 2 - Ejecutar aplicación Spring Boot') {
            steps {
                echo "🚀 Iniciando Spring Boot..."
                bat 'mvn spring-boot:run'
            }
        }
    }

    post {
        failure {
            echo "❌ Falló la ejecución del pipeline."
        }
    }
}
