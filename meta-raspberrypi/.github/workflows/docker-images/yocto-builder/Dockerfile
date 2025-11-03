# SPDX-FileCopyrightText: Andrei Gherzan <andrei.gherzan@huawei.com>
#
# SPDX-License-Identifier: MIT

FROM ubuntu:22.04

ARG DEBIAN_FRONTEND="noninteractive"
RUN apt-get update -q -y
RUN apt-get install -y eatmydata

# Yocto/OE build host dependencies
# Keep this in sync with
# https://git.yoctoproject.org/poky/tree/documentation/poky.yaml.in
# https://git.yoctoproject.org/poky/tree/documentation/tools/host_packages_scripts/ubuntu_essential.sh
RUN eatmydata apt-get install -qq -y \
	build-essential chrpath cpio debianutils diffstat file gawk gcc \
	git iputils-ping libacl1 liblz4-tool locales python3 python3-git \
	python3-jinja2 python3-pexpect python3-pip python3-subunit socat \
	texinfo unzip wget xz-utils zstd

# en_US.UTF-8 is required by the build system
RUN eatmydata apt-get install -qq -y locales \
	&& echo "en_US.UTF-8 UTF-8" > /etc/locale.gen \
	&& locale-gen
ENV LANG en_US.utf8

RUN eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

# Have bash as shell
RUN echo "dash dash/sh boolean false" | debconf-set-selections \
 && dpkg-reconfigure dash

# Fix the resource exhaustion problem on the build infrastructure
RUN echo 'vm.max_map_count = 4048576' >> /etc/sysctl.conf

# Run under normal user called 'ci'
RUN useradd --create-home --uid 1000 --shell /usr/bin/bash ci
USER ci
WORKDIR /home/ci

COPY ./yocto-builder/entrypoint-yocto-check-layer.sh /
COPY ./yocto-builder/entrypoint-build.sh /
COPY ./utils.sh /
