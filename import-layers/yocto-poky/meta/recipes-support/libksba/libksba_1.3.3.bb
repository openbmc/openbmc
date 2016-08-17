SUMMARY = "Easy API to create and parse X.509 and CMS related objects"
HOMEPAGE = "http://www.gnupg.org/related_software/libksba/"
LICENSE = "GPLv2+ | LGPLv3+ | GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd541d83f75d038c4e0617b672ed8bda \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=2f31b266d3440dd7ee50f92cf67d8e6c \
                    file://COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                   "

DEPENDS = "libgpg-error"

BINCONFIG = "${bindir}/ksba-config"

inherit autotools binconfig-disabled pkgconfig texinfo

SRC_URI = "ftp://ftp.gnupg.org/gcrypt/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://ksba-add-pkgconfig-support.patch"

SRC_URI[md5sum] = "a5dd3c57fca254935f5cf8db26e39065"
SRC_URI[sha256sum] = "0c7f5ffe34d0414f6951d9880a46fcc2985c487f7c36369b9f11ad41131c7786"

do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
}
