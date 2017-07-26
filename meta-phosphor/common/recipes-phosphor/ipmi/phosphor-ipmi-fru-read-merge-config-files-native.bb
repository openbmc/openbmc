SUMMARY = "To merge the host and non host config files generated from MRW."
DESCRIPTION = "Merge host and non host config files generated by \
gen-ipmi-fru.pl into a config file. The generated cpp file should \
have the map of both host and non host FRU's "
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

require phosphor-ipmi-host.inc

DEPENDS += "virtual/phosphor-ipmi-fru-read-nonhost-config"
DEPENDS += "virtual/phosphor-ipmi-fru-hostfw-config"
PROVIDES += "virtual/phosphor-ipmi-fru-read-merge-config-files"

S = "${WORKDIR}/git"

do_install_append() {
  DEST=${config_datadir}
  cat ${DEST}/nonhost-config.yaml >> ${DEST}/config.yaml
}
