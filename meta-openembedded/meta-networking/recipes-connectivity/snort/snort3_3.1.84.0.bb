SUMMARY = "snort3"
DESCRIPTION = "snort3 - a free lightweight network intrusion detection system for UNIX and Windows."
HOMEPAGE = "http://www.snort.org/"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=78fa8ef966b48fbf9095e13cc92377c5"

DEPENDS = "flex-native hwloc libdaq libdnet libpcap libpcre libtirpc libunwind luajit zlib"

SRC_URI = "git://github.com/snort3/snort3.git;protocol=https;branch=master \
           file://0001-cmake-Check-for-HP-libunwind.patch \
           file://0001-cmake-Pass-noline-flag-to-flex.patch"
SRCREV = "e7312efd840d66a52a2019abe1db7cc89ca0f39a"

S = "${WORKDIR}/git"

PACKAGES =+ "${PN}-scripts"

inherit cmake pkgconfig

do_install:append() {
    sed -i "s#${RECIPE_SYSROOT_NATIVE}##g; s#${RECIPE_SYSROOT}##g" ${D}${libdir}/pkgconfig/snort.pc
}

FILES:${PN} += "${libdir}/snort/daq/*.so"

FILES:${PN}-scripts = "${bindir}/appid_detector_builder.sh"

RDEPENDS:${PN}-scripts += "bash"

# mips64/ppc/ppc64/riscv64/riscv32 is not supported in this release
COMPATIBLE_HOST:mipsarchn32 = "null"
COMPATIBLE_HOST:mipsarchn64 = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:powerpc64 = "null"
COMPATIBLE_HOST:powerpc64le = "null"
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:riscv32 = "null"
