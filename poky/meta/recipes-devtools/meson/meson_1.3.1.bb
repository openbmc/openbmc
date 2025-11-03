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
SRC_URI[sha256sum] = "6020568bdede1643d4fb41e28215be38eff5d52da28ac7d125457c59e0032ad7"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)$"

inherit python_setuptools_build_meta github-releases

RDEPENDS:${PN} = "ninja python3-modules python3-pkg-resources"

FILES:${PN} += "${datadir}/polkit-1"

do_install:append () {
	# As per the same issue in the python recipe itself:
	# Unfortunately the following pyc files are non-deterministc due to 'frozenset'
	# being written without strict ordering, even with PYTHONHASHSEED = 0
	# Upstream is discussing ways to solve the issue properly, until then let's
	# just not install the problematic files.
	# More info: http://benno.id.au/blog/2013/01/15/python-determinism
	rm -f ${D}${libdir}/python*/site-packages/mesonbuild/dependencies/__pycache__/mpi.cpython*
}

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
    elif 'loongarch64' in build_arch:
        loader = 'ld-linux-loongarch-lp64d.so.1'
    elif 'riscv64' in build_arch:
        loader = 'ld-linux-riscv64-lp64d.so.1'

    if loader:
        val += ['-Wl,--dynamic-linker=@{OECORE_NATIVE_SYSROOT}${base_libdir_native}/' + loader]

    return repr(val)

install_templates() {
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
system = '$host_system'
cpu_family = '$host_cpu_family'
cpu = '$host_cpu'
endian = '$host_endian'
EOF
}

do_install:append:class-nativesdk() {
    host_system=${SDK_OS}
    host_cpu_family=${@meson_cpu_family("SDK_ARCH", d)}
    host_cpu=${SDK_ARCH}
    host_endian=${@meson_endian("SDK", d)}
    install_templates

    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/meson-setup.py ${D}${SDKPATHNATIVE}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin env setup wrapper.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${WORKDIR}/meson-wrapper ${D}${bindir}/meson
}

FILES:${PN}:append:class-nativesdk = "${datadir}/meson ${SDKPATHNATIVE}"

do_install:append:class-native() {
    host_system=${HOST_OS}
    host_cpu_family=${@meson_cpu_family("HOST_ARCH", d)}
    host_cpu=${HOST_ARCH}
    host_endian=${@meson_endian("HOST", d)}
    install_templates

    install -d ${D}${datadir}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/meson-setup.py ${D}${datadir}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin wrapper that substitues native/cross files
    # when running in a direct SDK environment.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${WORKDIR}/meson-wrapper ${D}${bindir}/meson
}
