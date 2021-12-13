include meson.inc

inherit meson-routines
inherit nativesdk

SRC_URI += "file://meson-setup.py \
            file://meson-wrapper"

# The cross file logic is similar but not identical to that in meson.bbclass,
# since it's generating for an SDK rather than a cross-compile. Important
# differences are:
# - We can't set vars like CC, CXX, etc. yet because they will be filled in with
#   real paths by meson-setup.sh when the SDK is extracted.
# - Some overrides aren't needed, since the SDK injects paths that take care of
#   them.
def var_list2str(var, d):
    items = d.getVar(var).split()
    return items[0] if len(items) == 1 else ', '.join(repr(s) for s in items)

def generate_native_link_template(d):
    val = ['-L@{OECORE_NATIVE_SYSROOT}${libdir_native}',
           '-L@{OECORE_NATIVE_SYSROOT}${base_libdir_native}',
           '-Wl,-rpath-link,@{OECORE_NATIVE_SYSROOT}${libdir_native}',
           '-Wl,-rpath-link,@{OECORE_NATIVE_SYSROOT}${base_libdir_native}',
           '-Wl,--allow-shlib-undefined'
        ]
    build_arch = d.getVar('BUILD_ARCH')
    if 'x86_64' in build_arch:
        loader = 'ld-linux-x86-64.so.2'
    elif 'i686' in build_arch:
        loader = 'ld-linux.so.2'
    elif 'aarch64' in build_arch:
        loader = 'ld-linux-aarch64.so.1'
    elif 'ppc64le' in build_arch:
        loader = 'ld64.so.2'

    if loader:
        val += ['-Wl,--dynamic-linker=@{OECORE_NATIVE_SYSROOT}${base_libdir_native}/' + loader]

    return repr(val)

do_install:append() {
    install -d ${D}${datadir}/meson

    cat >${D}${datadir}/meson/meson.native.template <<EOF
[binaries]
c = ${@meson_array('BUILD_CC', d)}
cpp = ${@meson_array('BUILD_CXX', d)}
ar = ${@meson_array('BUILD_AR', d)}
nm = ${@meson_array('BUILD_NM', d)}
strip = ${@meson_array('BUILD_STRIP', d)}
readelf = ${@meson_array('BUILD_READELF', d)}
pkgconfig = 'pkg-config-native'

[built-in options]
c_args = ['-isystem@{OECORE_NATIVE_SYSROOT}${includedir_native}' , ${@var_list2str('BUILD_OPTIMIZATION', d)}]
c_link_args = ${@generate_native_link_template(d)}
cpp_args = ['-isystem@{OECORE_NATIVE_SYSROOT}${includedir_native}' , ${@var_list2str('BUILD_OPTIMIZATION', d)}]
cpp_link_args = ${@generate_native_link_template(d)}
[properties]
sys_root = '@OECORE_NATIVE_SYSROOT'
EOF

    cat >${D}${datadir}/meson/meson.cross.template <<EOF
[binaries]
c = @CC
cpp = @CXX
ar = @AR
nm = @NM
strip = @STRIP
pkgconfig = 'pkg-config'

[built-in options]
c_args = @CFLAGS
c_link_args = @LDFLAGS
cpp_args = @CPPFLAGS
cpp_link_args = @LDFLAGS

[properties]
needs_exe_wrapper = true
sys_root = @OECORE_TARGET_SYSROOT

[host_machine]
system = '${SDK_OS}'
cpu_family = '${@meson_cpu_family("SDK_ARCH", d)}'
cpu = '${SDK_ARCH}'
endian = '${@meson_endian("SDK", d)}'
EOF

    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/meson-setup.py ${D}${SDKPATHNATIVE}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin env setup wrapper.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${WORKDIR}/meson-wrapper ${D}${bindir}/meson
}

RDEPENDS:${PN} += "\
    nativesdk-ninja \
    nativesdk-python3 \
    nativesdk-python3-setuptools \
    "

FILES:${PN} += "${datadir}/meson ${SDKPATHNATIVE}"
