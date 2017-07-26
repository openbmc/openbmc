SUMMARY = "Generate inventory map for non host FRU's from a MRW."
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

#TODO:
#Remove config.yaml and its directory during auto generation
#using gen_ipmi_fru.pl script
SRC_URI += "file://nonhost-config.yaml"

DEPENDS += "mrw-native mrw-perl-tools-native"
DEPENDS += "virtual/phosphor-ipmi-fru-read-nonhost-config"
PROVIDES += "virtual/phosphor-ipmi-fru-read-nonhost-inventory"
S = "${WORKDIR}"

do_install() {
    #TODO:
    #At present FRU ID'S are not defined for non host FRU's
    #in the witherspoon.xml file. Using handcoded config file.
    #Enable below changes and remove existing changes after
    #getting FRU details in witherspoon.xml file
    #DEST=${D}${config_datadir}
    #install -d ${DEST}
    #${bindir}/perl-native/perl \
    #${bindir}/gen_ipmi_fru.pl \
    #    -i ${datadir}/obmc-mrw/${MACHINE}.xml \
    #    -m ${nonhostfw_datadir}/config.yaml   \
    #    -o ${DEST}/non-host-config.yaml
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install nonhost-config.yaml ${DEST}
}

