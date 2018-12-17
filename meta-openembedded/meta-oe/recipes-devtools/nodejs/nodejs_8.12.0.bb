DESCRIPTION = "nodeJS Evented I/O for V8 JavaScript"
HOMEPAGE = "http://nodejs.org"
LICENSE = "MIT & BSD & Artistic-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fde91d5c5bbd1e0389623e1ac018d9e8"

DEPENDS = "openssl zlib icu"

inherit pkgconfig

COMPATIBLE_MACHINE_armv4 = "(!.*armv4).*"
COMPATIBLE_MACHINE_armv5 = "(!.*armv5).*"
COMPATIBLE_MACHINE_mips64 = "(!.*mips64).*"

SRC_URI = "http://nodejs.org/dist/v${PV}/node-v${PV}.tar.xz \
           file://0001-Disable-running-gyp-files-for-bundled-deps.patch \
"
SRC_URI[md5sum] = "8b3abd033dae96b6fadcb6a872a44d3c"
SRC_URI[sha256sum] = "5a9dff58016c18fb4bf902d963b124ff058a550ebcd9840c677757387bce419a"

S = "${WORKDIR}/node-v${PV}"

# v8 errors out if you have set CCACHE
CCACHE = ""

def map_nodejs_arch(a, d):
    import re

    if   re.match('i.86$', a): return 'ia32'
    elif re.match('x86_64$', a): return 'x64'
    elif re.match('aarch64$', a): return 'arm64'
    elif re.match('(powerpc64|ppc64le)$', a): return 'ppc64'
    elif re.match('powerpc$', a): return 'ppc'
    return a

ARCHFLAGS_arm = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', '--with-arm-float-abi=hard', '--with-arm-float-abi=softfp', d)} \
                 ${@bb.utils.contains('TUNE_FEATURES', 'neon', '--with-arm-fpu=neon', \
                    bb.utils.contains('TUNE_FEATURES', 'vfpv3d16', '--with-arm-fpu=vfpv3-d16', \
                    bb.utils.contains('TUNE_FEATURES', 'vfpv3', '--with-arm-fpu=vfpv3', \
                    '--with-arm-fpu=vfp', d), d), d)}"
GYP_DEFINES_append_mipsel = " mips_arch_variant='r1' "
ARCHFLAGS ?= ""

# Node is way too cool to use proper autotools, so we install two wrappers to forcefully inject proper arch cflags to workaround gypi
do_configure () {
    rm -rf ${S}/deps/openssl
    export LD="${CXX}"
    GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
    # $TARGET_ARCH settings don't match --dest-cpu settings
   ./configure --prefix=${prefix} --with-intl=system-icu --without-snapshot --shared-openssl --shared-zlib \
               --dest-cpu="${@map_nodejs_arch(d.getVar('TARGET_ARCH'), d)}" \
               --dest-os=linux \
               ${ARCHFLAGS}
}

do_compile () {
    export LD="${CXX}"
    oe_runmake BUILDTYPE=Release
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

do_install_append_class-native() {
    # use node from PATH instead of absolute path to sysroot
    # node-v0.10.25/tools/install.py is using:
    # shebang = os.path.join(node_prefix, 'bin/node')
    # update_shebang(link_path, shebang)
    # and node_prefix can be very long path to bindir in native sysroot and
    # when it exceeds 128 character shebang limit it's stripped to incorrect path
    # and npm fails to execute like in this case with 133 characters show in log.do_install:
    # updating shebang of /home/jenkins/workspace/build-webos-nightly/device/qemux86/label/open-webos-builder/BUILD-qemux86/work/x86_64-linux/nodejs-native/0.10.15-r0/image/home/jenkins/workspace/build-webos-nightly/device/qemux86/label/open-webos-builder/BUILD-qemux86/sysroots/x86_64-linux/usr/bin/npm to /home/jenkins/workspace/build-webos-nightly/device/qemux86/label/open-webos-builder/BUILD-qemux86/sysroots/x86_64-linux/usr/bin/node
    # /usr/bin/npm is symlink to /usr/lib/node_modules/npm/bin/npm-cli.js
    # use sed on npm-cli.js because otherwise symlink is replaced with normal file and
    # npm-cli.js continues to use old shebang
    sed "1s^.*^#\!/usr/bin/env node^g" -i ${D}${exec_prefix}/lib/node_modules/npm/bin/npm-cli.js
}

do_install_append_class-target() {
    sed "1s^.*^#\!${bindir}/env node^g" -i ${D}${exec_prefix}/lib/node_modules/npm/bin/npm-cli.js
}

PACKAGES =+ "${PN}-npm"
FILES_${PN}-npm = "${exec_prefix}/lib/node_modules ${bindir}/npm ${bindir}/npx"
RDEPENDS_${PN}-npm = "bash python-shell python-datetime python-subprocess python-textutils \
    python-compiler python-misc python-multiprocessing"

PACKAGES =+ "${PN}-systemtap"
FILES_${PN}-systemtap = "${datadir}/systemtap"


BBCLASSEXTEND = "native"
