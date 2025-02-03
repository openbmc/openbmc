DESCRIPTION = "nodeJS Evented I/O for V8 JavaScript"
HOMEPAGE = "http://nodejs.org"
LICENSE = "MIT & ISC & BSD-2-Clause & BSD-3-Clause & Artistic-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0bd28a461eccad39f85a29e33e8f879f"

CVE_PRODUCT = "nodejs node.js"

DEPENDS = "openssl openssl-native file-replacement-native python3-packaging-native"
DEPENDS:append:class-target = " qemu-native"
DEPENDS:append:class-native = " c-ares-native"

inherit pkgconfig python3native qemu ptest siteinfo

COMPATIBLE_MACHINE:armv4 = "(!.*armv4).*"
COMPATIBLE_MACHINE:armv5 = "(!.*armv5).*"
COMPATIBLE_MACHINE:mips64 = "(!.*mips64).*"

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:powerpc = "null"

SRC_URI = "http://nodejs.org/dist/v${PV}/node-v${PV}.tar.xz \
           file://0001-Do-not-use-glob-in-deps.patch \
           file://0001-Disable-running-gyp-files-for-bundled-deps.patch \
           file://0004-v8-don-t-override-ARM-CFLAGS.patch \
           file://system-c-ares.patch \
           file://0001-liftoff-Correct-function-signatures.patch \
           file://libatomic.patch \
           file://0001-deps-disable-io_uring-support-in-libuv.patch \
           file://0001-positional-args.patch \
           file://0001-custom-env.patch \
           file://run-ptest \
           "
SRC_URI:append:class-target = " \
           file://0001-Using-native-binaries.patch \
           "
SRC_URI:append:toolchain-clang:powerpc64le = " \
           file://0001-ppc64-Do-not-use-mminimal-toc-with-clang.patch \
           "
SRC_URI[sha256sum] = "cfce282119390f7e0c2220410924428e90dadcb2df1744c0c4a0e7baae387cc2"

S = "${WORKDIR}/node-v${PV}"

CVE_PRODUCT += "node.js"

# v8 errors out if you have set CCACHE
CCACHE = ""

def map_nodejs_arch(a, d):
    import re

    if   re.match('i.86$', a): return 'ia32'
    elif re.match('x86_64$', a): return 'x64'
    elif re.match('aarch64$', a): return 'arm64'
    elif re.match('(powerpc64|powerpc64le|ppc64le)$', a): return 'ppc64'
    elif re.match('powerpc$', a): return 'ppc'
    return a

ARCHFLAGS:arm = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', '--with-arm-float-abi=hard', '--with-arm-float-abi=softfp', d)} \
                 ${@bb.utils.contains('TUNE_FEATURES', 'neon', '--with-arm-fpu=neon', \
                    bb.utils.contains('TUNE_FEATURES', 'vfpv3d16', '--with-arm-fpu=vfpv3-d16', \
                    bb.utils.contains('TUNE_FEATURES', 'vfpv3', '--with-arm-fpu=vfpv3', \
                    '--with-arm-fpu=vfp', d), d), d)}"
ARCHFLAGS:append:mips = " --v8-lite-mode"
ARCHFLAGS:append:mipsel = " --v8-lite-mode"
ARCHFLAGS ?= ""

PACKAGECONFIG ??= "ares brotli icu zlib"

PACKAGECONFIG[ares] = "--shared-cares,,c-ares c-ares-native"
PACKAGECONFIG[brotli] = "--shared-brotli,,brotli brotli-native"
PACKAGECONFIG[icu] = "--with-intl=system-icu,--without-intl,icu icu-native"
PACKAGECONFIG[libuv] = "--shared-libuv,,libuv"
PACKAGECONFIG[nghttp2] = "--shared-nghttp2,,nghttp2"
PACKAGECONFIG[shared] = "--shared"
PACKAGECONFIG[zlib] = "--shared-zlib,,zlib"

EXTRANATIVEPATH += "file-native"

python prune_sources() {
    import shutil

    shutil.rmtree(d.getVar('S') + '/deps/openssl')
    if 'ares' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/cares')
    if 'brotli' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/brotli')
    if 'libuv' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/uv')
    if 'nghttp2' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/nghttp2')
    if 'zlib' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/zlib')
}
do_unpack[postfuncs] += "prune_sources"

# V8's JIT infrastructure requires binaries such as mksnapshot and
# mkpeephole to be run in the host during the build. However, these
# binaries must have the same bit-width as the target (e.g. a x86_64
# host targeting ARMv6 needs to produce a 32-bit binary).
# 1. If host and target have the different bit width, run those
#    binaries for the target and run them on the host with QEMU.
# 2. If host and target have the same bit width, enable upstream
#    cross compile support and no QEMU
python do_create_v8_qemu_wrapper () {
    """Creates a small wrapper that invokes QEMU to run some target V8 binaries
    on the host."""
    qemu_libdirs = [d.expand('${STAGING_DIR_HOST}${libdir}'),
                    d.expand('${STAGING_DIR_HOST}${base_libdir}')]
    qemu_cmd = qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST'),
                                    qemu_libdirs)

    if d.getVar("HOST_AND_TARGET_SAME_WIDTH") == "1":
        qemu_cmd = ""

    wrapper_path = d.expand('${B}/v8-qemu-wrapper.sh')
    with open(wrapper_path, 'w') as wrapper_file:
        wrapper_file.write("""#!/bin/sh

# This file has been generated automatically.
# It invokes QEMU to run binaries built for the target in the host during the
# build process.

%s "$@"
""" % qemu_cmd)
    os.chmod(wrapper_path, 0o755)
}

do_create_v8_qemu_wrapper[dirs] = "${B}"
addtask create_v8_qemu_wrapper after do_configure before do_compile

LDFLAGS:append:x86 = " -latomic"

export CC_host
export CFLAGS_host
export CXX_host
export CXXFLAGS_host
export LDFLAGS_host
export AR_host
export HOST_AND_TARGET_SAME_WIDTH

CROSS_FLAGS = "--cross-compiling"
CROSS_FLAGS:class-native = "--no-cross-compiling"

# Node is way too cool to use proper autotools, so we install two wrappers to forcefully inject proper arch cflags to workaround gypi
do_configure () {
    GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
    # $TARGET_ARCH settings don't match --dest-cpu settings
    python3 configure.py --verbose --prefix=${prefix} \
               --shared-openssl \
               --dest-cpu="${@map_nodejs_arch(d.getVar('TARGET_ARCH'), d)}" \
               --dest-os=linux \
               --libdir=${baselib} \
               ${CROSS_FLAGS} \
               ${ARCHFLAGS} \
               ${PACKAGECONFIG_CONFARGS}
}

do_compile () {
    install -D ${RECIPE_SYSROOT_NATIVE}/etc/ssl/openssl.cnf ${B}/deps/openssl/nodejs-openssl.cnf
    install -D ${B}/v8-qemu-wrapper.sh ${B}/out/Release/v8-qemu-wrapper.sh
    oe_runmake BUILDTYPE=Release
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

do_install_ptest () {
    cp -r  ${B}/out/Release/cctest ${D}${PTEST_PATH}/
    cp -r ${B}/test ${D}${PTEST_PATH}
    chown -R root:root ${D}${PTEST_PATH}
}

PACKAGES =+ "${PN}-npm"
FILES:${PN}-npm = "${nonarch_libdir}/node_modules ${bindir}/npm ${bindir}/npx ${bindir}/corepack"
RDEPENDS:${PN}-npm = "bash python3-core python3-shell python3-datetime \
    python3-misc python3-multiprocessing"

PACKAGES =+ "${PN}-systemtap"
FILES:${PN}-systemtap = "${datadir}/systemtap"

do_configure[prefuncs] += "set_gyp_variables"
do_compile[prefuncs] += "set_gyp_variables"
do_install[prefuncs] += "set_gyp_variables"
python set_gyp_variables () {
    if d.getVar("HOST_AND_TARGET_SAME_WIDTH") == "0":
        # We don't want to cross-compile during target compile,
        # and we need to use the right flags during host compile,
        # too.
        d.setVar("CC_host", d.getVar("CC") + " -pie -fPIE")
        d.setVar("CFLAGS_host", d.getVar("CFLAGS"))
        d.setVar("CXX_host", d.getVar("CXX") + " -pie -fPIE")
        d.setVar("CXXFLAGS_host", d.getVar("CXXFLAGS"))
        d.setVar("LDFLAGS_host", d.getVar("LDFLAGS"))
        d.setVar("AR_host", d.getVar("AR"))
    elif d.getVar("HOST_AND_TARGET_SAME_WIDTH") == "1":
        # Enable upstream cross compile support
        d.setVar("CC_host", d.getVar("BUILD_CC"))
        d.setVar("CFLAGS_host", d.getVar("BUILD_CFLAGS"))
        d.setVar("CXX_host", d.getVar("BUILD_CXX"))
        d.setVar("CXXFLAGS_host", d.getVar("BUILD_CXXFLAGS"))
        d.setVar("LDFLAGS_host", d.getVar("BUILD_LDFLAGS"))
        d.setVar("AR_host", d.getVar("BUILD_AR"))
}

python __anonymous () {
    # 32 bit target and 64 bit host (x86-64 or aarch64) have different bit width
    if d.getVar("SITEINFO_BITS") == "32" and "64" in d.getVar("BUILD_ARCH"):
        d.setVar("HOST_AND_TARGET_SAME_WIDTH", "0")
    else:
        d.setVar("HOST_AND_TARGET_SAME_WIDTH", "1")
}

BBCLASSEXTEND = "native"
