SUMMARY = "Utilities and libraries for producing multi-lingual messages"
DESCRIPTION = "GNU gettext is a set of tools that provides a framework to help other programs produce multi-lingual messages. \
These tools include a set of conventions about how programs should be written to support message catalogs, a directory and file \
naming organization for the message catalogs themselves, a runtime library supporting the retrieval of translated messages, and \
a few stand-alone programs to massage in various ways the sets of translatable and already translated strings."
HOMEPAGE = "http://www.gnu.org/software/gettext/gettext.html"
SECTION = "libs"
LICENSE = "GPLv3+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "gettext-native virtual/libiconv expat"
DEPENDS_class-native = "gettext-minimal-native"
PROVIDES = "virtual/libintl virtual/gettext"
PROVIDES_class-native = "virtual/gettext-native"
RCONFLICTS_${PN} = "proxy-libintl"
SRC_URI = "${GNU_MIRROR}/gettext/gettext-${PV}.tar.gz \
	   file://parallel.patch \
	   file://add-with-bisonlocaledir.patch \
	   file://cr-statement.c-timsort.h-fix-formatting-issues.patch \
"

SRC_URI[md5sum] = "97e034cf8ce5ba73a28ff6c3c0638092"
SRC_URI[sha256sum] = "ff942af0e438ced4a8b0ea4b0b6e0d6d657157c5e2364de57baa279c1c125c43"

PACKAGECONFIG[msgcat-curses] = "--with-libncurses-prefix=${STAGING_LIBDIR}/..,--disable-curses,ncurses,"

inherit autotools texinfo

EXTRA_OECONF += "--without-lispdir \
                 --disable-csharp \
                 --disable-libasprintf \
                 --disable-java \
                 --disable-native-java \
                 --disable-openmp \
                 --disable-acl \
                 --with-included-glib \
                 --without-emacs \
                 --without-cvs \
                 --without-git \
                 --with-included-libxml \
                 --with-included-libcroco \
                 --with-included-libunistring \
                "
EXTRA_OECONF_append_class-target = " \
                 --with-bisonlocaledir=${datadir}/locale \
"

acpaths = '-I ${S}/gettext-runtime/m4 \
           -I ${S}/gettext-tools/m4'

do_install_append_libc-musl () {
	rm -f ${D}${libdir}/charset.alias
	rm -f ${D}${includedir}/libintl.h
	rm -f ${D}${libdir}/libintl.la
}

# these lack the .x behind the .so, but shouldn't be in the -dev package
# Otherwise you get the following results:
# 7.4M    glibc/images/ep93xx/Angstrom-console-image-glibc-ipk-2008.1-test-20080104-ep93xx.rootfs.tar.gz
# 25M     uclibc/images/ep93xx/Angstrom-console-image-uclibc-ipk-2008.1-test-20080104-ep93xx.rootfs.tar.gz
# because gettext depends on gettext-dev, which pulls in more -dev packages:
# 15228   KiB /ep93xx/libstdc++-dev_4.2.2-r2_ep93xx.ipk
# 1300    KiB /ep93xx/uclibc-dev_0.9.29-r8_ep93xx.ipk
# 140     KiB /armv4t/gettext-dev_0.14.1-r6_armv4t.ipk
# 4       KiB /ep93xx/libgcc-s-dev_4.2.2-r2_ep93xx.ipk

PACKAGES =+ "libgettextlib libgettextsrc"
FILES_libgettextlib = "${libdir}/libgettextlib-*.so*"
FILES_libgettextsrc = "${libdir}/libgettextsrc-*.so*"

PACKAGES =+ "gettext-runtime gettext-runtime-dev gettext-runtime-doc"

FILES_${PN} += "${libdir}/${BPN}/*"

# The its/Makefile.am has defined:
# itsdir = $(pkgdatadir)$(PACKAGE_SUFFIX)/its
# not itsdir = $(pkgdatadir), so use wildcard to match the version.
FILES_${PN} += "${datadir}/${BPN}-*/*"

FILES_gettext-runtime = "${bindir}/gettext \
                         ${bindir}/ngettext \
                         ${bindir}/envsubst \
                         ${bindir}/gettext.sh \
                         ${libdir}/libasprintf.so* \
                         ${libdir}/GNU.Gettext.dll \
                        "
FILES_gettext-runtime-dev += "${libdir}/libasprintf.a \
                      ${includedir}/autosprintf.h \
                     "
FILES_gettext-runtime-doc = "${mandir}/man1/gettext.* \
                             ${mandir}/man1/ngettext.* \
                             ${mandir}/man1/envsubst.* \
                             ${mandir}/man1/.* \
                             ${mandir}/man3/* \
                             ${docdir}/gettext/gettext.* \
                             ${docdir}/gettext/ngettext.* \
                             ${docdir}/gettext/envsubst.* \
                             ${docdir}/gettext/*.3.html \
                             ${datadir}/gettext/ABOUT-NLS \
                             ${docdir}/gettext/csharpdoc/* \
                             ${docdir}/libasprintf/autosprintf.html \
                             ${infodir}/autosprintf.info \
                            "

do_install_append() {
    rm -f ${D}${libdir}/preloadable_libintl.so
}

do_install_append_class-native () {
	rm ${D}${datadir}/aclocal/*
	rm ${D}${datadir}/gettext/config.rpath
	rm ${D}${datadir}/gettext/po/Makefile.in.in
	rm ${D}${datadir}/gettext/po/remove-potcdate.sin

        create_wrapper ${D}${bindir}/msgfmt \
                GETTEXTDATADIR="${STAGING_DATADIR_NATIVE}/gettext-0.19.8/"

}

BBCLASSEXTEND = "native nativesdk"
