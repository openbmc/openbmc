SUMMARY  = "OpenCL ICD Loader"
DESCRIPTION = "OpenCL compute ICD Loader from Khronos Group"
LICENSE  = "Khronos"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ec724732ce73269486574c718ef0c79b"
SECTION = "base"

inherit pkgconfig cmake

S = "${WORKDIR}/git"
SRCREV = "b342ff7b7f70a4b3f2cfc53215af8fa20adc3d86"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-ICD-Loader.git"

do_install () {
	install -d ${D}${bindir}
	install -m 0755 ${B}/bin/icd_loader_test ${D}${bindir}/
	chrpath -d ${D}${bindir}/icd_loader_test
	install -d ${D}${libdir}
	install -m 0644 ${B}/lib/libIcdLog.so ${D}${libdir}/
	install -m 0644 ${B}/lib/libOpenCLDriverStub.so ${D}${libdir}/
	chrpath -d ${D}${libdir}/libOpenCLDriverStub.so
	install -m 0644 ${B}/lib/libOpenCL.so.1.2 ${D}${libdir}/
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

DEPENDS = "opencl-headers"
RDEPENDS_${PN} = "libicdlog"
