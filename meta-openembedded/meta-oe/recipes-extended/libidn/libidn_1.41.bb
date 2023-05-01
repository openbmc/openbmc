SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "(LGPL-2.1-or-later | LGPL-3.0-only) & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=f95a3dc99fecfa9a0c4e726d4b5d822f \
                    file://COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYINGv3;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
                    file://lib/idna.h;endline=21;md5=176de4fe1d98e59d743d3b12a850e4cb \
                    file://src/idn.c;endline=20;md5=dd17b9093355bf669e2ea108d2defbd0 \
                   "
                   
DEPENDS = "virtual/libiconv autoconf-archive"

inherit pkgconfig autotools gettext texinfo gtk-doc

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           file://0001-largefile.m4-Sync-with-latest-gnulib.patch \
           "

#SRC_URI[md5sum] = "813c7b268d1051ca02c3610986126f38"
#SRC_URI[sha256sum] = "14b67108344d81ba844631640df77c9071d9fb0659b080326ff5424e86b14038"
SRC_URI[sha256sum] = "884d706364b81abdd17bee9686d8ff2ae7431c5a14651047c68adf8b31fd8945"

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

