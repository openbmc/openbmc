SUMMARY = "Firmware testsuite"
DESCRIPTION = "The tool fwts comprises of over fifty tests that are designed to exercise and test different aspects of a machine's firmware. Many of these tests need super user access to read BIOS data and ACPI tables, so the tool requires running with super user privileges (e.g. with sudo)."
HOMEPAGE = "https://wiki.ubuntu.com/Kernel/Reference/fwts"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://src/main.c;beginline=1;endline=16;md5=31da590f3e9f3bd34dcdb9e4db568519"

SRCREV = "b0ec7aa2ef743d113fd8c5e57c0ca3d5edd86f0e"
SRC_URI = "http://fwts.ubuntu.com/release/fwts-V19.06.00.tar.gz;subdir=${BPN}-${PV} \
           file://0001-Include-poll.h-instead-of-deprecated-sys-poll.h.patch \
           file://0002-Define-__SWORD_TYPE-if-not-defined-by-libc.patch \
           file://0003-Undefine-PAGE_SIZE.patch \
           file://0001-Add-correct-printf-qualifier-for-off_t.patch \
           file://0003-use-intptr_t-to-fix-pointer-to-int-cast-issues.patch \
           file://0001-Remove-Werror-from-build.patch \
           "
SRC_URI[md5sum] = "012f933329510cc5a71817ede681eee2"
SRC_URI[sha256sum] = "13aa991f12c69f48df368aae5e5d0fbc9136413b4bfe115421bc3216d919f8a2"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|powerpc64).*-linux"

DEPENDS = "libpcre json-c glib-2.0 dtc bison-native libbsd"
DEPENDS_append_libc-musl = " libexecinfo"

inherit autotools bash-completion pkgconfig

CFLAGS += "-I${STAGING_INCDIR}/json-c -Wno-error=unknown-pragmas"
LDFLAGS_append_libc-musl = " -lexecinfo"

FILES_${PN} += "${libdir}/fwts/lib*${SOLIBS}"
FILES_${PN}-dev += "${libdir}/fwts/lib*${SOLIBSDEV} ${libdir}/fwts/lib*.la"
FILES_${PN}-staticdev += "${libdir}/fwts/lib*a"
FILES_${PN}-dbg += "${libdir}/fwts/.debug"

TOOLCHAIN = "gcc"

