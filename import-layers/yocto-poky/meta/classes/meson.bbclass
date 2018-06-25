inherit python3native

DEPENDS_append = " meson-native ninja-native"

# As Meson enforces out-of-tree builds we can just use cleandirs
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# Where the meson.build build configuration is
MESON_SOURCEPATH = "${S}"

def noprefix(var, d):
    return d.getVar(var).replace(d.getVar('prefix') + '/', '', 1)

MESONOPTS = " --prefix ${prefix} \
              --buildtype plain \
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

MESON_TOOLCHAIN_ARGS = "${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
MESON_C_ARGS = "${MESON_TOOLCHAIN_ARGS} ${CFLAGS}"
MESON_CPP_ARGS = "${MESON_TOOLCHAIN_ARGS} ${CXXFLAGS}"
MESON_LINK_ARGS = "${MESON_TOOLCHAIN_ARGS} ${LDFLAGS}"

# both are required but not used by meson
MESON_HOST_ENDIAN = "bogus-endian"
MESON_TARGET_ENDIAN = "bogus-endian"

EXTRA_OEMESON += "${PACKAGECONFIG_CONFARGS}"

MESON_CROSS_FILE = ""
MESON_CROSS_FILE_class-target = "--cross-file ${WORKDIR}/meson.cross"
MESON_CROSS_FILE_class-nativesdk = "--cross-file ${WORKDIR}/meson.cross"

def meson_array(var, d):
    return "', '".join(d.getVar(var).split()).join(("'", "'"))

addtask write_config before do_configure
do_write_config[vardeps] += "MESON_C_ARGS MESON_CPP_ARGS MESON_LINK_ARGS CC CXX LD AR NM STRIP READELF"
do_write_config() {
    # This needs to be Py to split the args into single-element lists
    cat >${WORKDIR}/meson.cross <<EOF
[binaries]
c = [${@meson_array('CC', d)}]
cpp = [${@meson_array('CXX', d)}]
ar = [${@meson_array('AR', d)}]
nm = [${@meson_array('NM', d)}]
ld = [${@meson_array('LD', d)}]
strip = [${@meson_array('STRIP', d)}]
readelf = [${@meson_array('READELF', d)}]
pkgconfig = 'pkg-config'

[properties]
needs_exe_wrapper = true
c_args = [${@meson_array('MESON_C_ARGS', d)}]
c_link_args = [${@meson_array('MESON_LINK_ARGS', d)}]
cpp_args = [${@meson_array('MESON_CPP_ARGS', d)}]
cpp_link_args = [${@meson_array('MESON_LINK_ARGS', d)}]
gtkdoc_exe_wrapper = '${B}/gtkdoc-qemuwrapper'

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

meson_do_configure_prepend_class-target() {
    # Set these so that meson uses the native tools for its build sanity tests,
    # which require executables to be runnable. The cross file will still
    # override these for the target build. Note that we do *not* set CFLAGS,
    # LDFLAGS, etc. as they will be slurped in by meson and applied to the
    # target build, causing errors.
    export CC="${BUILD_CC}"
    export CXX="${BUILD_CXX}"
    export LD="${BUILD_LD}"
    export AR="${BUILD_AR}"
}

meson_do_configure_prepend_class-nativesdk() {
    # Set these so that meson uses the native tools for its build sanity tests,
    # which require executables to be runnable. The cross file will still
    # override these for the nativesdk build. Note that we do *not* set CFLAGS,
    # LDFLAGS, etc. as they will be slurped in by meson and applied to the
    # nativesdk build, causing errors.
    export CC="${BUILD_CC}"
    export CXX="${BUILD_CXX}"
    export LD="${BUILD_LD}"
    export AR="${BUILD_AR}"
}

meson_do_configure_prepend_class-native() {
    export PKG_CONFIG="pkg-config-native"
}

do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
meson_do_compile() {
    ninja ${PARALLEL_MAKE}
}

meson_do_install() {
    DESTDIR='${D}' ninja ${PARALLEL_MAKEINST} install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
