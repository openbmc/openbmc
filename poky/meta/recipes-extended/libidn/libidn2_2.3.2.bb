SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "(GPL-2.0-or-later | LGPL-3.0-only) & GPL-3.0-or-later & Unicode-DFS-2016"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d834ea7d480438ada04e5d846152395 \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.unicode;md5=684cf5f7e3fded3546679424528261a9 \
                    file://src/idn2.c;endline=16;md5=e4b6d628a84a55f1fd8ae4c76c5f6509 \
                    file://lib/idn2.h.in;endline=27;md5=d0fc8ec628be130a1d5b889107e92477"

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "76940cd4e778e8093579a9d195b25fff5e936e9dc6242068528b437a76764f91"

DEPENDS = "virtual/libiconv libunistring"

inherit pkgconfig autotools gettext texinfo gtk-doc lib_package

EXTRA_OECONF += "--disable-rpath \
                 --with-libunistring-prefix=${STAGING_EXECPREFIXDIR} \
                 "

do_install:append() {
	# Need to remove any duplicate whitespace too for reproducibility
	sed -i -e 's|-L${STAGING_LIBDIR}||' -e 's/  */ /g' ${D}${libdir}/pkgconfig/libidn2.pc
}

LICENSE:${PN} = "(GPL-2.0-or-later | LGPL-3.0-only) & Unicode-DFS-2016"
LICENSE:${PN}-bin = "GPL-3.0-or-later"

BBCLASSEXTEND = "native nativesdk"
