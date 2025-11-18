def call(status, message) {

    def color = (status == "SUCCESS") ? "good" :
                (status == "FAILURE") ? "danger" :
                (status == "UNSTABLE") ? "#FFA500" :
                (status == "ABORTED") ? "#AAAAAA" :
                "#439FE0"   // default blue

    slackSend(
        channel: "jenkins-testing",
        color: color,
        message: "${env.JOB_NAME} (#${env.BUILD_NUMBER}) - ${message}\n${env.BUILD_URL}"
    )
}
