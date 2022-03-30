SUMMARY = "Proxy for UDP/TCP debug connections"
DESCRIPTION = "The agent-proxy will forward tcp or udp connections as well as allow for script multiplexing of terminal sessions."
HOMEPAGE = "http://kgdb.wiki.kernel.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SECTION = "devel"

EXTRA_OEMAKE = "'CC=${CC}'"

SRCREV = "468fe4c31e6c62c9bbb328b06ba71eaf7be0b76a"

SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/kgdb/agent-proxy.git;protocol=git;branch=master \
           file://0001-Makefile-Add-LDFLAGS-variable.patch \
"

BBCLASSEXTEND = "native nativesdk"

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 agent-proxy ${D}${bindir}
}
