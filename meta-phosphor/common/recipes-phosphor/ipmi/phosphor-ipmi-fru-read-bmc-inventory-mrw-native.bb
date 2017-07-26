SUMMARY = "Generate BMC accessible FRU's inventory map from a MRW."
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

#TODO: openbmc/openbmc#2105
#Remove config.yaml and its directory during auto generation
#using gen_ipmi_fru.pl script
SRC_URI += "file://bmc-fru-config.yaml"

DEPENDS += "virtual/phosphor-ipmi-fru-read-bmc-config"
PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-inventory"
S = "${WORKDIR}"

do_install() {
    #TODO: openbmc/openbmc#2105 
    #At present FRU ID'S are not defined for BMC accessible FRU's
    #in the witherspoon.xml file. Using handcoded config file.
    #Enable below changes and remove existing changes after
    #getting FRU details in witherspoon.xml file
    #DEST=${D}${config_datadir}
    #install -d ${DEST}
    #${bindir}/perl-native/perl \
    #${bindir}/gen_ipmi_fru.pl \
    #    -i ${datadir}/obmc-mrw/${MACHINE}.xml \
    #    -m ${bmcfw_datadir}/config.yaml   \
    #    -o ${DEST}/bmc-fru-config.yaml
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install bmc-fru-config.yaml ${DEST}
}

