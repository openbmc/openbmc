DESCRIPTION = "An implementation of RFC 7049 - Concise Binary Object Representation (CBOR)."
DEPENDS +="${PYTHON_PN}-setuptools-scm-native"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI[sha256sum] = "91759bd0ee5ef0d4fa24144dfa551670730baeca8cf2fff1cc59f734ecd21de6"
SRC_URI[md5sum] = "693c1bd6635bee8a591a70ea924ad676"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
       ${PYTHON_PN}-pytest \
       ${PYTHON_PN}-unixadmin \
"

do_install_ptest() {
      install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
