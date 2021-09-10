SUMMARY = "A generic, spec-compliant, thorough implementation of the OAuth request-signing logic"
HOMEPAGE = "https://github.com/idan/oauthlib"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=abd2675e944a2011aed7e505290ba482"

SRC_URI[sha256sum] = "8f0215fcc533dd8dd1bee6f4c412d4f0cd7297307d43ac61666389e3bc3198a3"

inherit pypi setuptools3

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

RDEPENDS:${PN} += "${PYTHON_PN}-core ${PYTHON_PN}-crypt ${PYTHON_PN}-datetime ${PYTHON_PN}-json ${PYTHON_PN}-logging ${PYTHON_PN}-math ${PYTHON_PN}-netclient ${PYTHON_PN}-unittest"
