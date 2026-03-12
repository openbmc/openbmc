#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python3native meson-routines qemu

DEPENDS:append = " meson-native ninja-native"

EXEWRAPPER_ENABLED:class-native = "False"
EXEWRAPPER_ENABLED ?= "${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d)}"
DEPENDS:append = "${@' qemu-native' if d.getVar('EXEWRAPPER_ENABLED') == 'True' else ''}"

# As Meson enforces out-of-tree builds we can just use cleandirs
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# Where the meson.build build configuration is
MESON_SOURCEPATH = "${S}"

# The target to build in do_compile. If unset the default targets are built.
MESON_TARGET ?= ""

# Since 0.60.0 you can specify custom tags to install
MESON_INSTALL_TAGS ?= ""

MESON_BUILDTYPE ?= "${@oe.utils.vartrue('DEBUG_BUILD', 'debug', 'plain', d)}"
MESON_BUILDTYPE[vardeps] += "DEBUG_BUILD"

MESONOPTS = " --buildtype ${MESON_BUILDTYPE} \
              --prefix ${prefix} \
              --bindir ${bindir} \
              --datadir ${datadir} \
              --includedir ${includedir} \
              --infodir ${infodir} \
              --libdir ${libdir} \
              --libexecdir ${libexecdir} \
              --localstatedir ${localstatedir} \
              --mandir ${mandir} \
              --sbindir ${sbindir} \
              --sharedstatedir ${sharedstatedir} \
              --sysconfdir ${sysconfdir} \
              --wrap-mode nodownload \
              --native-file ${WORKDIR}/meson.native"

EXTRA_OEMESON:append = " ${PACKAGECONFIG_CONFARGS}"

MESON_CROSS_FILE = ""
MESON_CROSS_FILE:class-target = "--cross-file ${WORKDIR}/meson.cross"
MESON_CROSS_FILE:class-nativesdk = "--cross-file ${WORKDIR}/meson.cross"

def rust_tool(d, target_var):
    rustc = d.getVar('RUSTC')
    if not rustc:
        return ""
    cmd = [rustc, "--target", d.getVar(target_var)] + d.getVar("RUSTFLAGS").split()
    return "rust = %s" % repr(cmd)

def bindgen_args(d):
    args = '${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} --target=${TARGET_SYS}'
    # For SDK packages TOOLCHAIN_OPTIONS don't contain full sysroot path
    if bb.data.inherits_class("nativesdk", d):
        args += ' --sysroot=${STAGING_DIR_HOST}${SDKPATHNATIVE}${prefix_nativesdk}'
    items = d.expand(args).split()
    return repr(items[0] if len(items) == 1 else items)

addtask write_config before do_configure
do_write_config[vardeps] += "CC CXX AR NM STRIP READELF OBJCOPY CFLAGS CXXFLAGS LDFLAGS RUSTC RUSTFLAGS EXEWRAPPER_ENABLED"
do_write_config() {
    # This needs to be Py to split the args into single-element lists
    cat >${WORKDIR}/meson.cross <<EOF
[binaries]
c = ${@meson_array('CC', d)}
cpp = ${@meson_array('CXX', d)}
ld = ${@meson_array('LD', d)}
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
bindgen_clang_arguments = ${@bindgen_args(d)}

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
ld = ${@meson_array('BUILD_LD', d)}
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

write_qemuwrapper() {
    # Write out a qemu wrapper that will be used as exe_wrapper so that meson
    # can run target helper binaries through that.
    qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_HOST}', ['${STAGING_LIBDIR}','${STAGING_BASELIBDIR}'])}"
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

do_write_config:append:class-target() {
    write_qemuwrapper
}

do_write_config:append:class-nativesdk() {
    write_qemuwrapper
}

# Tell externalsrc that changes to this file require a reconfigure
CONFIGURE_FILES = "meson.build"

meson_do_configure() {
    bbnote Executing meson ${EXTRA_OEMESON}...
    if ! meson setup ${MESONOPTS} "${MESON_SOURCEPATH}" "${B}" ${MESON_CROSS_FILE} ${EXTRA_OEMESON}; then
        MESON_LOG=${B}/meson-logs/meson-log.txt
        if test -f $MESON_LOG; then
            printf "\nLast 10 lines of meson-log.txt:\n"
            tail --lines=10 $MESON_LOG
            printf "\n"
        fi
        bbfatal_log meson setup failed
    fi
}

do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
meson_do_compile() {
    meson compile -v ${PARALLEL_MAKE} ${MESON_TARGET}
}

meson_do_install() {
    if [ "x${MESON_INSTALL_TAGS}" != "x" ] ; then
        meson_install_tags="--tags ${MESON_INSTALL_TAGS}"
    fi
    meson install --destdir ${D} --no-rebuild $meson_install_tags
}

EXPORT_FUNCTIONS do_configure do_compile do_install
