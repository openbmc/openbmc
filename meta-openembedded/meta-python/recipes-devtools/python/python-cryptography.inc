SUMMARY = "Provides cryptographic recipes and primitives to python developers"
HOMEPAGE = "https://cryptography.io/"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5ede125ed08d4fd14ec44d334bdb388"

DEPENDS += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-cffi-native \
    ${PYTHON_PN}-asn1crypto \
    ${PYTHON_PN}-six \
"

SRC_URI[md5sum] = "9f39d4d8f2b05f295fa0ff52475ec4f2"
SRC_URI[sha256sum] = "e4d967371c5b6b2e67855066471d844c5d52d210c36c28d49a8507b96e2c5291"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-asn1crypto \
    ${PYTHON_PN}-pycparser \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-six \
"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-asn1crypto \
    ${PYTHON_PN}-pycparser \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-threading \
"

RDEPENDS_${PN}-ptest += " \
    ${PN} \
    ${PYTHON_PN}-cryptography-vectors \
    ${PYTHON_PN}-iso8601 \
    ${PYTHON_PN}-pretend \
    ${PYTHON_PN}-pytest \
"

inherit ptest

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    install -d ${D}${PTEST_PATH}/tests/hazmat
    cp -rf ${S}/tests/hazmat/* ${D}${PTEST_PATH}/tests/hazmat/
}

FILES_${PN}-dbg += " \
    ${libdir}/${PYTHON_PN}2.7/site-packages/${SRCNAME}/hazmat/bindings/.debug \
"

BBCLASSEXTEND = "native nativesdk"
