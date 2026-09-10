SUMMARY = "An Enum that inherits from str"
HOMEPAGE = "https://github.com/irgeek/StrEnum"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ba0eb3de1df70bde0ed48488cfd81269"

SRC_URI += "file://0001-patch-versioneer-for-python-3.12-compatibility.patch"

DEPENDS = "python3-pytest-runner-native"

SRC_URI[sha256sum] = "878fb5ab705442070e4dd1929bb5e2249511c0bcf2b0eeacf3bcd80875c82eff"

PYPI_PACKAGE = "StrEnum"

inherit pypi python_setuptools_build_meta
