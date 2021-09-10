SUMMARY = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
DESCRIPTION = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
HOMEPAGE = "https://github.com/geomet/geomet"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"
SRCNAME = "geomet"

SRC_URI[sha256sum] = "cb52411978ee01ff104ab48f108d7333b14423ae7a15a65fee25b7d29bda2e1b"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/geomet/geomet.git;protocol=https;branch=release-${PV} \
           "
SRCREV = "73ec5ec96cca32f2e2461d3964fc3d4ab80248f9"

inherit setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-click \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
"
