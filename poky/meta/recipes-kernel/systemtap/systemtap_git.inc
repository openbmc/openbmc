LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRCREV = "4051c70c9318c837981384cbb23f3e9eb1bd0892"
PV = "3.2"

SRC_URI = "git://sourceware.org/git/systemtap.git \
           file://configure-allow-to-disable-libvirt.patch \
           file://x32_abi_time.patch \
           file://monitor-option.patch \
           file://no-msgfmt-check.patch \
           file://0001-Do-not-let-configure-write-a-python-location-into-th.patch \
           file://0001-Install-python-modules-to-correct-library-dir.patch \
           file://0001-buildrun-remove-quotes-around-I-include-line.patch \
           file://0001-staprun-stapbpf-don-t-support-installing-a-non-root.patch \
           file://0001-Fix-PR22551-by-updating-the-use-of-timers-for-the-4..patch \
           file://0001-Fixes-for-gcc-8.patch \
           file://0001-Use-sysroot-when-looking-for-the-System.map-file.patch \
           file://0001-debuginfo-lookup-with-sysroot-case-do-not-remove-sys.patch \
           file://0001-Make-sure-sysroot-paths-don-t-end-with-a-slash.patch \
           file://0001-sysroot-fix-short-release-r-option-handling.patch \
           file://0001-Delay-adding-sysroot-path-to-module-name-in-case-of-.patch \
           file://0001-Added-a-couple-of-small-sysroot-fixes.patch \
           file://0001-_stp_umodule_relocate-needs-target-file-path-not-hos.patch \
           file://0001-sysroot-handle-symbolic-links-with-absolute-name-rel.patch \
           file://0001-sysroot-fix-short-release-r-option-handling-follow-u.patch \
           "

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|arm|aarch64|microblazeel|mips).*-linux'
COMPATIBLE_HOST_libc-musl = 'null'

S = "${WORKDIR}/git"

# systemtap can't be built without optimization, if someone tries to compile an
# entire image as -O0, break with fatal.
python () {
    if bb.utils.contains("SELECTED_OPTIMIZATION", "-O0", "x", "", d) == "x":
        bb.fatal("systemtap can't be built with -O0, using -O1 -Wno-error or -O1 instead.")
}
