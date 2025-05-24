SUMMARY = "Library to instrument executable formats"
DESCRIPTION = "LIEF: Library to Instrument Executable Formats"
HOMEPAGE = "https://github.com/lief-project/LIEF"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9ab5db472ff936b441055522f5000547"
SECTION = "libs"

SRC_URI = " \
    git://github.com/lief-project/LIEF.git;protocol=https;branch=main \
    file://0001-build-requirements.txt-Allow-newer-versions.patch \
    file://0002-api-python-config-default.toml-Debug.patch \
    file://0001-Add-cstdio-include.patch \
"
SRCREV = "1e9b12bd14cbe087d52355b8b9af578f1b04d8ca"

PV .= "+git"

S = "${WORKDIR}/git"
PEP517_SOURCE_PATH = "${S}/api/python"

export LIEF_BUILD_DIR = "${B}"

inherit python_setuptools_build_meta

DEPENDS += "\
    python3-scikit-build-native \
    python3-scikit-build-core-native \
    python3-tomli-native \
    python3-pydantic-native \
    ninja-native \
    ccache-native \
    python3-typing-inspection \
"
# https://github.com/lief-project/LIEF/commit/3def579f75965aa19c021d840a759bce2afc0a31#r152197203
COMPATIBLE_HOST:x86 = "null"
# Needs pydantic and pydantic-core
COMPATIBLE_HOST:riscv32 = "null"

BBCLASSEXTEND = "native nativesdk"
