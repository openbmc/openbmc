SECTION = "e/apps"

inherit e-base autotools pkgconfig binconfig

do_prepsources () {
    make clean distclean || true
}
addtask prepsources after do_patch before do_configure

do_configure_append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
}

export CURL_CONFIG = "${STAGING_BINDIR_CROSS}/curl-config"
export FREETYPE_CONFIG = "${STAGING_BINDIR_CROSS}/freetype-config"

PACKAGES =+ "${PN}-themes"
PACKAGES += "${PN}-lib"

FILES_${PN}-lib = "${libdir}/lib*.so.*"
FILES_${PN}-themes = "${datadir}/${PN}/themes ${datadir}/${PN}/data ${datadir}/${PN}/fonts ${datadir}/${PN}/pointers ${datadir}/${PN}/images ${datadir}/${PN}/users ${datadir}/${PN}/images ${datadir}/${PN}/styles"
FILES_${PN}-dev += "${includedir} ${libdir}/lib*.so"
