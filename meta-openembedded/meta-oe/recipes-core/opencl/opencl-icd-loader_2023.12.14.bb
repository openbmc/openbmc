SUMMARY = "OpenCL ICD Loader"
DESCRIPTION = "OpenCL compute ICD Loader from Khronos Group"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SECTION = "base"

inherit pkgconfig cmake

DEPENDS += "opencl-headers"

PROVIDES = "virtual/opencl-icd"
RPROVIDES:${PN} = "virtual/opencl-icd"

S = "${WORKDIR}/git"

SRCREV = "229410f86a8c8c9e0f86f195409e5481a2bae067"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-ICD-Loader.git;branch=main;protocol=https"

EXTRA_OECMAKE:append = " \
    -DOPENCL_ICD_LOADER_HEADERS_DIR=${STAGING_INCDIR} \
"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/icd_loader_test ${D}${bindir}/
    chrpath -d ${D}${bindir}/icd_loader_test
    install -d ${D}${libdir}
    install -m 0644 ${B}/libIcdLog.so ${D}${libdir}/
    install -m 0644 ${B}/libOpenCLDriverStub.so ${D}${libdir}/
    chrpath -d ${D}${libdir}/libOpenCLDriverStub.so
    install -m 0644 ${B}/libOpenCL.so.1.2 ${D}${libdir}/
    cd ${D}${libdir}
    ln -s libOpenCL.so.1.2 libOpenCL.so.1
    ln -s libOpenCL.so.1 libOpenCL.so
}

PACKAGES = "opencl-icd-loader opencl-icd-loader-dev"
PACKAGES += "libicdlog libicdlog-dbg"
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

FILES:${PN} = " \
    ${bindir}/icd_loader_test \
    ${libdir}/libOpenCLDriverStub.so \
    ${libdir}/libOpenCL.so.1.2 \
    ${libdir}/libOpenCL.so.1 \
"
FILES:${PN}-dev = " \
    ${libdir}/libOpenCL.so \
"

FILES:libicdlog = "${libdir}/libIcdLog.so"
FILES:libicdlog-dbg = "${libdir}/.debug/libIcdLog.so"

RDEPENDS:${PN} = "libicdlog"
