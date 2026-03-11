SUMMARY = "pytest-json-report is a plugin that creates test reports as JSON"
HOMEPAGE = "https://github.com/numirias/pytest-json-report"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b4ca2f2ad5aaaebd8eb24f262f8fe60"

SRC_URI[sha256sum] = "2dde3c647851a19b5f3700729e8310a6e66efb2077d674f27ddea3d34dc615de"

PYPI_PACKAGE = "pytest-json-report"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} = "\
    python3-pytest \
    python3-pytest-metadata \
"

