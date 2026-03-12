SUMMARY = "The module provides handy URL class for url parsing and changing"
HOMEPAGE = "https://github.com/aio-libs/yarl/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "53b1ea6ca88ebd4420379c330aea57e258408dd0df9af0992e5de2078dc9f5d5"

PYPI_PACKAGE = "yarl"

inherit pypi ptest-python-pytest python_setuptools_build_meta cython

PEP517_BUILD_OPTS = "--config-setting=build-inplace=true"


DEPENDS += " \
    python3-expandvars-native \
"

RDEPENDS:${PN} = "\
    python3-multidict \
    python3-idna \
    python3-io \
    python3-propcache \
"

RDEPENDS:${PN}-ptest += " \
    python3-hypothesis \
    python3-image \
    python3-pytest \
    python3-pytest-codspeed \
    python3-rich \
    python3-unittest-automake-output \
"

INSANE_SKIP:${PN} = "already-stripped"

BBCLASSEXTEND = "native nativesdk"
