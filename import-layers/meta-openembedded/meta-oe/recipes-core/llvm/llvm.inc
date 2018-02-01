# LLVM does not provide ABI stability between different versions. For this
# reason OE makes it possible to build and install different llvm versions
# at the same time.
#
# This is true for the normal recipes as well as the native ones.
#
# All regular installation directories are prefixed with 'llvm${LLVM_RELEASE}'
# e.g. "${STAGING_BINDIR}/llvm2.5" or "${STAGING_INCDIR}/llvm2.5"
#
# For your program or library that makes use of llvm you do should not need to
# modify anything as long as it uses the results of various llvm-config
# invocations. If you need customizations something is wrong and it needs to be
# fixed (report bug).
#
# However the *recipe* for your program/library *must* declare
# export WANT_LLVM_RELEASE = "<valid version number>"
# The version number is picked up by a generic wrapper script which just calls
# the variant of the specified version.

DESCRIPTION = "The Low Level Virtual Machine"
HOMEPAGE = "http://llvm.org"

# 3-clause BSD-like
# University of Illinois/NCSA Open Source License
LICENSE = "NCSA"
LIC_FILES_CHKSUM = "file://LICENSE.TXT;md5=d0a3ef0d3e0e8f5cf59e5ffc273ab1f8"

DEPENDS = "libffi libxml2-native llvm-common"

inherit perlnative pythonnative autotools

LLVM_RELEASE = "${PV}"
LLVM_DIR = "llvm${LLVM_RELEASE}"

SRC_URI = "http://llvm.org/releases/${PV}/llvm-${PV}.src.tar.gz"
S = "${WORKDIR}/llvm-${PV}.src"

LLVM_BUILD_DIR = "${WORKDIR}/llvm-${PV}.build"
LLVM_INSTALL_DIR = "${WORKDIR}/llvm-install"

EXTRA_OECONF += "--disable-assertions \
                 --enable-debug-runtime \
                 --disable-expensive-checks \
                 --enable-bindings=none \
                 --enable-keep-symbols \
                 --enable-libffi \
                 --enable-optimized \
                 --enable-shared \
                 --enable-targets=host-only"
EXTRA_OEMAKE += "REQUIRES_RTTI=1 VERBOSE=1"

do_configure_prepend() {
    # Remove RPATHs
    sed -i 's:$(RPATH) -Wl,$(\(ToolDir\|LibDir\|ExmplDir\))::g' ${S}/Makefile.rules

    # Drop "svn" suffix from version string
    sed -i 's/${PV}svn/${PV}/g' ${S}/configure

    # Fix paths in llvm-config
    sed -i "s|sys::path::parent_path(CurrentPath))\.str()|sys::path::parent_path(sys::path::parent_path(CurrentPath))).str()|g" ${S}/tools/llvm-config/llvm-config.cpp
    sed -ri "s#/(bin|include|lib)(/?\")#/\1/${LLVM_DIR}\2#g" ${S}/tools/llvm-config/llvm-config.cpp

    # Fix the hardcoded libdir in llvm-config
    sed -i 's:/lib\>:/${baselib}:g' ${S}/tools/llvm-config/llvm-config.cpp

    # Fails to build unless using separate directory from source
    mkdir -p ${LLVM_BUILD_DIR}
    cd ${LLVM_BUILD_DIR}
}

do_compile() {
    cd ${LLVM_BUILD_DIR}

    # Fix libdir for multilib
    sed -i 's:(PROJ_prefix)/lib:(PROJ_prefix)/${baselib}:g' Makefile.config

    oe_runmake \
        AR="${BUILD_AR}" \
        CC="${BUILD_CC}" \
        CFLAGS="${BUILD_CFLAGS}" \
        CXX="${BUILD_CXX}" \
        CXXFLAGS="${BUILD_CXXFLAGS}" \
        CPP="${BUILD_CPP}" \
        CPPFLAGS="${BUILD_CPPFLAGS}" \
        NM="${BUILD_NM}" \
        RANLIB="${BUILD_RANLIB}" \
        PATH="${STAGING_BINDIR_NATIVE}:$PATH" \
        cross-compile-build-tools
    oe_runmake
}

do_install() {
    cd ${LLVM_BUILD_DIR}
    oe_runmake DESTDIR=${LLVM_INSTALL_DIR} install

    mv ${LLVM_INSTALL_DIR}${bindir}/${HOST_SYS}-llvm-config-host ${LLVM_INSTALL_DIR}/llvm-config-host

    install -d ${D}${bindir}/${LLVM_DIR}
    mv ${LLVM_INSTALL_DIR}${bindir}/* ${D}${bindir}/${LLVM_DIR}/

    install -d ${D}${includedir}/${LLVM_DIR}
    mv ${LLVM_INSTALL_DIR}${includedir}/* ${D}${includedir}/${LLVM_DIR}/

    install -d ${D}${libdir}/${LLVM_DIR}
    mv ${LLVM_INSTALL_DIR}${libdir}/* ${D}${libdir}/${LLVM_DIR}/
    ln -s ${LLVM_DIR}/libLLVM-${PV}.so ${D}${libdir}/libLLVM-${PV}.so

    install -d ${D}${docdir}/${LLVM_DIR}
    mv ${LLVM_INSTALL_DIR}${prefix}/docs/llvm/* ${D}${docdir}/${LLVM_DIR}
}

SYSROOT_PREPROCESS_FUNCS += "llvm_sysroot_preprocess"

llvm_sysroot_preprocess() {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}
    mv ${LLVM_INSTALL_DIR}/llvm-config-host ${SYSROOT_DESTDIR}${bindir_crossscripts}/llvm-config${PV}
}

PACKAGES += "${PN}-bugpointpasses ${PN}-llvmhello"
ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-staticdev = "1"
FILES_${PN} = ""
FILES_${PN}-staticdev = ""
FILES_${PN}-dbg = " \
    ${bindir}/${LLVM_DIR}/.debug \
    ${libdir}/${LLVM_DIR}/.debug/BugpointPasses.so \
    ${libdir}/${LLVM_DIR}/.debug/LLVMHello.so \
    /usr/src/debug \
"

FILES_${PN}-dev = " \
    ${bindir}/${LLVM_DIR} \
    ${includedir}/${LLVM_DIR} \
"
RRECOMMENDS_${PN}-dev += "${PN}-bugpointpasses ${PN}-llvmhello"

FILES_${PN}-bugpointpasses = "\
    ${libdir}/${LLVM_DIR}/BugpointPasses.so \
"
FILES_${PN}-llvmhello = "\
    ${libdir}/${LLVM_DIR}/LLVMHello.so \
"

PACKAGES_DYNAMIC = "^libllvm${LLVM_RELEASE}-.*$"
NOAUTOPACKAGEDEBUG = "1"

INSANE_SKIP_${MLPREFIX}libllvm${LLVM_RELEASE}-llvm-${LLVM_RELEASE} += "dev-so"

python llvm_populate_packages() {
    libdir = bb.data.expand('${libdir}', d)
    libllvm_libdir = bb.data.expand('${libdir}/${LLVM_DIR}', d)
    split_dbg_packages = do_split_packages(d, libllvm_libdir+'/.debug', '^lib(.*)\.so$', 'libllvm${LLVM_RELEASE}-%s-dbg', 'Split debug package for %s', allow_dirs=True)
    split_packages = do_split_packages(d, libdir, '^lib(.*)\.so$', 'libllvm${LLVM_RELEASE}-%s', 'Split package for %s', allow_dirs=True, allow_links=True, recursive=True)
    split_staticdev_packages = do_split_packages(d, libllvm_libdir, '^lib(.*)\.a$', 'libllvm${LLVM_RELEASE}-%s-staticdev', 'Split staticdev package for %s', allow_dirs=True)
    if split_packages:
        pn = d.getVar('PN')
        d.appendVar('RDEPENDS_' + pn, ' '+' '.join(split_packages))
        d.appendVar('RDEPENDS_' + pn + '-dbg', ' '+' '.join(split_dbg_packages))
        d.appendVar('RDEPENDS_' + pn + '-staticdev', ' '+' '.join(split_staticdev_packages))
}

PACKAGESPLITFUNCS_prepend = "llvm_populate_packages "
