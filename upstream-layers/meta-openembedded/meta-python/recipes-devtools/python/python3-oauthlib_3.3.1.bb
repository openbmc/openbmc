SUMMARY = "A generic, spec-compliant, thorough implementation of the OAuth request-signing logic"
HOMEPAGE = "https://github.com/idan/oauthlib"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2699a9fb0d71d5eafd75d8d7c302f7df"

SRC_URI[sha256sum] = "0f0f8aa759826a193cf66c12ea1af1637f87b9b4622d46e866952bb022e538c9"

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
