def call(body) {
    return {
        post {
            always {
                slackNotify()
            }
        }
        body.delegate = delegate
        body()
    }
}

