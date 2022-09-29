SUMMARY = "Functors, Applicatives, And Monads in Python"
HOMEPAGE = "https://github.com/dbrattli/oslash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;md5=ed79d2637878dfe97de89e357b5c02dd"

DEPENDS += "\
    ${PYTHON_PN}-pip-native \
    ${PYTHON_PN}-pytest-runner-native\
"

SRC_URI[sha256sum] = "868aeb58a656f2ed3b73d9dd6abe387b20b74fc9413d3e8653b615b15bf728f3"

PYPI_PACKAGE = "OSlash"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
