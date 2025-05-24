SUMMARY = "scdoc is a simple man page generator for POSIX systems written in C99."
HOMEPAGE = "https://git.sr.ht/~sircmpwn/scdoc"
SECTION = "base/doc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=31752b4a8edd3fb9ddba1cb90fada74d"

DEPENDS = "scdoc-native"

SRC_URI = "git://git.sr.ht/~sircmpwn/scdoc;protocol=https;branch=master \
           file://0001-Makefile-drop-static.patch "
SRCREV = "0528bcb993cac6c412acd3ae2e09539e994c0a59"

S = "${WORKDIR}/git"

do_install() {
	oe_runmake 'DESTDIR=${D}' install
}

EXTRA_OEMAKE = "PREFIX=${prefix}"
EXTRA_OEMAKE:append:class-target = " HOST_SCDOC=${STAGING_BINDIR_NATIVE}/scdoc"

BBCLASSEXTEND = "native"
