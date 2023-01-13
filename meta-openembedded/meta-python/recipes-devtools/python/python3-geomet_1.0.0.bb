SUMMARY = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
DESCRIPTION = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
HOMEPAGE = "https://github.com/geomet/geomet"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"
SRCNAME = "geomet"

SRC_URI[sha256sum] = "cb52411978ee01ff104ab48f108d7333b14423ae7a15a65fee25b7d29bda2e1b"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/geomet/geomet.git;protocol=https;branch=master"
SRCREV = "7fff4438510c30e592db858b2d519eddb5837a6c"

inherit setuptools3
PIP_INSTALL_PACKAGE = "geomet"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-click \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
"
