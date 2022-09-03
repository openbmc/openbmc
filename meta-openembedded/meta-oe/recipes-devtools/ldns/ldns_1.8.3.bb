SUMMARY = "LDNS is a DNS library that facilitates DNS tool programming"
HOMEPAGE = "https://nlnetlabs.nl/ldns"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34330f15b2b4abbbaaa7623f79a6a019"

SRC_URI = "https://www.nlnetlabs.nl/downloads/ldns/ldns-${PV}.tar.gz"
SRC_URI[sha256sum] = "c3f72dd1036b2907e3a56e6acf9dfb2e551256b3c1bbd9787942deeeb70e7860"

DEPENDS = "openssl"

inherit autotools-brokensep

PACKAGECONFIG ??= ""
PACKAGECONFIG[drill] = "--with-drill,--without-drill"

EXTRA_OECONF = "--with-ssl=${STAGING_EXECPREFIXDIR}"

do_install:append() {
    sed -e 's@[^ ]*-ffile-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
        -i ${D}${libdir}/pkgconfig/*.pc
}
