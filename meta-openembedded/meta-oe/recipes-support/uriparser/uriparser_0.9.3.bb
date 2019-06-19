SUMMARY = "RFC 3986 compliant URI parsing library"
HOMEPAGE = "https://uriparser.github.io"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=fc3bbde670fc6e95392a0e23bf57bda0"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${BP}/${BP}.tar.gz"
SRC_URI[md5sum] = "9874b64f6f4ff656f3f69598e38f12b7"
SRC_URI[sha256sum] = "6cef39d6eaf1a48504ee0264ce85f078758057dafb1edd0a898183b55ff76014"

inherit cmake

EXTRA_OECMAKE += "-DURIPARSER_BUILD_DOCS:BOOL=OFF -DURIPARSER_BUILD_TESTS:BOOL=OFF"

BBCLASSEXTEND += "native"
