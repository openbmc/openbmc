SUMMARY = "Intel ASL compiler/decompiler"
DESCRIPTION = "This is a cross development C compiler, assembler and linker environment for the production of 8086 executables (Optionally MSDOS COM)"
HOMEPAGE = "http://www.acpica.org/"
LICENSE = "Intel-ACPI"
LIC_FILES_CHKSUM = "file://source/compiler/aslcompiler.h;beginline=7;endline=114;md5=09f82edcd148ab4c8aa554bc3e9d0676"
SECTION = "console/tools"

DEPENDS = "bison-native flex-native"

SRC_URI = "https://www.acpica.org/sites/acpica/files/acpica-unix-${PV}.tar.gz \
           file://iasl.1 \
           file://Make-CC-definition-conditional.patch \
"

SRC_URI[md5sum] = "be677fc358de9dadc036e1ea678a101b"
SRC_URI[sha256sum] = "6b681732624de1eb58b2bcf1c7ef0744ba14ed35fcaa534d4421574782fbb848"

S = "${WORKDIR}/acpica-unix-${PV}/"

BBCLASSEXTEND = "native"
CFLAGS += "-D_LINUX -DACPI_ASL_COMPILER -I../include -I../compiler"

COMPATIBLE_HOST = "(x86_64.*|i.86.*)-linux"

# By setting NOOPT we suppress forcing -O2 and setting _FORTIFY_SOURCE=2.  Let the
# optimization and security cflags set them.
#
do_compile() {
        oe_runmake iasl NOOPT=TRUE NOFORTIFY=TRUE
}

do_install() {
	install -d ${D}${bindir} ${D}${mandir}/man1
	install -m 0755 ${S}/generate/unix/bin/iasl ${D}${bindir}
	install -m 0644 ${WORKDIR}/iasl.1 ${D}${mandir}/man1
}
