import org.jenkinsci.plugins.workflow.flow.FlowExecutionListener
import org.jenkinsci.plugins.workflow.flow.FlowExecution
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import groovy.time.TimeCategory

class autoHooks extends FlowExecutionListener {

    @Override
    void onRunning(FlowExecution execution) {
        WorkflowRun run = getRun(execution)
        if (run == null) return

        def jobName = run.parent.displayName
        def buildNumber = run.number
        def startedBy = run.getCause(hudson.model.Cause$UserIdCause)?.userName ?: "Auto trigger"

        globalSlackNotifier.sendMessage("""
🟡 *Build Started*

🔧 *Job:* ${jobName}
🏗 *Build:* #${buildNumber}
👤 *Triggered by:* ${startedBy}
⏱ *Time:* ${new Date().format("hh:mm a")}
        """)
    }

    @Override
    void onCompleted(FlowExecution execution) {
        WorkflowRun run = getRun(execution)
        if (run == null) return

        def result = run.result?.toString() ?: "UNKNOWN"
        def jobName = run.parent.displayName
        def buildNumber = run.number
        def durationMs = run.duration

        // Convert duration
        String duration = TimeCategory.minus(new Date(durationMs), new Date(0)).toString()

        if (result == "SUCCESS") {
            globalSlackNotifier.sendMessage("""
🟢 *Build Success*

🔧 *Job:* ${jobName}
🏗 *Build:* #${buildNumber}
⏱ *Duration:* ${duration}
            """)
        } else {
            globalSlackNotifier.sendMessage("""
🔴 *Build Failed*

🔧 *Job:* ${jobName}
🏗 *Build:* #${buildNumber}
⏱ *Duration:* ${duration}
📄 *Logs:* <${run.absoluteUrl}console>
            """)
        }
    }

    private WorkflowRun getRun(FlowExecution execution) {
        try {
            return execution.getOwner().getExecutable()
        } catch (Exception ignored) {
            return null
        }
    }
}
