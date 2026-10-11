import hudson.model.Cause
import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

import java.util.logging.Logger

Logger logger = Logger.getLogger('init.groovy.d.create-demo-pipeline')
String jobName = 'demo-pipeline'
File jenkinsfile = new File('/var/jenkins_home/seed/Jenkinsfile')

if (!jenkinsfile.exists()) {
  logger.info('[seed] ' + jenkinsfile + ' not found, skipping job creation')
  return
}

// Unsandboxed pipeline scripts must be approved in Script Approval before they can run
ScriptApproval.get().preapprove(jenkinsfile.text, GroovyLanguage.get())
logger.info('[seed] Approved pipeline script: ' + jenkinsfile)

Jenkins jenkins = Jenkins.instance
WorkflowJob job = jenkins.getItemByFullName(jobName, WorkflowJob)
CpsFlowDefinition definition = new CpsFlowDefinition(jenkinsfile.text, false)

if (job == null) {
  job = jenkins.createProject(WorkflowJob, jobName)
  job.definition = definition
  job.save()
  logger.info('[seed] Created pipeline job: ' + jobName)
  // scheduleBuild() without a cause attaches Cause.LegacyCodeCause ("Legacy code started this job")
  job.scheduleBuild(new Cause.RemoteCause('init.groovy.d', 'create-demo-pipeline.groovy'))
} else {
  String currentScript = (job.definition instanceof CpsFlowDefinition) ? job.definition.script : null
  if (currentScript == jenkinsfile.text) {
    logger.info('[seed] Pipeline job is up to date: ' + jobName)
  } else {
    job.definition = definition
    job.save()
    logger.info('[seed] Updated pipeline definition: ' + jobName)
  }
}
