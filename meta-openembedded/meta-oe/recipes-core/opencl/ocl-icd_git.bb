SUMMARY = "OpenCL ICD library"
DESCRIPTION = "Open Source alternative to vendor specific OpenCL ICD loaders."

# The LICENSE is BSD 2-Clause "Simplified" License
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1238d5bccbb6bda30654e48dcc0a554b"

SRC_URI = "git://github.com/OCL-dev/ocl-icd.git;protocol=https"

PV = "2.2.14+git${SRCPV}"
SRCREV = "109694ef2686fe25538c05f3c856912f8ef571a9"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS = "ruby-native"

BBCLASSEXTEND = "native"
