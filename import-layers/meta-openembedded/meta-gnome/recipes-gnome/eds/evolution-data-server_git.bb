SUMMARY = "Evolution database backend server"
HOMEPAGE = "http://www.gnome.org/projects/evolution/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6e689d19255cf0557f3fe7d7068212 \
                    file://camel/camel.h;endline=24;md5=b02175c88f821224746b347a89731a2b \
                    file://libedataserver/e-data-server-util.h;endline=20;md5=934502f03c84523aa059d4825887b380 \
                    file://calendar/libecal/e-cal.h;endline=24;md5=5d496b9b6fd2a4fdbbfc31ef9455c9d0"

DEPENDS = "intltool-native glib-2.0 gtk+3 gconf dbus db gnome-common virtual/libiconv zlib libsoup-2.4 libglade libical libgnome-keyring gperf-native libgdata nss"

SRCREV = "a9e4e74ec4473a4fd09e56b690bd4fa72f686687"

# 3.4 series needs libgdata-0.10*, 3.8 series needs also libsecret instead of gnome-keyring
PV = "3.2.3+git${SRCPV}"

SRC_URI = " \
    git://git.gnome.org/evolution-data-server;branch=gnome-3-2 \
    file://0001-contact-Replace-the-Novell-sample-contact-with-somet.patch \
    file://0002-Fix-for-automake-1.12.x.patch \
    file://0003-Disable-Werror-for-automake.patch \
    file://0004-configure-Fix-libical-pkg-config-trying-to-use-host-.patch \
    file://0005-soup-adapt-to-new-libxml2-API-from-2.9.0.patch \
    file://0006-configure.ac-do-not-overwrite-localedir.patch \
    file://iconv-detect.h \
"

S = "${WORKDIR}/git"

inherit autotools gtk-doc pkgconfig gettext gobject-introspection

# -ldb needs this on some platforms
LDFLAGS += "-lpthread"

# Parallel make shows many issues with this source code.
# Current problems seem to be duplicate execution of the calander/backends
# directories by make resulting in truncated/corrupt .la files
#PARALLEL_MAKE = ""

do_configure_append () {
    cp ${WORKDIR}/iconv-detect.h ${S}
}

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/camel/.libs:${B}/libedataserver/.libs"
}

EXTRA_OECONF = "--without-openldap \
                --with-libdb=${STAGING_DIR_HOST}${prefix} \
                --disable-nntp --disable-goa --disable-weather"

PACKAGES =+ "libcamel libcamel-dev libebook libebook-dev libecal libecal-dev \
             libedata-book libedata-book-dev libedata-cal libedata-cal-dev \
             libedataserver libedataserver-dev \
             libedataserverui libedataserverui-dev"

FILES_${PN} =+ "${datadir}/evolution-data-server-*/ui/"
FILES_${PN}-dev =+ "${libdir}/pkgconfig/evolution-data-server-*.pc"
FILES_${PN}-dbg =+ "${libdir}/evolution-data-server*/camel-providers/.debug \
                    ${libdir}/evolution-data-server*/calendar-backends/.debug \
                    ${libdir}/evolution-data-server*/addressbook-backends/.debug \
                    ${libdir}/evolution-data-server*/extensions/.debug/"

RRECOMMENDS_${PN}-dev += "libecal-dev libebook-dev"

FILES_libcamel = "${libdir}/libcamel-*.so.* \
                  ${libdir}/libcamel-provider-*.so.* \
                  ${libdir}/evolution-data-server*/camel-providers/*.so \
                  ${libdir}/evolution-data-server*/camel-providers/*.urls"
FILES_libcamel-dev = "${libdir}/libcamel-*.so ${libdir}/libcamel-provider-*.so \
                      ${libdir}/pkgconfig/camel*pc \
                      ${libdir}/evolution-data-server*/camel-providers/*.la \
                      ${includedir}/evolution-data-server*/camel"

FILES_libebook = "${libdir}/libebook-*.so.*"
FILES_libebook-dev = "${libdir}/libebook-1.2.so \
                      ${libdir}/pkgconfig/libebook-*.pc \
                      ${includedir}/evolution-data-server*/libebook/*.h"
RRECOMMENDS_libebook = "libedata-book"

FILES_libecal = "${libdir}/libecal-*.so.* \
                 ${datadir}/evolution-data-server-1.4/zoneinfo"
FILES_libecal-dev = "${libdir}/libecal-*.so ${libdir}/pkgconfig/libecal-*.pc \
                     ${includedir}/evolution-data-server*/libecal/*.h \
                     ${includedir}/evolution-data-server*/libical/*.h"
RRECOMMENDS_libecal = "libedata-cal tzdata"

FILES_libedata-book = "${libexecdir}/e-addressbook-factory \
                       ${datadir}/dbus-1/services/*.AddressBook.service \
                       ${libdir}/libedata-book-*.so.* \
                       ${libdir}/evolution-data-server-*/extensions/libebook*.so \
                       ${datadir}/evolution-data-server-1.4/weather/Locations.xml"
FILES_libedata-book-dev = "${libdir}/libedata-book-*.so \
                           ${libdir}/pkgconfig/libedata-book-*.pc \
                           ${libdir}/evolution-data-server-*/extensions/libebook*.la \
                           ${includedir}/evolution-data-server-*/libedata-book"

FILES_libedata-cal = "${libexecdir}/e-calendar-factory \
                      ${datadir}/dbus-1/services/*.Calendar.service \
                      ${libdir}/libedata-cal-*.so.* \
                      ${libdir}/evolution-data-server-*/extensions/libecal*.so"
FILES_libedata-cal-dev = "${libdir}/libedata-cal-*.so \
                          ${libdir}/pkgconfig/libedata-cal-*.pc \
                          ${includedir}/evolution-data-server-*/libedata-cal \
                          ${libdir}/evolution-data-server-*/extensions/libecal*.la"

FILES_libedataserver = "${libdir}/libedataserver-*.so.*"
FILES_libedataserver-dev = "${libdir}/libedataserver-*.so \
                            ${libdir}/pkgconfig/libedataserver-*.pc \
                            ${includedir}/evolution-data-server-*/libedataserver/*.h"

FILES_libedataserverui = "${libdir}/libedataserverui-*.so.* ${datadir}/evolution-data-server-1.4/glade/*.glade"
FILES_libedataserverui-dev = "${libdir}/libedataserverui-*.so \
                              ${libdir}/pkgconfig/libedataserverui-*.pc \
                              ${includedir}/evolution-data-server-*/libedataserverui/*.h"

