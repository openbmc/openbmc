SUMMARY = "A drop-in replacement for argparse that allows options to also be set via config files and/or environment variables."
HOMEPAGE = "https://github.com/bw2/ConfigArgParse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=da746463714cc35999ed9a42339f2943"

SRC_URI[md5sum] = "aba15b7973b7a70bea86fd69289f8fe3"
SRC_URI[sha256sum] = "baaf0fd2c1c108d007f402dab5481ac5f12d77d034825bf5a27f8224757bd0ac"

PYPI_PACKAGE = "ConfigArgParse"

inherit pypi setuptools3

PACKAGECONFIG ?= "yaml"
PACKAGECONFIG[yaml] = ",,,${PYTHON_PN}-pyyaml"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"

