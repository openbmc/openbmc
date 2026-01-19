# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "Sysproxy"
SECTION = "Sysproxy application"
LICENSE = "CLOSED"
PV = "0.1"

inherit obmc-phosphor-systemd

SRC_URI = "file://sysproxy.service \
           file://sysmgr_proxy \
           "

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/sysmgr_proxy ${D}${bindir}
}

FILES:${PN} = "${bindir}/sysmgr_proxy"
