LICENSE = "MIT"
PV = "0.3.0+svnr${SRCREV}"
PR = "${INC_PR}.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=344895f253c32f38e182dcaf30fe8a35"
SRC_URI += "file://screenshot-fix-mkinstalldirs.patch"

require e-module.inc

DEPENDS += "emprint"
RDEPENDS_${PN} += "emprint"

do_configure_prepend() {
    sed -i -e 's:AC_MSG_ERROR(emprint not found):echo foo:g' ${S}/configure.ac
}

PNBLACKLIST[screenshot] ?= "depends on blacklisted emprint"
