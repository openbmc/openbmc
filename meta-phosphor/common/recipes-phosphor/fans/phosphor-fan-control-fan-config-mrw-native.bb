SUMMARY = "Generate fan control YAML from the MRW"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan-control

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "

PROVIDES += "virtual/phosphor-fan-control-fan-config"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${control_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_fan_zone_yaml.pl \
            -i ${datadir}/obmc-mrw/${MACHINE}.xml \
            -o ${DEST}/fans.yaml
}
