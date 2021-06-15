SUMMARY = "Generate fan presence YAML from the MRW"
PR = "r1"
LICENSE = "Apache-2.0"

inherit allarch
inherit phosphor-fan
inherit mrw-xml

DEPENDS = "mrw-native mrw-perl-tools-native"
PROVIDES += "virtual/phosphor-fan-presence-config"

FILES_${PN} += "${presence_datadir}/config.yaml"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${presence_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_presence_yaml.pl \
            -i ${STAGING_DIR_NATIVE}${mrw_datadir}/${MRW_XML} \
            -o ${DEST}/config.yaml
}
