package ru.omniverse.jenkins.skip_cron_rebuild;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertTrue;

public class SkipCronRebuildTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testConfig() throws Exception {
        assertTrue(true);
    }
}