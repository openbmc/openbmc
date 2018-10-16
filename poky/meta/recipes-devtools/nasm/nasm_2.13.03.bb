SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 \
           file://0001-asmlib-Drop-pure-function-attribute-from-seg_init.patch \
           file://0001-assemble-Check-global-line-limit.patch \
           file://0001-fix-CVE-2018-8882.patch \
           file://0001-Verify-that-we-are-not-reading-past-end-of-a-buffer.patch \
           file://0001-eval-Eliminate-division-by-zero.patch \
           file://0001-preproc-parse_size-Check-for-string-provided.patch \
           "

SRC_URI[md5sum] = "0c581d482f39d5111879ca9601938f74"
SRC_URI[sha256sum] = "63ec86477ad3f0f6292325fd89e1d93aea2e2fd490070863f17d48f7cd387011"

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
