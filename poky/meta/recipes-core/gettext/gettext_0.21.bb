SUMMARY = "Utilities and libraries for producing multi-lingual messages"
DESCRIPTION = "GNU gettext is a set of tools that provides a framework to help other programs produce multi-lingual messages. \
These tools include a set of conventions about how programs should be written to support message catalogs, a directory and file \
naming organization for the message catalogs themselves, a runtime library supporting the retrieval of translated messages, and \
a few stand-alone programs to massage in various ways the sets of translatable and already translated strings."
HOMEPAGE = "http://www.gnu.org/software/gettext/gettext.html"
SECTION = "libs"
LICENSE = "GPLv3+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

DEPENDS = "gettext-native virtual/libiconv"
DEPENDS:class-native = "gettext-minimal-native"
PROVIDES = "virtual/libintl virtual/gettext"
PROVIDES:class-native = "virtual/gettext-native"
RCONFLICTS:${PN} = "proxy-libintl"
SRC_URI = "${GNU_MIRROR}/gettext/gettext-${PV}.tar.gz \
           file://parallel.patch \
           file://use-pkgconfig.patch \
           file://run-ptest \
           file://serial-tests-config.patch \
           file://0001-tests-autopoint-3-unset-MAKEFLAGS.patch \
           file://0001-init-env.in-do-not-add-C-CXX-parameters.patch \
           file://mingw.patch \
           file://0001-msgmerge-29-Add-executable-file-mode-bits.patch \
           "
SRC_URI[sha256sum] = "c77d0da3102aec9c07f43671e60611ebff89a996ef159497ce8e59d075786b12"

inherit autotools texinfo pkgconfig ptest

EXTRA_OECONF += "--without-lispdir \
                 --disable-csharp \
                 --disable-libasprintf \
                 --disable-java \
                 --disable-native-java \
                 --disable-openmp \
                 --disable-acl \
                 --without-emacs \
                 --without-cvs \
                 --without-git \
                 --cache-file=${B}/config.cache \
                "
EXTRA_OECONF:append:class-target = " \
                 --with-bisonlocaledir=${datadir}/locale \
                 gt_cv_locale_fr_utf8=fr_FR \
                 gt_cv_locale_fr=fr_FR.ISO-8859-1 \
                 gt_cv_locale_de_utf8=de_DE \
                 gt_cv_locale_de=de_DE.ISO-8859-1 \
"

PACKAGECONFIG ??= "croco glib libxml"
PACKAGECONFIG:class-native = ""
PACKAGECONFIG:class-nativesdk = ""

PACKAGECONFIG[croco] = "--without-included-libcroco,--with-included-libcroco,libcroco"
PACKAGECONFIG[glib] = "--without-included-glib,--with-included-glib,glib-2.0"
PACKAGECONFIG[libxml] = "--without-included-libxml,--with-included-libxml,libxml2"
# Need paths here to avoid host contamination but this can cause RPATH warnings
# or problems if $libdir isn't $prefix/lib.
PACKAGECONFIG[libunistring] = "--with-libunistring-prefix=${STAGING_LIBDIR}/..,--with-included-libunistring,libunistring"
PACKAGECONFIG[msgcat-curses] = "--with-libncurses-prefix=${STAGING_LIBDIR}/..,--disable-curses,ncurses,"

acpaths = '-I ${S}/gettext-runtime/m4 \
           -I ${S}/gettext-tools/m4'

do_install:append:libc-musl () {
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
FILES:libgettextlib = "${libdir}/libgettextlib-*.so*"
FILES:libgettextsrc = "${libdir}/libgettextsrc-*.so*"

PACKAGES =+ "gettext-runtime gettext-runtime-dev gettext-runtime-doc"

FILES:${PN} += "${libdir}/${BPN}/*"

# The its/Makefile.am has defined:
# itsdir = $(pkgdatadir)$(PACKAGE_SUFFIX)/its
# not itsdir = $(pkgdatadir), so use wildcard to match the version.
FILES:${PN} += "${datadir}/${BPN}-*/*"

FILES:gettext-runtime = "${bindir}/gettext \
                         ${bindir}/ngettext \
                         ${bindir}/envsubst \
                         ${bindir}/gettext.sh \
                         ${libdir}/libasprintf.so* \
                         ${libdir}/GNU.Gettext.dll \
                        "
FILES:gettext-runtime-dev += "${libdir}/libasprintf.a \
                      ${includedir}/autosprintf.h \
                     "
FILES:gettext-runtime-doc = "${mandir}/man1/gettext.* \
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

do_install:append() {
    rm -f ${D}${libdir}/preloadable_libintl.so
}

do_install:append:class-native () {
	rm ${D}${datadir}/aclocal/*
	rm ${D}${datadir}/gettext/config.rpath
	rm ${D}${datadir}/gettext/po/Makefile.in.in
	rm ${D}${datadir}/gettext/po/remove-potcdate.sin

        create_wrapper ${D}${bindir}/msgfmt \
                GETTEXTDATADIR="${STAGING_DATADIR_NATIVE}/gettext-${PV}/"

}

do_compile_ptest() {
        cd ${B}/gettext-tools/tests/
        sed -i '/^buildtest-TESTS: /c buildtest-TESTS: $(TESTS) $(check_PROGRAMS)' Makefile
        oe_runmake buildtest-TESTS
        cd -
}

do_install_ptest() {
    if [ ${PTEST_ENABLED} = "1" ]; then
        mkdir -p                                        ${D}${PTEST_PATH}/tests
        mkdir -p                                        ${D}${PTEST_PATH}/src
        mkdir -p                                        ${D}${PTEST_PATH}/po
        mkdir -p                                        ${D}${PTEST_PATH}/misc
        mkdir -p                                        ${D}${PTEST_PATH}/its
        mkdir -p                                        ${D}${PTEST_PATH}/styles
        mkdir -p                                        ${D}${PTEST_PATH}/gnulib-lib
        mkdir -p                                        ${D}${PTEST_PATH}/examples
        cp -rf ${S}/gettext-tools/its/*                 ${D}${PTEST_PATH}/its
        cp -rf ${S}/gettext-tools/styles/*              ${D}${PTEST_PATH}/styles
        cp -rf ${S}/gettext-tools/gnulib-lib/gettext.h  ${D}${PTEST_PATH}/gnulib-lib
        cp -rf ${S}/gettext-tools/examples/hello-c      ${D}${PTEST_PATH}/examples
        cp -rf ${S}/gettext-tools/tests/*               ${D}${PTEST_PATH}/tests
        cp -rf ${B}/gettext-tools/tests/.libs/*         ${D}${PTEST_PATH}/tests
        cp -rf ${B}/gettext-runtime/intl/.libs/libgnuintl.so.8*         ${D}${libdir}/
        cp -rf ${B}/gettext-tools/tests/Makefile        ${D}${PTEST_PATH}/tests
        cp -rf ${B}/gettext-tools/tests/init-env        ${D}${PTEST_PATH}/tests
        sed -i '/^Makefile:/c Makefile:'                ${D}${PTEST_PATH}/tests/Makefile
        sed -i -e 's:lang-c lang-c++:lang-c++:g'        ${D}${PTEST_PATH}/tests/Makefile
        install ${S}/gettext-tools/src/msgunfmt.tcl     ${D}${PTEST_PATH}/src
        install ${S}/gettext-tools/src/project-id       ${D}${PTEST_PATH}/src
        install ${B}/gettext-runtime/src/gettext.sh     ${D}${PTEST_PATH}/src
        install ${B}/gettext-runtime/src/ngettext       ${D}${PTEST_PATH}/src
        install ${B}/gettext-runtime/src/envsubst       ${D}${PTEST_PATH}/src
        install ${B}/gettext-runtime/src/gettext        ${D}${PTEST_PATH}/src
        install ${B}/gettext-tools/src/.libs/cldr-plurals   ${D}${PTEST_PATH}/src
        install ${S}/gettext-tools/po/gettext-tools.pot ${D}${PTEST_PATH}/po
        install ${B}/gettext-tools/misc/*       ${D}${PTEST_PATH}/misc
        find ${D}${PTEST_PATH}/ -name "*.o" -exec rm {} \;
        chmod 0755 ${D}${PTEST_PATH}/tests/lang-vala ${D}${PTEST_PATH}/tests/plural-1 ${D}${PTEST_PATH}/tests/xgettext-tcl-4 \
                   ${D}${PTEST_PATH}/tests/xgettext-vala-1  ${D}${PTEST_PATH}/tests/xgettext-po-2
        sed -i -e 's|${DEBUG_PREFIX_MAP}||g' ${D}${PTEST_PATH}/tests/init-env
    fi
}

RDEPENDS:${PN}-ptest += "make xz"
RDEPENDS:${PN}-ptest:append:libc-glibc = "\
    glibc-gconv-big5 \
    glibc-charmap-big5 \
    glibc-gconv-cp1251 \
    glibc-charmap-cp1251 \
    glibc-charmap-iso-8859-9 \
    glibc-gconv-iso8859-9 \
    glibc-charmap-koi8-r \
    glibc-gconv-koi8-r \
    glibc-gconv-iso8859-2 \
    glibc-charmap-iso-8859-2 \
    glibc-gconv-iso8859-1 \
    glibc-charmap-iso-8859-1 \
    glibc-gconv-euc-kr \
    glibc-charmap-euc-kr \
    glibc-gconv-euc-jp \
    glibc-charmap-euc-jp \
    locale-base-de-de \
    locale-base-fr-fr \
"

RRECOMMENDS:${PN}-ptest:append:libc-glibc = "\
    locale-base-de-de.iso-8859-1 \
    locale-base-fr-fr.iso-8859-1 \
"

INSANE_SKIP:${PN}-ptest += "ldflags"
INSANE_SKIP:${PN}-ptest += "rpaths"

BBCLASSEXTEND = "native nativesdk"
