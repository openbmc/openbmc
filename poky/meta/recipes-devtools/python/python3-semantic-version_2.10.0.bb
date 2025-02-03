SUMMARY  = "A library implementing the 'SemVer' scheme."
DESCRIPTION = "Semantic version comparison for Python (see http://semver.org/)"
HOMEPAGE = "https://github.com/rbarrois/python-semanticversion"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fb31e3c1c7eeb8b5e8c07657cdd54e2"

SRC_URI[sha256sum] = "bdabb6d336998cbb378d4b9db3a4b56a1e3235701dc05ea2690d9a997ed5041c"

PYPI_PACKAGE = "semantic_version"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-pkg-resources \
"

BBCLASSEXTEND = "native nativesdk"

