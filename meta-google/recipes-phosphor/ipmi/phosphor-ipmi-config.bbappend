FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " \
  file://gbmc_bridge_config.json \
  file://gbmc_bridge_access.json \
"

DEPENDS:append:gbmc = " jq-native"

GBMCBR_IPMI_CHANNEL ?= "11"

ENTITY_MAPPING ?= "default"

# Replace a channel in config.json to add gbmcbr reporting
do_install:append:gbmc() {
  config_json=${D}${datadir}/ipmi-providers/channel_config.json
  overlapping="$(jq '."${GBMCBR_IPMI_CHANNEL}" | .is_valid and .name != "gbmcbr"' $config_json)"
  if [ "$overlapping" != "false" ]; then
    echo "gBMC channel config overlaps on ${GBMCBR_IPMI_CHANNEL}" >&2
    cat $config_json
    exit 1
  fi
  jq --slurpfile brcfg ${WORKDIR}/gbmc_bridge_config.json \
    '. + {"${GBMCBR_IPMI_CHANNEL}": $brcfg[0]}' $config_json >${WORKDIR}/tmp
  mv ${WORKDIR}/tmp $config_json

  access_json=${D}${datadir}/ipmi-providers/channel_access.json
  overlapping="$(jq '."${GBMCBR_IPMI_CHANNEL}" | .access_mode and .access_mode != "always_available"' $access_json)"
  if [ "$overlapping" != "false" ]; then
    echo "gBMC channel access overlaps on ${GBMCBR_IPMI_CHANNEL}" >&2
    cat $access_json
    exit 1
  fi
  jq --slurpfile brcfg ${WORKDIR}/gbmc_bridge_access.json \
    '. + {"${GBMCBR_IPMI_CHANNEL}": $brcfg[0]}' $access_json >${WORKDIR}/tmp
  mv ${WORKDIR}/tmp $access_json

  # Set entity-map.json to empty json for gBMC by default.
  # Each system will override it if needed.
  if [[ "${ENTITY_MAPPING}" != "default" ]]; then
    echo "[]" > ${D}${datadir}/ipmi-providers/entity-map.json
  fi
}
