require ${BPN}.inc

DEPENDS = " \
    ${BPN}-native intltool-native gperf-native \
    glib-2.0 gtk+3 libgdata libxml2 icu \
    dbus db virtual/libiconv zlib libsoup-2.4 libical nss libsecret \
"

inherit pkgconfig gsettings gobject-introspection features_check cmake gtk-doc gettext perlnative vala

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
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
    -DWITH_KRB5=OFF \
    -DENABLE_UOA=OFF \
    -DENABLE_GOOGLE_AUTH=OFF \
    -DENABLE_WEATHER=OFF \
    -DG_IR_COMPILER=${STAGING_BINDIR}/g-ir-compiler-wrapper \
    -DG_IR_SCANNER=${STAGING_BINDIR}/g-ir-scanner-wrapper \
    -DVAPIGEN=${STAGING_BINDIR_NATIVE}/vapigen \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-DENABLE_INTROSPECTION=ON -DENABLE_VALA_BINDINGS=ON', '-DENABLE_INTROSPECTION=OFF', d)} \
    -D${LKSTRFTIME} \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"

PACKAGECONFIG[canberra] = "-DENABLE_CANBERRA=ON,-DENABLE_CANBERRA=OFF,libcanberra"
PACKAGECONFIG[oauth]    = "-DENABLE_OAUTH2=ON,-DENABLE_OAUTH2=OFF,webkitgtk json-glib"
PACKAGECONFIG[goa]    = "-DENABLE_GOA=ON,-DENABLE_GOA=OFF,gnome-online-accounts"

# BROKEN: due missing pkg-config in openldap eds' cmake finds host-libs when
# searching for openldap-libs
PACKAGECONFIG[openldap] = "-DWITH_OPENLDAP=ON,-DWITH_OPENLDAP=OFF,openldap"

# -ldb needs this on some platforms
LDFLAGS += "-lpthread -lgmodule-2.0 -lgthread-2.0"

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
