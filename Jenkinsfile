pipeline {
    agent any

    stages {
        stage('Clonar repositorio') {
            steps {
                git branch: 'main', url: 'https://github.com/CHACHO617/FileTransfer.git'
            }
        }

        stage('Step 1 - Verificar estructura del proyecto') {
            steps {
                echo "📁 Listando archivos en el proyecto"
                sh 'ls -la'
            }
        }

        stage('Step 2 - Ejecutar aplicación Spring Boot') {
            steps {
                echo "🚀 Ejecutando aplicación con Maven"
                sh 'mvn spring-boot:run'
            }
        }
    }

    post {
        success {
            echo '✅ La aplicación se ejecutó correctamente.'
        }
        failure {
            echo '❌ Falló la ejecución del pipeline.'
        }
    }
}
