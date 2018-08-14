SUMMARY = "Combined traceroute and ping utility"
DESCRIPTION = "mtr combines the functionality of the 'traceroute' and 'ping' programs in a single network diagnostic tool."
HOMEPAGE = "http://www.bitwizard.nl/mtr/"
SECTION = "net"
DEPENDS = "ncurses"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://ui/mtr.c;beginline=5;endline=16;md5=af1fafbbfa1bfd48af839f4bb3221106"

PV .= "+git${SRCPV}"

SRCREV = "e6d0a7e93129e8023654ebf58dfa8135d1b1af56"
SRC_URI = "git://github.com/traviscross/mtr"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--without-gtk"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

PACKAGES += "${PN}-bash-completions"

FILES_${PN}-bash-completions = "${datadir}/bash-completion/"
