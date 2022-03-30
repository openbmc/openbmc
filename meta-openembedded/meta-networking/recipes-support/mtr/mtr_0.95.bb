SUMMARY = "Combined traceroute and ping utility"
DESCRIPTION = "mtr combines the functionality of the 'traceroute' and 'ping' programs in a single network diagnostic tool."
HOMEPAGE = "http://www.bitwizard.nl/mtr/"
SECTION = "net"
DEPENDS = "ncurses"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://ui/mtr.c;beginline=5;endline=16;md5=00a894a39d53726a27386534d1c4e468"

SRCREV = "852e5617fbf331cf292723702161f0ac9afe257c"
SRC_URI = "git://github.com/traviscross/mtr;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--without-gtk"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

PACKAGES += "${PN}-bash-completions"

FILES:${PN}-bash-completions = "${datadir}/bash-completion/"
