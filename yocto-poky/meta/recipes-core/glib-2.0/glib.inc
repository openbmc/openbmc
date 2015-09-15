SUMMARY = "A general-purpose utility library"
DESCRIPTION = "GLib is a general-purpose utility library, which provides many useful data types, macros, type conversions, string utilities, file utilities, a main loop abstraction, and so on."
HOMEPAGE = "https://developer.gnome.org/glib/"

# pcre is under BSD;
# docs/reference/COPYING is with a 'public domai'-like license!
LICENSE = "LGPLv2+ & BSD & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://glib/glib.h;beginline=4;endline=17;md5=b4f0f4a399c19e5ebb20c31b79d6bc32 \
                    file://gmodule/COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gmodule/gmodule.h;beginline=4;endline=17;md5=b4f0f4a399c19e5ebb20c31b79d6bc32 \
                    file://glib/pcre/COPYING;md5=266ebc3ff74ee9ce6fad65577667c0f4 \
                    file://glib/pcre/pcre.h;beginline=11;endline=35;md5=de27f2bf633d20a2b7af0b1983423283 \
                    file://docs/reference/COPYING;md5=f51a5100c17af6bae00735cd791e1fcc"
BUGTRACKER = "http://bugzilla.gnome.org"
SECTION = "libs"

BBCLASSEXTEND = "native nativesdk"

DEPENDS = "glib-2.0-native virtual/libiconv libffi zlib"
DEPENDS_append_class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', ' dbus', '', d)}"
DEPENDS_class-native = "pkgconfig-native gettext-native libffi-native zlib-native"
DEPENDS_class-nativesdk = "nativesdk-libtool nativesdk-gettext nativesdk-libffi nativesdk-zlib ${BPN}-native"

PACKAGES =+ "${PN}-utils ${PN}-bash-completion ${PN}-codegen"

LEAD_SONAME = "libglib-2.0.*"
FILES_${PN}-utils = "${bindir}/* ${datadir}/glib-2.0/gettext"

inherit autotools gettext gtk-doc pkgconfig ptest-gnome

S = "${WORKDIR}/glib-${PV}"

CORECONF = "--disable-dtrace --disable-fam --disable-libelf --disable-systemtap --disable-man"

PRINTF = "--enable-included-printf=no"
PRINTF_darwin = "--enable-included-printf=yes"
PRINTF_mingw32 = "--enable-included-printf=yes"
EXTRA_OECONF = "${PRINTF} ${CORECONF}"
EXTRA_OECONF_class-native = "${CORECONF} --disable-selinux"
EXTRA_OECONF_append_libc-uclibc = " --with-libiconv=gnu"

do_configure_prepend() {
	sed -i -e '1s,#!.*,#!${USRBINPATH}/env python,' ${S}/gio/gdbus-2.0/codegen/gdbus-codegen.in
}

FILES_${PN} = "${libdir}/lib*${SOLIBS} ${libdir}/gio ${datadir}/glib-2.0/schemas \
               ${datadir}/glib-2.0/gettext/mkinstalldirs ${datadir}/glib-2.0/gettext/po/Makefile.in.in"
FILES_${PN}-dev += "${libdir}/glib-2.0/include \
                    ${libdir}/gio/modules/lib*${SOLIBSDEV} \
                    ${libdir}/gio/modules/*.la"
FILES_${PN}-dbg += "${datadir}/glib-2.0/gdb ${datadir}/gdb \
                    ${libdir}/gio/modules/.debug \
                    ${libdir}/glib-2.0/installed-tests/glib/.debug"
FILES_${PN}-codegen = "${datadir}/glib-2.0/codegen/*.py"
FILES_${PN}-bash-completion = "${sysconfdir}/bash_completion.d \
                               ${datadir}/bash-completion"

ARM_INSTRUCTION_SET = "arm"
USE_NLS = "yes"

do_install_append () {
	if [ -f ${D}${bindir}/gtester-report ]; then
		sed ${D}${bindir}/gtester-report -i -e '1s|^#!.*|#!/usr/bin/env python|'
	fi

	# Remove some unpackaged files
	rm -f ${D}${datadir}/glib-2.0/codegen/*.pyc
	rm -f ${D}${datadir}/glib-2.0/codegen/*.pyo

	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	if [ -f ${D}${bindir}/glib-mkenums ]; then
		sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/glib-mkenums
	fi
}

RDEPENDS_${PN}-ptest += "\
            gnome-desktop-testing \
            tzdata \
            tzdata-americas \
            tzdata-asia \
            tzdata-europe \
            tzdata-posix \
            python-pygobject \
            python-dbus \
            shared-mime-info \
           "

RDEPENDS_${PN}-ptest_append_libc-glibc = "\
            glibc-gconv-utf-16 \
            glibc-charmap-utf-8 \
            glibc-gconv-cp1255 \
            glibc-charmap-cp1255 \
            glibc-gconv-utf-32 \
            glibc-gconv-utf-7 \
            glibc-gconv-euc-jp \
            glibc-gconv-iso8859-1 \
            glibc-gconv-iso8859-15 \
            glibc-charmap-invariant \
            glibc-localedata-translit-cjk-variants \
           "

INSANE_SKIP_${PN}-ptest += "libdir"
