FROM jenkins/jenkins:lts

# switch to root for installing packages
USER root

RUN apt update && \
    apt install -y apt-utils && \
    apt upgrade -y && \
    apt dist-upgrade -y && \
    apt install -y gpg curl && \ # note: these should already be installed by the base image, just wanting to be sure they're there
    apt autoremove -y && \
    apt autoclean -y && \
    apt clean -y && \
    rm -rv /var/cache/apt/archives/* && \
    rm -rf /var/lib/apt/lists/* && \
    curl -fsSL https://get.docker.com | sh && \
    usermod -aG docker jenkins # let jenkins user run docker without sudo

# drop back to the regular jenkins user
USER jenkins
