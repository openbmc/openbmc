# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SUMMARY = "Flowmeter"
SECTION = "Flowmeter demo"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM ?= "file://COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRCREV ?= "890a2cbd3b64760040321e3df87347690dcf07be"
SRC_URI = "git://git@sourcevault.axiadord:7999/apps/flowmeter.git;protocol=ssh;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit meson pkgconfig
inherit obmc-phosphor-systemd

DEPENDS += "libpcap systemd"

SYSTEMD_SERVICE:${PN} = "flowmeter.service rwui.service"
