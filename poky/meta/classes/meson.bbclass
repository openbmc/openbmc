inherit siteinfo python3native

DEPENDS_append = " meson-native ninja-native"

# As Meson enforces out-of-tree builds we can just use cleandirs
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# Where the meson.build build configuration is
MESON_SOURCEPATH = "${S}"

def noprefix(var, d):
    return d.getVar(var).replace(d.getVar('prefix') + '/', '', 1)

MESON_BUILDTYPE ?= "plain"
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
              --wrap-mode nodownload"

EXTRA_OEMESON_append = " ${PACKAGECONFIG_CONFARGS}"

MESON_CROSS_FILE = ""
MESON_CROSS_FILE_class-target = "--cross-file ${WORKDIR}/meson.cross"
MESON_CROSS_FILE_class-nativesdk = "--cross-file ${WORKDIR}/meson.cross"

def meson_array(var, d):
    items = d.getVar(var).split()
    return repr(items[0] if len(items) == 1 else items)

# Map our ARCH values to what Meson expects:
# http://mesonbuild.com/Reference-tables.html#cpu-families
def meson_cpu_family(var, d):
    import re
    arch = d.getVar(var)
    if arch == 'powerpc':
        return 'ppc'
    elif arch == 'powerpc64' or arch == 'powerpc64le':
        return 'ppc64'
    elif arch == 'armeb':
        return 'arm'
    elif arch == 'aarch64_be':
        return 'aarch64'
    elif arch == 'mipsel':
        return 'mips'
    elif arch == 'mips64el':
        return 'mips64'
    elif re.match(r"i[3-6]86", arch):
        return "x86"
    elif arch == "microblazeel":
        return "microblaze"
    else:
        return arch

# Map our OS values to what Meson expects:
# https://mesonbuild.com/Reference-tables.html#operating-system-names
def meson_operating_system(var, d):
    os = d.getVar(var)
    if "mingw" in os:
        return "windows"
    # avoid e.g 'linux-gnueabi'
    elif "linux" in os:
        return "linux"
    else:
        return os

def meson_endian(prefix, d):
    arch, os = d.getVar(prefix + "_ARCH"), d.getVar(prefix + "_OS")
    sitedata = siteinfo_data_for_machine(arch, os, d)
    if "endian-little" in sitedata:
        return "little"
    elif "endian-big" in sitedata:
        return "big"
    else:
        bb.fatal("Cannot determine endianism for %s-%s" % (arch, os))

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

[properties]
needs_exe_wrapper = true
c_args = ${@meson_array('CFLAGS', d)}
c_link_args = ${@meson_array('LDFLAGS', d)}
cpp_args = ${@meson_array('CXXFLAGS', d)}
cpp_link_args = ${@meson_array('LDFLAGS', d)}
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
}

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

override_native_tools() {
    # Set these so that meson uses the native tools for its build sanity tests,
    # which require executables to be runnable. The cross file will still
    # override these for the target build.
    export CC="${BUILD_CC}"
    export CXX="${BUILD_CXX}"
    export LD="${BUILD_LD}"
    export AR="${BUILD_AR}"
    export STRIP="${BUILD_STRIP}"
    # These contain *target* flags but will be used as *native* flags.  The
    # correct native flags will be passed via -Dc_args and so on, unset them so
    # they don't interfere with tools invoked by Meson (such as g-ir-scanner)
    unset CPPFLAGS CFLAGS CXXFLAGS LDFLAGS
}

meson_do_configure_prepend_class-target() {
    override_native_tools
}

meson_do_configure_prepend_class-nativesdk() {
    override_native_tools
}

meson_do_configure_prepend_class-native() {
    export PKG_CONFIG="pkg-config-native"
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
