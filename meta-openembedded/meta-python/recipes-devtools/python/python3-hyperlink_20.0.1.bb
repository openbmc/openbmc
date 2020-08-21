DESCRIPTION = "A featureful, correct URL for Python"
HOMEPAGE = "https://github.com/python-hyper/hyperlink"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6dc5b4bd3d02faedf08461621aa2aeca"

SRC_URI[sha256sum] = "47fcc7cd339c6cb2444463ec3277bdcfe142c8b1daf2160bdd52248deec815af"
SRC_URI[md5sum] = "d7983e3d2625e5f7dffc8d12da8803ab"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-stringold ${PYTHON_PN}-netclient ${PYTHON_PN}-idna"

PACKAGES =. "${PN}-test "

FILES_${PN}-test += " \
        ${PYTHON_SITEPACKAGES_DIR}/hyperlinkt/test \
"
