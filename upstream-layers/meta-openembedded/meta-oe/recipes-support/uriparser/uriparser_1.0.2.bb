SUMMARY = "RFC 3986 compliant URI parsing library"
HOMEPAGE = "https://uriparser.github.io"

LICENSE = "BSD-3-Clause & LGPL-2.1-or-later"
LICENSE:${PN} = "BSD-3-Clause"
LICENSE:${PN}-ptest = "LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://src/COPYING;md5=fcc5a53146c2401f4b4f6a3bdf3f0168 \
                    file://test/COPYING;md5=b9e6430863a3ea22cf4b0a8518279ed3"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${BP}/${BP}.tar.gz \
           file://run-ptest"
SRC_URI[sha256sum] = "963554c32d40fb6cba5644f1ba63e6dd7a182b2948bd71ee448c532f53b07f1e"

inherit cmake github-releases ptest

UPSTREAM_CHECK_REGEX = "releases/tag/${BPN}-(?P<pver>\d+(\.\d+)+)"

PACKAGECONFIG = "${@bb.utils.contains('PTEST_ENABLED', '1', 'test', '', d)} "
PACKAGECONFIG[test] = "-DURIPARSER_BUILD_TESTS:BOOL=ON, -DURIPARSER_BUILD_TESTS:BOOL=OFF, googletest"

EXTRA_OECMAKE += " \
    -DURIPARSER_BUILD_DOCS:BOOL=OFF \
"

do_install_ptest(){
    install ${B}/testrunner ${D}${PTEST_PATH}
}

BBCLASSEXTEND += "native"
