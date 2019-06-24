# Jenkins

Custom [Jenkins](https://github.com/jenkinsci/jenkins) Docker image which is based on the 
[official Jenkins Docker image](https://github.com/jenkinsci/docker), 
which is used for performing CI/CD on projects in the
[gryphon-zone](https://github.com/gryphon-zone/) organization (as well as a few others).

Comes with a [collection of plugins](configuration/plugins.txt) pre-installed, as well as some
[default security setup](configuration/setup-default-security.groovy) applied.

## Systemd Integration
There is also a [systemd unit file](systemd/jenkins.service) provided.

Assuming you cloned this repo into the folder `~/git/gryphon-zone/jenkins`, you can enable it with the following command:
```bash
sudo ln -s ~/git/gryphon-zone/jenkins/systemd/jenkins.service /etc/systemd/system/jenkins.service
```
After that, you can run
```bash
sudo systemctl start jenkins
```
to launch the Jenkins master.
It can then be reached at [localhost:8080](http://localhost:8080)
(assuming you're enabling it on your local machine)

If you want Jenkins to run when the system boots, run
```bash
sudo systemctl enable jenkins
```

## Default Security Setup

When booted for the first time, the Jenkins instance will apply the following security settings:
1. Enable [role based authorization](https://plugins.jenkins.io/role-strategy) on the instance
1. Create a default `admin` user with a random password
1. Give the default `admin` user administrative privileges over the instance

It is recommended that after logging in for the first time, you create a new user with administrative privileges,
and then delete the default `admin` user, to reduce the feasibility of a brute-force password attack.
  