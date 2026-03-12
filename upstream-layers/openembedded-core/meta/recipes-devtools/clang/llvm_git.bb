# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "The LLVM Compiler Infrastructure"
HOMEPAGE = "http://llvm.org"
LICENSE = "Apache-2.0-with-LLVM-exception"
SECTION = "devel"

require common-clang.inc
require common-source.inc

LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=8a15a0759ef07f2682d2ba4b893c9afe"

DEPENDS = "llvm-tblgen-native libffi libxml2 zlib zstd"

inherit cmake pkgconfig lib_package multilib_header

OECMAKE_SOURCEPATH = "${S}/llvm"

# By default we build all the supported CPU architectures, and the GPU targets
# if the opencl, opengl or vulkan DISTRO_FEATURES are enabled.
#
# For target builds we default to building that specific architecture, BPF, and the GPU targets if required.
#
# The available target list can be seen in the source code
# in the LLVM_ALL_TARGETS assignment:
# https://github.com/llvm/llvm-project/blob/main/llvm/CMakeLists.txt
LLVM_TARGETS_GPU ?= "${@bb.utils.contains_any('DISTRO_FEATURES', 'opencl opengl vulkan', 'AMDGPU;NVPTX;SPIRV', '', d)}"
LLVM_TARGETS_TO_BUILD ?= "AArch64;ARM;BPF;Mips;PowerPC;RISCV;X86;LoongArch;${LLVM_TARGETS_GPU}"

LLVM_EXPERIMENTAL_TARGETS_TO_BUILD ?= ""

HF = ""
HF:class-target = "${@ bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', 'hf', '', d)}"
HF[vardepvalue] = "${HF}"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=MinSizeRel \
                  -DLLVM_ENABLE_BINDINGS=OFF \
                  -DLLVM_INSTALL_UTILS=ON \
                  -DLLVM_ENABLE_FFI=ON \
                  -DLLVM_ENABLE_RTTI=ON \
                  -DLLVM_TARGETS_TO_BUILD='${LLVM_TARGETS_TO_BUILD}' \
                  -DLLVM_EXPERIMENTAL_TARGETS_TO_BUILD='${LLVM_EXPERIMENTAL_TARGETS_TO_BUILD}' \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_VERSION_SUFFIX='${VER_SUFFIX}' \
                  -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=ON \
                  -DLLVM_INCLUDE_TESTS=OFF \
                  -DLLVM_INCLUDE_EXAMPLES=OFF \
                  -DLLVM_TOOL_OBJ2YAML_BUILD=OFF \
                  -DLLVM_TOOL_YAML2OBJ_BUILD=OFF \
                  -DLLVM_NATIVE_TOOL_DIR=${STAGING_BINDIR_NATIVE} \
                  -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
                  -DCROSS_TOOLCHAIN_FLAGS_NATIVE='-DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain-native.cmake' \
                 "

EXTRA_OECMAKE:append:class-target = "\
                  -DLLVM_DEFAULT_TARGET_TRIPLE=${TARGET_SYS}${HF} \
                  -DLLVM_TARGET_ARCH=${HOST_ARCH} \
                  -DLLVM_HOST_TRIPLE=${TARGET_SYS}${HF} \
                  -DLLVM_CONFIG_PATH=${STAGING_BINDIR_NATIVE}/llvm-config \
                 "

EXTRA_OECMAKE:append:class-nativesdk = "\
                  -DLLVM_HOST_TRIPLE=${SDK_SYS} \
                  -DLLVM_CONFIG_PATH=${STAGING_BINDIR_NATIVE}/llvm-config \
                 "

PACKAGECONFIG ??= "eh rtti shared-libs ${@bb.utils.filter('DISTRO_FEATURES', 'lto thin-lto', d)}"
PACKAGECONFIG:remove:class-native = "lto thin-lto"

PACKAGECONFIG[eh] = "-DLLVM_ENABLE_EH=ON,-DLLVM_ENABLE_EH=OFF"
PACKAGECONFIG[exegesis] = "-DLLVM_TOOL_LLVM_EXEGESIS_BUILD=ON,-DLLVM_TOOL_LLVM_EXEGESIS_BUILD=OFF"
PACKAGECONFIG[libedit] = "-DLLVM_ENABLE_LIBEDIT=ON,-DLLVM_ENABLE_LIBEDIT=OFF,libedit"
PACKAGECONFIG[rtti] = "-DLLVM_ENABLE_RTTI=ON,-DLLVM_ENABLE_RTTI=OFF"
PACKAGECONFIG[shared-libs] = "-DLLVM_BUILD_LLVM_DYLIB=ON -DLLVM_LINK_LLVM_DYLIB=ON,-DLLVM_BUILD_LLVM_DYLIB=OFF -DLLVM_LINK_LLVM_DYLIB=OFF"
PACKAGECONFIG[split-dwarf] = "-DLLVM_USE_SPLIT_DWARF=ON,-DLLVM_USE_SPLIT_DWARF=OFF"
PACKAGECONFIG[opt-viewer] = "-DLLVM_TOOL_OPT_VIEWER_BUILD=ON,-DLLVM_TOOL_OPT_VIEWER_BUILD=OFF, \
                             python3-pyyaml-native python3-pygments-native python3-pyyaml python3-pygments, \
                             python3-pyyaml python3-pygments,"
PACKAGECONFIG[lto] = "-DLLVM_ENABLE_LTO=Full -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[thin-lto] = "-DLLVM_ENABLE_LTO=Thin -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"

# LLVM debug symbols are very large (several gigabytes), reduce the debug level
# so they're just hundreds of megabytes.
DEBUG_LEVELFLAG = "-g1"

reproducible_build_variables() {
    sed -i -e "s,${DEBUG_PREFIX_MAP},,g" \
        -e "s,--sysroot=${RECIPE_SYSROOT},,g" \
        -e "s,${STAGING_DIR_HOST},,g" \
        -e "s,${S}/llvm,,g"  \
        -e "s,${B},,g" \
        ${B}/tools/llvm-config/BuildVariables.inc
}

do_configure:append:class-target() {
    reproducible_build_variables
}
do_configure:append:class-nativesdk() {
    reproducible_build_variables
}

do_install:append() {
    # llvm hardcodes lib as install path, this corrects it to actual libdir.
    # https://github.com/llvm/llvm-project/issues/152193
    if [ -d ${D}/${prefix}/lib -a ! -d ${D}/${libdir} ]; then
        mv ${D}/${prefix}/lib ${D}/${libdir}
    fi

    # Reproducibility fixes
    sed -i -e 's,${WORKDIR},,g' ${D}/${libdir}/cmake/llvm/LLVMConfig.cmake

    oe_multilib_header llvm/Config/llvm-config.h
}

do_install:append:class-native() {
    # These are provided by llvm-tblgen-native
    rm ${D}${bindir}/*-tblgen
}

SYSROOT_PREPROCESS_FUNCS:append:class-target = " llvm_sysroot_preprocess"
SYSROOT_PREPROCESS_FUNCS:append:class-nativesdk = " llvm_sysroot_preprocess"

llvm_sysroot_preprocess() {
        install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
        install -m 0755 ${S}/llvm/tools/llvm-config/llvm-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/
}

FILES:${PN}-dev += "${libdir}/llvm-config"

BBCLASSEXTEND = "native nativesdk"
