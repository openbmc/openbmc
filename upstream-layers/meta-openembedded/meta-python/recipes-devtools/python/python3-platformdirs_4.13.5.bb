SUMMARY = "A small Python module for determining appropriate platform-specific dirs"
HOMEPAGE = "https://github.com/platformdirs/platformdirs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea4f5a41454746a9ed111e3d8723d17a"

SRC_URI[sha256sum] = "05831adf87d392760f4b5c3a0a95579ab5a10b18b9b9bd43680dcb47544788a0"

inherit pypi python_hatchling ptest-python-pytest

SRC_URI += "file://run-ptest \
            file://0001-test-unix-use-host-strerror-in-warning-tests-630.patch \
            "

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
