DESCRIPTION = "Avahi Module for Apache2."
HOMEPAGE = "https://0pointer.de/lennart/projects/mod_dnssd/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "apache2 avahi"

SRC_URI = "git://git.0pointer.de/mod_dnssd;protocol=git;branch=master"
SRCREV = "be2fb9f6158f800685de7a1bc01c39b6cf1fa12c"

S = "${WORKDIR}/git"

EXTRA_OECONF = "--disable-lynx"

inherit autotools pkgconfig

do_install() {
	install -Dm755 ${S}/src/.libs/mod_dnssd.so ${D}${libexecdir}/apache2/modules/mod_dnssd.so
}

