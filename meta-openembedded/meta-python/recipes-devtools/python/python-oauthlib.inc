SUMMARY = "A generic, spec-compliant, thorough implementation of the OAuth request-signing logic"
HOMEPAGE = "https://github.com/idan/oauthlib"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ba9ce41463615e082609806255bce1b"

SRC_URI = "https://pypi.python.org/packages/a5/8a/212e9b47fb54be109f3ff0684165bb38c51117f34e175c379fce5c7df754/oauthlib-${PV}.tar.gz"
SRC_URI[md5sum] = "277a9a966cc8c72e492b4eeb41332445"
SRC_URI[sha256sum] = "ce57b501e906ff4f614e71c36a3ab9eacbb96d35c24d1970d2539bbc3ec70ce1"

S = "${WORKDIR}/oauthlib-${PV}"

# The following configs & dependencies are from setuptools extras_require.
# These dependencies are optional, hence can be controlled via PACKAGECONFIG.
# The upstream names may not correspond exactly to bitbake package names.
#
# Uncomment this line to enable all the optional features.
#PACKAGECONFIG ?= "test signedtoken signals rsa"
PACKAGECONFIG[test] = ",,,${PYTHON_PN}-blinker ${PYTHON_PN}-cryptography ${PYTHON_PN}-nose ${PYTHON_PN}-pyjwt"
PACKAGECONFIG[signedtoken] = ",,,${PYTHON_PN}-cryptography ${PYTHON_PN}-pyjwt"
PACKAGECONFIG[signals] = ",,,${PYTHON_PN}-blinker"
PACKAGECONFIG[rsa] = ",,,${PYTHON_PN}-cryptography"

RDEPENDS_${PN} += "${PYTHON_PN}-core ${PYTHON_PN}-crypt ${PYTHON_PN}-datetime ${PYTHON_PN}-json ${PYTHON_PN}-logging ${PYTHON_PN}-math ${PYTHON_PN}-netclient ${PYTHON_PN}-unittest"

