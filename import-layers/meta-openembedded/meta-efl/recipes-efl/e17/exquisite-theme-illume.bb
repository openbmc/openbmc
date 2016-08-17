DDESCRIPTION = "A theme for exquisite"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=f523ab5986cc79b52a90d2ac3d5454a2"
SECTION = "x11"
DEPENDS = "edje-native"
PV = "1.0+svnr${SRCPV}"
SRCREV = "${EFL_SRCREV}"

inherit e-base update-alternatives allarch

ALTERNATIVE_NAME = "exquisite-config"
ALTERNATIVE_LINK = "${sysconfdir}/exquisite/config"
ALTERNATIVE_PATH = "${sysconfdir}/exquisite/config-illume"
ALTERNATIVE_PRIORITY = "10"

SRCNAME = "b_and_w"

SRC_URI = "${E_SVN}/trunk/THEMES;module=${SRCNAME};protocol=http \
           file://LICENSE \
"
S = "${WORKDIR}/${SRCNAME}"

do_compile() {
    ${STAGING_BINDIR_NATIVE}/edje_cc exquisite.edc illume.edj
}

do_install() {
    install -d ${D}${sysconfdir}/exquisite
    install -d ${D}${datadir}/exquisite/data/themes
    install -m 0644 ${S}/illume.edj ${D}${datadir}/exquisite/data/themes/
    echo 'THEME="-t illume"' > ${D}${sysconfdir}/exquisite/config-illume
}

CONFFILES_${PN} = "${sysconfdir}/exquisite/config-illume"
FILES_${PN} = "${sysconfdir}/exquisite ${datadir}/exquisite/data/themes"
