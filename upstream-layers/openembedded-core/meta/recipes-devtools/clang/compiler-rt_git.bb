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

inherit cmake pkgconfig

LIC_FILES_CHKSUM = "\
    file://compiler-rt/LICENSE.TXT;md5=d846d1d65baf322d4c485d6ee54e877a \
    file://libunwind/LICENSE.TXT;md5=f66970035d12f196030658b11725e1a1 \
"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS:append:class-target = " virtual/cross-c++ ${MLPREFIX}clang-cross-${TARGET_ARCH} virtual/${MLPREFIX}libc libcxx-native compiler-rt-native"
DEPENDS:append:class-nativesdk = " virtual/cross-c++ clang-native clang-crosssdk-${SDK_SYS}"
DEPENDS:append:class-native = " clang-native"
DEPENDS:append:class-native = "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", " lld-native", "", d)}"
DEPENDS:remove:class-native = "libcxx-native compiler-rt-native"

PROVIDES += "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "libgcc", "", d)}"
RPROVIDES:${PN} += "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "libgcc", "", d)}"
RPROVIDES:${PN}-dev += "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "libgcc-dev", "", d)}"
RPROVIDES:${PN}-dbg += "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "libgcc-dbg", "", d)}"

PACKAGECONFIG ??= "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "crt", "", d)}"
PACKAGECONFIG[crt] = "-DCOMPILER_RT_BUILD_CRT:BOOL=ON,-DCOMPILER_RT_BUILD_CRT:BOOL=OFF"
PACKAGECONFIG[profile] = "-DCOMPILER_RT_BUILD_PROFILE=ON,-DCOMPILER_RT_BUILD_PROFILE=OFF"
# Context Profiling, might need to enable 'profile' too
PACKAGECONFIG[ctx-profile] = "-DCOMPILER_RT_BUILD_CTX_PROFILE=ON,-DCOMPILER_RT_BUILD_CTX_PROFILE=OFF"

HF = ""
HF:class-target = "${@ bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', 'hf', '', d)}"

LDFLAGS += "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", " -fuse-ld=lld -unwindlib=none -nostdlib -lpthread -lc", "", d)}"

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

INSTALL_VER ?= "${MAJOR_VER}.${MINOR_VER}.${PATCH_VER}${VER_SUFFIX}"
INSTALL_VER:class-native = "${@oe.utils.trim_version("${PV}", 1)}"

OECMAKE_SOURCEPATH = "${S}/runtimes"

RUNTIMES ?= "${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "llvm-libgcc", "compiler-rt", d)}"

LLVMLIBGCCOPTS = "-DLLVM_LIBGCC_EXPLICIT_OPT_IN=Yes"

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=RelWithDebInfo \
                  -DCMAKE_C_COMPILER_WORKS=ON \
                  -DCMAKE_CXX_COMPILER_WORKS=ON \
                  -DCXX_SUPPORTS_FNO_EXCEPTIONS_FLAG=ON \
                  -DCXX_SUPPORTS_FUNWIND_TABLES_FLAG=ON \
                  -DCOMPILER_RT_STANDALONE_BUILD=ON \
                  -DLLVM_LIBGCC_STANDALONE_BUILD=ON \
                  -DCOMPILER_RT_INCLUDE_TESTS=OFF \
                  -DCOMPILER_RT_BUILD_XRAY=OFF \
                  -DCOMPILER_RT_BUILD_SANITIZERS=OFF \
                  -DCOMPILER_RT_BUILD_MEMPROF=OFF \
                  -DCOMPILER_RT_BUILD_LIBFUZZER=OFF \
                  -DCOMPILER_RT_DEFAULT_TARGET_ARCH=${@get_compiler_rt_arch(bb, d)} \
                  -DLLVM_ENABLE_RUNTIMES=${RUNTIMES} \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DLLVM_APPEND_VC_REV=OFF \
                  -DCOMPILER_RT_BUILD_ORC=OFF \
                  -DCOMPILER_RT_DEFAULT_TARGET_ONLY=ON \
                  -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
                  -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
                  -DCMAKE_C_COMPILER_TARGET=${HOST_SYS} \
                  -DCOMPILER_RT_INSTALL_PATH=${nonarch_libdir}/clang/${INSTALL_VER} \
                  ${@bb.utils.contains("TC_CXX_RUNTIME", "llvm", "${LLVMLIBGCCOPTS}", "", d)} \
"
EXTRA_OECMAKE:append:class-target = "\
               -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
               -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
               -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
"

EXTRA_OECMAKE:append:class-nativesdk = "\
               -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
               -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
               -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
               -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
               -DCLANG_TABLEGEN=${STAGING_BINDIR_NATIVE}/clang-tblgen \
"

do_install:append () {
    if [ "${HF}" = "hf" ]; then
        install -Dm 0644 ${D}${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/libclang_rt.builtins-arm.a \
              ${D}${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/libclang_rt.builtins-armhf.a
    fi
    if ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', 'true', 'false', d)}; then
        ln -sf clang/${INSTALL_VER}/lib/linux/clang_rt.crtbegin-${@get_compiler_rt_arch(bb, d)}.o ${D}${nonarch_libdir}/crtbegin.o
        ln -sf clang/${INSTALL_VER}/lib/linux/clang_rt.crtbegin-${@get_compiler_rt_arch(bb, d)}.o ${D}${nonarch_libdir}/crtbeginS.o
        ln -sf clang/${INSTALL_VER}/lib/linux/clang_rt.crtend-${@get_compiler_rt_arch(bb, d)}.o ${D}${nonarch_libdir}/crtend.o
        ln -sf clang/${INSTALL_VER}/lib/linux/clang_rt.crtend-${@get_compiler_rt_arch(bb, d)}.o ${D}${nonarch_libdir}/crtendS.o
    fi
}

FILES:${PN} += "${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/lib*${SOLIBSDEV} \
                ${nonarch_libdir}/clang/${INSTALL_VER}/*.txt \
                ${nonarch_libdir}/clang/${INSTALL_VER}/share/*.txt \
                ${nonarch_libdir}/*.so"
FILES:${PN}-staticdev += "${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/*.a"
FILES:${PN}-dev += "${datadir} ${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/*.syms \
                    ${nonarch_libdir}/clang/${INSTALL_VER}/include \
                    ${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/clang_rt.crt*.o \
                    ${nonarch_libdir}/crt*.o \
                    ${nonarch_libdir}/clang/${INSTALL_VER}/lib/linux/libclang_rt.asan-preinit*.a"

INSANE_SKIP:${PN} = "dev-so libdir"
INSANE_SKIP:${PN}-dbg = "libdir"

RDEPENDS:${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"

SYSROOT_DIRS:append:class-target = " ${nonarch_libdir}"
