import groovy.transform.CompileStatic
import hudson.model.Cause
import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

    @CompileStatic
    Logger logger = Logger.getLogger('init.groovy.d.create-demo-pipeline')
    String jobName = 'demo-pipeline'
    Path jenkinsfile = Paths.get('/var/jenkins_home/seed/Jenkinsfile')

    if (!Files.exists(jenkinsfile)) {
        logger.info('[seed] ' + jenkinsfile + ' not found, skipping job creation')
        return
    }

    String jenkinsfileText = new String(Files.readAllBytes(jenkinsfile), StandardCharsets.UTF_8)

    // Unsandboxed pipeline scripts must be approved in Script Approval before they can run
    ScriptApproval.get().preapprove(jenkinsfileText, GroovyLanguage.get())
    logger.info('[seed] Approved pipeline script: ' + jenkinsfile)

    Jenkins jenkins = Jenkins.instance
    WorkflowJob job = jenkins.getItemByFullName(jobName, WorkflowJob)
    CpsFlowDefinition definition = new CpsFlowDefinition(jenkinsfileText, false)

    if (job == null) {
        job = jenkins.createProject(WorkflowJob, jobName)
        job.definition = definition
        job.save()
        logger.info('[seed] Created pipeline job: ' + jobName)
        // scheduleBuild() without a cause attaches Cause.LegacyCodeCause ("Legacy code started this job")
        job.scheduleBuild(new Cause.RemoteCause('init.groovy.d', 'create-demo-pipeline.groovy'))
    } else {
        String currentScript = job.definition instanceof CpsFlowDefinition ? (job.definition as CpsFlowDefinition).script : null
        if (currentScript == jenkinsfileText) {
            logger.info('[seed] Pipeline job is up to date: ' + jobName)
        } else {
            job.definition = definition
            job.save()
            logger.info('[seed] Updated pipeline definition: ' + jobName)
        }
    }
