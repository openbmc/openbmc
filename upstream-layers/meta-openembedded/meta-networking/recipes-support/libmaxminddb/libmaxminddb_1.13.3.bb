SUMMARY = "C library for the MaxMind DB file format"
DESCRIPTION = "The libmaxminddb library provides a C library for reading MaxMind DB files, \
ncluding the GeoIP2 databases from MaxMind. This is a custom binary \
format designed to facilitate fast lookups of IP addresses while allowing \
for great flexibility in the type of data associated with an address."

HOMEPAGE = "https://github.com/maxmind/libmaxminddb"
SECTION = "libs"
LICENSE = " Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"
SRC_URI = "https://github.com/maxmind/libmaxminddb/releases/download/${PV}/${BPN}-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "a66502ea76eadbe17f2cd6fd708946777253972d2ae8157dee1b23a2fb528171"

inherit autotools-brokensep

