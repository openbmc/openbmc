# Generates MRW settings override yaml for phosphor-settings-manager
# The default YAML file, mrw-override-settings.yaml, is empty, therefore no
# settings will be overwritten. To override, modify mrw-override-settings.yaml.

SUMMARY = "Generates MRW settings override YAML for phosphor-settings-manager."
PR = "r1"

inherit native
inherit phosphor-settings-manager
inherit mrw-xml

inherit obmc-phosphor-license

DEPENDS += "mrw-native mrw-perl-tools-native"

SRC_URI += "file://mrw-override-settings.yaml"

S = "${WORKDIR}"

MRW_EXPRESSION_VARS ?= ""

do_install() {
    DEST=${D}${config_datadir}
    install -D mrw-override-settings.yaml ${DEST}/mrw-override-settings.yaml

    SETTINGS=${D}${settings_datadir}
    install -d ${SETTINGS}

    # gen_settings.pl replaces any MRW variables with their value
    ${bindir}/perl-native/perl \
        ${bindir}/gen_settings.pl \
        -i ${mrw_datadir}/${MRW_XML} \
        -s ${DEST}/mrw-override-settings.yaml \
        -o ${SETTINGS}/mrw-settings.override.yaml \
        ${MRW_EXPRESSION_VARS} \
        -f
}
