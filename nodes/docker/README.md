# jenkins-agent

Docker based Jenkins build agent for gryphon-zone projects, based on [the official docker-slave image](https://github.com/jenkinsci/docker-slave/)

## Coordinates
`gryphonzone/jenkins-agent`

## Configuration
When using the image as a docker cloud provider, be sure to adjust the following settings:

| Configuration | Value  |
|---------------|--------|
| Volumes | `/var/run/docker.sock:/var/run/docker.sock` |
| Remote File System Root | `/home/jenkins/agent` |
