# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LLVM based C/C++ compiler Runtime"
DESCRIPTIOM = "Simple builtin library that provides an \
				implementation of the low-level target-specific \
				hooks required by code generation and other runtime \
				components"
HOMEPAGE = "http://compiler-rt.llvm.org/"
SECTION = "base"

require common-clang.inc
require common-source.inc

BPN = "compiler-rt"

inherit cmake pkgconfig python3native

LIC_FILES_CHKSUM = "file://compiler-rt/LICENSE.TXT;md5=d846d1d65baf322d4c485d6ee54e877a"

LIBCPLUSPLUS = ""
COMPILER_RT = ""

TUNE_CCARGS:remove = "-no-integrated-as"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS += "ninja-native libgcc"
DEPENDS:append:class-target = " virtual/cross-c++ clang-cross-${TARGET_ARCH} virtual/${MLPREFIX}libc gcc-runtime"
DEPENDS:append:class-nativesdk = " virtual/cross-c++ clang-native clang-crosssdk-${SDK_SYS} nativesdk-gcc-runtime"
DEPENDS:append:class-native = " clang-native"
DEPENDS:remove:class-native = "libcxx-native compiler-rt-native"

# Trick clang.bbclass into not creating circular dependencies
UNWINDLIB:class-nativesdk = "--unwindlib=libgcc"
COMPILER_RT:class-nativesdk = "-rtlib=libgcc"
LIBCPLUSPLUS:class-nativesdk = "-stdlib=libstdc++"
UNWINDLIB:class-native = "--unwindlib=libgcc"
COMPILER_RT:class-native = "-rtlib=libgcc"
LIBCPLUSPLUS:class-native = "-stdlib=libstdc++"
UNWINDLIB:class-target = "--unwindlib=libgcc"
COMPILER_RT:class-target = "-rtlib=libgcc"
LIBCPLUSPLUS:class-target = "-stdlib=libstdc++"

PACKAGECONFIG ??= ""
PACKAGECONFIG[crt] = "-DCOMPILER_RT_BUILD_CRT:BOOL=ON,-DCOMPILER_RT_BUILD_CRT:BOOL=OFF"
PACKAGECONFIG[profile] = "-DCOMPILER_RT_BUILD_PROFILE=ON,-DCOMPILER_RT_BUILD_PROFILE=OFF"
# Context Profiling, might need to enable 'profile' too
PACKAGECONFIG[ctx-profile] = "-DCOMPILER_RT_BUILD_CTX_PROFILE=ON,-DCOMPILER_RT_BUILD_CTX_PROFILE=OFF"

HF = ""
HF:class-target = "${@ bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', 'hf', '', d)}"

CC = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX = "${CCACHE}${HOST_PREFIX}clang++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
LDFLAGS += "${COMPILER_RT} ${UNWINDLIB}"
CXXFLAGS += "${LIBCPLUSPLUS}"

TOOLCHAIN = "clang"
TOOLCHAIN_NATIVE = "clang"

def get_compiler_rt_arch(bb, d):
    if bb.utils.contains('TUNE_FEATURES', 'armv5 thumb dsp', True, False, d):
        return 'armv5te'
    elif bb.utils.contains('TUNE_FEATURES', 'armv4 thumb', True, False, d):
        return 'armv4t'
    elif bb.utils.contains('TUNE_FEATURES', 'arm vfp callconvention-hard', True, False, d):
        return 'armhf'
    return d.getVar('HOST_ARCH')

OECMAKE_TARGET_COMPILE = "compiler-rt"
OECMAKE_TARGET_INSTALL = "install-compiler-rt install-compiler-rt-headers"
OECMAKE_SOURCEPATH = "${S}/llvm"

INSTALL_VER ?= "${MAJOR_VER}.${MINOR_VER}.${PATCH_VER}"
INSTALL_VER:class-native = "${@oe.utils.trim_version("${PV}", 1)}"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RelWithDebInfo \
                  -DCOMPILER_RT_STANDALONE_BUILD=ON \
                  -DCOMPILER_RT_INCLUDE_TESTS=OFF \
                  -DCOMPILER_RT_BUILD_XRAY=OFF \
                  -DCOMPILER_RT_BUILD_SANITIZERS=OFF \
                  -DCOMPILER_RT_BUILD_MEMPROF=OFF \
                  -DCOMPILER_RT_BUILD_LIBFUZZER=OFF \
                  -DCOMPILER_RT_DEFAULT_TARGET_ARCH=${@get_compiler_rt_arch(bb, d)} \
                  -DLLVM_ENABLE_RUNTIMES='compiler-rt' \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_APPEND_VC_REV=OFF \
                  -DCOMPILER_RT_INSTALL_PATH=${nonarch_libdir}/clang/${INSTALL_VER} \
                  -S ${S}/runtimes \
"
EXTRA_OECMAKE:append:class-native = "\
               -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=ON \
"
EXTRA_OECMAKE:append:class-target = "\
               -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
               -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
               -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
               -DCMAKE_C_COMPILER_TARGET=${HOST_SYS} \
               -DCOMPILER_RT_DEFAULT_TARGET_ONLY=ON \
               -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
               -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
"

EXTRA_OECMAKE:append:class-nativesdk = "\
               -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
               -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
               -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
               -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
               -DCLANG_TABLEGEN=${STAGING_BINDIR_NATIVE}/clang-tblgen \
               -DCMAKE_C_COMPILER_TARGET=${HOST_SYS} \
               -DCOMPILER_RT_DEFAULT_TARGET_ONLY=ON \
               -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
"

do_install:append () {
    if [ "${HF}" = "hf" ]; then
        mv -f ${D}${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/libclang_rt.builtins-arm.a \
              ${D}${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/libclang_rt.builtins-armhf.a
        mv -f ${D}${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/liborc_rt-arm.a \
              ${D}${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/liborc_rt-armhf.a
    fi
}

FILES_SOLIBSDEV = ""

FILES:${PN} += "${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/lib*${SOLIBSDEV} \
                ${nonarch_libdir}/clang/${INSTALL_VER}/*.txt \
                ${nonarch_libdir}/clang/${INSTALL_VER}/share/*.txt"
FILES:${PN}-staticdev += "${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/*.a"
FILES:${PN}-dev += "${datadir} ${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/*.syms \
                    ${nonarch_libdir}/clang/${INSTALL_VER}/include \
                    ${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/clang_rt.crt*.o \
                    ${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/libclang_rt.asan-preinit*.a"

INSANE_SKIP:${PN} = "dev-so libdir"
INSANE_SKIP:${PN}-dbg = "libdir"

RDEPENDS:${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"

ALLOW_EMPTY:${PN} = "1"

SYSROOT_DIRS:append:class-target = " ${nonarch_libdir}"
