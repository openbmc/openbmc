SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"
COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 "

SRC_URI[md5sum] = "d15843c3fb7db39af80571ee27ec6fad"
SRC_URI[sha256sum] = "00b0891c678c065446ca59bcee64719d0096d54d6886e6e472aeee2e170ae324"

inherit autotools-brokensep

do_configure_prepend () {
	if [ -f ${S}/aclocal.m4 ] && [ ! -f ${S}/acinclude.m4 ]; then
		mv ${S}/aclocal.m4 ${S}/acinclude.m4
	fi
}

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${mandir}/man1

	oe_runmake 'INSTALLROOT=${D}' install
}

BBCLASSEXTEND = "native"

DEPENDS = "groff-native"
