SUMMARY = "Name Service Switch module for Multicast DNS (zeroconf) name resolution"
HOMEPAGE = "https://github.com/lathiat/nss-mdns"
SECTION = "libs"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "avahi"

SRC_URI = "git://github.com/lathiat/nss-mdns \
           "

SRCREV = "41c9c5e78f287ed4b41ac438c1873fa71bfa70ae"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

COMPATIBLE_HOST_libc-musl = 'null'

EXTRA_OECONF = "--libdir=${base_libdir}"

RDEPENDS_${PN} = "avahi-daemon"

pkg_postinst_${PN} () {
	sed '
		/^hosts:/ !b
		/\<mdns\(4\|6\)\?\(_minimal\)\?\>/ b
		s/\([[:blank:]]\+\)dns\>/\1mdns4_minimal [NOTFOUND=return] dns/g
		' -i $D${sysconfdir}/nsswitch.conf
}

pkg_prerm_${PN} () {
	sed '
		/^hosts:/ !b
		s/[[:blank:]]\+mdns\(4\|6\)\?\(_minimal\( \[NOTFOUND=return\]\)\?\)\?//g
		' -i $D${sysconfdir}/nsswitch.conf
}
