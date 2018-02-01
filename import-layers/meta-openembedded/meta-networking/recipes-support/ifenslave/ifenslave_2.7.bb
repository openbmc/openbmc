SUMMARY = "Configure network interfaces for parallel routing"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/bonding"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=6807ba81c0744ab50d735c94628c3f64"

SRCREV = "400c490d52acb31f1064e1bf4fc9fcaf3791888f"
SRC_URI = "git://anonscm.debian.org/collab-maint/ifenslave.git"

S = "${WORKDIR}/git"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/ifenslave ${D}${sbindir}/

    install -m 0755 -D ${S}/debian/ifenslave.if-pre-up ${D}${sysconfdir}/network/if-pre-up.d/ifenslave
    install -m 0755 -D ${S}/debian/ifenslave.if-post-down ${D}${sysconfdir}/network/if-post-down.d/ifenslave
    install -m 0755 -D ${S}/debian/ifenslave.if-up ${D}${sysconfdir}/network/if-up.d/ifenslave
    install -m 0644 -D ${S}/debian/ifenslave.8 ${D}${mandir}/man8/ifenslave.8
}

FILES_${PN}-doc_remove = "${mandir}"
FILES_${PN} += "${mandir}/man8/ifenslave.8"

RDEPENDS_${PN} = "man"
