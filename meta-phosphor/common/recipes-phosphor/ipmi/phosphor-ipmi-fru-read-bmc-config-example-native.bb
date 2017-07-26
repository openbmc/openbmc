SUMMARY = "Sample BMC acessible FRU's inventory map for phosphor-ipmi-host"
DESCRIPTION = "This is to copy the meta data file to facilitate in \
parsing of the BMC accessible FRU IPMI spec data. No changes required for \
phosphor as the final generated config file is copied using \
phosphor-ipmi-fru-read-bmc-inventory."

PR = "r1"
inherit native

require phosphor-ipmi-host.inc

PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-config"

S = "${WORKDIR}/git"
