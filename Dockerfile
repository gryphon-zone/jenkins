# Copyright 2019-2019 Gryphon Zone
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# File based on documentation from https://github.com/jenkinsci/docker/blob/master/README.md
FROM jenkins/jenkins:lts

# switch to root for installing packages
USER root

RUN apt update && \
    apt install -y apt-utils && \
    apt upgrade -y && \
    apt dist-upgrade -y && \
    apt install -y gpg curl && \
    apt autoremove -y && \
    apt autoclean -y && \
    apt clean -y && \
    rm -rv /var/cache/apt/archives/* && \
    rm -rf /var/lib/apt/lists/* && \
    curl -fsSL https://get.docker.com | sh && \
    usermod -aG docker jenkins

# drop back to the regular jenkins user
USER jenkins

# Pre-install desired plugins
# https://github.com/jenkinsci/docker/blob/master/README.md#script-usage
COPY configuration/plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Disable installation of plugins during setup wizard
COPY configuration/disable-plugin-install-wizard.groovy /usr/share/jenkins/ref/init.groovy.d/

# Configure executors
# https://github.com/jenkinsci/docker/blob/master/README.md#setting-the-number-of-executors
COPY configuration/executors.groovy /usr/share/jenkins/ref/init.groovy.d/

# Configure security for the instance
COPY configuration/setup-default-security.groovy /usr/share/jenkins/ref/init.groovy.d/
