LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35"

PV = "0.1.0+svnr${SRCPV}"
PR = "${INC_PR}.0"

require e-module.inc

do_configure_prepend() {
    autopoint || touch config.rpath
}

# Calls /usr/bin/eject for media
RRECOMMENDS_${PN} += "eject"

SRC_URI += "file://configure.patch"
