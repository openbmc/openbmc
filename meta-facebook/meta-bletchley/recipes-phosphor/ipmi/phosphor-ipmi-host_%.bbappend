DEPENDS:append:bletchley = " bletchley-yaml-config"

EXTRA_OECONF:bletchley = " \
    FRU_YAML_GEN=${STAGING_DIR_HOST}${datadir}/bletchley-yaml-config/ipmi-fru-read.yaml \
    "

# host watchdog does not support on bletchley
RDEPENDS:${PN}:remove:bletchley = "virtual/obmc-watchdog"
