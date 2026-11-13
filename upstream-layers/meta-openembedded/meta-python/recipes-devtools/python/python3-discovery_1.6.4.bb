SUMMARY = "Python interpreter discovery"
HOMEPAGE = "https://github.com/tox-dev/python-discovery"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11610a9d8fd95649cf8159be12b98cb7"

PYPI_PACKAGE = "python-discovery"

SRC_URI[sha256sum] = "bc70d31f0ec5de11eda13d4d64c16e24b3506a65984684f2b6c98cb83b2ea9df"

inherit pypi python_hatchling ptest-python-pytest

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += "\
    python3-core \
    python3-logging \
    python3-platformdirs \
"

RDEPENDS:${PN}-ptest += " \
    python3-setuptools \
    python3-filelock \
    python3-vermin \
"

BBCLASSEXTEND = "native nativesdk"
