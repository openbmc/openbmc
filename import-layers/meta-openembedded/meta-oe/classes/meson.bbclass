inherit python3native

DEPENDS_append = " meson-native ninja-native"

# As Meson enforces out-of-tree builds we can just use cleandirs
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# Where the meson.build build configuration is
MESON_SOURCEPATH = "${S}"

# These variables in the environment override the *native* tools, not the cross.
export CPPFLAGS = "${BUILD_CPPFLAGS}"
export CC = "${BUILD_CC}"
export CFLAGS = "${BUILD_CFLAGS}"
export CXX = "${BUILD_CXX}"
export CXXFLAGS = "${BUILD_CXXFLAGS}"
export LD = "${BUILD_LD}"
export LDFLAGS = "${BUILD_LDFLAGS}"
export AR = "${BUILD_AR}"
export PKG_CONFIG = "pkg-config-native"

def noprefix(var, d):
    return d.getVar(var, True).replace(d.getVar('prefix', True) + '/', '', 1)

MESONOPTS = " --prefix ${prefix} \
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
              --sharedstatedir ${sharedstatedir}"

MESON_C_ARGS = "${TARGET_CC_ARCH}${TOOLCHAIN_OPTIONS}"
MESON_LINK_ARGS = "${MESON_C_ARGS} ${LDFLAGS}"

MESON_HOST_ENDIAN = "${@bb.utils.contains('SITEINFO_ENDIANNESS', 'be', 'big', 'little', d)}"
MESON_TARGET_ENDIAN = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'big', 'little', d)}"

EXTRA_OEMESON += "${PACKAGECONFIG_CONFARGS}"

MESON_CROSS_FILE = ""
MESON_CROSS_FILE_class-target = "--cross-file ${WORKDIR}/meson.cross"

def meson_array(var, d):
    return "', '".join(d.getVar(var, True).split()).join(("'", "'"))

addtask write_config before do_configure
do_write_config[vardeps] += "MESON_C_ARGS TOOLCHAIN_OPTIONS"
do_write_config() {
    # This needs to be Py to split the args into single-element lists
    cat >${WORKDIR}/meson.cross <<EOF
[binaries]
c = '${HOST_PREFIX}gcc'
cpp = '${HOST_PREFIX}g++'
ar = '${HOST_PREFIX}ar'
ld = '${HOST_PREFIX}ld'
strip = '${HOST_PREFIX}strip'
readelf = '${HOST_PREFIX}readelf'
pkgconfig = 'pkg-config'

[properties]
needs_exe_wrapper = true
c_args = [${@meson_array('MESON_C_ARGS', d)}]
c_link_args = [${@meson_array('MESON_LINK_ARGS', d)}]
cpp_args = [${@meson_array('MESON_C_ARGS', d)}]
cpp_link_args = [${@meson_array('MESON_LINK_ARGS', d)}]

[host_machine]
system = '${HOST_OS}'
cpu_family = '${HOST_ARCH}'
cpu = '${HOST_ARCH}'
endian = '${MESON_HOST_ENDIAN}'

[target_machine]
system = '${TARGET_OS}'
cpu_family = '${TARGET_ARCH}'
cpu = '${TARGET_ARCH}'
endian = '${MESON_TARGET_ENDIAN}'
EOF
}

CONFIGURE_FILES = "meson.build"

meson_do_configure() {
    if ! meson ${MESONOPTS} "${MESON_SOURCEPATH}" "${B}" ${MESON_CROSS_FILE} ${EXTRA_OEMESON}; then
        cat ${B}/meson-logs/meson-log.txt
        bbfatal_log meson failed
    fi
}

do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
meson_do_compile() {
    ninja ${PARALLEL_MAKE}
}

meson_do_install() {
    DESTDIR='${D}' ninja ${PARALLEL_MAKEINST} install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
