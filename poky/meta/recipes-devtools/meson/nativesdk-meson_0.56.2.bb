include meson.inc

inherit siteinfo
inherit nativesdk

SRC_URI += "file://meson-setup.py \
            file://meson-wrapper"

def meson_endian(prefix, d):
    arch, os = d.getVar(prefix + "_ARCH"), d.getVar(prefix + "_OS")
    sitedata = siteinfo_data_for_machine(arch, os, d)
    if "endian-little" in sitedata:
        return "little"
    elif "endian-big" in sitedata:
        return "big"
    else:
        bb.fatal("Cannot determine endianism for %s-%s" % (arch, os))

# The cross file logic is similar but not identical to that in meson.bbclass,
# since it's generating for an SDK rather than a cross-compile. Important
# differences are:
# - We can't set vars like CC, CXX, etc. yet because they will be filled in with
#   real paths by meson-setup.sh when the SDK is extracted.
# - Some overrides aren't needed, since the SDK injects paths that take care of
#   them.
do_install_append() {
    install -d ${D}${datadir}/meson
    cat >${D}${datadir}/meson/meson.cross.template <<EOF
[binaries]
c = @CC
cpp = @CXX
ar = @AR
nm = @NM
strip = @STRIP
pkgconfig = 'pkg-config'

[properties]
needs_exe_wrapper = true
c_args = @CFLAGS
c_link_args = @LDFLAGS
cpp_args = @CPPFLAGS
cpp_link_args = @LDFLAGS
sys_root = @OECORE_TARGET_SYSROOT

[host_machine]
system = '${SDK_OS}'
cpu_family = '${SDK_ARCH}'
cpu = '${SDK_ARCH}'
endian = '${@meson_endian("SDK", d)}'
EOF

    install -d ${D}${SDKPATHNATIVE}/post-relocate-setup.d
    install -m 0755 ${WORKDIR}/meson-setup.py ${D}${SDKPATHNATIVE}/post-relocate-setup.d/

    # We need to wrap the real meson with a thin env setup wrapper.
    mv ${D}${bindir}/meson ${D}${bindir}/meson.real
    install -m 0755 ${WORKDIR}/meson-wrapper ${D}${bindir}/meson
}

RDEPENDS_${PN} += "\
    nativesdk-ninja \
    nativesdk-python3 \
    nativesdk-python3-setuptools \
    "

FILES_${PN} += "${datadir}/meson ${SDKPATHNATIVE}"
