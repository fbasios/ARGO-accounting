pipeline {
    agent none
    options {
        checkoutToSubdirectory('accounting.system')
        newContainerPerStage()
    }
    environment {
        PROJECT_DIR='accounting.system'
        GH_USER = 'newgrnetci'
        GH_EMAIL = '<argo@grnet.gr>'
    }
    stages {
        stage('Accounting System API Packaging & Testing') {
            agent {
                docker {
                    image 'argo.registry:5000/rocky9-java17-mvn3.9.9:latest'
                    args '-v $HOME/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -u root:root'
                }
            }
            steps {
                echo 'Accounting System API Packaging & Testing'
                sh """
                cd ${WORKSPACE}/${PROJECT_DIR}
                mvn clean install -DskipTests=true -U

                mvn clean package -Dquarkus.package.type=uber-jar -am

                mkdir -p dist
                cp api/target/*-runner.jar dist/

                mkdir -p reports/surefire
                cp -r api/target/surefire-reports/. reports/surefire/ || true

                mvn clean package -Dquarkus.package.type=uber-jar -am -Pcredit-management -DskipTests=true
                
                cp api/target/*-runner.jar dist/
                """
                junit 'reports/surefire/*.xml'
                archiveArtifacts artifacts: '**/dist/*.jar'
                step( [ $class: 'JacocoPublisher' ] )
            }
            post {
                always {
                    cleanWs()
                }
            }
        }
    }
    post {
        success {
            script{
                if ( env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rocket: New version for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME !")
                }
            }
        }
        failure {
            script{
                if ( env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rain_cloud: Build Failed for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME")
                }
            }
        }
    }
}
