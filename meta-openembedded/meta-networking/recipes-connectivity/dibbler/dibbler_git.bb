SUMMARY = "Dibbler DHCPv6 client"
DESCRIPTION = "Dibbler is a portable DHCPv6 implementation. It supports stateful as well as stateless autoconfiguration for IPv6."
HOMEPAGE = "http://klub.com.pl/dhcpv6"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7236695bb6d4461c105d685a8b61c4e3"

SRCREV = "a7c6cf58a88a510cb00841351e75030ce78d36bf"

SRC_URI = "git://github.com/tomaszmrugalski/dibbler \
           file://dibbler_fix_getSize_crash.patch \
           "
PV = "1.0.1+1.0.2RC1+git${SRCREV}"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "debug bind-reuse resolvconf dns-update"

PACKAGECONFIG[debug] = "--enable-debug,,,"
PACKAGECONFIG[efence] = "--enable-efence,,,"
PACKAGECONFIG[bind-reuse] = "--enable-bind-reuse,,,"
PACKAGECONFIG[dst-addr-filter] = "--enable-dst-addr-check,,,"
PACKAGECONFIG[resolvconf] = "--enable-resolvconf,,,"
PACKAGECONFIG[dns-update] = "--enable-dns-update,,,"
PACKAGECONFIG[auth] = "--enable-auth,,,"
PACKAGECONFIG[gtest] = "--enable-gtest-static,,,"

inherit autotools

DEPENDS += "flex-native"

PACKAGES =+ "${PN}-requestor ${PN}-client ${PN}-relay ${PN}-server"

FILES_${PN}-client = "${sbindir}/${PN}-client"
FILES_${PN}-relay = "${sbindir}/${PN}-relay"
FILES_${PN}-requestor = "${sbindir}/${PN}-requestor"
FILES_${PN}-server = "${sbindir}/${PN}-server"
