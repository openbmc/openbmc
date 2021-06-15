#"Copyright (c) 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

DEPENDS_append_hr855xg2 = " hr855xg2-yaml-config"

EXTRA_OECONF_append_hr855xg2 = " \
    YAML_GEN=${STAGING_DIR_HOST}${datadir}/hr855xg2-yaml-config/ipmi-fru-read.yaml \
    PROP_YAML=${STAGING_DIR_HOST}${datadir}/hr855xg2-yaml-config/ipmi-extra-properties.yaml \
    "
