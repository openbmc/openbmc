SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "https://github.com/libexpat/libexpat"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=f4fedd6116da0e171f7cb4d2923d7ac2"

VERSION_TAG = "${@d.getVar('PV').replace('.', '_')}"

SRC_URI = "${GITHUB_BASE_URI}/download/R_${VERSION_TAG}/expat-${PV}.tar.bz2  \
           file://run-ptest \
           "

GITHUB_BASE_URI = "https://github.com/libexpat/libexpat/releases/"
UPSTREAM_CHECK_REGEX = "releases/tag/R_(?P<pver>.+)"

SRC_URI[sha256sum] = "b4cc2483927d5e90bf8c40b44a6b95b368b42a8a96e25883fce188b48a92b670"

EXTRA_OECMAKE:class-native += "-DEXPAT_BUILD_DOCS=OFF"

RDEPENDS:${PN}-ptest += "bash"

inherit cmake lib_package ptest github-releases

do_install_ptest:class-target() {
	install -m 755 ${B}/tests/runtests* ${D}${PTEST_PATH}
	install -m 755 ${B}/tests/benchmark/benchmark ${D}${PTEST_PATH}
}

BBCLASSEXTEND += "native nativesdk"

CVE_PRODUCT = "expat libexpat"
