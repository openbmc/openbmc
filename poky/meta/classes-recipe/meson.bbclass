#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python3native meson-routines qemu

DEPENDS:append = " meson-native ninja-native"

EXEWRAPPER_ENABLED:class-native = "False"
EXEWRAPPER_ENABLED:class-nativesdk = "False"
EXEWRAPPER_ENABLED ?= "${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d)}"
DEPENDS:append = "${@' qemu-native' if d.getVar('EXEWRAPPER_ENABLED') == 'True' else ''}"

# As Meson enforces out-of-tree builds we can just use cleandirs
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# Where the meson.build build configuration is
MESON_SOURCEPATH = "${S}"

# The target to build in do_compile. If unset the default targets are built.
MESON_TARGET ?= ""

def noprefix(var, d):
    return d.getVar(var).replace(d.getVar('prefix') + '/', '', 1)

MESON_BUILDTYPE ?= "${@oe.utils.vartrue('DEBUG_BUILD', 'debug', 'plain', d)}"
MESON_BUILDTYPE[vardeps] += "DEBUG_BUILD"
MESONOPTS = " --prefix ${prefix} \
              --buildtype ${MESON_BUILDTYPE} \
              --bindir ${@noprefix('bindir', d)} \
              --sbindir ${@noprefix('sbindir', d)} \
              --datadir ${@noprefix('datadir', d)} \
              --libdir ${@noprefix('libdir', d)} \
              --libexecdir ${@noprefix('libexecdir', d)} \
              --includedir ${@noprefix('includedir', d)} \
              --mandir ${@noprefix('mandir', d)} \
              --infodir ${@noprefix('infodir', d)} \
              --sysconfdir ${sysconfdir} \
              --localstatedir ${localstatedir} \
              --sharedstatedir ${sharedstatedir} \
              --wrap-mode nodownload \
              --native-file ${WORKDIR}/meson.native"

EXTRA_OEMESON:append = " ${PACKAGECONFIG_CONFARGS}"

MESON_CROSS_FILE = ""
MESON_CROSS_FILE:class-target = "--cross-file ${WORKDIR}/meson.cross"
MESON_CROSS_FILE:class-nativesdk = "--cross-file ${WORKDIR}/meson.cross"

# Needed to set up qemu wrapper below
export STAGING_DIR_HOST

def rust_tool(d, target_var):
    rustc = d.getVar('RUSTC')
    if not rustc:
        return ""
    cmd = [rustc, "--target", d.getVar(target_var)] + d.getVar("RUSTFLAGS").split()
    return "rust = %s" % repr(cmd)

addtask write_config before do_configure
do_write_config[vardeps] += "CC CXX AR NM STRIP READELF OBJCOPY CFLAGS CXXFLAGS LDFLAGS RUSTC RUSTFLAGS EXEWRAPPER_ENABLED"
do_write_config() {
    # This needs to be Py to split the args into single-element lists
    cat >${WORKDIR}/meson.cross <<EOF
[binaries]
c = ${@meson_array('CC', d)}
cpp = ${@meson_array('CXX', d)}
cython = 'cython3'
ar = ${@meson_array('AR', d)}
nm = ${@meson_array('NM', d)}
strip = ${@meson_array('STRIP', d)}
readelf = ${@meson_array('READELF', d)}
objcopy = ${@meson_array('OBJCOPY', d)}
pkg-config = 'pkg-config'
llvm-config = 'llvm-config'
cups-config = 'cups-config'
g-ir-scanner = '${STAGING_BINDIR}/g-ir-scanner-wrapper'
g-ir-compiler = '${STAGING_BINDIR}/g-ir-compiler-wrapper'
${@rust_tool(d, "RUST_HOST_SYS")}
${@"exe_wrapper = '${WORKDIR}/meson-qemuwrapper'" if d.getVar('EXEWRAPPER_ENABLED') == 'True' else ""}

[built-in options]
c_args = ${@meson_array('CFLAGS', d)}
c_link_args = ${@meson_array('LDFLAGS', d)}
cpp_args = ${@meson_array('CXXFLAGS', d)}
cpp_link_args = ${@meson_array('LDFLAGS', d)}

[properties]
needs_exe_wrapper = true
sys_root = '${STAGING_DIR_HOST}'

[host_machine]
system = '${@meson_operating_system('HOST_OS', d)}'
cpu_family = '${@meson_cpu_family('HOST_ARCH', d)}'
cpu = '${HOST_ARCH}'
endian = '${@meson_endian('HOST', d)}'

[target_machine]
system = '${@meson_operating_system('TARGET_OS', d)}'
cpu_family = '${@meson_cpu_family('TARGET_ARCH', d)}'
cpu = '${TARGET_ARCH}'
endian = '${@meson_endian('TARGET', d)}'
EOF

    cat >${WORKDIR}/meson.native <<EOF
[binaries]
c = ${@meson_array('BUILD_CC', d)}
cpp = ${@meson_array('BUILD_CXX', d)}
cython = 'cython3'
ar = ${@meson_array('BUILD_AR', d)}
nm = ${@meson_array('BUILD_NM', d)}
strip = ${@meson_array('BUILD_STRIP', d)}
readelf = ${@meson_array('BUILD_READELF', d)}
objcopy = ${@meson_array('BUILD_OBJCOPY', d)}
llvm-config = '${STAGING_BINDIR_NATIVE}/llvm-config'
pkg-config = 'pkg-config-native'
${@rust_tool(d, "RUST_BUILD_SYS")}

[built-in options]
c_args = ${@meson_array('BUILD_CFLAGS', d)}
c_link_args = ${@meson_array('BUILD_LDFLAGS', d)}
cpp_args = ${@meson_array('BUILD_CXXFLAGS', d)}
cpp_link_args = ${@meson_array('BUILD_LDFLAGS', d)}
EOF
}

do_write_config:append:class-target() {
    # Write out a qemu wrapper that will be used as exe_wrapper so that meson
    # can run target helper binaries through that.
    qemu_binary="${@qemu_wrapper_cmdline(d, '$STAGING_DIR_HOST', ['$STAGING_DIR_HOST/${libdir}','$STAGING_DIR_HOST/${base_libdir}'])}"
    cat > ${WORKDIR}/meson-qemuwrapper << EOF
#!/bin/sh
# Use a modules directory which doesn't exist so we don't load random things
# which may then get deleted (or their dependencies) and potentially segfault
export GIO_MODULE_DIR=${STAGING_LIBDIR}/gio/modules-dummy

# meson sets this wrongly (only to libs in build-dir), qemu_wrapper_cmdline() and GIR_EXTRA_LIBS_PATH take care of it properly
unset LD_LIBRARY_PATH

$qemu_binary "\$@"
EOF
    chmod +x ${WORKDIR}/meson-qemuwrapper
}

# Tell externalsrc that changes to this file require a reconfigure
CONFIGURE_FILES = "meson.build"

meson_do_configure() {
    # Meson requires this to be 'bfd, 'lld' or 'gold' from 0.53 onwards
    # https://github.com/mesonbuild/meson/commit/ef9aeb188ea2bc7353e59916c18901cde90fa2b3
    unset LD

    bbnote Executing meson ${EXTRA_OEMESON}...
    if ! meson setup ${MESONOPTS} "${MESON_SOURCEPATH}" "${B}" ${MESON_CROSS_FILE} ${EXTRA_OEMESON}; then
        bbfatal_log meson failed
    fi
}

python meson_do_qa_configure() {
    import re
    warn_re = re.compile(r"^WARNING: Cross property (.+) is using default value (.+)$", re.MULTILINE)
    with open(d.expand("${B}/meson-logs/meson-log.txt")) as logfile:
        log = logfile.read()
    for (prop, value) in warn_re.findall(log):
        bb.warn("Meson cross property %s used without explicit assignment, defaulting to %s" % (prop, value))
}
do_configure[postfuncs] += "meson_do_qa_configure"

do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
meson_do_compile() {
    meson compile -v ${PARALLEL_MAKE} ${MESON_TARGET}
}

meson_do_install() {
    meson install --destdir ${D} --no-rebuild
}

EXPORT_FUNCTIONS do_configure do_compile do_install
