LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRCREV = "7009f2f782e52d6105fe68d6ea2a9820a1099c06"
PV = "2.9"

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
# entire image as -O0, break with fatal.
python () {
    if bb.utils.contains("SELECTED_OPTIMIZATION", "-O0", "x", "", d) == "x":
        bb.fatal("systemtap can't be built with -O0, using -O1 -Wno-error or -O1 instead.")
}
