# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LLVM based C/C++ compiler Sanitizers Runtime"
DESCRIPTION = "Runtime libraries that are required \
				to run the code with sanitizer instrumentation"
HOMEPAGE = "http://compiler-rt.llvm.org/"
SECTION = "base"

require common-clang.inc
require common-source.inc

BPN = "compiler-rt-sanitizers"

inherit cmake pkgconfig

def get_compiler_rt_arch(bb, d):
    if bb.utils.contains('TUNE_FEATURES', 'armv5 thumb dsp', True, False, d):
        return 'armv5te'
    elif bb.utils.contains('TUNE_FEATURES', 'armv4 thumb', True, False, d):
        return 'armv4t'
    elif bb.utils.contains('TUNE_FEATURES', 'arm vfp callconvention-hard', True, False, d):
        return 'armhf'
    return d.getVar('HOST_ARCH')

LIC_FILES_CHKSUM = "file://compiler-rt/LICENSE.TXT;md5=d846d1d65baf322d4c485d6ee54e877a"

CC = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX = "${CCACHE}${HOST_PREFIX}clang++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
BUILD_CC = "${CCACHE}${HOST_PREFIX}clang ${BUILD_CC_ARCH}"
BUILD_CXX = "${CCACHE}${HOST_PREFIX}clang++ ${BUILD_CC_ARCH}$"

TOOLCHAIN = "clang"
TOOLCHAIN_NATIVE = "clang"

DEPENDS += "virtual/crypt compiler-rt"
DEPENDS:append:class-native = " clang-native libxcrypt-native libcxx-native"
DEPENDS:append:class-nativesdk = " virtual/cross-c++ clang-native clang-crosssdk-${SDK_SYS} nativesdk-libxcrypt"
DEPENDS:append:class-nativesdk = " ${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "nativesdk-libcxx", "nativesdk-gcc-runtime", d)}"
DEPENDS:append:class-target = " virtual/cross-c++ ${MLPREFIX}clang-cross-${TARGET_ARCH} virtual/${MLPREFIX}libc"
DEPENDS:append:class-target = " ${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "libcxx", "gcc-runtime", d)}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[crt] = "-DCOMPILER_RT_BUILD_CRT:BOOL=ON,-DCOMPILER_RT_BUILD_CRT:BOOL=OFF"
PACKAGECONFIG[static-libcxx] = "-DSANITIZER_USE_STATIC_CXX_ABI=ON -DSANITIZER_USE_STATIC_LLVM_UNWINDER=ON -DCOMPILER_RT_ENABLE_STATIC_UNWINDER=ON,,"
# Context Profiling
PACKAGECONFIG[ctx-profile] = "-DCOMPILER_RT_BUILD_CTX_PROFILE=ON,-DCOMPILER_RT_BUILD_CTX_PROFILE=OFF"

CXXFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

OECMAKE_SOURCEPATH = "${S}/runtimes"

INSTALL_VER ?= "${MAJOR_VER}.${MINOR_VER}.${PATCH_VER}${VER_SUFFIX}"
INSTALL_VER:class-native = "${@oe.utils.trim_version("${PV}", 1)}"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RelWithDebInfo \
                  -DCOMPILER_RT_STANDALONE_BUILD=ON \
                  -DCOMPILER_RT_USE_BUILTINS_LIBRARY=ON \
                  -DCOMPILER_RT_BUILD_BUILTINS=OFF \
                  -DCOMPILER_RT_INCLUDE_TESTS=OFF \
                  -DSANITIZER_CXX_ABI_LIBNAME=${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "libc++", "libstdc++", d)} \
                  -DCOMPILER_RT_BUILD_XRAY=ON \
                  -DCOMPILER_RT_BUILD_SANITIZERS=ON \
                  -DCOMPILER_RT_BUILD_LIBFUZZER=ON \
                  -DCOMPILER_RT_BUILD_PROFILE=ON \
                  -DCOMPILER_RT_BUILD_MEMPROF=ON \
                  -DCOMPILER_RT_DEFAULT_TARGET_ARCH=${@get_compiler_rt_arch(bb, d)} \
                  -DLLVM_ENABLE_RUNTIMES='compiler-rt' \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_APPEND_VC_REV=OFF \
                  -DCOMPILER_RT_INSTALL_PATH=${nonarch_libdir}/clang/${INSTALL_VER} \
"

EXTRA_OECMAKE:append:class-native = "\
               -DCOMPILER_RT_USE_BUILTINS_LIBRARY=OFF \
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

EXTRA_OECMAKE:append:libc-musl = " -DLIBCXX_HAS_MUSL_LIBC=ON "

FILES_SOLIBSDEV = ""
FILES:${PN} += "${nonarch_libdir}/clang/${INSTALL_VER} \
				${nonarch_libdir}/clang/${MAJOR_VER}/lib/linux/lib*${SOLIBSDEV} \
                ${nonarch_libdir}/clang/${MAJOR_VER}/*.txt \
                ${nonarch_libdir}/clang/${MAJOR_VER}/share/*.txt"
FILES:${PN}-staticdev += "${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/*.a"
FILES:${PN}-dev += "${datadir} ${nonarch_libdir}/clang/${MAJOR_VER}/lib/linux/*.syms \
                    ${nonarch_libdir}/clang/${MAJOR_VER}/include \
                    ${nonarch_libdir}/clang/${MAJOR_VER}/lib/linux/clang_rt.crt*.o \
                    ${nonarch_libdir}/clang/${MAJOR_VER}/lib/linux/libclang_rt.asan-preinit*.a"
INSANE_SKIP:${PN} = "dev-so libdir"
INSANE_SKIP:${PN}-dbg = "libdir"

#PROVIDES:append:class-target = "\
#        virtual/${MLPREFIX}compilerlibs \
#        libgcc \
#        libgcc-initial \
#        libgcc-dev \
#        libgcc-initial-dev \
#        "
#

RDEPENDS:${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"

ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dev = "1"

SYSROOT_DIRS:append:class-target = " ${nonarch_libdir}"
