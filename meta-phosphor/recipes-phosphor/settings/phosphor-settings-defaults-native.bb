SUMMARY = "Default settings"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-settings-manager
inherit native

SRC_URI += "file://defaults.yaml"
SRC_URI += "file://host-template.yaml"

PROVIDES += "virtual/phosphor-settings-defaults"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${settings_datadir}
        install -d ${DEST}
        install defaults.yaml ${DEST}

        for i in ${OBMC_HOST_INSTANCES};
        do
                cat host-template.yaml | sed "s/{}/${i}/g" >> ${DEST}/defaults.yaml

        done
}
