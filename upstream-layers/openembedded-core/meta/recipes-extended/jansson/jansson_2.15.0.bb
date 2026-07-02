SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT & dtoa"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9911525d4128bee234ee2d3ccaa2537"

GITHUB_BASE_URI = "https://github.com/akheron/jansson/releases"
SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://0001-configure-only-use-default-symver-when-the-linker-su.patch \
		  "

SRC_URI[sha256sum] = "070a629590723228dc3b744ae90e965a569efb9c535b3309b52e80e75d8eb3be"

inherit autotools pkgconfig github-releases

CVE_STATUS[CVE-2020-36325] = "disputed: upstream considers it isn't a real bug per https://github.com/akheron/jansson/issues/548"

BBCLASSEXTEND = "native"
