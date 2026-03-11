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
SRC_URI[sha256sum] = "a5fdf6c7b4880fdc7620f8ace5bd5cbe9f65650c9493034b5b9fc7d83551a439"

inherit autotools-brokensep

