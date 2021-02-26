SUMMARY = "Generate inventory map for phosphor-ipmi-host from a MRW."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-ipmi-host
inherit mrw-xml
inherit native

require phosphor-ipmi-host.inc

DEPENDS += "mrw-native mrw-perl-tools-native"

DEPENDS += "virtual/phosphor-ipmi-fru-hostfw-config"
PROVIDES += "virtual/phosphor-ipmi-fru-read-inventory"

S = "${WORKDIR}/git"

do_install() {

    DEST=${D}${config_datadir}
    install -d ${DEST}

    ${bindir}/perl-native/perl \
        ${bindir}/gen_ipmi_fru.pl \
        -i ${mrw_datadir}/${MRW_XML} \
        -m ${hostfw_datadir}/config.yaml \
        -o ${DEST}/config.yaml
}
