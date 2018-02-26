SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"
SECTION = "libs"

# helper program gcryptrnd and getrandom are under GPL, rest LGPL
LICENSE = "GPLv2+ & LGPLv2.1+ & GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-dev = "GPLv2+ & LGPLv2.1+"
LICENSE_dumpsexp-dev = "GPLv3+"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff"

DEPENDS = "libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgcrypt/libgcrypt-${PV}.tar.gz \
           file://0001-Add-and-use-pkg-config-for-libgcrypt-instead-of-conf.patch \
           file://0003-tests-bench-slope.c-workaround-ICE-failure-on-mips-w.patch \
           file://0002-libgcrypt-fix-building-error-with-O2-in-sysroot-path.patch \
           file://0004-tests-Makefile.am-fix-undefined-reference-to-pthread.patch \
           file://0005-ecc-Add-input-validation-for-X25519.patch \
           file://0006-Fix-building-AArch32-CE-implementations-when-target-.patch \
"
SRC_URI[md5sum] = "110ce4352f9ea6f560bdc6c5644ae93c"
SRC_URI[sha256sum] = "f6e470b7f2d3a703e8747f05a8c19d9e10e26ebf2d5f3d71ff75a40f504e12ee"

BINCONFIG = "${bindir}/libgcrypt-config"

inherit autotools texinfo binconfig-disabled pkgconfig

EXTRA_OECONF = "--disable-asm"
EXTRA_OEMAKE_class-target = "LIBTOOLFLAGS='--tag=CC'"

PACKAGECONFIG ??= "capabilities"
PACKAGECONFIG[capabilities] = "--with-capabilities,--without-capabilities,libcap"

do_configure_prepend () {
	# Else this could be used in preference to the one in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
}

# libgcrypt.pc is added locally and thus installed here
do_install_append() {
	install -d ${D}/${libdir}/pkgconfig
	install -m 0644 ${B}/src/libgcrypt.pc ${D}/${libdir}/pkgconfig/
}

PACKAGES =+ "dumpsexp-dev"

FILES_${PN}-dev += "${bindir}/hmac256"
FILES_dumpsexp-dev += "${bindir}/dumpsexp"

BBCLASSEXTEND = "native nativesdk"
