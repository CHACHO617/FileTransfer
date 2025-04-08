pipeline {
    agent any

    stages {
        stage('Verificar estructura del proyecto') {
            steps {
                echo "📁 Listando archivos en el proyecto"
                bat 'dir'
                bat 'git branch'
                bat 'git log -1'
            }
        }

        stage('Step 2 - Ejecutar aplicación Spring Boot') {
            steps {
                script {
                    echo "🚀 Verificando si existe pom.xml..."
                    def pomExists = fileExists('pom.xml')
                    if (pomExists) {
                        echo "✅ pom.xml encontrado. Iniciando Spring Boot..."
                        bat 'mvn spring-boot:run'
                    } else {
                        error("❌ No se encontró el archivo pom.xml. Abortando ejecución.")
                    }
                }
            }
        }
    }

    post {
        failure {
            echo "❌ Falló la ejecución del pipeline."
        }
    }
}
