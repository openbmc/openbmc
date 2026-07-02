SUMMARY = "Python interpreter discovery"
HOMEPAGE = "https://github.com/tox-dev/python-discovery"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11610a9d8fd95649cf8159be12b98cb7"

PYPI_PACKAGE = "python_discovery"

SRC_URI[sha256sum] = "8f3746c4b4968d22afbb97d36e1a0e5b66e6c0f297290f2e95f05b9b8bf18690"

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
"

BBCLASSEXTEND = "native nativesdk"
