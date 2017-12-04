LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRCREV = "b8ea350dc13adb6190d9044a5b80110a4c441270"
PV = "3.1"

SRC_URI = "git://sourceware.org/git/systemtap.git \
           file://system_map_location.patch \
           file://configure-allow-to-disable-libvirt.patch \
           file://x32_abi_time.patch \
           file://monitor-option.patch \
           file://no-msgfmt-check.patch \
           file://0001-Do-not-let-configure-write-a-python-location-into-th.patch \
           file://0001-Install-python-modules-to-correct-library-dir.patch \
           file://0001-buildrun-remove-quotes-around-I-include-line.patch \
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
