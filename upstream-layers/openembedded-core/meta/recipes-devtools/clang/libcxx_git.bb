# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "libc++ is a new implementation of the C++ standard library, targeting C++11 and above"
HOMEPAGE = "http://libcxx.llvm.org/"
SECTION = "base"

require common-clang.inc
require common-source.inc

inherit cmake

BPN = "libcxx"

PACKAGECONFIG ??= "exceptions ${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "llvm-unwind", "", d)}"

PACKAGECONFIG[exceptions] = "-DLIBCXXABI_ENABLE_EXCEPTIONS=ON -DLIBCXX_ENABLE_EXCEPTIONS=ON,-DLIBCXXABI_ENABLE_EXCEPTIONS=OFF -DLIBCXX_ENABLE_EXCEPTIONS=OFF -DCMAKE_REQUIRED_FLAGS='-fno-exceptions',"
PACKAGECONFIG[llvm-unwind] = "-DLIBCXXABI_USE_LLVM_UNWINDER=ON,-DLIBCXXABI_USE_LLVM_UNWINDER=OFF"

DEPENDS:append:class-target = " virtual/cross-c++ ${MLPREFIX}clang-cross-${TARGET_ARCH} virtual/${MLPREFIX}libc compiler-rt libgcc"
DEPENDS:append:class-nativesdk = " virtual/cross-c++ clang-crosssdk-${SDK_SYS} nativesdk-compiler-rt virtual/nativesdk-libc nativesdk-compiler-rt nativesdk-libgcc"
DEPENDS:append:class-native = " clang-native compiler-rt-native"
DEPENDS:remove:class-native = "libcxx-native"

INHIBIT_DEFAULT_DEPS = "1"

LIC_FILES_CHKSUM = "file://libcxx/LICENSE.TXT;md5=55d89dd7eec8d3b4204b680e27da3953 \
                    file://libcxxabi/LICENSE.TXT;md5=7b9334635b542c56868400a46b272b1e \
"

OECMAKE_TARGET_COMPILE = "cxxabi cxx"
OECMAKE_TARGET_INSTALL = "install-cxxabi install-cxx"

CC = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX = "${CCACHE}${HOST_PREFIX}clang++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
BUILD_CC = "${CCACHE}clang ${BUILD_CC_ARCH}"
BUILD_CXX = "${CCACHE}clang++ ${BUILD_CC_ARCH}"

TOOLCHAIN = "clang"
TOOLCHAIN_NATIVE = "clang"

LDFLAGS:append:class-target = "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", " -fuse-ld=lld -lpthread -lc", "", d)}"
LDFLAGS:append:class-nativesdk = "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", " -fuse-ld=lld -lpthread -lc", "", d)}"

OECMAKE_SOURCEPATH = "${S}/runtimes"
EXTRA_OECMAKE += "\
                  -DCMAKE_BUILD_TYPE=RelWithDebInfo \
                  -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
                  -DCMAKE_CROSSCOMPILING=ON \
                  -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=ON \
                  -DLLVM_ENABLE_RTTI=ON \
                  -DCOMPILER_RT_USE_BUILTINS_LIBRARY=ON \
                  -DLIBCXX_ENABLE_STATIC_ABI_LIBRARY=OFF \
                  -DLIBCXXABI_INCLUDE_TESTS=OFF \
                  -DLIBCXXABI_ENABLE_SHARED=ON \
                  -DLIBCXXABI_LIBCXX_INCLUDES=${S}/libcxx/include \
                  -DLIBCXX_CXX_ABI=libcxxabi \
                  -DLIBCXX_CXX_ABI_INCLUDE_PATHS=${S}/libcxxabi/include \
                  -DLIBCXX_CXX_ABI_LIBRARY_PATH=${B}/lib${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_ENABLE_RUNTIMES='libcxx;libcxxabi;libunwind' \
                  -DLLVM_RUNTIME_TARGETS=${HOST_ARCH} \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_APPEND_VC_REV=OFF \
"

EXTRA_OECMAKE:append:class-target = " \
                  -DCMAKE_C_COMPILER_WORKS=ON \
                  -DCMAKE_CXX_COMPILER_WORKS=ON \
                  -DCXX_SUPPORTS_FNO_EXCEPTIONS_FLAG=ON \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${AR} \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${NM} \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${RANLIB} \
                  -DLLVM_HOST_TRIPLE=${TARGET_SYS} \
                  -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
"

EXTRA_OECMAKE:append:class-nativesdk = " \
                  -DCMAKE_C_COMPILER_WORKS=ON \
                  -DCMAKE_CXX_COMPILER_WORKS=ON \
                  -DCXX_SUPPORTS_FNO_EXCEPTIONS_FLAG=ON \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${AR} \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${NM} \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${RANLIB} \
                  -DLLVM_DEFAULT_TARGET_TRIPLE=${HOST_SYS} \
"

EXTRA_OECMAKE:append:libc-musl = " -DLIBCXX_HAS_MUSL_LIBC=ON "

CXXFLAGS:append:armv5 = " -mfpu=vfp2"

ALLOW_EMPTY:${PN} = "1"

# Package library module manifest path
FILES:${PN}-dev += "${datadir}/libc++/v1/ ${libdir}/libc++.modules.json"

BBCLASSEXTEND = "native nativesdk"
