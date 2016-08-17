DESCRIPTION = "EFL based widget set for mobile devices"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a173054a93896e933df53e3382d472c2"
DEPENDS = "eet-native edje-native elementary-native efreet evas ecore edje eet eldbus ethumb poppler eio"
DEPENDS_class-native = "efl-native"

inherit efl gettext

EXTRA_OECONF_append_class-target = "\
    --with-edje-cc=${STAGING_BINDIR_NATIVE}/edje_cc \
    --with-eet-eet=${STAGING_BINDIR_NATIVE}/eet \
    --with-elementary-codegen=${STAGING_BINDIR_NATIVE}/elementary_codegen \
    --with-elm-prefs-cc=${STAGING_BINDIR_NATIVE}/elm_prefs_cc \
    --with-eolian-gen=${STAGING_BINDIR_NATIVE}/eolian_gen \
    --with-eolian-cxx=${STAGING_BINDIR_NATIVE}/eolian_cxx \
"

BBCLASSEXTEND = "native"

PACKAGECONFIG[eweather] = "--enable-eweather,--disable-eweather,libeweather"
PACKAGECONFIG[ewebkit] = "--with-elementary-web-backend=ewebkit,--with-elementary-web-backend=none,webkit-efl"
# be aware when enabling this for efl-native, we don't provide gcc-native so you need to make
# sure that all your builders have g++ which supports -std=gnu++11
PACKAGECONFIG[c++11] = "--enable-cxx-bindings,--disable-cxx-bindings"

do_configure_prepend() {
    # prefix returned eolian paths with STAGING_DIR_HOST
    sed -i 's%^\(DEPS_EOLIAN_FLAGS=`.*\)`$%\1 | sed "s#-I\\${prefix}#-I\\${STAGING_DIR_HOST}\\${prefix}#g"`%g' ${S}/configure.ac
}

do_compile_append_class-target() {
    sed -i -e s:${STAGING_DIR_TARGET}::g \
           -e s:/${TARGET_SYS}::g \
              elementary.pc
}

PACKAGES += "${PN}-configs ${PN}-accessibility"

RDEPENDS_${PN}_class-native = ""

RDEPENDS_${PN} = "\
    ${PN}-themes \
    ${PN}-configs \
"
RSUGGESTS_${PN} = "${PN}-tests ${PN}-accessibility"

FILES_${PN} += "\
    ${libdir}/edje/modules/elm \
    ${libdir}/elementary/modules/datetime_input_ctxpopup/*/module.so \
    ${libdir}/elementary/modules/prefs/*/module.so \
    ${libdir}/elementary/modules/prefs/*/*.edj \
"

FILES_${PN}-dev += "\
    ${bindir}/elm_prefs_cc \
    ${libdir}/elementary/modules/*/*/module.la \
    ${libdir}/cmake \
"

FILES_${PN}-themes = "\
    ${datadir}/elementary/themes \
"

FILES_${PN}-configs = "\
    ${datadir}/elementary/config \
"

FILES_${PN}-dbg += "\
    ${libdir}/elementary/modules/*/*/.debug \
    ${libdir}/edje/modules/elm/*/.debug \
"

FILES_${PN}-tests = "\
    ${bindir}/elementary* \
    ${datadir}/elementary/images \
    ${datadir}/elementary/objects \
    ${datadir}/elementary/examples \
    ${datadir}/applications/* \
    ${datadir}/icons/* \
    ${libdir}/elementary/modules/test_entry/* \
    ${libdir}/elementary/modules/test_map/* \
"

FILES_${PN}-accessibility = "\
    ${libdir}/elementary/modules/access_output/* \
"

# Some upgrade path tweaking, as in evas
AUTO_LIBNAME_PKGS = ""
