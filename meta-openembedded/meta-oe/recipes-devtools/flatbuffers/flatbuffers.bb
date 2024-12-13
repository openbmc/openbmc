SUMMARY = "Memory Efficient Serialization Library"
HOMEPAGE = "https://github.com/google/flatbuffers"
SECTION = "console/tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

require flatbuffers.inc

PACKAGE_BEFORE_PN = "${PN}-compiler"

DEPENDS = "flatbuffers-native"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-dev += "${PN}-compiler"

S = "${WORKDIR}/git"

EXTRA_OECMAKE += " \
    -DFLATBUFFERS_BUILD_TESTS=OFF \    
    -DFLATBUFFERS_BUILD_SHAREDLIB=ON \
"
EXTRA_OECMAKE:append:class-target = " -DFLATBUFFERS_FLATC_EXECUTABLE=${STAGING_BINDIR_NATIVE}/flatc"

inherit cmake python3native

rm_flatc_cmaketarget_for_target() {
    rm -f "${SYSROOT_DESTDIR}/${libdir}/cmake/flatbuffers/FlatcTargets.cmake"
}
SYSROOT_PREPROCESS_FUNCS:class-target += "rm_flatc_cmaketarget_for_target"

FILES:${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
