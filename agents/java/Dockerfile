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
FROM openjdk:11-jdk

ARG user=agent
ARG group=agent
ARG uid=1000
ARG gid=1000
ARG home=/home/agent
ARG mavenVersion=3.6.1
ARG mavenMirror=http://www.apache.org/dist

USER root

RUN apt update && \
    apt install -y apt-utils && \
    apt upgrade -y && \
    apt dist-upgrade -y && \
    apt install -y git curl bash tar && \
    apt autoremove -y && \
    apt autoclean -y && \
    apt clean -y && \
    rm -rv /var/cache/apt/archives/* && \
    rm -rf /var/lib/apt/lists/* && \
    curl -sS --fail ${mavenMirror}/maven/maven-3/${mavenVersion}/binaries/apache-maven-${mavenVersion}-bin.tar.gz -o /tmp/maven.tar.gz && \
    tar -xf /tmp/maven.tar.gz --directory /tmp && \
    rm -fv /tmp/maven.tar.gz && \
    mv -v /tmp/apache-maven-${mavenVersion} /usr/lib && \
    ln -s /usr/lib/apache-maven-${mavenVersion}/bin/mvn /usr/bin/mvn && \
    ln -s /usr/lib/apache-maven-${mavenVersion}/bin/mvnDebug /usr/bin/mvnDebug && \
    mkdir -p $home && \
    chown ${uid}:${gid} $home && \
    groupadd -g ${gid} ${group} && \
    useradd -d "$home" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

USER $user
