DESCRIPTION = "IPTraf is a console-based network statistics utility for Linux. \
It gathers a variety of figures such as TCP connection packet and byte counts, \
interface statistics and activity indicators, TCP/UDP traffic breakdowns, \
and LAN station packet and byte counts."

HOMEPAGE = "http://iptraf.seul.org"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc0bdc29df738baf327368b1bbb15a45"

DEPENDS = "ncurses"

SRC_URI = " \
    ftp://iptraf.seul.org/pub/iptraf/iptraf-3.0.0.tar.gz \
    file://0001-src-Fix-error-in-cross-compile.patch"
SRC_URI[md5sum] = "377371c28ee3c21a76f7024920649ea8"
SRC_URI[sha256sum] = "9ee433d95573d612539da4b452e6cdcbca6ab6674a88bfbf6eaf12d4902b5163"
RDEPENDS_${PN} = "ncurses"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
    oe_runmake -C src all  
}

do_install() {
    install -d ${D}${bindir}
    oe_runmake -C src install \
        TARGET=${D}${bindir} \
        WORKDIR=${D}${localstatedir}/local/iptraf \
        LOGDIR=${D}${localstatedir}/log/iptraf \
        LOCKDIR=${D}${localstatedir}/run/iptraf 
}

FILES_${PN} += "${bindir} ${localstatedir} /run"
