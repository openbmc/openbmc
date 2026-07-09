SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT & dtoa"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9911525d4128bee234ee2d3ccaa2537"

GITHUB_BASE_URI = "https://github.com/akheron/jansson/releases"
SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz"

SRC_URI[sha256sum] = "0c7114dc0b2d22a670724a1f95922029d7077c19dbf79a584cb8084d2f267f2f"

inherit autotools pkgconfig github-releases

CVE_STATUS[CVE-2020-36325] = "disputed: upstream considers it isn't a real bug per https://github.com/akheron/jansson/issues/548"

BBCLASSEXTEND = "native"
