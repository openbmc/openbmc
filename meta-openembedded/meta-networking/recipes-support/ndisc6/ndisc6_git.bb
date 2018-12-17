DESCRIPTION = "This package includes some useful diagnostics tools for \
IPv6 networks, including ndisc6, rdisc6, tcptraceroute6 and traceroute6."
SECTION = "net"
HOMEPAGE = "http://www.remlab.net/ndisc6/"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

PV = "1.0.4+git${SRCPV}"
SRCREV = "4c794b5512d23c649def1f94a684225dcbb6ac3e"
SRC_URI = "git://git.remlab.net/git/ndisc6.git;protocol=http \
           file://0001-replace-VLAIS-with-malloc-free-pair.patch \
           file://0002-Do-not-undef-_GNU_SOURCE.patch \
           file://0001-autogen-Do-not-symlink-gettext.h-from-build-host.patch \
           "

S = "${WORKDIR}/git"

inherit autotools gettext

EXTRA_OECONF += "PERL=${USRBINPATH}/perl"

USE_NLS = "yes"

EXTRA_OECONF += "--disable-rpath"

do_configure_prepend() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/gettext.h ${S}/include
    ${S}/autogen.sh
}

do_install_append () {
    rm -rf ${D}${localstatedir}
    # Enable SUID bit for applications that need it
    chmod 4555 ${D}${bindir}/rltraceroute6
    chmod 4555 ${D}${bindir}/ndisc6
    chmod 4555 ${D}${bindir}/rdisc6
}
ALLOW_EMPTY_${PN} = "1"

# Split into seperate packages since we normal don't want them all
# The main package is left empty and therefore not created.
PACKAGES += "${PN}-ndisc6 ${PN}-tcpspray6 ${PN}-rdisc6 \
    ${PN}-tcptraceroute6 ${PN}-rltraceroute6 \
    ${PN}-tracert6 ${PN}-rdnssd ${PN}-misc"
FILES_${PN}            = ""
FILES_${PN}-ndisc6        = "${bindir}/ndisc6"
FILES_${PN}-tcpspray6         = "${bindir}/tcpspray6"
FILES_${PN}-rdisc6        = "${bindir}/rdisc6"
FILES_${PN}-tcptraceroute6    = "${bindir}/tcptraceroute6"
FILES_${PN}-rltraceroute6    = "${bindir}/rltraceroute6"
FILES_${PN}-tracert6        = "${bindir}/tracert6"
FILES_${PN}-rdnssd        = "${sbindir}/rdnssd ${sysconfdir}/rdnssd"
FILES_${PN}-misc                = "${bindir}/dnssort ${bindir}/name2addr ${bindir}/tcpspray ${bindir}/addr2name"

DESCRIPTION_${PN}-ndisc6    = "ICMPv6 Neighbor Discovery tool. \
Performs IPv6 neighbor discovery in userland. Replaces arping from the \
IPv4 world."
DESCRIPTION_${PN}-rdisc6    = "ICMPv6 Router Discovery tool. \
Queries IPv6 routers on the network for advertised prefixes. Can be used \
to detect rogue IPv6 routers, monitor legitimate IPv6 routers."
DESCRITPION_${PN}-tcpspray6    = "Performs bandwidth measurements of TCP \
sessions between the local system and a remote echo server in either IPv6 \
or IPv4."

DESCRITPION_${PN}-rdnssd       = "Daemon to autoconfigure the list of DNS \
servers through slateless IPv6 autoconfiguration."

# The tcptraceroute6 and tracert6 commands depend on rltraceroute6 to
# perform the actual trace operation.
RDEPENDS_${PN}-tcptraceroute6 = "${PN}-rltraceroute6"
RDEPENDS_${PN}-tracert6 = "${PN}-rltraceroute6"
RDEPENDS_${PN}-misc += "perl"

