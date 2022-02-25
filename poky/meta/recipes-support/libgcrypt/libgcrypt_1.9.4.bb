SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
DESCRIPTION = "A cryptography library developed as a separated module of GnuPG. \
It can also be used independently of GnuPG, but depends on its error-reporting \
library Libgpg-error."
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"
SECTION = "libs"

# helper program gcryptrnd and getrandom are under GPL, rest LGPL
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & GPL-3.0-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LICENSE:${PN}-dev = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:dumpsexp-dev = "GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://LICENSES;md5=42fa35a25e138166cc40588387f9159d \
                    "

DEPENDS = "libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgcrypt/libgcrypt-${PV}.tar.bz2 \
           file://0001-libgcrypt-fix-m4-file-for-oe-core.patch \
           file://0003-tests-bench-slope.c-workaround-ICE-failure-on-mips-w.patch \
           file://0002-libgcrypt-fix-building-error-with-O2-in-sysroot-path.patch \
           file://0004-tests-Makefile.am-fix-undefined-reference-to-pthread.patch \
           file://0001-Makefile.am-add-a-missing-space.patch \
           "
SRC_URI[sha256sum] = "ea849c83a72454e3ed4267697e8ca03390aee972ab421e7df69dfe42b65caaf7"

# Below whitelisted CVEs are disputed and not affecting crypto libraries for any distro.
CVE_CHECK_IGNORE += "CVE-2018-12433 CVE-2018-12438"

BINCONFIG = "${bindir}/libgcrypt-config"

inherit autotools texinfo binconfig-disabled pkgconfig

EXTRA_OECONF = "--disable-asm"
EXTRA_OEMAKE:class-target = "LIBTOOLFLAGS='--tag=CC'"

PACKAGECONFIG ??= "capabilities"
PACKAGECONFIG[capabilities] = "--with-capabilities,--without-capabilities,libcap"

do_configure:prepend () {
	# Else this could be used in preference to the one in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
}

# libgcrypt.pc is added locally and thus installed here
do_install:append() {
	install -d ${D}/${libdir}/pkgconfig
	install -m 0644 ${B}/src/libgcrypt.pc ${D}/${libdir}/pkgconfig/
}

PACKAGES =+ "dumpsexp-dev"

FILES:${PN}-dev += "${bindir}/hmac256"
FILES:dumpsexp-dev += "${bindir}/dumpsexp"

BBCLASSEXTEND = "native nativesdk"
