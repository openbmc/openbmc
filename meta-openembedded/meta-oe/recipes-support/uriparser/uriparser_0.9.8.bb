SUMMARY = "RFC 3986 compliant URI parsing library"
HOMEPAGE = "https://uriparser.github.io"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=fcc5a53146c2401f4b4f6a3bdf3f0168"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${BP}/${BP}.tar.gz"
SRC_URI[sha256sum] = "4cd0e4f93b477272fabceeb0202f269169fe6ec3044e10fac6acbcdf354bb080"

inherit cmake github-releases

UPSTREAM_CHECK_REGEX = "releases/tag/${BPN}-(?P<pver>\d+(\.\d+)+)"

EXTRA_OECMAKE += "-DURIPARSER_BUILD_DOCS:BOOL=OFF -DURIPARSER_BUILD_TESTS:BOOL=OFF"

BBCLASSEXTEND += "native"
