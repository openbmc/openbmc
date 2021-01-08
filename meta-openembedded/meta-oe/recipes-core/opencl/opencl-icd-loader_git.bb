SUMMARY  = "OpenCL ICD Loader"
DESCRIPTION = "OpenCL compute ICD Loader from Khronos Group"
LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SECTION = "base"

DEPENDS += "opencl-headers"

inherit pkgconfig cmake

S = "${WORKDIR}/git"
PV = "2020.12.18+git${SRCPV}"
SRCREV = "1d5315c3ed30d026acb79a1aa53a276fc833ffa7"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-ICD-Loader.git"

do_install () {
	install -d ${D}${bindir}
	install -m 0755 ${B}/test/loader_test/icd_loader_test ${D}${bindir}/
	chrpath -d ${D}${bindir}/icd_loader_test
	install -d ${D}${libdir}
	install -m 0644 ${B}/test/log/libIcdLog.so ${D}${libdir}/
	install -m 0644 ${B}/test/driver_stub/libOpenCLDriverStub.so ${D}${libdir}/
	chrpath -d ${D}${libdir}/libOpenCLDriverStub.so
	install -m 0644 ${B}/libOpenCL.so.1.2 ${D}${libdir}/
	cd ${D}${libdir}
	ln -s libOpenCL.so.1.2 libOpenCL.so.1
	ln -s libOpenCL.so.1 libOpenCL.so
}

PACKAGES = "opencl-icd-loader opencl-icd-loader-dev"
PACKAGES += "libicdlog libicdlog-dbg"
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

FILES_${PN} = " \
	${bindir}/icd_loader_test \
	${libdir}/libOpenCLDriverStub.so \
	${libdir}/libOpenCL.so.1.2 \
"
FILES_${PN}-dev = " \
	${libdir}/libOpenCL.so \
	${libdir}/libOpenCL.so.1 \
"

FILES_libicdlog = "${libdir}/libIcdLog.so"
FILES_libicdlog-dbg = "${libdir}/.debug/libIcdLog.so"

RDEPENDS_${PN} = "libicdlog"
