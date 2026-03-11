# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A console-based network monitoring utility"
DESCRIPTION = "IPTraf-ng is a console-based network monitoring utility.  IPTraf gathers \
data like TCP connection packet and byte counts, interface statistics \
and activity indicators, TCP/UDP traffic breakdowns, and LAN station \
packet and byte counts.  IPTraf-ng features include an IP traffic monitor \
which shows TCP flag information, packet and byte counts, ICMP \
details, OSPF packet types, and oversized IP packet warnings; \
interface statistics showing IP, TCP, UDP, ICMP, non-IP and other IP \
packet counts, IP checksum errors, interface activity and packet size \
counts; a TCP and UDP service monitor showing counts of incoming and \
outgoing packets for common TCP and UDP application ports, a LAN \
statistics module that discovers active hosts and displays statistics \
about their activity; TCP, UDP and other protocol display filters so \
you can view just the traffic you want; logging; support for Ethernet, \
FDDI, ISDN, SLIP, PPP, and loopback interfaces; and utilization of the \
built-in raw socket interface of the Linux kernel, so it can be used \
on a wide variety of supported network cards."

HOMEPAGE = "https://fedorahosted.org/iptraf-ng/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e2b3850593b899b1a17594ed4cc4c731"
DEPENDS = "ncurses"

SRC_URI = "https://src.fedoraproject.org/repo/pkgs/iptraf-ng/v${PV}.tar.gz/sha512/275a345ffd3ab9578c4d159c3a8667326224b4a58b2e7787c4db518b81973d87c04b4b6c5275a721858d01a9b44a0200f8defc06c6f88655aa38d4fa6146ea1d/v${PV}.tar.gz \
           file://iptraf-ng-tmpfiles.conf \
           file://ncurses-config.patch \
           file://0001-make-Make-CC-weak-assignment.patch \
           "
SRC_URI[sha256sum] = "75fd653745ea0705995c25e6c07b34252ecc2563c6a91b007a3a8c26f29cc252"

inherit pkgconfig

CFLAGS += "-D_GNU_SOURCE"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
    install -D -m 0644 -p ${S}/iptraf-ng-logrotate.conf ${D}${sysconfdir}/logrotate.d/iptraf-ng
    install -Dm 0644 ${UNPACKDIR}/iptraf-ng-tmpfiles.conf ${D}${libdir}/tmpfiles.d/iptraf-ng-tmpfiles.conf
}

FILES:${PN} += "${libdir}/tmpfiles.d"
PROVIDES = "iptraf"
RPROVIDES:${PN} += "iptraf"
RREPLACES:${PN} += "iptraf"
RCONFLICTS:${PN} += "iptraf"

