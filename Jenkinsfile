pipeline {
	agent any
    environment {
		WEBAPP_CREDENTIALS = credentials('MyProfileApp-secrets');
    }
    stages {
		stage ('Init') {
			steps {
				script {
					def props = readProperties file: env.WEBAPP_CREDENTIALS
                    env.SPRING_MAIL_HOST = props.SPRING_MAIL_HOST
                    env.SPRING_MAIL_PASSWORD = props.SPRING_MAIL_PASSWORD
                    env.SPRING_MAIL_PORT = props.SPRING_MAIL_PORT
                    env.SPRING_MAIL_USERNAME = props.SPRING_MAIL_USERNAME
                    env.MAIL_FROM_ADDRESS = props.MAIL_FROM_ADDRESS
                    env.RECAPTCHA_SITE_KEY = props.RECAPTCHA_SITE_KEY
                    env.RECAPTCHA_SECRET_KEY = props.RECAPTCHA_SECRET_KEY
                    env.WEBAPP_DATASOURCE_URL = props.WEBAPP_DATASOURCE_URL
                    env.WEBAPP_DATASOURCE_USERNAME = props.WEBAPP_DATASOURCE_USERNAME
                    env.WEBAPP_DATASOURCE_PASSWORD = props.WEBAPP_DATASOURCE_PASSWORD
                    env.AWS_ACCESS_KEY_ID_ENV = props.AWS_ACCESS_KEY_ID_ENV
                    env.AWS_ACCESS_KEY_ENV = props.AWS_ACCESS_KEY_ENV
                    env.AWS_DEFAULT_REGION_ENV = props.AWS_DEFAULT_REGION_ENV
                    env.S3_WAR_PATH = props.S3_WAR_PATH
                    env.DEPLOY_LOCALLY = false
                    env.DOCKER_PAT = props.DOCKER_PAT
                    env.DOCKER_USERNAME = props.DOCKER_USERNAME
                    env.SERVER_PORT = 8081
                    //env.GITHUB_PAT = credentials('GithubPAT')
                    env.GITHUB_TOKEN = props.GITHUB_PAT
                }
            }
        }
        stage('Docker') {
			when {
				expression {
					return true
                }
            }
            steps {
				sh '''
docker build --tag shyamkp4/upload-github-release -f docker/Dockerfile .
#docker build --tag shyamkp4/my-profile-app .
#mkdir -p .m2
                '''
            }
        }
        stage('Build project') {
			agent {
				docker {
					image 'shyamkp4/upload-github-release'
					//args '-e GITHUB_TOKEN=${env.GITHUB_TOKEN}'
					reuseNode true
				}
            }
            steps {
				sh '''
mvn -Dmaven.repo.local=.m2/repository -DskipTests clean install -e
                '''
            }
        }
        stage('Upload Github release') {
        when {
        	expression {
        		return true
                }
            }
			agent {
				docker {
					image 'shyamkp4/upload-github-release'
					//args '-e GITHUB_TOKEN=${env.GITHUB_TOKEN}'
					reuseNode true
				}
			}
			when {
				beforeAgent true;
				expression {
					def scriptOutput = sh(returnStdout: true, script: '''
                    #!/bin/bash
                    commit1=$(git rev-list -1 $(git describe --tags --abbrev=0));
                    commit2=$(git rev-parse HEAD);
                    if [ "$commit1" = "$commit2" ]; then
                        echo "true"
                    else
                        echo "false"
                    fi''').trim()
                    echo "$scriptOutput"
                    return scriptOutput == "true"
                }
            }
            steps {
				script {
					def tagName = sh(returnStdout: true, script:'git describe --tags --abbrev=0').trim()
                    def commitish = sh(returnStdout: true, script:'git rev-parse HEAD').trim()
                    sh """
#echo $GITHUB_TOKEN
# Check if the release already exists
if gh release view "${tagName}" &>/dev/null; then
    echo "Release ${tagName} already exists. Skipping creation."
else
    # Create the release if it does not exist
    echo "Creating release ${tagName}..."
    gh release create ${tagName} target/MyProfileApp.jar
fi
"""
                }
            }
        }
        stage ('Build dockerImage') {
			when {
				beforeAgent true;
                expression {
					return true;
                }
            }
            steps {
				echo "Building docker image"
                sh '''
ls -lrt
cat >entrypoint.sh <<EOL
java -jar MyProfileApp.jar --spring.datasource.url=$WEBAPP_DATASOURCE_URL --spring.datasource.username=$WEBAPP_DATASOURCE_USERNAME --spring.datasource.password=$WEBAPP_DATASOURCE_PASSWORD --spring.mail.host=$SPRING_MAIL_HOST --spring.mail.password="$SPRING_MAIL_PASSWORD" --spring.mail.port=$SPRING_MAIL_PORT --spring.mail.username=$SPRING_MAIL_USERNAME --RECAPTCHA_SITE_KEY=$RECAPTCHA_SITE_KEY --RECAPTCHA_SECRET_KEY=$RECAPTCHA_SECRET_KEY --server.port=$SERVER_PORT --MAIL_FROM_ADDRESS=$MAIL_FROM_ADDRESS
EOL
docker image build -t shyamkp4/deployed_my_profile_app:$BUILD_NUMBER -t shyamkp4/deployed_my_profile_app:latest .
docker images
                '''
            }
        }

        stage('Upload Docker image'){
            when {
                beforeAgent true;
                expression {
        	        return true;
                }
            }
            steps {
                echo "Uploading docker image"
                sh '''
echo "$DOCKER_PAT" | docker login --username $DOCKER_USERNAME --password-stdin
docker push shyamkp4/deployed_my_profile_app --all-tags
                '''
            }
        }
        stage('Deploy locally') {
			when {
				beforeAgent true;
                expression {
					return env.DEPLOY_LOCALLY.toBoolean() == true;
                }
            }
            steps {
				echo "Running locally"
                script {
					def inspectExitCode = sh script: "docker container inspect deployed_my_profile_app", returnStatus: true
                if (inspectExitCode == 0) {
						// remove container if exist
                    sh "docker stop deployed_my_profile_app"
                    sh "docker rm deployed_my_profile_app"
                    }
                }
                sh '''docker run -d -p 0.0.0.0:8081:8080 \
--name deployed_my_profile_app \
-e RECAPTCHA_SITE_KEY=$RECAPTCHA_SITE_KEY \
-e RECAPTCHA_SECRET_KEY=$RECAPTCHA_SECRET_KEY \
-e spring.datasource.url=$WEBAPP_DATASOURCE_URL \
-e spring.datasource.username=$WEBAPP_DATASOURCE_USERNAME \
-e spring.datasource.password=$WEBAPP_DATASOURCE_PASSWORD \
-e spring.mail.host=$SPRING_MAIL_HOST \
-e spring.mail.password="$SPRING_MAIL_PASSWORD" \
-e spring.mail.port=$SPRING_MAIL_PORT \
-e spring.mail.username=$SPRING_MAIL_USERNAME \
-e MAIL_FROM_ADDRESS=$MAIL_FROM_ADDRESS \
-e SERVER_PORT='8080' \
deployed_my_profile_app:$BUILD_NUMBER'''
            }
        }
        stage('Test') {
			steps {
				sh '''
echo Testing
                '''
            }
        }
    }
    post {
		success {
			sh '''
echo post on Success

'''
            // archiveArtifacts artifacts: 'build/**'
        }
        // always {
        // }
    }
}