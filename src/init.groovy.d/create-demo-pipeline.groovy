import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def jobName = "demo-pipeline"
def jenkinsfile = new File("/var/jenkins_home/seed/Jenkinsfile")

if (!jenkinsfile.exists()) {
  println "[seed] ${jenkinsfile} not found, skipping job creation"
  return
}

def jenkins = Jenkins.instance
def job = jenkins.getItemByFullName(jobName, WorkflowJob)
def definition = new CpsFlowDefinition(jenkinsfile.text, false)

if (job == null) {
  job = jenkins.createProject(WorkflowJob, jobName)
  job.definition = definition
  job.save()
  println "[seed] Created pipeline job: ${jobName}"
  job.scheduleBuild()
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
