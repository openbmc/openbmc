DESCRIPTION = "This is a cross development C compiler, assembler and linker environment for the production of 8086 executables (Optionally MSDOS COM)"
HOMEPAGE = "http://www.debath.co.uk/dev86/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"
SECTION = "console/tools"
PR="r0"

SRC_URI="http://v3.sk/~lkundrak/dev86/archive/Dev86src-${PV}.tar.gz"

SRC_URI[md5sum] = "567cf460d132f9d8775dd95f9208e49a"
SRC_URI[sha256sum] = "61817a378c8c8ba65f36c6792d457a305dc4eedae8cdc8b6233bf2bb28e5fe8d"

S = "${WORKDIR}/dev86-${PV}"

BBCLASSEXTEND = "native"
EXTRA_OEMAKE = "VERSION=${PV} PREFIX=${prefix} DIST=${D}"

do_compile() {

	oe_runmake make.fil
	oe_runmake -f make.fil bcc86 as86 ld86

}

do_install() {

	if [ "${prefix}"=="" ] ; then
		export prefix=/usr
	fi

	oe_runmake install-bcc
	ln -s ../lib/bcc/bcc-cpp ${D}${prefix}/bin/bcc-cpp
	ln -s ../lib/bcc/bcc-cc1 ${D}${prefix}/bin/bcc-cc1

}
COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
FILES_${PN} += "${libdir}/bcc"
INSANE_SKIP_${PN} = "already-stripped"
