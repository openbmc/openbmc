SUMMARY = "A small Python module for determining appropriate platform-specific dirs"
HOMEPAGE = "https://github.com/platformdirs/platformdirs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4f5a41454746a9ed111e3d8723d17a"

SRC_URI[sha256sum] = "3bfa75b0ad0db84096ae777218481852c0ebc6c727b3168c1b9e0118e458cf0a"

inherit pypi python_hatchling ptest-python-pytest

DEPENDS += " \
    python3-hatch-vcs-native \
"

RDEPENDS:${PN}-ptest += " \
    python3-appdirs \
    python3-covdefaults \
    python3-pytest-cov \
    python3-pytest-mock \
"

BBCLASSEXTEND = "native nativesdk"
