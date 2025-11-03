DESCRIPTION = "This package includes some useful diagnostics tools for \
IPv6 networks, including ndisc6, rdisc6, tcptraceroute6 and traceroute6."
SECTION = "net"
HOMEPAGE = "http://www.remlab.net/ndisc6/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "92e5d1cf6547fe40316b2a6ca2f7b8195ae0cbe5"
SRC_URI = "git://git.remlab.net/git/ndisc6.git;protocol=http;branch=master \
           file://0001-autogen-Do-not-symlink-gettext.h-from-build-host.patch \
           file://0001-Remove-use-of-variables-indicating-buildtime-informa.patch \
           "

S = "${WORKDIR}/git"

DEPENDS = "coreutils-native"

inherit autotools gettext

EXTRA_OECONF += "PERL=${USRBINPATH}/perl"

USE_NLS = "yes"

EXTRA_OECONF += "--disable-rpath"

do_configure:prepend() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/gettext.h ${S}/include
    ${S}/autogen.sh
}

do_configure:append() {
    sed -i -e 's|${WORKDIR}|<scrubbed>|g' ${B}/config.h
}

do_install:append () {
    rm -rf ${D}${localstatedir}
    # Enable SUID bit for applications that need it
    chmod 4555 ${D}${bindir}/rltraceroute6
    chmod 4555 ${D}${bindir}/ndisc6
    chmod 4555 ${D}${bindir}/rdisc6
}
ALLOW_EMPTY:${PN} = "1"

# Split into seperate packages since we normal don't want them all
# The main package is left empty and therefore not created.
PACKAGES += "${PN}-ndisc6 ${PN}-tcpspray6 ${PN}-rdisc6 \
    ${PN}-tcptraceroute6 ${PN}-rltraceroute6 \
    ${PN}-tracert6 ${PN}-rdnssd ${PN}-misc"
FILES:${PN}            = ""
FILES:${PN}-ndisc6        = "${bindir}/ndisc6"
FILES:${PN}-tcpspray6         = "${bindir}/tcpspray6"
FILES:${PN}-rdisc6        = "${bindir}/rdisc6"
FILES:${PN}-tcptraceroute6    = "${bindir}/tcptraceroute6"
FILES:${PN}-rltraceroute6    = "${bindir}/rltraceroute6"
FILES:${PN}-tracert6        = "${bindir}/tracert6"
FILES:${PN}-rdnssd        = "${sbindir}/rdnssd ${sysconfdir}/rdnssd"
FILES:${PN}-misc                = "${bindir}/dnssort ${bindir}/name2addr ${bindir}/tcpspray ${bindir}/addr2name"

DESCRIPTION:${PN}-ndisc6    = "ICMPv6 Neighbor Discovery tool. \
Performs IPv6 neighbor discovery in userland. Replaces arping from the \
IPv4 world."
DESCRIPTION:${PN}-rdisc6    = "ICMPv6 Router Discovery tool. \
Queries IPv6 routers on the network for advertised prefixes. Can be used \
to detect rogue IPv6 routers, monitor legitimate IPv6 routers."
DESCRIPTION:${PN}-tcpspray6    = "Performs bandwidth measurements of TCP \
sessions between the local system and a remote echo server in either IPv6 \
or IPv4."

DESCRIPTION:${PN}-rdnssd       = "Daemon to autoconfigure the list of DNS \
servers through slateless IPv6 autoconfiguration."

# The tcptraceroute6 and tracert6 commands depend on rltraceroute6 to
# perform the actual trace operation.
RDEPENDS:${PN}-tcptraceroute6 = "${PN}-rltraceroute6"
RDEPENDS:${PN}-tracert6 = "${PN}-rltraceroute6"
RDEPENDS:${PN}-misc += "perl"

