DESCRIPTION = "This is a cross development C compiler, assembler and linker environment for the production of 8086 executables (Optionally MSDOS COM)"
HOMEPAGE = "http://www.acpica.org/"
LICENSE = "Intel-ACPI"
LIC_FILES_CHKSUM = "file://asldefine.h;endline=115;md5=d4d7cf809b8b5e03131327b3f718e8f0"
SECTION = "console/tools"
PR="r1"

DEPENDS="flex bison"

SRC_URI="https://acpica.org/sites/acpica/files/acpica-unix-${PV}.tar.gz"

SRC_URI[md5sum] = "324c89e5bb9002e2711e0494290ceacc"
SRC_URI[sha256sum] = "b2b497415f29ddbefe7be8b9429b62c1f1f6e1ec11456928e4e7da86578e5b8d"

S="${WORKDIR}/acpica-unix-${PV}/source/compiler"

NATIVE_INSTALL_WORKS = "1"
BBCLASSEXTEND = "native"

do_compile() {
	CFLAGS="-Wno-error=redundant-decls" $MAKE
}

do_install() {
	mkdir -p ${D}${prefix}/bin
	cp ${S}/iasl ${D}${prefix}/bin
}


