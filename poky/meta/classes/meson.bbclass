inherit python3native meson-routines

DEPENDS:append = " meson-native ninja-native"

# As Meson enforces out-of-tree builds we can just use cleandirs
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# Where the meson.build build configuration is
MESON_SOURCEPATH = "${S}"

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

addtask write_config before do_configure
do_write_config[vardeps] += "CC CXX LD AR NM STRIP READELF CFLAGS CXXFLAGS LDFLAGS"
do_write_config() {
    # This needs to be Py to split the args into single-element lists
    cat >${WORKDIR}/meson.cross <<EOF
[binaries]
c = ${@meson_array('CC', d)}
cpp = ${@meson_array('CXX', d)}
ar = ${@meson_array('AR', d)}
nm = ${@meson_array('NM', d)}
strip = ${@meson_array('STRIP', d)}
readelf = ${@meson_array('READELF', d)}
pkgconfig = 'pkg-config'
llvm-config = 'llvm-config${LLVMVERSION}'
cups-config = 'cups-config'
g-ir-scanner = '${STAGING_BINDIR}/g-ir-scanner-wrapper'
g-ir-compiler = '${STAGING_BINDIR}/g-ir-compiler-wrapper'

[built-in options]
c_args = ${@meson_array('CFLAGS', d)}
c_link_args = ${@meson_array('LDFLAGS', d)}
cpp_args = ${@meson_array('CXXFLAGS', d)}
cpp_link_args = ${@meson_array('LDFLAGS', d)}

[properties]
needs_exe_wrapper = true
gtkdoc_exe_wrapper = '${B}/gtkdoc-qemuwrapper'

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
ar = ${@meson_array('BUILD_AR', d)}
nm = ${@meson_array('BUILD_NM', d)}
strip = ${@meson_array('BUILD_STRIP', d)}
readelf = ${@meson_array('BUILD_READELF', d)}
pkgconfig = 'pkg-config-native'

[built-in options]
c_args = ${@meson_array('BUILD_CFLAGS', d)}
c_link_args = ${@meson_array('BUILD_LDFLAGS', d)}
cpp_args = ${@meson_array('BUILD_CXXFLAGS', d)}
cpp_link_args = ${@meson_array('BUILD_LDFLAGS', d)}
EOF
}

# Tell externalsrc that changes to this file require a reconfigure
CONFIGURE_FILES = "meson.build"

meson_do_configure() {
    # Meson requires this to be 'bfd, 'lld' or 'gold' from 0.53 onwards
    # https://github.com/mesonbuild/meson/commit/ef9aeb188ea2bc7353e59916c18901cde90fa2b3
    unset LD

    # Work around "Meson fails if /tmp is mounted with noexec #2972"
    mkdir -p "${B}/meson-private/tmp"
    export TMPDIR="${B}/meson-private/tmp"
    bbnote Executing meson ${EXTRA_OEMESON}...
    if ! meson ${MESONOPTS} "${MESON_SOURCEPATH}" "${B}" ${MESON_CROSS_FILE} ${EXTRA_OEMESON}; then
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
    ninja -v ${PARALLEL_MAKE}
}

meson_do_install() {
    DESTDIR='${D}' ninja -v ${PARALLEL_MAKEINST} install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
