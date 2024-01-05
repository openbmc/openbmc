SUMMARY = "Configure network interfaces for parallel routing"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/bonding"
SECTION = "net"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=acc89812938cf9ad6b1debc37cea0253"

inherit manpages
MAN_PKG = "${PN}"

SRCREV = "62e60904afbe58fda0c7104fdad1c4f618266ba6"
SRC_URI = "git://salsa.debian.org/debian/ifenslave.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

do_install() {
    install -m 0755 -D ${S}/debian/ifenslave.if-pre-up ${D}${sysconfdir}/network/if-pre-up.d/ifenslave
    install -m 0755 -D ${S}/debian/ifenslave.if-post-down ${D}${sysconfdir}/network/if-post-down.d/ifenslave
    install -m 0755 -D ${S}/debian/ifenslave.if-up ${D}${sysconfdir}/network/if-up.d/ifenslave
}

FILES:${PN}-doc:remove = "${mandir}"
