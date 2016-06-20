SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "LGPLv2.1+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://lib/idna.h;beginline=6;endline=18;md5=1336e848ca7b8e25767c3c7e8fa38a89 \
                    file://src/idn.c;beginline=6;endline=18;md5=56c89e359652a71cda128d75f0ffdac4"
PR = "r1"

inherit pkgconfig autotools gettext texinfo

SRC_URI = "http://alpha.gnu.org/gnu/libidn/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "040f012a45feb56168853998bb87ad4d"
SRC_URI[sha256sum] = "98910c2ad664bdf4eed2c2fff88e24f8882636ec9d26669366ff03b469c05ae3"

do_configure_prepend() {
	# this version of libidn copies AC_USE_SYSTEM_EXTENSIONS from 
	# autoconf CVS because atm the autoconf it uses is a bit old
	# now with cross autotool, that macro is already there and this
	# local definition causes circular dependency. Actually AC_GNU_SOURCE
	# is identical to AC_USE_SYSTEM_EXTENSIONS. So remove all local
	# references to the latter here.
	sed -i -e "/AC_REQUIRE(\[gl_USE_SYSTEM_EXTENSIONS/d" ${S}/lib/gl/m4/gnulib-comp.m4
	rm -f ${S}/lib/gl/m4/extensions.m4
}

do_install_append() {
	rm -rf ${D}${libdir}/Libidn.dll
	rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"

