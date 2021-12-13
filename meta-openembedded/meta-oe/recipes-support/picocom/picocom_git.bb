SUMMARY = "Lightweight and minimal dumb-terminal emulation program"
SECTION = "console/utils"
LICENSE = "GPLv2+"
HOMEPAGE = "https://github.com/npat-efault/picocom"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3000e4830620e310fe65c0eb69df9e8a"

BASEPV = "3.1"
PV = "${BASEPV}+git${SRCPV}"

SRCREV = "90385aabe2b51f39fa130627d46b377569f82d4a"

SRC_URI = "git://github.com/npat-efault/picocom;branch=master;protocol=https \
           file://0001-Fix-building-with-musl.patch \
           "

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CC=${CC}' 'LD=${CC}' 'VERSION=${BASEPV}' \
		'CFLAGS=${CFLAGS}' 'LDFLAGS=${LDFLAGS}' "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${BPN} pcasc pcxm pcym pczm ${D}${bindir}/
}

