# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

DESCRIPTION = "TCU reset package"
LICENSE = "CLOSED"

LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI += "file://tcu-reset.sh"

do_install:append() {
    install -d ${D}/usr/lib/systemd/system-shutdown
    install -m 0755 ${UNPACKDIR}/tcu-reset.sh ${D}/usr/lib/systemd/system-shutdown/
}

FILES:${PN} += "/usr/lib/systemd/system-shutdown/tcu-reset.sh"

RDEPENDS:${PN} = "bash"
