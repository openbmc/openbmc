SUMMARY = "A drop-in replacement for argparse that allows options to also be set via config files and/or environment variables."
HOMEPAGE = "https://github.com/bw2/ConfigArgParse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=da746463714cc35999ed9a42339f2943"

SRC_URI[md5sum] = "8978866babc7ff0994f691ebe48b5aba"
SRC_URI[sha256sum] = "edd17be986d5c1ba2e307150b8e5f5107aba125f3574dddd02c85d5cdcfd37dc"

PYPI_PACKAGE = "ConfigArgParse"

inherit pypi setuptools3

PACKAGECONFIG ?= "yaml"
PACKAGECONFIG[yaml] = ",,,${PYTHON_PN}-pyyaml"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"

