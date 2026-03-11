SUMMARY = "LDNS is a DNS library that facilitates DNS tool programming"
HOMEPAGE = "https://nlnetlabs.nl/ldns"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34330f15b2b4abbbaaa7623f79a6a019"

SRC_URI = "https://www.nlnetlabs.nl/downloads/ldns/ldns-${PV}.tar.gz"
SRC_URI[sha256sum] = "838b907594baaff1cd767e95466a7745998ae64bc74be038dccc62e2de2e4247"

DEPENDS = "openssl"

inherit autotools-brokensep

PACKAGECONFIG ??= ""
PACKAGECONFIG[drill] = "--with-drill,--without-drill"

EXTRA_OECONF = "--with-ssl=${STAGING_EXECPREFIXDIR}"

do_install:append() {
    sed -e 's@[^ ]*-ffile-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fdebug-prefix-map=[^ "]*@@g' \
        -e 's@[^ ]*-fmacro-prefix-map=[^ "]*@@g' \
        -e 's@${RECIPE_SYSROOT}@@g' \
        -i ${D}${libdir}/pkgconfig/*.pc ${D}${bindir}/ldns-config
}
