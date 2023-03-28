SUMMARY = "run tests in isolated forked subprocesses"
HOMEPAGE = "https://github.com/pytest-dev/pytest-forked"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772fcdaca14b378878d05c7d857e6c3e"

SRC_URI[sha256sum] = "4dafd46a9a600f65d822b8f605133ecf5b3e1941ebb3588e943b4e3eb71a5a3f"

inherit pypi python_setuptools_build_meta

PEP517_BUILD_OPTS = "--skip-dependency-check"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-py \
    python3-pytest \
"
