SUMMARY = "Name Service Switch module for Multicast DNS (zeroconf) name resolution"
HOMEPAGE = "http://0pointer.de/lennart/projects/nss-mdns/"
SECTION = "libs"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "avahi"
PR = "r7"

SRC_URI = "http://0pointer.de/lennart/projects/nss-mdns/nss-mdns-${PV}.tar.gz"

SRC_URI[md5sum] = "03938f17646efbb50aa70ba5f99f51d7"
SRC_URI[sha256sum] = "1e683c2e7c3921814706d62fbbd3e9cbf493a75fa00255e0e715508d8134fa6d"

S = "${WORKDIR}/nss-mdns-${PV}"

inherit autotools

EXTRA_OECONF = "--libdir=${base_libdir} --disable-lynx --enable-avahi"

# suppress warning, but don't bother with autonamer
LEAD_SONAME = "libnss_mdns.so"
DEBIANNAME_${PN} = "libnss-mdns"

RDEPENDS_${PN} = "avahi-daemon"

pkg_postinst_${PN} () {
	sed -e '/^hosts:/s/\s*\<mdns\>//' \
		-e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 mdns4_minimal [NOTFOUND=return]\3\4 mdns\5/' \
		-i $D${sysconfdir}/nsswitch.conf
}

pkg_prerm_${PN} () {
	sed -e '/^hosts:/s/\s*\<mdns\>//' \
		-e '/^hosts:/s/\s*mdns4_minimal\s\+\[NOTFOUND=return\]//' \
		-i $D${sysconfdir}/nsswitch.conf
}
