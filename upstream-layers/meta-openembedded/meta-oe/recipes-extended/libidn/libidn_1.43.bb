SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "(LGPL-2.1-or-later | LGPL-3.0-only) & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423\
                    file://COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://lib/idna.h;endline=28;md5=82ce5c462ff87f37c7c0d3a92f8d2f6b \
                    file://src/idn.c;endline=19;md5=21b4efac8525241da4d38dc1c2e3a461 \
                   "

DEPENDS = "virtual/libiconv autoconf-archive"

inherit pkgconfig autotools gettext texinfo gtk-doc

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           "

SRC_URI[sha256sum] = "bdc662c12d041b2539d0e638f3a6e741130cdb33a644ef3496963a443482d164"

# command tool is under GPLv3+, while libidn itself is under LGPLv2.1+ or LGPLv3
# so package command into a separate package
PACKAGES =+ "idn"
FILES:idn = "${bindir}/*"

LICENSE:${PN} = "LGPL-2.1-or-later | LGPL-3.0-only"
LICENSE:idn = "GPL-3.0-or-later"

EXTRA_OECONF = "--disable-csharp"

do_install:append() {
	rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"
