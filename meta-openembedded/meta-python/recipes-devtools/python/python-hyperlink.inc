DESCRIPTION = "A featureful, correct URL for Python"
HOMEPAGE = "https://github.com/python-hyper/hyperlink"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3893d4ed05dcc823f8ed685a9ea19bcb"

SRC_URI[sha256sum] = "4288e34705da077fada1111a24a0aa08bb1e76699c9ce49876af722441845654"
SRC_URI[md5sum] = "4772fb4d87c26a1ab22a6161424e3cba"

inherit pypi

RDEPENDS_${PN} += "${PYTHON_PN}-stringold ${PYTHON_PN}-netclient ${PYTHON_PN}-idna"

PACKAGES =. "${PN}-test "

FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/hyperlinkt/test \
"
