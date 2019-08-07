SUMMARY  = "Host API C++ bindings"
DESCRIPTION = "OpenCL compute API headers C++ bindings from Khronos Group"
LICENSE  = "Khronos"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7e4a01f0c56b39419aa287361a82df00"
SECTION = "base"

SRC_URI = "git://github.com/KhronosGroup/OpenCL-CLHPP.git;protocol=https"

PV = "2.0.10+git${SRCPV}"
SRCREV = "acd6972bc65845aa28bd9f670dec84cbf8b760f3"

S = "${WORKDIR}/git"

do_configure () {
:
}

# Only cl2.hpp is necessary.
# Base on the repo, Directly input_cl2.hpp copied as cl2.hpp
do_compile () {
:
}

do_install () {
	install -d ${D}${includedir}/CL/
	install -m 0644 ${S}/input_cl2.hpp ${D}${includedir}/CL/cl2.hpp
}

ALLOW_EMPTY_${PN} = "1"
