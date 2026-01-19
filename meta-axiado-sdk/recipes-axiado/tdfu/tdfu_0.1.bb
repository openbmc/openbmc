# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "tdfu"
DESCRIPTION = "TCU Firmware Upgrade Application"
LICENSE = "CLOSED"
PV = "0.1"

SRC_URI = "file://ax3000-fw-update"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/ax3000-fw-update ${D}${bindir}
}
