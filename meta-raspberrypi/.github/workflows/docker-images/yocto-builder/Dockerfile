# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

FROM ubuntu:20.04

ARG DEBIAN_FRONTEND="noninteractive"
RUN apt-get update -qq
RUN apt-get install -y eatmydata

# Yocto/OE build host dependencies
# Keep this in sync with
# https://git.yoctoproject.org/poky/tree/documentation/poky.yaml
RUN eatmydata apt-get install -qq -y \
	gawk wget git diffstat unzip texinfo gcc build-essential chrpath \
	socat cpio python3 python3-pip python3-pexpect xz-utils debianutils \
	iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev \
	pylint3 xterm python3-subunit mesa-common-dev zstd liblz4-tool

# en_US.UTF-8 is required by the build system
RUN eatmydata apt-get install -qq -y locales \
	&& echo "en_US.UTF-8 UTF-8" > /etc/locale.gen \
	&& locale-gen
ENV LANG en_US.utf8

RUN eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

# Have bash as shell
RUN echo "dash dash/sh boolean false" | debconf-set-selections \
 && dpkg-reconfigure dash

# Run under normal user called 'ci'
RUN useradd --create-home --uid 1000 --shell /usr/bin/bash ci
USER ci
WORKDIR /home/ci

COPY ./yocto-builder/entrypoint-yocto-check-layer.sh /
COPY ./yocto-builder/entrypoint-build.sh /
COPY ./utils.sh /
