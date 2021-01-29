DESCRIPTION = "A featureful, correct URL for Python"
HOMEPAGE = "https://github.com/python-hyper/hyperlink"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6dc5b4bd3d02faedf08461621aa2aeca"

SRC_URI[sha256sum] = "427af957daa58bc909471c6c40f74c5450fa123dd093fc53efd2e91d2705a56b"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-stringold ${PYTHON_PN}-netclient ${PYTHON_PN}-idna"

PACKAGES =. "${PN}-test "

FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/hyperlinkt/test \
"
