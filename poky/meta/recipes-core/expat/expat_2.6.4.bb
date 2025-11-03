SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"
HOMEPAGE = "https://github.com/libexpat/libexpat"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=7b3b078238d0901d3b339289117cb7fb"

VERSION_TAG = "${@d.getVar('PV').replace('.', '_')}"

SRC_URI = "${GITHUB_BASE_URI}/download/R_${VERSION_TAG}/expat-${PV}.tar.bz2  \
           file://run-ptest \
           file://0001-tests-Cover-indirect-entity-recursion.patch;striplevel=2 \
           file://CVE-2024-8176-01.patch;striplevel=2 \
           file://CVE-2024-8176-02.patch;striplevel=2 \
           file://CVE-2024-8176-03.patch \
           file://CVE-2024-8176-04.patch \
           file://CVE-2024-8176-05.patch \
           file://CVE-2025-59375-00.patch \
           file://CVE-2025-59375-01.patch \
           file://CVE-2025-59375-02.patch \
           file://CVE-2025-59375-03.patch \
           file://CVE-2025-59375-04.patch \
           file://CVE-2025-59375-05.patch \
           file://CVE-2025-59375-06.patch \
           file://CVE-2025-59375-07.patch \
           file://CVE-2025-59375-08.patch \
           file://CVE-2025-59375-09.patch \
           file://CVE-2025-59375-10.patch \
           file://CVE-2025-59375-11.patch \
           file://CVE-2025-59375-12.patch \
           file://CVE-2025-59375-13.patch \
           file://CVE-2025-59375-14.patch \
           file://CVE-2025-59375-15.patch \
           file://CVE-2025-59375-16.patch \
           file://CVE-2025-59375-17.patch \
           file://CVE-2025-59375-18.patch \
           file://CVE-2025-59375-19.patch \
           file://CVE-2025-59375-20.patch \
           file://CVE-2025-59375-21.patch \
           file://CVE-2025-59375-22.patch \
           file://CVE-2025-59375-23.patch \
           file://CVE-2025-59375-24.patch \
           "

GITHUB_BASE_URI = "https://github.com/libexpat/libexpat/releases/"
UPSTREAM_CHECK_REGEX = "releases/tag/R_(?P<pver>.+)"

SRC_URI[sha256sum] = "8dc480b796163d4436e6f1352e71800a774f73dbae213f1860b60607d2a83ada"

EXTRA_OECMAKE:class-native += "-DEXPAT_BUILD_DOCS=OFF"

RDEPENDS:${PN}-ptest += "bash"

inherit cmake lib_package ptest github-releases

do_install_ptest:class-target() {
	install -m 755 ${B}/tests/runtests* ${D}${PTEST_PATH}
	install -m 755 ${B}/tests/benchmark/benchmark ${D}${PTEST_PATH}
}

BBCLASSEXTEND += "native nativesdk"

CVE_PRODUCT = "expat libexpat"
