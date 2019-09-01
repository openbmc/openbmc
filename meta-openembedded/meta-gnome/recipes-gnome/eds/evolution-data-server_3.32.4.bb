SUMMARY = "Evolution database backend server"
HOMEPAGE = "http://www.gnome.org/projects/evolution/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6e689d19255cf0557f3fe7d7068212 \
                    file://src/camel/camel.h;endline=24;md5=342fc5e9357254bc30c24e43ae47d9a1 \
                    file://src/libedataserver/e-data-server-util.h;endline=20;md5=8f21a9c80ea82a4fb80b5f959f672543 \
                    file://src/calendar/libecal/e-cal.h;endline=24;md5=e699ec3866f73f129f7a4ffffdcfc196"

DEPENDS = " \
    intltool-native gperf-native glib-2.0-native \
    glib-2.0 gtk+3 libgnome-keyring libgdata libcanberra icu \
    dbus db virtual/libiconv zlib libsoup-2.4 libical nss libsecret \
"

inherit gnomebase cmake gtk-doc gettext gobject-introspection perlnative upstream-version-is-even vala

SRC_URI += "file://0001-CMakeLists.txt-Remove-TRY_RUN-for-iconv.patch \
            file://0002-CMakeLists.txt-remove-CHECK_C_SOURCE_RUNS-check.patch \
            file://0003-contact-Replace-the-Novell-sample-contact-with-somet.patch \
            file://0004-Add-native-suffix-to-exacutables-produced-and-run-du.patch \
            file://0005-Use-LC_MESSAGES-for-address-localization-when-LC_ADD.patch \
            file://0006-Dont-add-usr-lib-to-LDFLAGS-when-linking-libphonenum.patch \
            file://0007-Modify-gobject-intrispection-support-to-work-with-OE.patch \
            file://iconv-detect.h \
           "
SRC_URI[archive.md5sum] = "57820f3f88fc554e1a58665a52e12c05"
SRC_URI[archive.sha256sum] = "83f67cb4b680e892b22b51bcde64c788b7ac63e92a99de401fb347e3794f4c7f"

LKSTRFTIME = "HAVE_LKSTRFTIME=ON"
LKSTRFTIME_libc-musl = "HAVE_LKSTRFTIME=OFF"

EXTRA_OECMAKE = " \
    -D${LKSTRFTIME} \
    -DSYSCONF_INSTALL_DIR=${sysconfdir} \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"

PACKAGECONFIG ??= "${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'introspection', '', d)}"
PACKAGECONFIG[openldap] = "-DWITH_OPENLDAP=ON,-DWITH_OPENLDAP=OFF,openldap"
PACKAGECONFIG[oauth2] = "-DENABLE_OAUTH2=ON,-DENABLE_OAUTH2=OFF,json-glib webkitgtk"
PACKAGECONFIG[mitkrb5] = "-DWITH_KRB5=ON,-DWITH_KRB5=OFF,krb5"
PACKAGECONFIG[goa] = "-DENABLE_GOA=ON,-DENABLE_GOA=OFF,gnome-online-accounts"
PACKAGECONFIG[weather] = "-DENABLE_WEATHER=ON,-DENABLE_WEATHER=OFF,libgweather"
PACKAGECONFIG[phonenumber] = "-DWITH_PHONENUMBER=ON,-DWITH_PHONENUMBER=OFF,libphonenumber"
PACKAGECONFIG[introspection] = "-DENABLE_INTROSPECTION=ON,-DENABLE_INTROSPECTION=OFF"
PACKAGECONFIG[vala] = "-DENABLE_VALA_BINDINGS=ON -DVAPIGEN=${STAGING_BINDIR_NATIVE}/vapigen,-DENABLE_VALA_BINDINGS=OFF"

# -ldb needs this on some platforms
LDFLAGS += "-lpthread -lgmodule-2.0 -lgthread-2.0"

do_configure_append () {
    cp ${WORKDIR}/iconv-detect.h ${S}/src

    # fix native perl shebang
    sed -i 's:${STAGING_BINDIR_NATIVE}/perl-native:${bindir}:' ${B}/src/tools/addressbook-export/csv2vcard
}

do_compile_prepend() {
    # CMake does not support building native binaries when cross compiling. As result
    # it always cross compiles them for the target and then aborts when they fail to run.
    # To work around this manually build required tools and patch cmake targets to use
    # those native binaries we built here.
    ${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} -I${B} ${S}/src/camel/camel-gen-tables.c \
        -o ${B}/src/camel/camel-gen-tables-native
    ${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} -I${B} $(pkg-config-native --cflags glib-2.0) \
        ${S}/src/addressbook/libebook-contacts/gen-western-table.c \
        -o ${B}/src/addressbook/libebook-contacts/gen-western-table-native \
        $(pkg-config-native --libs glib-2.0)
}


PACKAGES =+ "libebook-contacts libebook-contacts-dev \
             libcamel libcamel-dev libebook libebook-dev libecal libecal-dev \
             libedata-book libedata-book-dev libedata-cal libedata-cal-dev \
             libedataserver libedataserver-dev \
             libedataserverui libedataserverui-dev"

FILES_${PN} =+ "${systemd_user_unitdir} \
                ${datadir}/dbus-1 \
                ${datadir}/evolution-data-server-*/ui/"
RDEPENDS_${PN} += "perl"

FILES_${PN}-dev =+ "${libdir}/pkgconfig/evolution-data-server-*.pc"
FILES_${PN}-dbg =+ "${libdir}/evolution-data-server*/camel-providers/.debug \
                    ${libdir}/evolution-data-server*/calendar-backends/.debug \
                    ${libdir}/evolution-data-server*/addressbook-backends/.debug \
                    ${libdir}/evolution-data-server*/extensions/.debug/"

RRECOMMENDS_${PN}-dev += "libecal-dev libebook-dev"

FILES_libcamel = "${libdir}/libcamel-*.so.* \
                  ${libdir}/libcamel-provider-*.so.* \
                  ${libdir}/girepository-*/Camel-*.typelib \
                  ${libdir}/evolution-data-server*/camel-providers/*.so \
                  ${libdir}/evolution-data-server*/camel-providers/*.urls"
FILES_libcamel-dev = "${libdir}/libcamel-*.so ${libdir}/libcamel-provider-*.so \
                      ${libdir}/pkgconfig/camel*pc \
                      ${includedir}/evolution-data-server*/camel \
                      ${datadir}/vala/vapi/camel-* \
                      ${datadir}/gir-*/Camel-*.gir"

FILES_libebook = "${libdir}/libebook-*.so.* \
                  ${libdir}/girepository-*/EBook-*.typelib"
FILES_libebook-dev = "${libdir}/libebook-*.so \
                      ${libdir}/pkgconfig/libebook-*.pc \
                      ${datadir}/gir-*/EBook-*.gir \
                      ${datadir}/vala/vapi/libebook-*.* \
                      ${includedir}/evolution-data-server*/libebook/*.h"
RRECOMMENDS_libebook = "libedata-book"

FILES_libebook-contacts = "${libdir}/libebook-contacts-*.so.* \
                           ${libdir}/girepository-*/EBookContacts-*.typelib"
FILES_libebook-contacts-dev = "${libdir}/libebook-contacts-*.so \
                               ${libdir}/pkgconfig/libebook-contacts-*.pc \
                               ${datadir}/gir-*/EBookContacts-*.gir \
                               ${datadir}/vala/vapi/libebook-contacts-* \
                               ${includedir}/evolution-data-server*/libebook-contacts/*.h"

FILES_libecal = "${libdir}/libecal-*.so.*"
FILES_libecal-dev = "${libdir}/libecal-*.so ${libdir}/pkgconfig/libecal-*.pc \
                     ${includedir}/evolution-data-server*/libecal/*.h \
                     ${includedir}/evolution-data-server*/libical/*.h"
RRECOMMENDS_libecal = "libedata-cal tzdata"

FILES_libedata-book = "${libexecdir}/e-addressbook-factory \
                       ${datadir}/dbus-1/services/*.AddressBook.service \
                       ${libdir}/libedata-book-*.so.* \
                       ${libdir}/evolution-data-server-*/extensions/libebook*.so"
FILES_libedata-book-dev = "${libdir}/libedata-book-*.so \
                           ${libdir}/pkgconfig/libedata-book-*.pc \
                           ${includedir}/evolution-data-server-*/libedata-book"

FILES_libedata-cal = "${libexecdir}/e-calendar-factory \
                      ${datadir}/dbus-1/services/*.Calendar.service \
                      ${libdir}/libedata-cal-*.so.* \
                      ${libdir}/evolution-data-server-*/extensions/libecal*.so"
FILES_libedata-cal-dev = "${libdir}/libedata-cal-*.so \
                          ${libdir}/pkgconfig/libedata-cal-*.pc \
                          ${includedir}/evolution-data-server-*/libedata-cal"

FILES_libedataserver = "${libdir}/libedataserver-*.so.* \
                        ${libdir}/girepository-*/EDataServer-*.typelib"
FILES_libedataserver-dev = "${libdir}/libedataserver-*.so \
                            ${libdir}/pkgconfig/libedataserver-*.pc \
                            ${datadir}/vala/vapi/libedataserver-* \
                            ${includedir}/evolution-data-server-*/libedataserver/*.h"

FILES_libedataserverui = "${libdir}/libedataserverui-*.so.*"
FILES_libedataserverui-dev = "${libdir}/libedataserverui-*.so \
                              ${libdir}/pkgconfig/libedataserverui-*.pc \
                              ${datadir}/gir-*/EDataServerUI-*.gir \
                              ${datadir}/vala/vapi/libedataserverui-* \
                              ${includedir}/evolution-data-server-*/libedataserverui/*.h"
