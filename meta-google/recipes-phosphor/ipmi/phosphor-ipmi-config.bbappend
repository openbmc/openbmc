FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " \
  file://gbmc_eth_config.json \
  file://gbmc_eth_access.json \
"

DEPENDS:append:gbmc = " jq-native"

GBMCBR_IPMI_CHANNEL ?= "11"
GBMC_NCSI_IPMI_CHANNEL ??= "1"
GBMC_NCSI_IPMI_CHANNEL:npcm8xx ??= "2"
# Only used for extra channels, GBMCBR and NCSI are autopopulated
# Format looks like "<channel>|<intf> <channel2>|<intf2>", Ex. "2|eth0 3|back"
GBMC_IPMI_CHANNEL_MAP ??= ""

ENTITY_MAPPING ?= "default"

gbmc_add_channel() {
  local chan="$1"
  local intf="$2"

  jq --slurpfile ecfg ${UNPACKDIR}/gbmc_eth_config.json --arg CHAN "$chan" --arg INTF "$intf" \
    '. + {$CHAN: ($ecfg[0] + {"name": $INTF})}' $config_json >${UNPACKDIR}/tmp-config.json
  mv ${UNPACKDIR}/tmp-config.json $config_json

  jq --slurpfile ecfg ${UNPACKDIR}/gbmc_eth_access.json --arg CHAN "$chan" \
    '. + {$CHAN: $ecfg[0]}' $access_json >${UNPACKDIR}/tmp-access.json
  mv ${UNPACKDIR}/tmp-access.json $access_json
}

# Replace a channel in config.json to add gbmcbr reporting
do_install:append:gbmc() {
  config_json=${D}${datadir}/ipmi-providers/channel_config.json
  access_json=${D}${datadir}/ipmi-providers/channel_access.json

  overlapping="$(jq '."${GBMCBR_IPMI_CHANNEL}" | .is_valid and .name != "gbmcbr"' $config_json)"
  if [ "$overlapping" != "false" ]; then
    echo "gBMC channel config overlaps on ${GBMCBR_IPMI_CHANNEL}" >&2
    cat $config_json
    exit 1
  fi
  overlapping="$(jq '."${GBMCBR_IPMI_CHANNEL}" | .access_mode and .access_mode != "always_available"' $access_json)"
  if [ "$overlapping" != "false" ]; then
    echo "gBMC channel access overlaps on ${GBMCBR_IPMI_CHANNEL}" >&2
    cat $access_json
    exit 1
  fi
  gbmc_add_channel ${GBMCBR_IPMI_CHANNEL} gbmcbr
  if [ -n "${GBMC_NCSI_IF_NAME}" ]; then
    gbmc_add_channel ${GBMC_NCSI_IPMI_CHANNEL} ${GBMC_NCSI_IF_NAME}
  fi
  map="${GBMC_IPMI_CHANNEL_MAP}"
  # Split the map over the space separated entries
  for entry in $map; do
    OLDIFS="$IFS"
    # Split the entry over the `|` separator
    IFS='|'
    gbmc_add_channel $entry
    IFS="$OLDIFS"
  done
}

python do_gbmc_version () {
  import json

  if d.getVar('GBMC_VERSION') is None:
    return

  version = d.getVar('GBMC_VERSION').split('.')
  major = min(int(version[0]), 0x7F) & 0x7F
  minor = min(int(version[1]), 99)
  minor = int(minor / 10) * 16 + minor % 10;
  point = int(version[2])
  subpoint = int(version[3])

  dir = d.getVar('D') + d.getVar('datadir') + '/ipmi-providers'
  path = os.path.join(dir, 'dev_id.json')

  dev_id = {}

  # Open existing dev_id and override the fields not needed for version.
  with open(path, 'r') as f:
    dev_id = json.load(f)
    dev_id["firmware_revision"] = {
      "major": major,
      "minor": minor
    }
    dev_id["aux"] =  subpoint << 16 | (0xFFFF & point)

    dev_id["manuf_id"] = 11129
    dev_id["prod_id"] = 14426

  with open(path, 'w') as f:
    json.dump(dev_id, f, sort_keys=True, indent=4)
}

addtask gbmc_version after do_install before do_package
