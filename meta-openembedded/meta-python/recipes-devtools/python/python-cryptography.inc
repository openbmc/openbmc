SUMMARY = "Provides cryptographic recipes and primitives to python developers"
HOMEPAGE = "https://cryptography.io/"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=097f805837700cfac572ac274cd38124"

LDSHARED += "-pthread"

DEPENDS += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-cffi-native \
    ${PYTHON_PN}-asn1crypto \
    ${PYTHON_PN}-six \
"

SRC_URI[md5sum] = "401cc8268f89496643da3f7a48eb4e8e"
SRC_URI[sha256sum] = "26c821cbeb683facb966045e2064303029d572a87ee69ca5a1bf54bf55f93ca6"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-asn1crypto \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-six \
"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-asn1crypto \
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
    ${PYTHON_PN}-pytz \
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
