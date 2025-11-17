def call(String status = currentBuild.currentResult) {

    def colorCode = [
        "SUCCESS": "good",
        "FAILURE": "danger",
        "UNSTABLE": "warning",
        "ABORTED": "#808080"
    ]

    def message = "Job: ${env.JOB_NAME}\n" +
                  "Build: #${env.BUILD_NUMBER}\n" +
                  "Status: ${status}\n" +
                  "URL: ${env.BUILD_URL}"

    slackSend(
        channel: 'jenkins-testing',
        color: colorCode[status],
        message: message
    )
}
