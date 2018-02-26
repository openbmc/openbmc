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
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e2b3850593b899b1a17594ed4cc4c731"
DEPENDS = "ncurses"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/iptraf-ng/iptraf-ng-1.1.4.tar.gz/e0f8df3b7baf2b5106709abc4f8c029a/${BP}.tar.gz \
           file://ncurses-config.patch \
           file://0001-Fix-printd-formatting-strings.patch \
           "
SRC_URI[md5sum] = "e0f8df3b7baf2b5106709abc4f8c029a"
SRC_URI[sha256sum] = "16b9b05bf5d3725d86409b901696639ad46944d02de6def87b1ceae5310dd35c"

inherit autotools-brokensep pkgconfig

CFLAGS += "-D_GNU_SOURCE"

PROVIDES = "iptraf"
RPROVIDES_${PN} += "iptraf"
RREPLACES_${PN} += "iptraf"
RCONFLICTS_${PN} += "iptraf"

