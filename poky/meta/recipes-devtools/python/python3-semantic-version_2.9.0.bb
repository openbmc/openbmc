SUMMARY  = "A library implementing the 'SemVer' scheme."
DESCRIPTION = "Semantic version comparison for Python (see http://semver.org/)"
HOMEPAGE = "https://github.com/rbarrois/python-semanticversion"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fb31e3c1c7eeb8b5e8c07657cdd54e2"

SRC_URI[sha256sum] = "abf54873553e5e07a6fd4d5f653b781f5ae41297a493666b59dcf214006a12b2"

PYPI_PACKAGE = "semantic_version"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/semantic-version/(?P<pver>(\d+[\.\-_]*)+)/"
