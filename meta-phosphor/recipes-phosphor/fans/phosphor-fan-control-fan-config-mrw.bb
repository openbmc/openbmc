SUMMARY = "Generate fan control YAML from the MRW"
PR = "r1"
LICENSE = "Apache-2.0"

inherit allarch
inherit phosphor-fan
inherit mrw-xml

S = "${WORKDIR}"
DEPENDS = "mrw-native mrw-perl-tools-native"
PROVIDES += "virtual/phosphor-fan-control-fan-config"

do_compile() {
    ${bindir}/perl-native/perl \
        ${bindir}/gen_fan_zone_yaml.pl \
        -i ${STAGING_DIR_NATIVE}${mrw_datadir}/${MRW_XML} \
        -o fans.yaml
}

do_install() {
        install -D fans.yaml ${D}${control_datadir}/fans.yaml
}

FILES_${PN} += "${control_datadir}/fans.yaml"
