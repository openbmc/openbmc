SUMMARY = "Lightweight and minimal dumb-terminal emulation program"
SECTION = "console/utils"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "https://gitlab.com/wsakernel/picocom"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3000e4830620e310fe65c0eb69df9e8a"

SRCREV = "7b6acbd421a2d4ca99376b7b427828dc1bcba4d8"

SRC_URI = "git://gitlab.com/wsakernel/picocom;branch=master;protocol=https \
           "

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CC=${CC}' 'LD=${CC}' 'VERSION=${PV}' \
		'CFLAGS=${CFLAGS}' 'LDFLAGS=${LDFLAGS}' "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${BPN} pcasc pcxm pcym pczm ${D}${bindir}/
}

