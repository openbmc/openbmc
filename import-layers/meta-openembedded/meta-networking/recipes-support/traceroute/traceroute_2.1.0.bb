SUMMARY = "A new modern implementation of traceroute(8) utility for Linux systems"
DESCRIPTION = "The traceroute utility displays the route used by IP packets on \
their way to a specified network (or Internet) host.  Traceroute displays \
the IP number and host name (if possible) of the machines along the \
route taken by the packets.  Traceroute is used as a network debugging \
tool.  If you're having network connectivity problems, traceroute will \
show you where the trouble is coming from along the route."
SECTION = "net"
HOMEPAGE = "http://traceroute.sourceforge.net/"
LICENSE = "GPL-2.0+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

inherit update-alternatives

SRC_URI = "${SOURCEFORGE_MIRROR}/traceroute/traceroute/${BP}/${BP}.tar.gz \
           file://filter-out-the-patches-from-subdirs.patch \
"

SRC_URI[md5sum] = "84d329d67abc3fb83fc8cb12aeaddaba"
SRC_URI[sha256sum] = "3669d22a34d3f38ed50caba18cd525ba55c5c00d5465f2d20d7472e5d81603b6"

EXTRA_OEMAKE = "VPATH=${STAGING_LIBDIR}"

do_compile() {
    export LDFLAGS="${TARGET_LDFLAGS} -L${S}/libsupp"
    oe_runmake "env=yes"
}

do_install() {
    install -d ${D}${bindir}
    install -m755 ${BPN}/${BPN} ${D}${bindir}

    install -m755 wrappers/tcptraceroute ${D}${bindir}

    install -d ${D}${mandir}
    install -p -m644 ${BPN}/${BPN}.8 ${D}${mandir}
    ln -s ${BPN}.8 ${D}${mandir}/${BPN}6.8
    ln -s ${BPN}.8 ${D}${mandir}/tcptraceroute.8

}

ALTERNATIVE_PRIORITY = "60"
ALTERNATIVE_${PN} = "traceroute"
ALTERNATIVE_LINK_NAME[traceroute] = "${bindir}/traceroute"
