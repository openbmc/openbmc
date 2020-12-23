SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=25b90379a52351261c51272e7923d240"

SRC_URI[md5sum] = "54d28a7c80b3e4d974ec2fee86768be9"
SRC_URI[sha256sum] = "76f360636957d1c976db7466bc71dcb713bb95ac8911944dffc55c01cb516de6"

inherit pypi setuptools3 ptest

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-stringold \
"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
