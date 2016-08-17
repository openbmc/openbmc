SUMMARY = "EFL-based weather widget library"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24 \
                    file://COPYING-PLAIN;md5=c910b645eda0005ccec46f75203dc96e"

DEPENDS = "ecore edje"
PV = "0.0.0+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"

inherit efl
SRC_URI = "${E_SVN}/trunk/PROTO;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"

EXTRA_OECONF = "--with-edje-cc=${STAGING_BINDIR_NATIVE}/edje_cc"

RSUGGESTS_${PN} = "elementary-tests"

do_compile_append() {
    sed -i -e s:${STAGING_DIR_TARGET}::g \
           -e s:/${TARGET_SYS}::g \
              eweather.pc
}

PACKAGES += "${PN}-plugins"

FILES_${PN}-themes = "\
    ${datadir} \
"

FILES_${PN}-plugins = "\
    ${libdir}/eweather/plugins/*.so \
"

FILES_${PN}-dbg += "\
    ${libdir}/eweather/plugins/.debug \
"

FILES_${PN}-dev += "\
    ${libdir}/eweather/plugins/*.a \
    ${libdir}/eweather/plugins/*.la \
"

RRECOMMENDS_${PN} = "\
    ${PN}-themes \
    ${PN}-plugins \
"
