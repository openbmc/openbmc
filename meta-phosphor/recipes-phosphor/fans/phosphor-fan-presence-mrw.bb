SUMMARY = "Generate fan presence YAML from the MRW"
LICENSE = "Apache-2.0"
DEPENDS = "mrw-native mrw-perl-tools-native"
PROVIDES += "virtual/phosphor-fan-presence-config"
PR = "r1"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-fan
inherit mrw-xml

do_install() {
        DEST=${D}${presence_datadir}
        install -d ${DEST}
        ${bindir}/perl-native/perl \
            ${bindir}/gen_presence_yaml.pl \
            -i ${STAGING_DIR_NATIVE}${mrw_datadir}/${MRW_XML} \
            -o ${DEST}/config.yaml
}

FILES:${PN} += "${presence_datadir}/config.yaml"
