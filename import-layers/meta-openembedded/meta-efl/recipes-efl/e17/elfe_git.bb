LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35"
PV = "0.0.1+gitr${SRCPV}"
PE = "1"

require e-module.inc

SRC_URI = " \
    git://git.enlightenment.org/enlightenment/modules/${BPN}.git \
"
S = "${WORKDIR}/git"

SRCREV = "1ec0e7713c3ca901014811ff78277d9e2aaac981"

DEPENDS += "elementary"
