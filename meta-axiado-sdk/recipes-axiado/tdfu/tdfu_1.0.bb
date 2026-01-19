# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "tdfu"
DESCRIPTION = "TCU Firmware Upgrade Application"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM ?= "file://COPYING.axiado;md5=01d0d9bdb04606d39dcbff1ca352f133"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRCREV_FORMAT = "tdfu_axhal_sdk_porting"
SRCREV = "4693be8750640592275644351cb15eafde03f8c3"
SRC_URI = "git://git@sourcevault.axiadord:7999/apps/tdfu.git;protocol=ssh;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
