package org.apexneural.hooks

import hudson.Extension
import org.jenkinsci.plugins.workflow.flow.FlowExecutionListener
import org.jenkinsci.plugins.workflow.flow.FlowExecution
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import groovy.time.TimeCategory

@Extension
class AutoHooks extends FlowExecutionListener {

    @Override
    void onRunning(FlowExecution execution) {
        WorkflowRun run = getRun(execution)
        if (!run) return

        def jobName = run.parent.displayName
        def buildNumber = run.number
        def startedBy = run.getCause(hudson.model.Cause$UserIdCause)?.userName ?: "Auto trigger"

        globalSlackNotifier.sendMessage("""
🟡 *Build Started*

🔧 *Job:* ${jobName}
🏗 *Build:* #${buildNumber}
👤 *Triggered by:* ${startedBy}
⏱ *Time:* ${new Date().format("hh:mm a")}
        """.stripIndent())
    }

    @Override
    void onCompleted(FlowExecution execution) {
        WorkflowRun run = getRun(execution)
        if (!run) return

        def result = run.result?.toString() ?: "UNKNOWN"
        def jobName = run.parent.displayName
        def buildNumber = run.number
        def duration = TimeCategory.minus(new Date(run.duration), new Date(0)).toString()

        if (result == "SUCCESS") {
            globalSlackNotifier.sendMessage("""
🟢 *Build Success*

🔧 *Job:* ${jobName}
🏗 *Build:* #${buildNumber}
⏱ *Duration:* ${duration}
            """.stripIndent())
        } else {
            globalSlackNotifier.sendMessage("""
🔴 *Build Failed*

🔧 *Job:* ${jobName}
🏗 *Build:* #${buildNumber}
⏱ *Duration:* ${duration}
📄 *Logs:* <${run.absoluteUrl}console>
            """.stripIndent())
        }
    }

    private WorkflowRun getRun(FlowExecution execution) {
        try {
            return execution.getOwner().getExecutable()
        } catch(Exception ignore) {
            return null
        }
    }
}
