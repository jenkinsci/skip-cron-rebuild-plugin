# Skip Cron Rebuild plugin

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/skip-cron-rebuild.svg)](https://plugins.jenkins.io/skip-cron-rebuild)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/skip-cron-rebuild-plugin.svg?label=changelog)](https://github.com/jenkinsci/skip-cron-rebuild-plugin/releases/latest)

## Introduction

This plugin allows skipping tag rebuilds when a job is triggered by a cron trigger.

Plugin supports multibranch pipelines only.

## Getting started

To enable plugin for a job:
1. Go to job settings
2. Enable the option "Skip tag rebuild on cron trigger"

By default, the plugin is turned off for all projects.

You can check which SCM tags were skipped during a build at Jenkins console log.


License information
-------------------

You can use any code from this project under the terms of [The 3-Clause BSD License](https://opensource.org/licenses/BSD-3-Clause)
