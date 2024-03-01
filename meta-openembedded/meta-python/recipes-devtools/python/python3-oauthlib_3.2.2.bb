SUMMARY = "A generic, spec-compliant, thorough implementation of the OAuth request-signing logic"
HOMEPAGE = "https://github.com/idan/oauthlib"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=abd2675e944a2011aed7e505290ba482"

SRC_URI[sha256sum] = "9859c40929662bec5d64f34d01c99e093149682a3f38915dc0655d5a633dd918"

inherit pypi setuptools3

# The following configs & dependencies are from setuptools extras_require.
# These dependencies are optional, hence can be controlled via PACKAGECONFIG.
# The upstream names may not correspond exactly to bitbake package names.
#
# Uncomment this line to enable all the optional features.
#PACKAGECONFIG ?= "test signedtoken signals rsa"
PACKAGECONFIG[test] = ",,,python3-blinker python3-cryptography python3-pytest python3-pyjwt"
PACKAGECONFIG[signedtoken] = ",,,python3-cryptography python3-pyjwt"
PACKAGECONFIG[signals] = ",,,python3-blinker"
PACKAGECONFIG[rsa] = ",,,python3-cryptography"

RDEPENDS:${PN} += "python3-core python3-crypt python3-datetime python3-json python3-logging python3-math python3-netclient python3-unittest"
