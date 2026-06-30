require gbmc-nic-config.bb

FILESEXTRAPATHS:prepend := "${THISDIR}/gbmc-nic-config:"

RPROVIDES:${PN} = "gbmc-nic-config"
RCONFLICTS:${PN} = "gbmc-nic-config"

GBMC_NIC_CONFIG_L2BR = "1"
