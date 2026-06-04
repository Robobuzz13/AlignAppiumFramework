pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    parameters {
        choice(name: 'PLATFORM', choices: ['android', 'ios', 'both'],
               description: 'Target platform')
        choice(name: 'ENV', choices: ['cloud', 'local'],
               description: 'Execution environment')
        booleanParam(name: 'PARALLEL', defaultValue: true,
                     description: 'Run tests in parallel')
        booleanParam(name: 'VIDEO_RECORDING', defaultValue: false,
                     description: 'Enable video recording')
        string(name: 'APP_PATH', defaultValue: '',
               description: 'App binary path or cloud app ID (bs://...)')
        string(name: 'APP_URL', defaultValue: '',
               description: 'Download URL for app binary (optional)')
        string(name: 'BUILD_NAME', defaultValue: "${env.BUILD_TAG}",
               description: 'Build label for cloud reports')
    }

    environment {
        CLOUD_URL = credentials('browserstack-url')
        CLOUD_KEY = credentials('browserstack-key')
        BUILD_NAME = "${params.BUILD_NAME ?: env.BUILD_TAG}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn install -DskipTests -pl core,android,ios,tests'
            }
        }

        stage('Test') {
            steps {
                script {
                    def suite = params.PARALLEL
                            ? 'src/test/resources/suites/parallel-suite.xml'
                            : "src/test/resources/suites/${params.PLATFORM}-suite.xml"

                    if (params.PLATFORM == 'both' && !params.PARALLEL) {
                        runTests('android', 'src/test/resources/suites/android-suite.xml')
                        runTests('ios', 'src/test/resources/suites/ios-suite.xml')
                    } else {
                        runTests(params.PLATFORM, suite)
                    }
                }
            }
        }

        stage('Reports') {
            steps {
                sh 'mvn allure:report -pl tests'
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'tests/target/extent-reports',
                    reportFiles: 'report.html',
                    reportName: 'Extent Report'
                ])
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'tests/target/allure-results']]
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'tests/target/videos/**/*.mp4',
                             allowEmptyArchive: true
            archiveArtifacts artifacts: 'tests/target/screenshots/**/*.png',
                             allowEmptyArchive: true
            archiveArtifacts artifacts: 'tests/target/logs/**/*.log',
                             allowEmptyArchive: true
        }
    }
}

def runTests(String platform, String suite) {
    sh """
        mvn test -pl tests \\
          -Dplatform=${platform} \\
          -Denv=${params.ENV} \\
          -Dsuite=${suite} \\
          -Dvideo.recording.enabled=${params.VIDEO_RECORDING} \\
          -Dapp.path="${params.APP_PATH}" \\
          -Dapp.url="${params.APP_URL}"
    """
}
