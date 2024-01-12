SUMMARY = "Lightweight and minimal dumb-terminal emulation program"
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "https://gitlab.com/wsakernel/picocom"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3000e4830620e310fe65c0eb69df9e8a"

BASEPV = "2023-04"
PV = "${BASEPV}+git${SRCPV}"

SRCREV = "12537df0314767d5af35bddddbbca3694e6a0342"

SRC_URI = "git://gitlab.com/wsakernel/picocom;branch=master;protocol=https \
           "

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CC=${CC}' 'LD=${CC}' 'VERSION=${BASEPV}' \
		'CFLAGS=${CFLAGS}' 'LDFLAGS=${LDFLAGS}' "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${BPN} pcasc pcxm pcym pczm ${D}${bindir}/
}

