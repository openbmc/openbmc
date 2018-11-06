LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${QUANTABASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native

DEPENDS += "virtual/phosphor-ipmi-fru-inventory"
PROVIDES += "virtual/phosphor-ipmi-fru-merge-config"

# Put the fru_config in the right place with the right name.
# Pull the IPMI FRU YAML config to use it in the IPMI HOST YAML.
do_install_append() {
  IPMI_FRU_SRC=${datadir}/phosphor-ipmi-fru/config
  IPMI_HOST_DEST=${D}${datadir}/phosphor-ipmi-host/config
  install -d ${IPMI_HOST_DEST}
  cat ${IPMI_FRU_SRC}/config.yaml > ${IPMI_HOST_DEST}/fru_config.yaml
}
