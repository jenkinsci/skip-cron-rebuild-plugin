package ru.omniverse.jenkins.skip_cron_rebuild;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Queue;
import hudson.triggers.TimerTrigger;
import jenkins.branch.Branch;
import jenkins.scm.api.mixin.TagSCMHead;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.logging.Logger;

@Restricted(NoExternalUse.class)
public class SkipCronRebuild extends AbstractFolderProperty<AbstractFolder<?>>  {
    private static final Logger LOGGER = Logger.getLogger(SkipCronRebuild.class.getName());

    private boolean skipTagRebuild = false;

    @DataBoundConstructor
    public SkipCronRebuild() {
    }

    public boolean isSkipTagRebuild() {
        return skipTagRebuild;
    }

    @DataBoundSetter
    public void setSkipTagRebuild(boolean skipTagRebuild) {
        this.skipTagRebuild = skipTagRebuild;
    }

    @Symbol("skipCronRebuild")
    @Extension
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {
        @Override
        @Nonnull
        public String getDisplayName() {
            return Messages.skipCronRebuild_displayName();
        }
    }

    @Extension
    public static class Dispatcher extends Queue.QueueDecisionHandler {
        @Override
        public boolean shouldSchedule(Queue.Task p, List<Action> actions) {
            // Work only for multibranch jobs
            if (!(p instanceof WorkflowJob && ((WorkflowJob) p).getParent() instanceof WorkflowMultiBranchProject))
                return true;

            WorkflowJob wj = (WorkflowJob) p;
            WorkflowMultiBranchProject multiBranchProject = (WorkflowMultiBranchProject) wj.getParent();

            // Check property
            boolean hasProperty = false;
            for (AbstractDescribableImpl<?> prop : multiBranchProject.getProperties()) {
                if (prop instanceof SkipCronRebuild) {
                    SkipCronRebuild theProp = (SkipCronRebuild) prop;
                    hasProperty = true;
                    if (!theProp.skipTagRebuild) {
                        // no need to limit
                        return true;
                    }
                }
            }
            if (!hasProperty) {
                // not configured
                return true;
            }

            // check timer action
            boolean hasTimerTriggerCause = false;
            for (Action action : actions) {
                if (action instanceof CauseAction) {
                    for (Cause c : ((CauseAction) action).getCauses()) {
                        if (c instanceof TimerTrigger.TimerTriggerCause) {
                            hasTimerTriggerCause = true;
                            break;
                        }
                    }
                }
            }
            if (!hasTimerTriggerCause)
                return true;

            // Check if this job going to build a tag SCM
            // If there is at least one run, deny it
            Branch branch = multiBranchProject.getProjectFactory().getBranch(wj);
            if (branch.getHead() != null && branch.getHead() instanceof TagSCMHead) {
                if (wj.getLastBuild() != null) {
                    LOGGER.info(String.format("Denying timer scheduled job for head %s of job %s",
                            branch.getHead(), p));
                    // deny run
                    return false;
                }
            }
            // default behaviour is to return true
            return true;
        }
    }
}
