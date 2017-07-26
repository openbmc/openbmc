SUMMARY = "Sample non host inventory map for phosphor-ipmi-host"
DESCRIPTION = "This is to copy the meta data file to facilitate in \
parsing of the non host FRU IPMI spec data. No changes required for \
phosphor as the final generated config file is copied using \
phosphor-ipmi-fru-read-nonhost-inventory."

PR = "r1"
inherit native

require phosphor-ipmi-host.inc

PROVIDES += "virtual/phosphor-ipmi-fru-read-nonhost-config"

S = "${WORKDIR}/git"
