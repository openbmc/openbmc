# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "LLVM based C/C++ compiler"
DESCRIPTION = "Clang is an LLVM based C/C++/Objective-C compiler, \
                which aims to deliver amazingly fast compiles, \
                extremely useful error and warning messages and \
                to provide a platform for building great source \
                level tools. The Clang Static Analyzer and \
                clang-tidy are tools that automatically find bugs \
                in your code, and are great examples of the sort \
                of tools that can be built using the Clang frontend \
                as a library to parse C/C++ code"
HOMEPAGE = "http://clang.llvm.org/"
SECTION = "devel"

require common-clang.inc
require common-source.inc

BPN = "clang"

CVE_PRODUCT += "llvm:clang"

INHIBIT_DEFAULT_DEPS:class-native = "1"

LDFLAGS:append:class-target:riscv32 = " -Wl,--no-as-needed -latomic -Wl,--as-needed"
LDFLAGS:append:class-target:mips = " -Wl,--no-as-needed -latomic -Wl,--as-needed"

inherit cmake pkgconfig python3native python3targetconfig multilib_header

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

def get_clang_arch(bb, d, arch_var):
    import re
    a = d.getVar(arch_var)
    if   re.match('(i.86|athlon|x86.64)$', a):         return 'X86'
    elif re.match('arm$', a):                          return 'ARM'
    elif re.match('armeb$', a):                        return 'ARM'
    elif re.match('aarch64$', a):                      return 'AArch64'
    elif re.match('aarch64_be$', a):                   return 'AArch64'
    elif re.match('mips(isa|)(32|64|)(r6|)(el|)$', a): return 'Mips'
    elif re.match('riscv32$', a):                      return 'RISCV'
    elif re.match('riscv64$', a):                      return 'RISCV'
    elif re.match('p(pc|owerpc)(|64)', a):             return 'PowerPC'
    elif re.match('loongarch64$', a):                  return 'LoongArch'
    else:
        bb.note("'%s' is not a primary llvm architecture" % a)
    return ""

def get_clang_host_arch(bb, d):
    return get_clang_arch(bb, d, 'HOST_ARCH')

def get_clang_target_arch(bb, d):
    return get_clang_arch(bb, d, 'TARGET_ARCH')

PACKAGECONFIG_CLANG_COMMON = "build-id eh libedit rtti shared-libs libclang-python \
                              ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', 'lld', '', d)} \
                              ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', 'compiler-rt libcplusplus libomp unwindlib', '', d)} \
                              "

PACKAGECONFIG ??= "${PACKAGECONFIG_CLANG_COMMON} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'lto thin-lto', d)} \
                   "
PACKAGECONFIG:class-native = "clangd \
                              ${PACKAGECONFIG_CLANG_COMMON} \
                              "
PACKAGECONFIG:class-nativesdk = "clangd \
                                 ${PACKAGECONFIG_CLANG_COMMON} \
                                 ${@bb.utils.filter('DISTRO_FEATURES', 'lto thin-lto', d)} \
                                 "

PACKAGECONFIG[bootstrap] = "-DCLANG_ENABLE_BOOTSTRAP=On -DCLANG_BOOTSTRAP_PASSTHROUGH='${PASSTHROUGH}' -DBOOTSTRAP_LLVM_ENABLE_LTO=Thin -DBOOTSTRAP_LLVM_ENABLE_LLD=ON,,,"
PACKAGECONFIG[build-id] = "-DENABLE_LINKER_BUILD_ID=ON,-DENABLE_LINKER_BUILD_ID=OFF,,"
PACKAGECONFIG[clangd] = "-DCLANG_ENABLE_CLANGD=ON,-DCLANG_ENABLE_CLANGD=OFF,,"

# Activate to build the dexp tool in clangd
# Disabled by default for -native since it is known to trigger compiler failure on Debian 11
# See: https://bugzilla.yoctoproject.org/show_bug.cgi?id=15803
PACKAGECONFIG[clangd-dexp] = "-DCLANGD_BUILD_DEXP=ON,-DCLANGD_BUILD_DEXP=OFF,,"

PACKAGECONFIG[compiler-rt] = "-DCLANG_DEFAULT_RTLIB=compiler-rt,,"
PACKAGECONFIG[eh] = "-DLLVM_ENABLE_EH=ON,-DLLVM_ENABLE_EH=OFF,,"
PACKAGECONFIG[libcplusplus] = "-DCLANG_DEFAULT_CXX_STDLIB=libc++,,"
PACKAGECONFIG[libedit] = "-DLLVM_ENABLE_LIBEDIT=ON,-DLLVM_ENABLE_LIBEDIT=OFF,libedit libedit-native"
PACKAGECONFIG[libomp] = "-DCLANG_DEFAULT_OPENMP_RUNTIME=libomp,,"
PACKAGECONFIG[lld] = "-DCLANG_DEFAULT_LINKER=lld,,"
PACKAGECONFIG[lto] = "-DLLVM_ENABLE_LTO=Full -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[pfm] = "-DLLVM_ENABLE_LIBPFM=ON,-DLLVM_ENABLE_LIBPFM=OFF,libpfm,"
PACKAGECONFIG[rtti] = "-DLLVM_ENABLE_RTTI=ON,-DLLVM_ENABLE_RTTI=OFF,,"
PACKAGECONFIG[shared-libs] = "-DLLVM_BUILD_LLVM_DYLIB=ON -DLLVM_LINK_LLVM_DYLIB=ON,,,"
PACKAGECONFIG[split-dwarf] = "-DLLVM_USE_SPLIT_DWARF=ON,-DLLVM_USE_SPLIT_DWARF=OFF,,"
PACKAGECONFIG[thin-lto] = "-DLLVM_ENABLE_LTO=Thin -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[unwindlib] = "-DCLANG_DEFAULT_UNWINDLIB=libunwind,-DCLANG_DEFAULT_UNWINDLIB=libgcc,,"
PACKAGECONFIG[libclang-python] = "-DCLANG_PYTHON_BINDINGS_VERSIONS=${PYTHON_BASEVERSION},,"

OECMAKE_SOURCEPATH = "${S}/llvm"

OECMAKE_TARGET_COMPILE = "${@bb.utils.contains('PACKAGECONFIG', 'bootstrap', 'stage2', 'all', d)}"
OECMAKE_TARGET_INSTALL = "${@bb.utils.contains('PACKAGECONFIG', 'bootstrap', 'stage2-install', 'install', d)}"
BINPATHPREFIX = "${@bb.utils.contains('PACKAGECONFIG', 'bootstrap', '/tools/clang/stage2-bins/NATIVE', '', d)}"

PASSTHROUGH = "\
CLANG_DEFAULT_RTLIB;CLANG_DEFAULT_CXX_STDLIB;LLVM_BUILD_LLVM_DYLIB;LLVM_LINK_LLVM_DYLIB;\
LLVM_ENABLE_ASSERTIONS;LLVM_ENABLE_EXPENSIVE_CHECKS;LLVM_ENABLE_PIC;\
LLVM_BINDINGS_LIST;LLVM_ENABLE_FFI;FFI_INCLUDE_DIR;LLVM_OPTIMIZED_TABLEGEN;\
LLVM_ENABLE_RTTI;LLVM_ENABLE_EH;LLVM_BUILD_EXTERNAL_COMPILER_RT;CMAKE_SYSTEM_NAME;\
CMAKE_BUILD_TYPE;BUILD_SHARED_LIBS;LLVM_ENABLE_PROJECTS;LLVM_ENABLE_RUNTIMES;LLVM_BINUTILS_INCDIR;\
LLVM_TARGETS_TO_BUILD;LLVM_EXPERIMENTAL_TARGETS_TO_BUILD;PYTHON_EXECUTABLE;\
PYTHON_LIBRARY;PYTHON_INCLUDE_DIR;LLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN;\
LLVM_ENABLE_LIBEDIT;CMAKE_C_FLAGS_RELEASE;CMAKE_CXX_FLAGS_RELEASE;CMAKE_ASM_FLAGS_RELEASE;\
CLANG_DEFAULT_CXX_STDLIB;CLANG_DEFAULT_RTLIB;CLANG_DEFAULT_UNWINDLIB;\
CLANG_DEFAULT_OPENMP_RUNTIME;LLVM_ENABLE_PER_TARGET_RUNTIME_DIR;\
LLVM_BUILD_TOOLS;LLVM_USE_HOST_TOOLS;LLVM_CONFIG_PATH;LLVM_EXTERNAL_SPIRV_HEADERS_SOURCE_DIR;\
"

# By default we build all the supported CPU architectures, and the GPU targets
# if the opengl or vulkan DISTRO_FEATURES are enabled.
#
# For target builds we default to building that specific architecture, BPF, and the GPU targets if required.
#
# The available target list can be seen in the source code
# in the LLVM_ALL_TARGETS assignment:
# https://github.com/llvm/llvm-project/blob/main/llvm/CMakeLists.txt
LLVM_TARGETS_GPU ?= "${@bb.utils.contains_any('DISTRO_FEATURES', 'opengl vulkan', 'AMDGPU;NVPTX;SPIRV', '', d)}"
LLVM_TARGETS_TO_BUILD ?= "AArch64;ARM;BPF;Mips;PowerPC;RISCV;X86;LoongArch;${LLVM_TARGETS_GPU}"
LLVM_TARGETS_TO_BUILD:class-target ?= "${@get_clang_host_arch(bb, d)};BPF;${LLVM_TARGETS_GPU}"

LLVM_EXPERIMENTAL_TARGETS_TO_BUILD ?= ""

HF = ""
HF:class-target = "${@ bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', 'hf', '', d)}"
HF[vardepvalue] = "${HF}"

# Ensure that LLVM_PROJECTS does not contain compiler runtime components e.g. libcxx etc
# they are enabled via LLVM_ENABLE_RUNTIMES
LLVM_PROJECTS ?= "clang;clang-tools-extra;lld"

# linux hosts (.so) on Windows .pyd
SOLIBSDEV:mingw32 = ".pyd"

#CMAKE_VERBOSE = "VERBOSE=1"

EXTRA_OECMAKE += "-DLLVM_ENABLE_ASSERTIONS=OFF \
                  -DLLVM_APPEND_VC_REV=OFF \
                  -DLLVM_ENABLE_PER_TARGET_RUNTIME_DIR=OFF \
                  -DLLVM_ENABLE_EXPENSIVE_CHECKS=OFF \
                  -DLLVM_ENABLE_PIC=ON \
                  -DCLANG_DEFAULT_PIE_ON_LINUX=ON \
                  -DLLVM_BINDINGS_LIST='' \
                  -DLLVM_ENABLE_FFI=ON \
                  -DLLVM_ENABLE_ZSTD=ON \
                  -DFFI_INCLUDE_DIR=$(pkg-config --variable=includedir libffi) \
                  -DLLVM_BUILD_EXTERNAL_COMPILER_RT=ON \
                  -DCMAKE_SYSTEM_NAME=Linux \
                  -DCMAKE_BUILD_TYPE=Release \
                  -DLLVM_ENABLE_PROJECTS='${LLVM_PROJECTS}' \
                  -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR} \
                  -DLLVM_VERSION_SUFFIX='${VER_SUFFIX}' \
                  -DLLVM_TEMPORARILY_ALLOW_OLD_TOOLCHAIN=ON \
                  -DLLVM_TARGETS_TO_BUILD='${LLVM_TARGETS_TO_BUILD}' \
                  -DLLVM_EXPERIMENTAL_TARGETS_TO_BUILD='${LLVM_EXPERIMENTAL_TARGETS_TO_BUILD}' \
                  -DLLVM_NATIVE_TOOL_DIR=${STAGING_BINDIR_NATIVE} \
                  -DLLVM_HEADERS_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-min-tblgen \
"

EXTRA_OECMAKE:append:class-nativesdk = "\
                  -DCROSS_TOOLCHAIN_FLAGS_NATIVE='-DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain-native.cmake' \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
                  -DCMAKE_STRIP=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-strip \
                  -DPYTHON_LIBRARY=${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so \
                  -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI} \
"
EXTRA_OECMAKE:append:class-target = "\
                  -DCMAKE_RANLIB=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ranlib \
                  -DCMAKE_AR=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-ar \
                  -DCMAKE_NM=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-nm \
                  -DCMAKE_STRIP=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}llvm-strip \
                  -DLLVM_TARGET_ARCH=${HOST_ARCH} \
                  -DLLVM_DEFAULT_TARGET_TRIPLE=${TARGET_SYS}${HF} \
                  -DLLVM_HOST_TRIPLE=${TARGET_SYS}${HF} \
                  -DPYTHON_LIBRARY=${STAGING_LIBDIR}/lib${PYTHON_DIR}${PYTHON_ABI}.so \
                  -DPYTHON_INCLUDE_DIR=${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI} \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
"

DEPENDS = "binutils zlib zstd libffi libxml2 libxml2-native ninja-native swig-native spirv-tools-native llvm-tblgen-native"
DEPENDS:append:class-nativesdk = " clang-crosssdk-${SDK_SYS} virtual/nativesdk-cross-binutils nativesdk-python3"
DEPENDS:append:class-target = " clang-cross-${TARGET_ARCH} python3 ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', 'compiler-rt libcxx', '', d)}"

RRECOMMENDS:${PN} = "binutils"
RRECOMMENDS:${PN}:append:class-target = "${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', ' libcxx-dev', '', d)}"

# patch out build host paths for reproducibility
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
    rm -rf ${D}${libdir}/python*/site-packages/six.py
    for t in clang-pseudo clang-pseudo-gen clang-rename; do
        if [ -e ${B}${BINPATHPREFIX}/bin/$t ]; then
            install -Dm 0755 ${B}${BINPATHPREFIX}/bin/$t ${D}${bindir}/$t
        fi
    done

    oe_multilib_header llvm/Config/llvm-config.h
    oe_multilib_header clang/Config/config.h
}

do_install:append:class-target () {
    # Allow bin path to change based on YOCTO_ALTERNATE_EXE_PATH
    sed -i 's;${_IMPORT_PREFIX}/bin;${_IMPORT_PREFIX_BIN};g' ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake

    # Insert function to populate Import Variables
    sed -i "4i\
if(DEFINED ENV{YOCTO_ALTERNATE_EXE_PATH})\n\
  execute_process(COMMAND \"llvm-config\" \"--bindir\" OUTPUT_VARIABLE _IMPORT_PREFIX_BIN OUTPUT_STRIP_TRAILING_WHITESPACE)\n\
else()\n\
  set(_IMPORT_PREFIX_BIN \"\${_IMPORT_PREFIX}/bin\")\n\
endif()\n" ${D}${libdir}/cmake/llvm/LLVMExports-release.cmake

    if [ -n "${LLVM_LIBDIR_SUFFIX}" ]; then
        mkdir -p ${D}${nonarch_libdir}
        mv ${D}${libdir}/clang ${D}${nonarch_libdir}/clang
        ln -rs ${D}${nonarch_libdir}/clang ${D}${libdir}/clang
        rmdir --ignore-fail-on-non-empty ${D}${libdir}
    fi
    for t in clang clang++ llvm-nm llvm-ar llvm-as llvm-ranlib llvm-strip llvm-objcopy llvm-objdump llvm-readelf \
        llvm-addr2line llvm-dwp llvm-size llvm-strings llvm-cov; do
        ln -sf $t ${D}${bindir}/${TARGET_PREFIX}$t
    done

    # reproducibility
    sed -i -e 's,${B},,g' ${D}${libdir}/cmake/llvm/LLVMConfig.cmake
}

do_install:append:class-native () {
    if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
        install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clangd-indexer ${D}${bindir}/clangd-indexer
    fi
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tidy-confusable-chars-gen ${D}${bindir}/clang-tidy-confusable-chars-gen

    for f in `find ${D}${bindir} -executable -type f -not -type l`; do
        test -n "`file -b $f|grep -i ELF`" && ${STRIP} $f
        echo "stripped $f"
    done
    ln -sf llvm-config ${D}${bindir}/llvm-config${PV}

    # These are provided by llvm-tblgen-native
    rm ${D}${bindir}/*-tblgen
}

do_install:append:class-nativesdk () {
    if [ -e ${D}${libdir}/cmake/llvm/LLVMConfig.cmake ] ; then
        sed -i -e "s|${B}/./bin/||g" ${D}${libdir}/cmake/llvm/LLVMConfig.cmake
    fi
    if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
        install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clangd-indexer ${D}${bindir}/clangd-indexer
    fi
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tblgen ${D}${bindir}/clang-tblgen
    install -Dm 0755 ${B}${BINPATHPREFIX}/bin/clang-tidy-confusable-chars-gen ${D}${bindir}/clang-tidy-confusable-chars-gen
    for f in `find ${D}${bindir} -executable -type f -not -type l`; do
        test -n "`file -b $f|grep -i ELF`" && ${STRIP} $f
    done
    ln -sf clang-tblgen ${D}${bindir}/clang-tblgen${PV}
    ln -sf llvm-tblgen ${D}${bindir}/llvm-tblgen${PV}
    ln -sf llvm-config ${D}${bindir}/llvm-config${PV}
    rm -rf ${D}${datadir}/llvm/cmake
    rm -rf ${D}${datadir}/llvm

    #reproducibility
    if [ -e ${D}${libdir}/cmake/llvm/LLVMConfig.cmake ] ; then
        sed -i -e 's,${B},,g' ${D}${libdir}/cmake/llvm/LLVMConfig.cmake
    fi
}

PROVIDES:append:class-native = " llvm-native"
PROVIDES:append:class-target = " llvm"
PROVIDES:append:class-nativesdk = " nativesdk-llvm"

PACKAGES =+ "${PN}-libllvm ${PN}-libclang-python ${PN}-libclang-cpp ${PN}-tidy ${PN}-format ${PN}-tools \
             libclang llvm-linker-tools"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}-tools += "\
  perl-module-digest-md5 \
  perl-module-file-basename \
  perl-module-file-copy \
  perl-module-file-find \
  perl-module-file-path \
  perl-module-findbin \
  perl-module-hash-util \
  perl-module-sys-hostname \
  perl-module-term-ansicolor \
"

RRECOMMENDS:${PN}-tidy += "${PN}-tools"

FILES:llvm-linker-tools = "${libdir}/LLVMgold* ${libdir}/libLTO.so.* ${libdir}/LLVMPolly*"

FILES:${PN}-libclang-cpp = "${libdir}/libclang-cpp.so.*"

FILES:${PN}-libclang-python = "${PYTHON_SITEPACKAGES_DIR}/clang/*"

FILES:${PN}-tidy = "${bindir}/*clang-tidy*"
FILES:${PN}-format = "${bindir}/*clang-format*"

FILES:${PN}-tools = "${bindir}/analyze-build \
  ${bindir}/c-index-test \
  ${bindir}/clang-apply-replacements \
  ${bindir}/clang-change-namespace \
  ${bindir}/clang-check \
  ${bindir}/clang-doc \
  ${bindir}/clang-extdef-mapping \
  ${bindir}/clang-include-fixer \
  ${bindir}/clang-linker-wrapper \
  ${bindir}/clang-move \
  ${bindir}/clang-nvlink-wrapper \
  ${bindir}/clang-offload-bundler \
  ${bindir}/clang-offload-packager \
  ${bindir}/clang-pseudo* \
  ${bindir}/clang-query \
  ${bindir}/clang-refactor \
  ${bindir}/clang-rename* \
  ${bindir}/clang-reorder-fields \
  ${bindir}/clang-repl \
  ${bindir}/clang-scan-deps \
  ${bindir}/diagtool \
  ${bindir}/find-all-symbols \
  ${bindir}/hmaptool \
  ${bindir}/hwasan_symbolize \
  ${bindir}/intercept-build \
  ${bindir}/modularize \
  ${bindir}/pp-trace \
  ${bindir}/sancov \
  ${bindir}/scan-build \
  ${bindir}/scan-build-py \
  ${bindir}/scan-view \
  ${bindir}/split-file \
  ${libdir}/libscanbuild/* \
  ${libdir}/libear/* \
  ${libexecdir}/analyze-c++ \
  ${libexecdir}/analyze-cc \
  ${libexecdir}/c++-analyzer \
  ${libexecdir}/ccc-analyzer \
  ${libexecdir}/intercept-c++ \
  ${libexecdir}/intercept-cc \
  ${datadir}/scan-build/* \
  ${datadir}/scan-view/* \
  ${datadir}/opt-viewer/* \
  ${datadir}/clang/* \
"

FILES:${PN} += "\
  ${bindir}/clang-cl \
  ${libdir}/BugpointPasses.so \
  ${libdir}/LLVMHello.so \
  ${libdir}/*Plugin.so \
  ${libdir}/${BPN} \
  ${nonarch_libdir}/${BPN}/*/include/ \
"

FILES:${PN}-libllvm =+ "\
  ${libdir}/libLLVM.so.${MAJOR_VER}.${MINOR_VER} \
  ${libdir}/libLLVM-${MAJOR_VER}.so \
  ${libdir}/libRemarks.so.* \
"

FILES:libclang = "\
  ${libdir}/libclang.so.* \
"

FILES:${PN}-dev += "\
  ${datadir}/llvm/cmake \
  ${libdir}/cmake \
  ${nonarch_libdir}/libear \
  ${nonarch_libdir}/${BPN}/*.la \
"
FILES:${PN}-doc += "${datadir}/clang-doc"

FILES:${PN}-staticdev += "${nonarch_libdir}/${BPN}/*.a"

FILES:${PN}-staticdev:remove = "${libdir}/${BPN}/*.a"
FILES:${PN}-dev:remove = "${libdir}/${BPN}/*.la"
FILES:${PN}:remove = "${libdir}/${BPN}/*"

INSANE_SKIP:${PN} += "already-stripped"
#INSANE_SKIP:${PN}-dev += "dev-elf"
INSANE_SKIP:${PN}-libllvm = "dev-so"

#Avoid SSTATE_SCAN_COMMAND running sed over llvm-config.
SSTATE_SCAN_FILES:remove = "*-config"

COMPILER_RT:class-nativesdk:toolchain-clang:runtime-llvm = "-rtlib=libgcc --unwindlib=libgcc"
LIBCPLUSPLUS:class-nativesdk:toolchain-clang:runtime-llvm = "-stdlib=libstdc++"

SYSROOT_DIRS:append:class-target = " ${nonarch_libdir}"

SYSROOT_PREPROCESS_FUNCS:append:class-target = " clang_sysroot_preprocess"
SYSROOT_PREPROCESS_FUNCS:append:class-nativesdk = " clang_sysroot_preprocess"

clang_sysroot_preprocess() {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	install -m 0755 ${S}/llvm/tools/llvm-config/llvm-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	ln -sf llvm-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/llvm-config${PV}
	# LLDTargets.cmake references the lld executable(!) that some modules/plugins link to
	install -d ${SYSROOT_DESTDIR}${bindir}

	binaries="lld diagtool clang-${MAJOR_VER} clang-format clang-offload-packager
	                clang-offload-bundler clang-scan-deps clang-repl
	                clang-refactor clang-check clang-extdef-mapping clang-apply-replacements
	                clang-reorder-fields clang-tidy clang-change-namespace clang-doc clang-include-fixer
	                find-all-symbols clang-move clang-query pp-trace modularize"

	if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
	        binaries="${binaries} clangd"
	fi

	for f in ${binaries}
	do
		install -m 755 ${D}${bindir}/$f ${SYSROOT_DESTDIR}${bindir}/
	done
}
