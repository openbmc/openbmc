require ${BPN}.inc

DEPENDS = " \
    ${BPN}-native intltool-native gperf-native \
    glib-2.0 gtk+3 libgdata \
    dbus db virtual/libiconv zlib libsoup-2.4 libical nss libsecret \
"

inherit gsettings gobject-introspection features_check cmake gtk-doc gettext perlnative

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
    file://0001-CMakeLists.txt-Remove-TRY_RUN-for-iconv.patch \
    file://0002-CMakeLists.txt-remove-CHECK_C_SOURCE_RUNS-check.patch \
    file://0003-contact-Replace-the-Novell-sample-contact-with-somet.patch \
    file://0004-call-native-helpers.patch \
    file://iconv-detect.h \
"

LKSTRFTIME = "HAVE_LKSTRFTIME=ON"
LKSTRFTIME_libc-musl = "HAVE_LKSTRFTIME=OFF"

# For arm qemu-arm runs at 100% CPU load and never returns - so disable introspection for now
GI_DATA_ENABLED="False"

EXTRA_OECMAKE = " \
    -DSYSCONF_INSTALL_DIR=${sysconfdir} \
    -DWITH_KRB5=OFF \
    -DENABLE_GOA=OFF \
    -DENABLE_UOA=OFF \
    -DENABLE_GOOGLE_AUTH=OFF \
    -DENABLE_WEATHER=OFF \
    -DENABLE_INTROSPECTION=${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'ON', 'OFF', d)} \
    -D${LKSTRFTIME} \
    -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"

PACKAGECONFIG[canberra] = "-DENABLE_CANBERRA=ON,-DENABLE_CANBERRA=OFF,libcanberra"
PACKAGECONFIG[oauth]    = "-DENABLE_OAUTH2=ON,-DENABLE_OAUTH2=OFF,webkitgtk json-glib"

# BROKEN: due missing pkg-config in openldap eds' cmake finds host-libs when
# searching for openldap-libs
PACKAGECONFIG[openldap] = "-DWITH_OPENLDAP=ON,-DWITH_OPENLDAP=OFF,openldap"

# -ldb needs this on some platforms
LDFLAGS += "-lpthread -lgmodule-2.0 -lgthread-2.0"

do_configure_append () {
    cp ${WORKDIR}/iconv-detect.h ${S}/src

    # fix native perl shebang
    sed -i 's:${STAGING_BINDIR_NATIVE}/perl-native:${bindir}:' ${B}/src/tools/addressbook-export/csv2vcard

    # fix abs path for g-ir-scanner-wrapper
    sed  -i ${B}/build.ninja \
        -e 's: ${bindir}/g-ir-scanner-wrapper: ${STAGING_BINDIR}/g-ir-scanner-wrapper:g'
}

do_compile_prepend() {
    export GIR_EXTRA_LIBS_PATH="${B}/camel/.libs:${B}/libedataserver/.libs"
}

FILES_${PN} =+ " \
    ${datadir}/dbus-1 \
    ${datadir}/evolution-data-server-*/ui/ \
    ${systemd_user_unitdir} \
"

RDEPENDS_${PN} += "perl"
