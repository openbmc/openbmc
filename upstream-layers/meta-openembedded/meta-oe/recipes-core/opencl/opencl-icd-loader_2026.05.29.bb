SUMMARY = "OpenCL ICD Loader"
DESCRIPTION = "OpenCL compute ICD Loader from Khronos Group"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SECTION = "base"

inherit pkgconfig cmake features_check

REQUIRED_DISTRO_FEATURES = "opencl"

DEPENDS += "opencl-headers"

PROVIDES = "virtual/libopencl1"

SRCREV = "b7bd2803acc779c03d96588e9ca9e9568a18698a"
SRC_URI = "git://github.com/KhronosGroup/OpenCL-ICD-Loader.git;branch=main;protocol=https;tag=v${PV}"

EXTRA_OECMAKE:append = " \
    -DOPENCL_ICD_LOADER_HEADERS_DIR=${STAGING_INCDIR} \
    -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/icd_loader_test ${D}${bindir}/
    chrpath -d ${D}${bindir}/icd_loader_test
    install -d ${D}${libdir}
    install -m 0644 ${B}/libIcdLog.so ${D}${libdir}/
    install -m 0644 ${B}/libOpenCLDriverStub.so ${D}${libdir}/
    chrpath -d ${D}${libdir}/libOpenCLDriverStub.so
    install -m 0644 ${B}/libOpenCL.so.1.0.0 ${D}${libdir}/
    cd ${D}${libdir}
    ln -s libOpenCL.so.1.0.0 libOpenCL.so.1
    ln -s libOpenCL.so.1 libOpenCL.so
}

PACKAGES = "opencl-icd-loader opencl-icd-loader-dev"
PACKAGES += "libicdlog libicdlog-dbg"
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

FILES:${PN} = " \
    ${bindir}/icd_loader_test \
    ${libdir}/libOpenCLDriverStub.so \
    ${libdir}/libOpenCL.so.1.0.0 \
    ${libdir}/libOpenCL.so.1 \
"
FILES:${PN}-dev = " \
    ${libdir}/libOpenCL.so \
"

FILES:libicdlog = "${libdir}/libIcdLog.so"
FILES:libicdlog-dbg = "${libdir}/.debug/libIcdLog.so"

RDEPENDS:${PN} = "libicdlog"
RRECOMMENDS:${PN} = "virtual-opencl-icd"
