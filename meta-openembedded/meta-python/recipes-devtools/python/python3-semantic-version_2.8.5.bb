SUMMARY  = "A library implementing the 'SemVer' scheme."
DESCRIPTION = "Semantic version comparison for Python (see http://semver.org/)"
HOMEPAGE = "https://github.com/rbarrois/python-semanticversion"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fb31e3c1c7eeb8b5e8c07657cdd54e2"

SRC_URI[sha256sum] = "d2cb2de0558762934679b9a104e82eca7af448c9f4974d1f3eeccff651df8a54"

PYPI_PACKAGE = "semantic_version"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
