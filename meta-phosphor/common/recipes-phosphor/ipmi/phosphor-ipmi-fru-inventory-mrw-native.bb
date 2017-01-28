SUMMARY = "Generate inventory map for phosphor-ipmi-fru from an MRW."
PR = "r1"

inherit native
inherit phosphor-ipmi-fru

require phosphor-ipmi-fru.inc

DEPENDS += "mrw-native mrw-perl-tools-native"

# TODO: remove this dependency after the MRW script
# has been updated to not require the hostfw metadata.
DEPENDS += "virtual/phosphor-ipmi-fru-hostfw-config"

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

do_install() {

    DEST=${D}${config_datadir}
    install -d ${DEST}

    # TODO: Uncomment when mrw-tools script is ready.
    #${STAGING_BINDIR_NATIVE}/perl-native/perl \
        #${STAGING_BINDIR_NATIVE}/gen_ipmi_fru.pl \
        #-i ${STAGING_DATADIR_NATIVE}/obmc-mrw/${MACHINE}.xml \
        #-m ${STAGING_DIR_NATIVE}/${hostfw_datadir}/config.yaml \
        #-o ${DEST}/config.yaml
}
