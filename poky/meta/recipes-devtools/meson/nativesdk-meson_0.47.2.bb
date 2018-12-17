include meson.inc

inherit nativesdk

SRC_URI += "file://meson-setup.py \
            file://meson-wrapper"

def meson_array(var, d):
    return "', '".join(d.getVar(var).split()).join(("'", "'"))

# both are required but not used by meson
MESON_SDK_ENDIAN = "bogus-endian"
MESON_TARGET_ENDIAN = "bogus-endian"

MESON_TOOLCHAIN_ARGS = "${BUILDSDK_CC_ARCH}${TOOLCHAIN_OPTIONS}"
MESON_C_ARGS = "${MESON_TOOLCHAIN_ARGS} ${BUILDSDK_CFLAGS}"
MESON_CPP_ARGS = "${MESON_TOOLCHAIN_ARGS} ${BUILDSDK_CXXFLAGS}"
MESON_LINK_ARGS = "${MESON_TOOLCHAIN_ARGS} ${BUILDSDK_LDFLAGS}"

# This logic is similar but not identical to that in meson.bbclass, since it's
# generating for an SDK rather than a cross-compile. Important differences are:
# - We can't set vars like CC, CXX, etc. yet because they will be filled in with
#   real paths by meson-setup.sh when the SDK is extracted.
# - Some overrides aren't needed, since the SDK injects paths that take care of
#   them.
addtask write_config before do_install
do_write_config[vardeps] += "MESON_C_ARGS MESON_CPP_ARGS MESON_LINK_ARGS CC CXX LD AR NM STRIP READELF"
do_write_config() {
    # This needs to be Py to split the args into single-element lists
    cat >${WORKDIR}/meson.cross <<EOF
[binaries]
c = @@CC@@
cpp = @@CXX@@
ar = @@AR@@
nm = @@NM@@
ld = @@LD@@
strip = @@STRIP@@
pkgconfig = 'pkg-config'

[properties]
needs_exe_wrapper = true
c_args = @@CFLAGS@@
c_link_args = @@LDFLAGS@@
cpp_args = @@CPPFLAGS@@
cpp_link_args = @@LDFLAGS@@

[host_machine]
system = '${SDK_OS}'
cpu_family = '${SDK_ARCH}'
cpu = '${SDK_ARCH}'
endian = '${MESON_SDK_ENDIAN}'
EOF
}

do_install_append() {
    install -d ${D}${datadir}/meson
    install -m 0644 ${WORKDIR}/meson.cross ${D}${datadir}/meson/

    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/meson-setup.py ${D}${SDKPATHNATIVE}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin env setup wrapper.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${WORKDIR}/meson-wrapper ${D}${bindir}/meson
}

RDEPENDS_${PN} += "\
    nativesdk-ninja \
    nativesdk-python3-core \
    nativesdk-python3-misc \
    nativesdk-python3-modules \
    "

FILES_${PN} += "${datadir}/meson ${SDKPATHNATIVE}"
