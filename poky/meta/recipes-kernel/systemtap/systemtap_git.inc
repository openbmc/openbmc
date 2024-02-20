LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRCREV = "e72dc118e563c645d93a1a2d771e8d90e8fec1ae"
PV = "5.0"

SRC_URI = "git://sourceware.org/git/systemtap.git;branch=master;protocol=https \
           file://0001-Do-not-let-configure-write-a-python-location-into-th.patch \
           file://0001-Install-python-modules-to-correct-library-dir.patch \
           file://0001-staprun-stapbpf-don-t-support-installing-a-non-root.patch \
           file://0001-Makefile.am-remove-runtime-linux-uprobes-and-runtime.patch \
           file://0001-prerelease-datestamp-fixes.patch \
           file://0001-configure.ac-fix-broken-libdebuginfod-library-auto-d.patch \
           file://0001-bpf-translate.cxx-fix-build-against-upcoming-gcc-14-.patch \
           file://0001-staprun-fix-build-against-upcoming-gcc-14-Werror-cal.patch \
           "

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|arm|aarch64|microblazeel|mips|riscv64).*-linux'
COMPATIBLE_HOST:libc-musl = 'null'

S = "${WORKDIR}/git"

# systemtap can't be built without optimization, if someone tries to compile an
# entire image as -O0, break with fatal.
python () {
    if bb.utils.contains("SELECTED_OPTIMIZATION", "-O0", "x", "", d) == "x":
        bb.fatal("systemtap can't be built with -O0, using -O1 -Wno-error or -O1 instead.")
}
