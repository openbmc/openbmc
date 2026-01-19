# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "Axiado uboot recipe"
SECTION = "bootloaders"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://README;beginline=1;endline=4;md5=744e7e3bb0c94b4b9f6b3db3bf893897"

require recipes-bsp/u-boot/u-boot.inc
PROVIDES += "u-boot"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRC_URI = "git://git@sourcevault.axiadord:7999/linux/u-boot.git;protocol=ssh;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

DEPENDS += " dtc-native bison-native"

UBOOT_ENV_SIZE = "0x10000"
UBOOT_ENV = "u-boot-env"
UBOOT_ENV_SUFFIX = "bin"
