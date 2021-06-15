SUMMARY = "LDNS is a DNS library that facilitates DNS tool programming"
HOMEPAGE = "https://nlnetlabs.nl/ldns"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34330f15b2b4abbbaaa7623f79a6a019"

SRC_URI = "https://www.nlnetlabs.nl/downloads/ldns/ldns-${PV}.tar.gz"
SRC_URI[md5sum] = "166262a46995d9972aba417fd091acd5"
SRC_URI[sha256sum] = "8ac84c16bdca60e710eea75782356f3ac3b55680d40e1530d7cea474ac208229"

DEPENDS = "openssl"

inherit autotools-brokensep

PACKAGECONFIG ??= ""
PACKAGECONFIG[drill] = "--with-drill,--without-drill"

EXTRA_OECONF = "--with-ssl=${STAGING_EXECPREFIXDIR} \
                libtool=${TARGET_PREFIX}libtool"
