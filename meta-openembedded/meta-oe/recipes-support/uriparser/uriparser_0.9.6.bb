SUMMARY = "RFC 3986 compliant URI parsing library"
HOMEPAGE = "https://uriparser.github.io"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=fcc5a53146c2401f4b4f6a3bdf3f0168"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${BP}/${BP}.tar.gz"
SRC_URI[sha256sum] = "10e6f90d359c1087c45f907f95e527a8aca84422251081d1533231e031a084ff"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"

inherit cmake

EXTRA_OECMAKE += "-DURIPARSER_BUILD_DOCS:BOOL=OFF -DURIPARSER_BUILD_TESTS:BOOL=OFF"

BBCLASSEXTEND += "native"
