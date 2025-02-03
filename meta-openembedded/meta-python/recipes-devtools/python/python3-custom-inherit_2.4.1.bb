SUMMARY = "A Python package that provides customized docstring inheritance schemes between derived classes and their parents."
HOMEPAGE = "https://github.com/rsokl/custom_inherit"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f910a85a4c3da51edf780f17a7608434"

PYPI_PACKAGE = "custom_inherit"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI += "file://0001-versioneer.py-do-not-use-SafeConfigParser.patch"
SRC_URI[sha256sum] = "7052eb337bcce83551815264391cc4efc2bf70b295a3c52aba64f1ab57c3a8a2"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-json \
    python3-stringold \
"
