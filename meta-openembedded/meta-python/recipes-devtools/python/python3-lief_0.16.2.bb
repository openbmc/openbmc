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
"
SRCREV = "d3ad812008fd286322b7085e84136e422b3afea4"

S = "${WORKDIR}/git"
PEP517_SOURCE_PATH = "${S}/api/python"

inherit python_setuptools_build_meta

DEPENDS += "\
    python3-scikit-build-native \
    python3-scikit-build-core-native \
    python3-tomli-native \
    python3-pydantic-native \
    ninja-native \
    ccache-native \
"

BBCLASSEXTEND = "native nativesdk"
