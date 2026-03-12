SUMMARY = "Retry code until it succeeds"
HOMEPAGE = "https://github.com/jd/tenacity"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI[sha256sum] = "adb31d4c263f2bd041081ab33b498309a57c77f9acf2db65aadf0898179cf93a"

SRC_URI:append = "file://0001-ptest-skip-a-test-that-does-not-pass-on-qemu.patch"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PYPI_PACKAGE = "tenacity"

RDEPENDS:${PN}-ptest += "\
    python3-tornado \
    python3-typeguard \
"
