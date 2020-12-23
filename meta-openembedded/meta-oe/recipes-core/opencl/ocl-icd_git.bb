SUMMARY = "OpenCL ICD library"
DESCRIPTION = "Open Source alternative to vendor specific OpenCL ICD loaders."

# The LICENSE is BSD 2-Clause "Simplified" License
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1238d5bccbb6bda30654e48dcc0a554b"

SRC_URI = "git://github.com/OCL-dev/ocl-icd.git;protocol=https"

PV = "2.2.13+git${SRCPV}"
SRCREV = "3b7ded60ebb7e1afddcbae6f82ac8645b276e358"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS = "ruby-native"

BBCLASSEXTEND = "native"
