SUMMARY     = "Tools and libraries to manage the phal devicetree"
DESCRIPTION = "phal(power hardware abstraction layer) devicetree data \
modelling mainly includes the host hardware topology and attributes, \
which includes the configuration data"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PDATA_DTB_PATH="${datadir}/pdata"
FILES:${PN} += "${PDATA_DTB_PATH}"

do_install() {

    DTB_FILE_ENV=power-target.sh
    DTB_FILE_CONF_PATH=${D}${PDATA_DTB_PATH}

    install -d ${DTB_FILE_CONF_PATH}
    install -m 744 ${THISDIR}/files/${DTB_FILE_ENV} ${DTB_FILE_CONF_PATH}/${DTB_FILE_ENV}
    install -d ${D}${sysconfdir}/profile.d
    ln -s ${PDATA_DTB_PATH}/${DTB_FILE_ENV} ${D}${sysconfdir}/profile.d/${DTB_FILE_ENV}
}
