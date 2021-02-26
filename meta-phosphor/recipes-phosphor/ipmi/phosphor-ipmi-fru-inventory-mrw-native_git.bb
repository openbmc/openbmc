SUMMARY = "Generate inventory map for phosphor-ipmi-fru from an MRW."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-ipmi-fru
inherit mrw-xml
inherit native

require phosphor-ipmi-fru.inc

DEPENDS += "mrw-native mrw-perl-tools-native"

# TODO: remove this dependency after the MRW script
# has been updated to not require the hostfw metadata.
DEPENDS += "virtual/phosphor-ipmi-fru-hostfw-config"

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

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
