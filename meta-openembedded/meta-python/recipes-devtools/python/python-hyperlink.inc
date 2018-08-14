DESCRIPTION = "A featureful, correct URL for Python"
HOMEPAGE = "https://github.com/python-hyper/hyperlink"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3893d4ed05dcc823f8ed685a9ea19bcb"

SRC_URI[md5sum] = "eaccb9845b559817e838846669cbc68a"
SRC_URI[sha256sum] = "bc4ffdbde9bdad204d507bd8f554f16bba82dd356f6130cb16f41422909c33bc"

inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-stringold ${PYTHON_PN}-netclient"

PACKAGES =. "${PN}-test "

FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/hyperlinkt/test \
"
