SUMMARY = "Firmware testsuite"
DESCRIPTION = "The tool fwts comprises of over fifty tests that are designed to exercise and test different aspects of a machine's firmware. Many of these tests need super user access to read BIOS data and ACPI tables, so the tool requires running with super user privileges (e.g. with sudo)."
HOMEPAGE = "https://wiki.ubuntu.com/Kernel/Reference/fwts"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://src/main.c;beginline=1;endline=16;md5=31da590f3e9f3bd34dcdb9e4db568519"

SRC_URI = "http://fwts.ubuntu.com/release/fwts-V${PV}.tar.gz;subdir=${BP} \
           file://0001-Add-correct-printf-qualifier-for-off_t.patch \
           file://0003-Remove-Werror-from-build.patch \
           file://0004-Define-__SWORD_TYPE-if-not-defined-by-libc.patch \
           file://0005-Undefine-PAGE_SIZE.patch \
           file://0006-use-intptr_t-to-fix-pointer-to-int-cast-issues.patch \
           file://0001-libfwtsiasl-Disable-parallel-builds-of-lex-bison-fil.patch \
           file://0001-Makefile.am-Add-missing-link-with-zlib.patch \
           "
SRC_URI[sha256sum] = "4af4e1e0f1ae9313297af722d744ba47a81c81bc5bdeab3f4f40837a39e4b808"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|powerpc64).*-linux"

DEPENDS = "libpcre glib-2.0 dtc bison-native flex-native libbsd"
DEPENDS:append:libc-musl = " libexecinfo"

inherit autotools bash-completion pkgconfig

LDFLAGS:append:libc-musl = " -lexecinfo"

# We end up linker barfing with undefined symbols on ppc64 but not on other arches
# surprisingly
ASNEEDED:powerpc64le = ""

FILES:${PN} += "${libdir}/fwts/lib*${SOLIBS}"
FILES:${PN}-dev += "${libdir}/fwts/lib*${SOLIBSDEV} ${libdir}/fwts/lib*.la"
FILES:${PN}-staticdev += "${libdir}/fwts/lib*a"

RDEPENDS:${PN} += "dtc"
