import hudson.model.Cause
import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jobName = "demo-pipeline"
def jenkinsfile = new File("/var/jenkins_home/seed/Jenkinsfile")

if (!jenkinsfile.exists()) {
  println "[seed] ${jenkinsfile} not found, skipping job creation"
  return
}

// Unsandboxed pipeline scripts must be approved in Script Approval before they can run
ScriptApproval.get().preapprove(jenkinsfile.text, GroovyLanguage.get())
println "[seed] Approved pipeline script: ${jenkinsfile}"

def jenkins = Jenkins.instance
def job = jenkins.getItemByFullName(jobName, WorkflowJob)
def definition = new CpsFlowDefinition(jenkinsfile.text, false)

if (job == null) {
  job = jenkins.createProject(WorkflowJob, jobName)
  job.definition = definition
  job.save()
  println "[seed] Created pipeline job: ${jobName}"
  // scheduleBuild() without a cause attaches Cause.LegacyCodeCause ("Legacy code started this job")
  job.scheduleBuild(new Cause.RemoteCause("init.groovy.d", "create-demo-pipeline.groovy"))
} else {
  def currentScript = (job.definition instanceof CpsFlowDefinition) ? job.definition.script : null
  if (currentScript != jenkinsfile.text) {
    job.definition = definition
    job.save()
    println "[seed] Updated pipeline definition: ${jobName}"
  } else {
    println "[seed] Pipeline job is up to date: ${jobName}"
  }
}
