SUMMARY = "Default settings"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-settings-defaults"
PR = "r1"

SRC_URI += "file://defaults.yaml"
SRC_URI += "file://host-template.yaml"

SETTINGS_HOST_TEMPLATES:append = " host-template.yaml"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-settings-manager
inherit native

do_install() {
        DEST=${D}${settings_datadir}
        install -d ${DEST}
        install defaults.yaml ${DEST}
        for i in ${OBMC_HOST_INSTANCES};
        do
            for f in ${SETTINGS_HOST_TEMPLATES};
            do
                sed "s/{}/${i}/g" ${f} >> ${DEST}/defaults.yaml
            done
        done
        for f in ${SETTINGS_BMC_TEMPLATES};
        do
            cat $f >> ${DEST}/defaults.yaml
        done
}
