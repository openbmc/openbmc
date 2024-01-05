require ${BPN}.inc

DEPENDS = " \
    ${BPN}-native gperf-native \
    glib-2.0 gtk+3 gtk4 libxml2 icu \
    dbus db virtual/libiconv zlib libsoup-3.0 libical nss libsecret \
"

inherit pkgconfig gsettings gobject-introspection features_check cmake gtk-doc gettext perlnative vala

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI += " \
    file://0001-cmake-Do-not-export-CC-into-gir-compiler.patch \
    file://0001-CMakeLists.txt-Remove-TRY_RUN-for-iconv.patch \
    file://0002-CMakeLists.txt-remove-CHECK_C_SOURCE_RUNS-check.patch \
    file://0003-contact-Replace-the-Novell-sample-contact-with-somet.patch \
    file://0004-call-native-helpers.patch \
    file://iconv-detect.h \
"

LKSTRFTIME = "HAVE_LKSTRFTIME=ON"
LKSTRFTIME:libc-musl = "HAVE_LKSTRFTIME=OFF"

EXTRA_OECMAKE = " \
    -DSYSCONF_INSTALL_DIR=${sysconfdir} \
    -DVAPIGEN=${STAGING_BINDIR_NATIVE}/vapigen \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_INTROSPECTION=ON -DENABLE_VALA_BINDINGS=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
    -D${LKSTRFTIME} \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"

EXTRA_OECMAKE:append:class-target = " -DG_IR_COMPILER=${STAGING_BINDIR}/g-ir-compiler-wrapper"
EXTRA_OECMAKE:append:class-target = " -DG_IR_SCANNER=${STAGING_BINDIR}/g-ir-scanner-wrapper"

PACKAGECONFIG ?= "oauth"

PACKAGECONFIG[canberra] = "-DENABLE_CANBERRA=ON,-DENABLE_CANBERRA=OFF,libcanberra"
PACKAGECONFIG[oauth]    = "-DENABLE_OAUTH2_WEBKITGTK=ON -DENABLE_OAUTH2_WEBKITGTK4=OFF,-DENABLE_OAUTH2_WEBKITGTK4=OFF -DENABLE_OAUTH2_WEBKITGTK=OFF,webkitgtk3 json-glib"
PACKAGECONFIG[goa]    = "-DENABLE_GOA=ON,-DENABLE_GOA=OFF,gnome-online-accounts"
PACKAGECONFIG[kerberos]    = "-DWITH_KRB5=ON,-DWITH_KRB5=OFF,krb5"
# BROKEN: due missing pkg-config in openldap eds' cmake finds host-libs when
# searching for openldap-libs
PACKAGECONFIG[openldap] = "-DWITH_OPENLDAP=ON,-DWITH_OPENLDAP=OFF,openldap"
PACKAGECONFIG[weather] = "-DENABLE_WEATHER=ON,-DENABLE_WEATHER=OFF,libgweather4"


# -ldb needs this on some platforms
LDFLAGS += "-lpthread -lgmodule-2.0 -lgthread-2.0"

# invokes libraries from build host
GI_DATA_ENABLED:libc-musl="False"

do_configure:append () {
    cp ${WORKDIR}/iconv-detect.h ${S}/src
    # avoid writing perl-native path into csv2vcard shebang
    sed -i "s|@PERL@|${bindir}/perl|" ${S}/src/tools/addressbook-export/csv2vcard.in
}

FILES:${PN} =+ " \
    ${datadir}/dbus-1 \
    ${datadir}/evolution-data-server-*/ui/ \
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "perl"
