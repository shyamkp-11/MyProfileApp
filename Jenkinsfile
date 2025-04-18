pipeline {
    agent any
    environment {
        WEBAPP_CREDENTIALS=credentials('MyProfileApp-secrets');
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
                    env.DEPLOY_LOCALLY = false
                }
            }
        }
        stage('Docker') {
            when {
                expression {
                    return false
                }
            }
            steps {
                sh '''
docker build --tag shyamkp4/my-profile-app .
mkdir -p .m2
                '''
            }
        }
        stage('Build project') {
            agent {
                docker {
                    image 'maven:3.9.9-amazoncorretto-21'
                    reuseNode true
                }
            }
            steps {
                sh '''
mvn -Dmaven.repo.local=.m2/repository -DskipTests clean install -e
                '''
            }
        }
        stage ('Build dockerImage') {
            when {
                beforeAgent true;
                expression {
                    return env.DEPLOY_LOCALLY.toBoolean() == true;
                }
            }
            steps {
                echo "Building docker image"
                sh '''
ls -lrt
docker image build -t deployed_my_profile_app:$BUILD_NUMBER .
docker images
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
        stage('Archive Artifacts') {
            when {
                expression {
                    return false
                }
            }
            steps {
                archiveArtifacts artifacts: 'target/*.war', allowEmptyArchive: false, fingerprint: true, onlyIfSuccessful: true
            }
        }
        stage('Deploy') {
            when {
                 beforeAgent true;
                 expression {
                     return env.DEPLOY_LOCALLY.toBoolean() == false;
                 }
            }
            steps {
                // todo change hard coded name
                sh '''
mkdir -p .ebextensions
cat >.ebextensions/environment.config <<EOL
option_settings:
    - namespace: aws:autoscaling:launchconfiguration
      option_name: SecurityGroups
      value: sg-074f3dd3d21cab7d9
    - namespace: aws:elbv2:loadbalancer
      option_name: SecurityGroups
      value: sg-074f3dd3d21cab7d9
    - namespace: aws:elbv2:loadbalancer
      option_name: ManagedSecurityGroup
      value: sg-074f3dd3d21cab7d9
    - option_name: spring.mail.host
      value: $SPRING_MAIL_HOST
    - option_name: spring.mail.password
      value: $SPRING_MAIL_PASSWORD
    - option_name: spring.mail.port
      value: $SPRING_MAIL_PORT
    - option_name: spring.mail.username
      value: $SPRING_MAIL_USERNAME
    - option_name: MAIL_FROM_ADDRESS
      value: $MAIL_FROM_ADDRESS
    - option_name: RECAPTCHA_SITE_KEY
      value: $RECAPTCHA_SITE_KEY
    - option_name: RECAPTCHA_SECRET_KEY
      value: $RECAPTCHA_SECRET_KEY
    - option_name: spring.datasource.password
      value: $WEBAPP_DATASOURCE_PASSWORD
    - option_name: spring.datasource.username
      value: $WEBAPP_DATASOURCE_USERNAME
    - option_name: spring.datasource.url
      value: $WEBAPP_DATASOURCE_URL
    - option_name: SERVER_PORT
      value: 8080
EOL
# Because if not target directory won't be uploaded to eb instance's docker image
cat >.ebignore <<EOL
src/
.git/
EOL
eb init -p docker -r us-east-2 -k DeployEC2KeyPair MyProfileAppEBS
if eb status | grep -q " Application name: MyProfileAppEBS"; then
eb deploy MyProfileAppEBS -l $BUILD_NUMBER
fi
#else
#eb create MyProfileAppEBS --vpc.publicip --vpc.elbpublic --instance_profile iam-ebs-role --instance-types t2.micro --enable-spot  --vpc.id vpc-011aed36112c9889e --vpc.ec2subnets subnet-0a83820bfcd09082e --vpc.elbsubnets subnet-08f62229703fb2168,subnet-0a83820bfcd09082e --vpc.securitygroups sg-074f3dd3d21cab7d9
#fi
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