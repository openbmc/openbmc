SUMMARY = "Generate fan control YAML from the MRW"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-fan
inherit mrw-xml

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "

PROVIDES += "virtual/phosphor-fan-control-fan-config"

S = "${WORKDIR}"

do_compile() {
    ${bindir}/perl-native/perl \
        ${bindir}/gen_fan_zone_yaml.pl \
        -i ${mrw_datadir}/${MRW_XML} \
        -o ${S}/fans.yaml
}

do_install() {
        DEST=${D}${control_datadir}
        install -D ${S}/fans.yaml ${DEST}/fans.yaml
}
