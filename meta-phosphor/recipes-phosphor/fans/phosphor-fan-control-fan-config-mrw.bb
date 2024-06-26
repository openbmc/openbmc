SUMMARY = "Generate fan control YAML from the MRW"
LICENSE = "Apache-2.0"
DEPENDS = "mrw-native mrw-perl-tools-native"
PROVIDES += "virtual/phosphor-fan-control-fan-config"
PR = "r1"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-fan
inherit mrw-xml

do_compile() {
    ${bindir}/perl-native/perl \
        ${bindir}/gen_fan_zone_yaml.pl \
        -i ${STAGING_DIR_NATIVE}${mrw_datadir}/${MRW_XML} \
        -o fans.yaml
}
do_install() {
        install -D fans.yaml ${D}${control_datadir}/fans.yaml
}

FILES:${PN} += "${control_datadir}/fans.yaml"
