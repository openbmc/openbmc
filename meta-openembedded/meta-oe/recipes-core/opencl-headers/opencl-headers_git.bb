SUMMARY  = "OpenCL API Headers"
DESCRIPTION = "OpenCL compute API headers from Khronos Group"
LICENSE  = "Khronos"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dcefc90f4c3c689ec0c2489064e7273b"
SECTION = "base"

S = "${WORKDIR}/git"
SRCREV = "40c5d226c7c0706f0176884e9b94b3886679c983"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-Headers.git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install () {
	install -d ${D}${includedir}/CL/
	install -m 0644 ${S}/CL/*.h ${D}${includedir}/CL
}
