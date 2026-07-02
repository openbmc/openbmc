# Description, License, and version from tools/cot_dt2c/pyproject.toml in TF-A source
DESCRIPTION = "CoT-dt2c Tool is a python script to convert CoT DT file into corresponding C file"
LICENSE = "BSD-3-Clause"

SRC_URI_TRUSTED_FIRMWARE_A ?= "git://review.trustedfirmware.org/TF-A/trusted-firmware-a;protocol=https"
SRC_URI = "${SRC_URI_TRUSTED_FIRMWARE_A};branch=${SRCBRANCH}"
LIC_FILES_CHKSUM = "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"

# Use cot-dt2c from TF-A v2.15.0
SRCREV = "da738d5eae93af342fdc4995dd3c05acb4c9d757"
SRCBRANCH = "master"

inherit python_poetry_core

BBCLASSEXTEND = "native nativesdk"

# Dependencies of plotly and igraph are not needed for the standard case
RDEPENDS:${PN} += "python3-click \
                   python3-pydevicetree \
                   python3-pyparsing \
                  "

PEP517_SOURCE_PATH = "${S}/tools/cot_dt2c"
