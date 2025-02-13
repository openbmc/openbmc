SUMMARY = "Name Service Switch module for Multicast DNS (zeroconf) name resolution"
HOMEPAGE = "https://github.com/lathiat/nss-mdns"
DESCRIPTION = "nss-mdns is a plugin for the GNU Name Service Switch (NSS) functionality of the GNU C Library (glibc) providing host name resolution via Multicast DNS (aka Zeroconf, aka Apple Rendezvous, aka Apple Bonjour), effectively allowing name resolution by common Unix/Linux programs in the ad-hoc mDNS domain .local."
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "avahi"

SRC_URI = "git://github.com/lathiat/nss-mdns;branch=master;protocol=https \
           "

SRCREV = "4b3cfe818bf72d99a02b8ca8b8813cb2d6b40633"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

COMPATIBLE_HOST:libc-musl = 'null'

EXTRA_OECONF = "--libdir=${base_libdir}"

RDEPENDS:${PN} = "avahi-daemon"
RPROVIDES:${PN} = "libnss-mdns"

pkg_postinst:${PN} () {
	sed '
		/^hosts:/ !b
		/\<mdns\(4\|6\)\?\(_minimal\)\?\>/ b
		s/\([[:blank:]]\+\)dns\>/\1mdns4_minimal [NOTFOUND=return] dns/g
		' -i $D${sysconfdir}/nsswitch.conf
}

pkg_prerm:${PN} () {
	sed '
		/^hosts:/ !b
		s/[[:blank:]]\+mdns\(4\|6\)\?\(_minimal\( \[NOTFOUND=return\]\)\?\)\?//g
		' -i $D${sysconfdir}/nsswitch.conf
}
