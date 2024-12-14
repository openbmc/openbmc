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
                    file://lib/idna.h;endline=28;md5=bff92cd5c90908728225a065d6908289 \
                    file://src/idn.c;endline=19;md5=dc51ec9ffae14c4f96fb7db4a3d96b2d \
                   "

DEPENDS = "virtual/libiconv autoconf-archive"

inherit pkgconfig autotools gettext texinfo gtk-doc

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz \
           file://dont-depend-on-help2man.patch \
           "

SRC_URI[sha256sum] = "d6c199dcd806e4fe279360cb4b08349a0d39560ed548ffd1ccadda8cdecb4723"

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
