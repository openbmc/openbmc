SUMMARY = "scdoc is a simple man page generator for POSIX systems written in C99."
HOMEPAGE = "https://git.sr.ht/~sircmpwn/scdoc"
SECTION = "base/doc"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=31752b4a8edd3fb9ddba1cb90fada74d"

DEPENDS = "scdoc-native"

SRC_URI = "git://git.sr.ht/~sircmpwn/scdoc;protocol=https;branch=master"
SRCREV = "70de0e5e5d3bc40134ee65c35f1631656199f4be"

do_install() {
	oe_runmake 'DESTDIR=${D}' install
}

EXTRA_OEMAKE = "PREFIX=${prefix}"
EXTRA_OEMAKE:append:class-target = " HOST_SCDOC=${STAGING_BINDIR_NATIVE}/scdoc"

BBCLASSEXTEND = "native"
