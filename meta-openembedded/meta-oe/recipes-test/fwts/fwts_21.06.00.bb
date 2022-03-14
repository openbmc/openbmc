SUMMARY = "Firmware testsuite"
DESCRIPTION = "The tool fwts comprises of over fifty tests that are designed to exercise and test different aspects of a machine's firmware. Many of these tests need super user access to read BIOS data and ACPI tables, so the tool requires running with super user privileges (e.g. with sudo)."
HOMEPAGE = "https://wiki.ubuntu.com/Kernel/Reference/fwts"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://src/main.c;beginline=1;endline=16;md5=31da590f3e9f3bd34dcdb9e4db568519"

SRC_URI = "http://fwts.ubuntu.com/release/fwts-V${PV}.tar.gz;subdir=${BP} \
           file://0001-Add-correct-printf-qualifier-for-off_t.patch \
           file://0002-Include-poll.h-instead-of-deprecated-sys-poll.h.patch \
           file://0003-Remove-Werror-from-build.patch \
           file://0004-Define-__SWORD_TYPE-if-not-defined-by-libc.patch \
           file://0005-Undefine-PAGE_SIZE.patch \
           file://0006-use-intptr_t-to-fix-pointer-to-int-cast-issues.patch \
           "
SRC_URI[sha256sum] = "ca43439707976f6664fe3f6eb7f356a51ac7d7f8a4e246ef4d1b16305e066909"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|powerpc64).*-linux"

DEPENDS = "libpcre glib-2.0 dtc bison-native libbsd"
DEPENDS:append:libc-musl = " libexecinfo"

inherit autotools bash-completion pkgconfig

LDFLAGS:append:libc-musl = " -lexecinfo"

FILES:${PN} += "${libdir}/fwts/lib*${SOLIBS}"
FILES:${PN}-dev += "${libdir}/fwts/lib*${SOLIBSDEV} ${libdir}/fwts/lib*.la"
FILES:${PN}-staticdev += "${libdir}/fwts/lib*a"

RDEPENDS:${PN} += "dtc"
