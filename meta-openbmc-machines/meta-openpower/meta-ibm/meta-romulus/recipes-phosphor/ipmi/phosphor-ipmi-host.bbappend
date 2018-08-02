FILESEXTRAPATHS_append_romulus := ":${THISDIR}/${PN}"
SRC_URI_append_romulus = " \
    file://channel.yaml \
    "

EXTRA_OECONF_append_romulus = " \
    CHANNEL_YAML_GEN=${WORKDIR}/channel.yaml \
    "
