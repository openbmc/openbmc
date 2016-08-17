SUMMARY = "Extensions to the standard Python datetime module"
DESCRIPTION = "The dateutil module provides powerful extensions to the datetime module available in the Python standard library."
HOMEPAGE = "https://dateutil.readthedocs.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51430b33c900f45f999c459ee29ca493"
PR = "r0"

SRC_URI[md5sum] = "eafe168e8f404bf384514f5116eedbb6"
SRC_URI[sha256sum] = "063907ef47f6e187b8fe0728952e4effb587a34f2dc356888646f9b71fbb2e4b"

PYPI_PACKAGE = "python-dateutil"
inherit pypi 

PACKAGES =+ "${PN}-zoneinfo"
FILES_${PN}-zoneinfo = "${libdir}/${PYTHON_DIR}/site-packages/dateutil/zoneinfo"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-six \
    "
