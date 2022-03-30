DESCRIPTION = "nodeJS Evented I/O for V8 JavaScript"
HOMEPAGE = "http://nodejs.org"
LICENSE = "MIT & ISC & BSD-2-Clause & BSD-3-Clause & Artistic-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ba5b21ac7a505195ca69344d3d7a94a"

DEPENDS = "openssl"
DEPENDS:append:class-target = " qemu-native"
DEPENDS:append:class-native = " c-ares-native"

inherit pkgconfig python3native qemu

COMPATIBLE_MACHINE:armv4 = "(!.*armv4).*"
COMPATIBLE_MACHINE:armv5 = "(!.*armv5).*"
COMPATIBLE_MACHINE:mips64 = "(!.*mips64).*"

COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:powerpc = "null"

SRC_URI = "http://nodejs.org/dist/v${PV}/node-v${PV}.tar.xz \
           file://0001-Disable-running-gyp-files-for-bundled-deps.patch \
           file://0002-Install-both-binaries-and-use-libdir.patch \
           file://0004-v8-don-t-override-ARM-CFLAGS.patch \
           file://0005-add-openssl-legacy-provider-option.patch \
           file://big-endian.patch \
           file://mips-less-memory.patch \
           file://system-c-ares.patch \
           file://0001-liftoff-Correct-function-signatures.patch \
           "
SRC_URI:append:class-target = " \
           file://0002-Using-native-binaries.patch \
           "
SRC_URI:append:toolchain-clang:x86 = " \
           file://libatomic.patch \
           "
SRC_URI:append:toolchain-clang:powerpc64le = " \
           file://0001-ppc64-Do-not-use-mminimal-toc-with-clang.patch \
           "
SRC_URI[sha256sum] = "05eb64193e391fa8a2c159c0f60c171824715165f80c67fcab9dbc944e30c623"

S = "${WORKDIR}/node-v${PV}"

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

PACKAGECONFIG[ares] = "--shared-cares,,c-ares"
PACKAGECONFIG[brotli] = "--shared-brotli,,brotli"
PACKAGECONFIG[icu] = "--with-intl=system-icu,--without-intl,icu"
PACKAGECONFIG[libuv] = "--shared-libuv,,libuv"
PACKAGECONFIG[nghttp2] = "--shared-nghttp2,,nghttp2"
PACKAGECONFIG[shared] = "--shared"
PACKAGECONFIG[zlib] = "--shared-zlib,,zlib"

# We don't want to cross-compile during target compile,
# and we need to use the right flags during host compile,
# too.
EXTRA_OEMAKE = "\
    CC.host='${CC}' \
    CFLAGS.host='${CPPFLAGS} ${CFLAGS}' \
    CXX.host='${CXX}' \
    CXXFLAGS.host='${CPPFLAGS} ${CXXFLAGS}' \
    LDFLAGS.host='${LDFLAGS}' \
    AR.host='${AR}' \
    \
    builddir_name=./ \
"

python do_unpack() {
    import shutil

    bb.build.exec_func('base_do_unpack', d)
    shutil.rmtree(d.getVar('S') + '/deps/openssl', True)
    if 'ares' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/cares', True)
    if 'brotli' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/brotli', True)
    if 'libuv' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/uv', True)
    if 'nghttp2' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/nghttp2', True)
    if 'zlib' in d.getVar('PACKAGECONFIG'):
        shutil.rmtree(d.getVar('S') + '/deps/zlib', True)
}

# V8's JIT infrastructure requires binaries such as mksnapshot and
# mkpeephole to be run in the host during the build. However, these
# binaries must have the same bit-width as the target (e.g. a x86_64
# host targeting ARMv6 needs to produce a 32-bit binary). Instead of
# depending on a third Yocto toolchain, we just build those binaries
# for the target and run them on the host with QEMU.
python do_create_v8_qemu_wrapper () {
    """Creates a small wrapper that invokes QEMU to run some target V8 binaries
    on the host."""
    qemu_libdirs = [d.expand('${STAGING_DIR_HOST}${libdir}'),
                    d.expand('${STAGING_DIR_HOST}${base_libdir}')]
    qemu_cmd = qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST', True),
                                    qemu_libdirs)
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

# Node is way too cool to use proper autotools, so we install two wrappers to forcefully inject proper arch cflags to workaround gypi
do_configure () {
    export LD="${CXX}"
    GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
    # $TARGET_ARCH settings don't match --dest-cpu settings
    python3 configure.py --prefix=${prefix} --cross-compiling \
               --shared-openssl \
               --without-dtrace \
               --without-etw \
               --dest-cpu="${@map_nodejs_arch(d.getVar('TARGET_ARCH'), d)}" \
               --dest-os=linux \
               --libdir=${D}${libdir} \
               ${ARCHFLAGS} \
               ${PACKAGECONFIG_CONFARGS}
}

do_compile () {
    export LD="${CXX}"
    install -D ${B}/v8-qemu-wrapper.sh ${B}/out/Release/v8-qemu-wrapper.sh
    oe_runmake BUILDTYPE=Release
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

BINARIES = " \
    bytecode_builtins_list_generator \
    ${@bb.utils.contains('PACKAGECONFIG', 'icu', 'gen-regexp-special-case', '', d)} \
    mkcodecache \
    node_mksnapshot \
    torque \
"

do_install:append:class-native() {
    # Install the native binaries to provide it within sysroot for the target compilation
    install -d ${D}${bindir}
    (cd ${S}/out/Release && install ${BINARIES} ${D}${bindir})
}

PACKAGES =+ "${PN}-npm"
FILES:${PN}-npm = "${nonarch_libdir}/node_modules ${bindir}/npm ${bindir}/npx"
RDEPENDS:${PN}-npm = "bash python3-core python3-shell python3-datetime \
    python3-misc python3-multiprocessing"

PACKAGES =+ "${PN}-systemtap"
FILES:${PN}-systemtap = "${datadir}/systemtap"

BBCLASSEXTEND = "native"
