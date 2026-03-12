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

CVE_PRODUCT = "llvm:clang"

LDFLAGS:append:class-target:riscv32 = " -Wl,--no-as-needed -latomic -Wl,--as-needed"
LDFLAGS:append:class-target:mips = " -Wl,--no-as-needed -latomic -Wl,--as-needed"

inherit cmake pkgconfig multilib_header python3-dir

PACKAGECONFIG ??= "build-id clangd libclang-python \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'lto thin-lto', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', 'lld', '', d)} \
                   ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', 'compiler-rt libcplusplus libomp unwindlib', '', d)} \
                   "
PACKAGECONFIG:remove:class-native = "lto thin-lto"

PACKAGECONFIG[build-id] = "-DENABLE_LINKER_BUILD_ID=ON,-DENABLE_LINKER_BUILD_ID=OFF,,"
PACKAGECONFIG[clangd] = "-DCLANG_ENABLE_CLANGD=ON,-DCLANG_ENABLE_CLANGD=OFF,,"
# Activate to build the dexp tool in clangd
# Disabled by default for -native since it is known to trigger compiler failure on Debian 11
# See: https://bugzilla.yoctoproject.org/show_bug.cgi?id=15803
PACKAGECONFIG[clangd-dexp] = "-DCLANGD_BUILD_DEXP=ON,-DCLANGD_BUILD_DEXP=OFF,,"
PACKAGECONFIG[compiler-rt] = "-DCLANG_DEFAULT_RTLIB=compiler-rt,,"
PACKAGECONFIG[libcplusplus] = "-DCLANG_DEFAULT_CXX_STDLIB=libc++,,"
PACKAGECONFIG[libomp] = "-DCLANG_DEFAULT_OPENMP_RUNTIME=libomp,,"
PACKAGECONFIG[lld] = "-DCLANG_DEFAULT_LINKER=lld,,,"
PACKAGECONFIG[lto] = "-DLLVM_ENABLE_LTO=Full -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[thin-lto] = "-DLLVM_ENABLE_LTO=Thin -DLLVM_BINUTILS_INCDIR=${STAGING_INCDIR},,binutils,"
PACKAGECONFIG[unwindlib] = "-DCLANG_DEFAULT_UNWINDLIB=libunwind,-DCLANG_DEFAULT_UNWINDLIB=libgcc,,"
PACKAGECONFIG[libclang-python] = "-DCLANG_PYTHON_BINDINGS_VERSIONS=${PYTHON_BASEVERSION},,"

OECMAKE_SOURCEPATH = "${S}/clang"


# linux hosts (.so) on Windows .pyd
SOLIBSDEV:mingw32 = ".pyd"

#CMAKE_VERBOSE = "VERBOSE=1"

EXTRA_OECMAKE += "-DLLVM_ENABLE_ASSERTIONS=OFF \
                  -DLLVM_ENABLE_PIC=ON \
                  -DCLANG_DEFAULT_PIE_ON_LINUX=ON \
                  -DFFI_INCLUDE_DIR=$(pkg-config --variable=includedir libffi) \
                  -DLLVM_BUILD_EXTERNAL_COMPILER_RT=ON \
                  -DCMAKE_SYSTEM_NAME=Linux \
                  -DCMAKE_BUILD_TYPE=MinSizeRel \
                  -DLLVM_VERSION_SUFFIX='${VER_SUFFIX}' \
                  -DLLVM_CMAKE_DIR=${STAGING_LIBDIR}/cmake/llvm \
                  -DLLVM_NATIVE_TOOL_DIR=${STAGING_BINDIR_NATIVE} \
                  -DLLVM_TABLEGEN_EXE=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
                  -DCLANG_TABLEGEN_EXE=${STAGING_BINDIR_NATIVE}/clang-tblgen \
                  -DLLVM_INCLUDE_TESTS=OFF \
                  -DCROSS_TOOLCHAIN_FLAGS_NATIVE='-DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/toolchain-native.cmake' \
                  -DLLVM_LIBDIR_SUFFIX=${LLVM_LIBDIR_SUFFIX} \
                  -DCMAKE_AR=${STAGING_BINDIR_NATIVE}/llvm-ar \
                  -DCMAKE_NM=${STAGING_BINDIR_NATIVE}/llvm-nm \
                  -DCMAKE_RANLIB=${STAGING_BINDIR_NATIVE}/llvm-ranlib \
                  -DCMAKE_STRIP=${STAGING_BINDIR_NATIVE}/llvm-strip \
"

DEPENDS = "llvm-tblgen-native llvm-native llvm binutils zlib zstd libffi libxml2 libxml2-native"
DEPENDS:append:class-target = " ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', 'compiler-rt libcxx', '', d)}"

RDEPENDS:${PN}:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' lld', '', d)}"
RRECOMMENDS:${PN}:append:class-target = "binutils ${@bb.utils.contains('TC_CXX_RUNTIME', 'llvm', ' libcxx-dev', '', d)}"

do_configure:prepend() {
  # Link clang-tools-extra into the clang tree as clang will look for it here
  # if it's doing a standalone build.
  ln -srf ${S}/clang-tools-extra ${S}/clang/tools/extra
}

do_install:append() {
    oe_multilib_header clang/Config/config.h
}

do_install:append:class-target () {
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
}

do_install:append:class-nativesdk () {
    #reproducibility
    if [ -e ${D}${libdir}/cmake/llvm/LLVMConfig.cmake ] ; then
        sed -i -e 's,${B},,g' ${D}${libdir}/cmake/llvm/LLVMConfig.cmake
    fi
}

PACKAGES =+ "${PN}-libclang-python ${PN}-libclang-cpp ${PN}-tidy ${PN}-format ${PN}-tools libclang"

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

#Avoid SSTATE_SCAN_COMMAND running sed over llvm-config.
SSTATE_SCAN_FILES:remove = "*-config"

COMPILER_RT:class-nativesdk:toolchain-clang = "-rtlib=libgcc --unwindlib=libgcc"
LIBCPLUSPLUS:class-nativesdk:toolchain-clang = "-stdlib=libstdc++"

SYSROOT_DIRS:append:class-target = " ${nonarch_libdir}"

SYSROOT_PREPROCESS_FUNCS:append:class-target = " clang_sysroot_preprocess"
SYSROOT_PREPROCESS_FUNCS:append:class-nativesdk = " clang_sysroot_preprocess"

clang_sysroot_preprocess() {
	install -d ${SYSROOT_DESTDIR}${bindir}/

	# clang and clang-tools
	binaries="diagtool clang-${MAJOR_VER} clang-format clang-offload-packager
	          clang-offload-bundler clang-scan-deps clang-repl
	          clang-refactor clang-check clang-extdef-mapping"

	# clang-extra-tools
	binaries="${binaries} clang-apply-replacements clang-reorder-fields
	          clang-tidy clang-change-namespace clang-doc clang-include-fixer
	          find-all-symbols clang-move clang-query pp-trace modularize"

	if ${@bb.utils.contains('PACKAGECONFIG', 'clangd', 'true', 'false', d)}; then
	        binaries="${binaries} clangd"
	fi

	for f in ${binaries}
	do
		install -m 755 ${D}${bindir}/$f ${SYSROOT_DESTDIR}${bindir}/
	done
}
