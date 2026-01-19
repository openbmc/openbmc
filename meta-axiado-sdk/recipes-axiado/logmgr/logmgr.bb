# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SUMMARY = "Log Manager"
DESCRIPTION = "Log Manager application to manage TCU logs"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM ?= "file://COPYING.axiado;md5=01d0d9bdb04606d39dcbff1ca352f133"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRCREV ?= "a5e1398c025e0530aa27674712810560da1d51e6"
SRC_URI =  "git://git@sourcevault.axiadord:7999/apps/logmgr.git;protocol=ssh;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit meson pkgconfig
inherit obmc-phosphor-systemd

DEPENDS += "systemd"

SYSTEMD_SERVICE:${PN} = "ax_logger.service"
