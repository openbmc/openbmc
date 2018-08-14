SUMMARY = "Proxy for UDP/TCP debug connections"
DESCRIPTION = "The agent-proxy will forward tcp or udp connections as well as allow for script multiplexing of terminal sessions."
HOMEPAGE = "http://kgdb.wiki.kernel.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SECTION = "devel"

EXTRA_OEMAKE = "'CC=${CC}'"

SRCREV = "e6c9d3d996bd55e7ab14dbd74deb7841e0c3a4f1"
PV = "1.96+git${SRCPV}"

SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/kgdb/agent-proxy.git;protocol=git \
           file://0001-Makefile-Add-LDFLAGS-variable.patch \
"

BBCLASSEXTEND = "native nativesdk"

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 agent-proxy ${D}${bindir}
}
