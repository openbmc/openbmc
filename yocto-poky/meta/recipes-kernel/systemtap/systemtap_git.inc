LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRCREV = "bf16266782e1f2588b519a50d9684279d4e21036"
PV = "2.7+git${SRCPV}"

SRC_URI = "git://sourceware.org/git/systemtap.git \
           file://docproc-build-fix.patch \
           file://obsolete_automake_macros.patch \
           file://system_map_location.patch \
           file://configure-allow-to-disable-libvirt.patch \
           file://x32_abi_time.patch \
          "

# systemtap doesn't support mips
COMPATIBLE_HOST = '(x86_64|i.86|powerpc|arm|aarch64).*-linux'

S = "${WORKDIR}/git"

# systemtap can't be built without optimization, if someone tries to compile an
# entire image as -O0, we override it with -O2 here and give a note about it.
def get_optimization(d):
    selected_optimization = d.getVar("SELECTED_OPTIMIZATION", True)
    if bb.utils.contains("SELECTED_OPTIMIZATION", "-O0", "x", "", d) == "x":
        bb.note("systemtap can't be built with -O0, -O2 will be used instead.")
        return selected_optimization.replace("-O0", "-O2")
    return selected_optimization

SELECTED_OPTIMIZATION := "${@get_optimization(d)}"
