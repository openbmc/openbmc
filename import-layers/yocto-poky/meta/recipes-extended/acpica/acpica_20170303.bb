SUMMARY = "ACPICA tools for the development and debug of ACPI tables"
DESCRIPTION = "The ACPI Component Architecture (ACPICA) project provides an \
OS-independent reference implementation of the Advanced Configuration and \
Power Interface Specification (ACPI). ACPICA code contains those portions of \
ACPI meant to be directly integrated into the host OS as a kernel-resident \
subsystem, and a small set of tools to assist in developing and debugging \
ACPI tables."

HOMEPAGE = "http://www.acpica.org/"
SECTION = "console/tools"

LICENSE = "BSD | GPLv2"
LIC_FILES_CHKSUM = "file://generate/unix/readme.txt;md5=204407e197c1a01154a48f6c6280c3aa"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

DEPENDS = "bison flex"

SRC_URI = "https://acpica.org/sites/acpica/files/acpica-unix2-${PV}.tar.gz \
    file://rename-yy_scan_string-manually.patch \
    file://manipulate-fds-instead-of-FILE.patch;striplevel=2 \
    file://0001-Linux-add-support-for-X32-ABI-compilation.patch \
    "
SRC_URI[md5sum] = "48ef4314fb4ffdd0c96f14dcf20544e1"
SRC_URI[sha256sum] = "b2d81e84107ac9a02be86ea43cbea7afa8fd4b4150270bc88c2d4c9fea0b8aad"
UPSTREAM_CHECK_URI = "https://acpica.org/downloads"

S = "${WORKDIR}/acpica-unix2-${PV}"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "acpixtract"

EXTRA_OEMAKE = "CC='${CC}' 'OPT_CFLAGS=-Wall'"

do_install() {
    install -D -p -m0755 generate/unix/bin*/iasl ${D}${bindir}/iasl
    install -D -p -m0755 generate/unix/bin*/acpibin ${D}${bindir}/acpibin
    install -D -p -m0755 generate/unix/bin*/acpiexec ${D}${bindir}/acpiexec
    install -D -p -m0755 generate/unix/bin*/acpihelp ${D}${bindir}/acpihelp
    install -D -p -m0755 generate/unix/bin*/acpinames ${D}${bindir}/acpinames
    install -D -p -m0755 generate/unix/bin*/acpisrc ${D}${bindir}/acpisrc
    install -D -p -m0755 generate/unix/bin*/acpixtract ${D}${bindir}/acpixtract
}

# iasl*.bb is a subset of this recipe, so RREPLACE it
PROVIDES = "iasl"
RPROVIDES_${PN} += "iasl"
RREPLACES_${PN} += "iasl"
RCONFLICTS_${PN} += "iasl"

BBCLASSEXTEND = "native"
