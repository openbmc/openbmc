HOMEPAGE = "http://mesonbuild.com"
SUMMARY = "A high performance build system"
DESCRIPTION = "Meson is a build system designed to increase programmer \
productivity. It does this by providing a fast, simple and easy to use \
interface for modern software development tools and practices."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

GITHUB_BASE_URI = "https://github.com/mesonbuild/meson/releases/"
SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/meson-${PV}.tar.gz \
           file://meson-setup.py \
           file://meson-wrapper \
           file://0001-python-module-do-not-manipulate-the-environment-when.patch \
           file://0001-Make-CPU-family-warnings-fatal.patch \
           file://0002-Support-building-allarch-recipes-again.patch \
           "
SRC_URI[sha256sum] = "c105816d8158c76b72adcb9ff60297719096da7d07f6b1f000fd8c013cd387af"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)$"

inherit python_setuptools_build_meta github-releases

RDEPENDS:${PN} = "ninja python3-modules python3-pkg-resources"

FILES:${PN} += "${datadir}/polkit-1"

BBCLASSEXTEND = "native nativesdk"

inherit meson-routines

# The cross file logic is similar but not identical to that in meson.bbclass,
# since it's generating for an SDK rather than a cross-compile. Important
# differences are:
# - We can't set vars like CC, CXX, etc. yet because they will be filled in with
#   real paths by meson-setup.sh when the SDK is extracted.
# - Some overrides aren't needed, since the SDK injects paths that take care of
#   them.
def var_list2str(var, d):
    items = d.getVar(var).split()
    return repr(items[0]) if len(items) == 1 else ', '.join(repr(s) for s in items)

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
    elif 'loongarch64' in build_arch:
        loader = 'ld-linux-loongarch-lp64d.so.1'
    elif 'riscv64' in build_arch:
        loader = 'ld-linux-riscv64-lp64d.so.1'

    if loader:
        val += ['-Wl,--dynamic-linker=@{OECORE_NATIVE_SYSROOT}${base_libdir_native}/' + loader]

    return repr(val)

install_native_template() {
    install -d ${D}${datadir}/meson

    cat >${D}${datadir}/meson/meson.native.template <<EOF
[binaries]
c = ${@meson_array('BUILD_CC', d)}
cpp = ${@meson_array('BUILD_CXX', d)}
ar = ${@meson_array('BUILD_AR', d)}
nm = ${@meson_array('BUILD_NM', d)}
strip = ${@meson_array('BUILD_STRIP', d)}
readelf = ${@meson_array('BUILD_READELF', d)}
pkg-config = 'pkg-config-native'

[built-in options]
c_args = ['-isystem@{OECORE_NATIVE_SYSROOT}${includedir_native}' , ${@var_list2str('BUILD_OPTIMIZATION', d)}]
c_link_args = ${@generate_native_link_template(d)}
cpp_args = ['-isystem@{OECORE_NATIVE_SYSROOT}${includedir_native}' , ${@var_list2str('BUILD_OPTIMIZATION', d)}]
cpp_link_args = ${@generate_native_link_template(d)}
EOF
}

install_nativesdk_template() {
    install -d ${D}${datadir}/meson

    cat >${D}${datadir}/meson/meson.native.template <<EOF
[binaries]
pkg-config = 'pkg-config-native'

[built-in options]
c_args = ['-isystem@{OECORE_NATIVE_SYSROOT}${includedir_native}']
c_link_args = ['-L@{OECORE_NATIVE_SYSROOT}${libdir_native}', '-L@{OECORE_NATIVE_SYSROOT}${base_libdir_native}',]
cpp_args = ['-isystem@{OECORE_NATIVE_SYSROOT}${includedir_native}']
cpp_link_args = ['-L@{OECORE_NATIVE_SYSROOT}${libdir_native}', '-L@{OECORE_NATIVE_SYSROOT}${base_libdir_native}',]
EOF
}

install_cross_template() {
    install -d ${D}${datadir}/meson

    cat >${D}${datadir}/meson/meson.cross.template <<EOF
[binaries]
c = @CC
cpp = @CXX
ar = @AR
nm = @NM
strip = @STRIP
pkg-config = 'pkg-config'

[built-in options]
c_args = @CFLAGS
c_link_args = @LDFLAGS
cpp_args = @CPPFLAGS
cpp_link_args = @LDFLAGS

[properties]
needs_exe_wrapper = true
sys_root = @OECORE_TARGET_SYSROOT

[host_machine]
system = @OECORE_MESON_HOST_SYSTEM
cpu_family = @OECORE_MESON_HOST_CPU_FAMILY
cpu = @OECORE_MESON_HOST_CPU
endian = @OECORE_MESON_HOST_ENDIAN
EOF
}

do_install:append:class-nativesdk() {
    install_nativesdk_template
    install_cross_template

    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${UNPACKDIR}/meson-setup.py ${D}${SDKPATHNATIVE}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin env setup wrapper.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${UNPACKDIR}/meson-wrapper ${D}${bindir}/meson
}

FILES:${PN}:append:class-nativesdk = "${datadir}/meson ${SDKPATHNATIVE}"

do_install:append:class-native() {
    install_native_template
    install_cross_template

    install -d ${D}${datadir}/post-relocate-setup.d
    install -m 0755 ${UNPACKDIR}/meson-setup.py ${D}${datadir}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin wrapper that substitues native/cross files
    # when running in a direct SDK environment.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${UNPACKDIR}/meson-wrapper ${D}${bindir}/meson
}
