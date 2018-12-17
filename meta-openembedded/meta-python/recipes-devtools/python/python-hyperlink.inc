DESCRIPTION = "A featureful, correct URL for Python"
HOMEPAGE = "https://github.com/python-hyper/hyperlink"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3893d4ed05dcc823f8ed685a9ea19bcb"

SRC_URI[sha256sum] = "f01b4ff744f14bc5d0a22a6b9f1525ab7d6312cb0ff967f59414bbac52f0a306"
SRC_URI[md5sum] = "f41a96779b324d824864696a94b24823"

inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-stringold ${PYTHON_PN}-netclient"

PACKAGES =. "${PN}-test "

FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/hyperlinkt/test \
"
