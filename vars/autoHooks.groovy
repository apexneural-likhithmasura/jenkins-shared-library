def call() {

    // Send STARTED notification
    globalSlackNotifier("STARTED", "Build Started")

    // Listener called when build finishes
    def listener = { run, listener ->

        def result = run.getResult().toString()

        if (result == "SUCCESS") {
            globalSlackNotifier("SUCCESS", "Build Succeeded")
        } 
        else if (result == "FAILURE") {
            globalSlackNotifier("FAILURE", "Build Failed")
        } 
        else if (result == "UNSTABLE") {
            globalSlackNotifier("UNSTABLE", "Build Unstable")
        }
        else if (result == "ABORTED") {
            globalSlackNotifier("ABORTED", "Build Aborted")
        }
    }

    // Attach listener globally to Jenkins
    def ext = jenkins.model.Jenkins.instance.getExtensionList(
        org.jenkinsci.plugins.workflow.flow.FlowExecutionListener
    )
    ext.add(new org.jenkinsci.plugins.workflow.flow.FlowExecutionListener() {
        @Override
        void onCompleted(org.jenkinsci.plugins.workflow.flow.FlowExecution exec, org.jenkinsci.plugins.workflow.Job job, java.util.List actions) {
            listener(exec.owner.run, null)
        }
    })
}

