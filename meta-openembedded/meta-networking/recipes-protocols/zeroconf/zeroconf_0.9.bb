SUMMARY = "IPv4 link-local address allocator"
DESCRIPTION = "Zeroconf is a program that is used to claim IPv4 \
link-local addresses. IPv4 link-local addresses are useful when setting \
up ad-hoc networking between devices without the involvement of a either \
a DHCP server or network administrator. \
These addresses are allocated from the 169.254.0.0/16 address range and \
are normally attached to each Ethernet device in your computer. \
Addresses are assigned randomly by each host and, in case of collision, \
both hosts (are supposed to) renumber."
HOMEPAGE = "http://www.progsoc.org/~wildfire/zeroconf/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4 \
                    file://zeroconf.c;beginline=1;endline=13;md5=a5bada96e1e34b08eb7446b28e2630b2"
SECTION = "net"

PR = "r1"

SRC_URI = "http://www.progsoc.org/~wildfire/zeroconf/download/${BPN}-${PV}.tar.gz \
           file://compilefix.patch \
           file://makefile-add-ldflags.patch \
           file://zeroconf-default \
           file://debian-zeroconf \
           file://0001-zeroconf-Rename-arp_op-to-avoid-namespace-conflicts-.patch \
           "

SRC_URI[md5sum] = "bdafb16b008ebb5633e4e581f77821d2"
SRC_URI[sha256sum] = "a8c74df127753e2310fa1e072f3c9ca44a404bb0bbce9cfec7a84c6dff8bec7b"

EXTRA_OEMAKE += "CPPFLAGS='${CFLAGS}'"

do_install () {
    install -d ${D}${sbindir}
    install -d ${D}${sysconfdir}/network/if-up.d
    install -d ${D}${sysconfdir}/default
    install -c -m 755 ${S}/zeroconf ${D}${sbindir}/zeroconf
    install -c -m 755 ${WORKDIR}/debian-zeroconf ${D}${sysconfdir}/network/if-up.d/zeroconf
    install -c ${WORKDIR}/zeroconf-default ${D}${sysconfdir}/default/zeroconf
}
