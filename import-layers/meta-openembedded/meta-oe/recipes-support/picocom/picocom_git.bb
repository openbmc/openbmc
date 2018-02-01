SUMMARY = "Lightweight and minimal (~20K) dumb-terminal emulation program"
SECTION = "console/utils"
LICENSE = "GPLv2+"
HOMEPAGE = "http://code.google.com/p/picocom/"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3000e4830620e310fe65c0eb69df9e8a"

BASEPV = "2.2"
PV = "${BASEPV}+git${SRCPV}"

SRCREV = "deffd18c24145bd6f965f44e735a50b65810ccdc"

SRC_URI = "git://github.com/npat-efault/picocom"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CC=${CC}' 'LD=${CC}' 'VERSION=${BASEPV}' \
		'CFLAGS=${CFLAGS}' 'LDFLAGS=${LDFLAGS}' "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${BPN} pcasc pcxm pcym pczm ${D}${bindir}/
}

