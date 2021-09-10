FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " file://gbmc_bridge.json"

DEPENDS:append:gbmc = " jq-native"

GBMCBR_IPMI_CHANNEL ?= "11"

# Replace a channel in config.json to add gbmcbr reporting
do_install:append:gbmc() {
  chjson=${D}${datadir}/ipmi-providers/channel_config.json
  overlapping="$(jq '."${GBMCBR_IPMI_CHANNEL}" | .is_valid and .name != "gbmcbr"' $chjson)"
  if [ "$overlapping" != "false" ]; then
    echo "gBMC channel config overlaps on ${GBMCBR_IPMI_CHANNEL}" >&2
    cat $chjson
    exit 1
  fi
  jq --slurpfile brcfg ${WORKDIR}/gbmc_bridge.json \
    '. + {"${GBMCBR_IPMI_CHANNEL}": $brcfg[0]}' $chjson >${WORKDIR}/tmp
  mv ${WORKDIR}/tmp $chjson
}

