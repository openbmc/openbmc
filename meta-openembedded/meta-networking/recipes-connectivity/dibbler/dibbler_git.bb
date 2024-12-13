SUMMARY = "Dibbler DHCPv6 client"
DESCRIPTION = "Dibbler is a portable DHCPv6 implementation. It supports stateful as well as stateless autoconfiguration for IPv6."
HOMEPAGE = "http://klub.com.pl/dhcpv6"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7236695bb6d4461c105d685a8b61c4e3"

SRCREV = "a7c6cf58a88a510cb00841351e75030ce78d36bf"

SRC_URI = "git://github.com/tomaszmrugalski/dibbler;branch=master;protocol=https \
           file://dibbler_fix_getSize_crash.patch \
           file://0001-port-linux-Re-order-header-includes.patch \
           file://0001-Define-alignof-using-_Alignof-when-using-C11-or-newe.patch \
           file://0002-make-Do-not-enforce-c99.patch \
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

CPPFLAGS += "-D_GNU_SOURCE -Dregister=''"
LDFLAGS += "-pthread"

PACKAGES =+ "${PN}-requestor ${PN}-client ${PN}-relay ${PN}-server"

FILES:${PN}-client = "${sbindir}/${PN}-client"
FILES:${PN}-relay = "${sbindir}/${PN}-relay"
FILES:${PN}-requestor = "${sbindir}/${PN}-requestor"
FILES:${PN}-server = "${sbindir}/${PN}-server"

# http://errors.yoctoproject.org/Errors/Details/766880/
# git/Port-linux/interface.c:118:18: error: assignment to '__caddr_t' {aka 'char *'} from incompatible pointer type 'struct ethtool_value *' [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
