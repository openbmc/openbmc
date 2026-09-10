SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "GPL-3.0-or-later AND (LGPL-2.1-or-later OR LGPL-3.0-only)"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423\
                    file://COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://lib/idna.h;endline=28;md5=125a92599dac82ffe99800efc5cc03bc \
                    file://src/idn.c;endline=19;md5=c6ef5ce9b4a04f1e2ac007e42bc86575 \
                   "

DEPENDS = "virtual/libiconv autoconf-archive"

inherit pkgconfig autotools gettext texinfo gtk-doc

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           "

SRC_URI[sha256sum] = "499608bab3a65650a0ea52888c13a8deebe3f71408e319acd9ec52e02eb13959"

# command tool is under GPLv3+, while libidn itself is under LGPLv2.1+ or LGPLv3
# so package command into a separate package
PACKAGES =+ "idn"
FILES:idn = "${bindir}/*"

LICENSE:${PN} = "LGPL-2.1-or-later OR LGPL-3.0-only"
LICENSE:idn = "GPL-3.0-or-later"

EXTRA_OECONF = "--disable-csharp"

do_install:append() {
	rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"
