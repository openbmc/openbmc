# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "libc++ is a new implementation of the C++ standard library, targeting C++11 and above"
HOMEPAGE = "http://libcxx.llvm.org/"
SECTION = "base"

require common-clang.inc
require common-source.inc

inherit cmake python3native

BPN = "libcxx"

PACKAGECONFIG ??= "compiler-rt exceptions ${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "unwind unwind-shared", "", d)}"
PACKAGECONFIG:append:armv5 = " no-atomics"
PACKAGECONFIG:remove:class-native = "compiler-rt"
PACKAGECONFIG[unwind] = "-DLIBCXXABI_USE_LLVM_UNWINDER=ON -DLIBCXXABI_ENABLE_STATIC_UNWINDER=ON,-DLIBCXXABI_USE_LLVM_UNWINDER=OFF,,"
PACKAGECONFIG[exceptions] = "-DLIBCXXABI_ENABLE_EXCEPTIONS=ON -DLIBCXX_ENABLE_EXCEPTIONS=ON,-DLIBCXXABI_ENABLE_EXCEPTIONS=OFF -DLIBCXX_ENABLE_EXCEPTIONS=OFF -DCMAKE_REQUIRED_FLAGS='-fno-exceptions',"
PACKAGECONFIG[no-atomics] = "-D_LIBCXXABI_HAS_ATOMIC_BUILTINS=OFF -DCMAKE_SHARED_LINKER_FLAGS='-latomic',,"
PACKAGECONFIG[compiler-rt] = "-DLIBCXX_USE_COMPILER_RT=ON -DLIBCXXABI_USE_COMPILER_RT=ON -DLIBUNWIND_USE_COMPILER_RT=ON,,compiler-rt"
PACKAGECONFIG[unwind-shared] = "-DLIBUNWIND_ENABLE_SHARED=ON,-DLIBUNWIND_ENABLE_SHARED=OFF,,"

DEPENDS += "ninja-native"
DEPENDS:append:class-target = " virtual/cross-c++ clang-cross-${TARGET_ARCH} virtual/${MLPREFIX}libc virtual/${MLPREFIX}compilerlibs"
DEPENDS:append:class-nativesdk = " virtual/cross-c++ clang-crosssdk-${SDK_SYS} nativesdk-compiler-rt"
DEPENDS:append:class-native = " clang-native compiler-rt-native"
DEPENDS:remove:class-native = "libcxx-native"

COMPILER_RT ?= "${@bb.utils.contains("PACKAGECONFIG", "compiler-rt", "-rtlib=compiler-rt", "-rtlib=libgcc", d)}"
UNWINDLIB ?= "${@bb.utils.contains("PACKAGECONFIG", "unwind", "-unwindlib=none", "-unwindlib=libgcc", d)}"
LIBCPLUSPLUS ?= "-stdlib=libstdc++"
# Trick clang.bbclass into not creating circular dependencies
UNWINDLIB:class-nativesdk = "-unwindlib=libgcc"
LIBCPLUSPLUS:class-nativesdk = "-stdlib=libstdc++"
UNWINDLIB:class-native = "-unwindlib=libgcc"
LIBCPLUSPLUS:class-native = "-stdlib=libstdc++"

LDFLAGS:append = " ${UNWINDLIB}"

INHIBIT_DEFAULT_DEPS = "1"

LIC_FILES_CHKSUM = "file://libcxx/LICENSE.TXT;md5=55d89dd7eec8d3b4204b680e27da3953 \
                    file://libcxxabi/LICENSE.TXT;md5=7b9334635b542c56868400a46b272b1e \
                    file://libunwind/LICENSE.TXT;md5=f66970035d12f196030658b11725e1a1 \
"

OECMAKE_TARGET_COMPILE = "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "unwind", "", d)} cxxabi cxx"
OECMAKE_TARGET_INSTALL = "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "install-unwind", "", d)} install-cxxabi install-cxx"

CC = "${CCACHE}${HOST_PREFIX}clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX = "${CCACHE}${HOST_PREFIX}clang++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
BUILD_CC = "${CCACHE}clang ${BUILD_CC_ARCH}"
BUILD_CXX = "${CCACHE}clang++ ${BUILD_CC_ARCH}"
LDFLAGS += "${COMPILER_RT} ${UNWINDLIB} ${LIBCPLUSPLUS}"
CXXFLAGS += "${LIBCPLUSPLUS}"

TOOLCHAIN = "clang"
TOOLCHAIN_NATIVE = "clang"

OECMAKE_SOURCEPATH = "${S}/llvm"
EXTRA_OECMAKE += "\
                  -DCMAKE_BUILD_TYPE=RelWithDebInfo \
                  -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
                  -DCMAKE_CROSSCOMPILING=ON \
                  -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=ON \
                  -DLLVM_ENABLE_RTTI=ON \
                  -DLIBUNWIND_ENABLE_CROSS_UNWINDING=ON \
                  -DLIBCXX_ENABLE_STATIC_ABI_LIBRARY=ON \
                  -DLIBCXXABI_INCLUDE_TESTS=OFF \
                  -DLIBCXXABI_ENABLE_SHARED=ON \
                  -DLIBCXXABI_LIBCXX_INCLUDES=${S}/libcxx/include \
                  -DLIBCXX_CXX_ABI=libcxxabi \
                  -DLIBCXX_CXX_ABI_INCLUDE_PATHS=${S}/libcxxabi/include \
                  -DLIBCXX_CXX_ABI_LIBRARY_PATH=${B}/lib${LLVM_LIBDIR_SUFFIX} \
                  -S ${S}/runtimes \
                  -DLLVM_ENABLE_RUNTIMES='libcxx;libcxxabi;libunwind' \
                  -DLLVM_RUNTIME_TARGETS=${HOST_ARCH} \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_APPEND_VC_REV=OFF \
                  -DCMAKE_BUILD_WITH_INSTALL_RPATH=ON \
"

EXTRA_OECMAKE:append:class-target = " \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${AR} \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${NM} \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${RANLIB} \
                  -DLLVM_HOST_TRIPLE=${TARGET_SYS} \
                  -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
"

EXTRA_OECMAKE:append:class-nativesdk = " \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${AR} \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${NM} \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${RANLIB} \
                  -DLLVM_DEFAULT_TARGET_TRIPLE=${HOST_SYS} \
"

EXTRA_OECMAKE:append:libc-musl = " -DLIBCXX_HAS_MUSL_LIBC=ON "

CXXFLAGS:append:armv5 = " -mfpu=vfp2"

ALLOW_EMPTY:${PN} = "1"

PROVIDES:append:runtime-llvm = " libunwind"

do_install:append() {
    if ${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "true", "false", d)}
    then
        for f in libunwind.h __libunwind_config.h unwind.h unwind_itanium.h unwind_arm_ehabi.h
        do
            install -Dm 0644 ${S}/libunwind/include/$f ${D}${includedir}/$f
        done
        install -d ${D}${libdir}/pkgconfig
        sed -e 's,@LIBDIR@,${libdir},g;s,@VERSION@,${PV},g' ${S}/libunwind/libunwind.pc.in > ${D}${libdir}/pkgconfig/libunwind.pc
    fi
}

PACKAGES:append:runtime-llvm = " libunwind"
FILES:libunwind:runtime-llvm = "${libdir}/libunwind.so.*"
# Package library module manifest path
FILES:${PN}-dev += "${datadir}/libc++/v1/ ${libdir}/libc++.modules.json"

BBCLASSEXTEND = "native nativesdk"
