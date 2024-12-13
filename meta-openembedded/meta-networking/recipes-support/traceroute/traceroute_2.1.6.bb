SUMMARY = "A new modern implementation of traceroute(8) utility for Linux systems"
DESCRIPTION = "The traceroute utility displays the route used by IP packets on \
their way to a specified network (or Internet) host.  Traceroute displays \
the IP number and host name (if possible) of the machines along the \
route taken by the packets.  Traceroute is used as a network debugging \
tool.  If you're having network connectivity problems, traceroute will \
show you where the trouble is coming from along the route."
SECTION = "net"
HOMEPAGE = "http://traceroute.sourceforge.net/"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

inherit update-alternatives

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/traceroute/files/traceroute/"

SRC_URI = "${SOURCEFORGE_MIRROR}/traceroute/traceroute/${BP}/${BP}.tar.gz \
"
SRC_URI[sha256sum] = "9ccef9cdb9d7a98ff7fbf93f79ebd0e48881664b525c4b232a0fcec7dcb9db5e"

EXTRA_OEMAKE = "VPATH=${STAGING_LIBDIR}"
LTOEXTRA += "-flto-partition=none"

do_compile() {
    oe_runmake "env=yes"
}

do_install() {
    install -d ${D}${bindir}
    install -m755 ${BPN}/${BPN} ${D}${bindir}

    install -m755 wrappers/tcptraceroute ${D}${bindir}

    install -d ${D}${mandir}/man8
    install -p -m644 ${BPN}/${BPN}.8 ${D}${mandir}/man8
    ln -s ${BPN}.8 ${D}${mandir}/man8/${BPN}6.8
    ln -s ${BPN}.8 ${D}${mandir}/man8/tcptraceroute.8

}

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "traceroute"
ALTERNATIVE_LINK_NAME[traceroute] = "${bindir}/traceroute"
