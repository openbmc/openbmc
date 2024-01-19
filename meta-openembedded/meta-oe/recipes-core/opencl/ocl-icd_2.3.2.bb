SUMMARY = "OpenCL ICD library"
DESCRIPTION = "Open Source alternative to vendor specific OpenCL ICD loaders."

# The LICENSE is BSD 2-Clause "Simplified" License
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1238d5bccbb6bda30654e48dcc0a554b"

SRC_URI = "git://github.com/OCL-dev/ocl-icd.git;protocol=https;branch=master"

SRCREV = "fdde6677b21329432db8b481e2637cd10f7d3cb2"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS = "ruby-native"
PROVIDES = "virtual/opencl-icd"
RPROVIDES:${PN} = "virtual-opencl-icd"

BBCLASSEXTEND = "native nativesdk"
