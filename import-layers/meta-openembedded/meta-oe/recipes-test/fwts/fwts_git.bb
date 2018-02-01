SUMMARY = "Firmware testsuite"
DESCRIPTION = "The tool fwts comprises of over fifty tests that are designed to exercise and test different aspects of a machine's firmware. Many of these tests need super user access to read BIOS data and ACPI tables, so the tool requires running with super user privileges (e.g. with sudo)."
HOMEPAGE = "https://wiki.ubuntu.com/Kernel/Reference/fwts"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://src/main.c;beginline=1;endline=16;md5=31da590f3e9f3bd34dcdb9e4db568519"

PV = "17.03.00"

SRCREV = "e3e9d1442b4cc70f5b30199f584cec8e25aeaad4"
SRC_URI = "git://kernel.ubuntu.com/hwe/fwts.git \
           file://0001-ignore-constant-logical-operand-warning-with-clang.patch \
           "

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|powerpc64).*-linux"

DEPENDS = "libpcre json-c glib-2.0 dtc"

inherit autotools pkgconfig

CFLAGS += "-I${STAGING_INCDIR}/json-c -Wno-error=unknown-pragmas"

FILES_${PN} += "${libdir}/fwts/lib*${SOLIBS}"
FILES_${PN}-dev += "${libdir}/fwts/lib*${SOLIBSDEV} ${libdir}/fwts/lib*.la"
FILES_${PN}-staticdev += "${libdir}/fwts/lib*a"
FILES_${PN}-dbg += "${libdir}/fwts/.debug"

TOOLCHAIN = "gcc"

