SUMMARY = "A small Python module for determining appropriate platform-specific dirs"
HOMEPAGE = "https://github.com/platformdirs/platformdirs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4f5a41454746a9ed111e3d8723d17a"

SRC_URI[sha256sum] = "357fb2acbc885b0419afd3ce3ed34564c13c9b95c89360cd9563f73aa5e2b907"

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
