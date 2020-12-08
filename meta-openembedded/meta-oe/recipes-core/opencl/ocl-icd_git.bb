SUMMARY = "OpenCL ICD library"
DESCRIPTION = "Open Source alternative to vendor specific OpenCL ICD loaders."

# The LICENSE is BSD 2-Clause "Simplified" License
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1238d5bccbb6bda30654e48dcc0a554b"

SRC_URI = "git://github.com/OCL-dev/ocl-icd.git;protocol=https"

PV = "2.2.12+git${SRCPV}"
SRCREV = "a9e57b199ff1e8d03fa3e5c53c5544de3dc72fe6"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS = "ruby-native"

BBCLASSEXTEND = "native"
