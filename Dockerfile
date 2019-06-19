FROM jenkins/jenkins:lts

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
