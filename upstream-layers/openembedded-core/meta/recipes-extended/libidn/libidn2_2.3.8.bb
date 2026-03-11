SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "(GPL-2.0-or-later | LGPL-3.0-only) & GPL-3.0-or-later & Unicode-DFS-2016"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://COPYING.LESSERv3;md5=3000208d539ec061b899bce1d9ce9404 \
                    file://COPYINGv2;md5=570a9b3749dd0463a1778803b12a6dce \
                    file://COPYING.unicode;md5=684cf5f7e3fded3546679424528261a9 \
                    file://src/idn2.c;endline=16;md5=c44fb6f201f22da3a19857499b49b15d \
                    file://lib/idn2.h.in;endline=27;md5=f27aee1b44326be3a28961c55319099f \
                    "

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "f557911bf6171621e1f72ff35f5b1825bb35b52ed45325dcdee931e5d3c0787a"

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
