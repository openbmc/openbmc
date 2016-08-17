SUMMARY = "A psplash replacement for display"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=14defa372a91118e755133bc8e6aff83"
DEPENDS = "eet evas ecore embryo edje"
PV = "0.0.1+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"
RRECOMMENDS_${PN} = "exquisite-themes"
RCONFLICTS_${PN} = "psplash virtual-psplash"

SRCNAME = "exquisite"

inherit e
SRC_URI = "${E_SVN}/trunk;module=${SRCNAME};protocol=http;scmdata=keep"
S = "${WORKDIR}/${SRCNAME}"

EXTRA_OECONF = "--with-edje-cc=${STAGING_BINDIR_NATIVE}/edje_cc"

SRC_URI += "file://exquisite-init"
SRC_URI += "file://splashfuncs"

inherit update-rc.d

do_install_prepend() {
    install -d ${D}/mnt/.splash/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/exquisite-init ${D}${sysconfdir}/init.d/exquisite
    install -d ${D}${sysconfdir}/default
    install -m 0755 ${WORKDIR}/splashfuncs ${D}${sysconfdir}/default/splashfuncs
    install -d ${D}${bindir}
    ln -s exquisite-write ${D}${bindir}/splash-write
}


do_install_append() {
    rm -rf ${D}${datadir}/exquisite/data/fonts/*
}

INITSCRIPT_NAME = "exquisite"
INITSCRIPT_PARAMS = "start 01 S . stop 20 0 1 6 ."

FILES_${PN} += "/mnt/.splash/"
