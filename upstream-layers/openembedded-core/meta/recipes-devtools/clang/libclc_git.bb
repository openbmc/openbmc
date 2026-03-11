SUMMARY = "Implementation of the library requirements of the OpenCL C programming language."
HOMEPAGE = "https://libclc.llvm.org"
SECTION = "devel"

require common-clang.inc
require common-source.inc

LIC_FILES_CHKSUM = "file://libclc/LICENSE.TXT;md5=7cc795f6cbb2d801d84336b83c8017db"

inherit cmake pkgconfig

# Depend explicitly on clang-native instead of using TOOLCHAIN as the build
# objects from this recipe are build explicitly using clang for GPU targets.
# We could INHIBIT_DEFAULT_DEPS to avoid any other toolchain but then we need
# to wrestle CMake to configure without a toolchain.
DEPENDS += "clang-native spirv-llvm-translator-native"

OECMAKE_SOURCEPATH = "${S}/libclc"
B_NATIVE = "${B}-native"

# Semicolon-separated list of targets to build
LIBCLC_TARGETS ?= "all"

EXTRA_OECMAKE = "-DLIBCLC_TARGETS_TO_BUILD=${LIBCLC_TARGETS} \
                 -DPREPARE_BUILTINS=${B_NATIVE}/prepare_builtins"

# Need to build a native prepare_builtins binary in target builds. The easiest
# way to do this is with a second native cmake build tree.
do_build_prepare_builtins() {
    cmake --fresh -G Ninja \
        -S ${OECMAKE_SOURCEPATH} -B ${B_NATIVE} \
        -DCMAKE_TOOLCHAIN_FILE:FILEPATH=${WORKDIR}/toolchain-native.cmake \
        -DLIBCLC_TARGETS_TO_BUILD=
    cmake --build ${B_NATIVE} --target prepare_builtins
}
do_build_prepare_builtins:class-native() {
    :
}
do_configure[prefuncs] += "do_build_prepare_builtins"

FILES:${PN} += "${datadir}/clc"

BBCLASSEXTEND = "native nativesdk"
