MACHINEOVERRIDES =. "yosemite4-common:"

require conf/distro/include/phosphor-static-norootfs.inc
require conf/distro/include/pldm.inc
require conf/machine/include/facebook-compute-multihost.inc
require conf/machine/include/obmc-bsp-common.inc
require conf/machine/include/tpm2.inc
require conf/machine/include/facebook-ncsi-nic.inc

FLASH_SIZE = "131072"

OBMC_HOST_INSTANCES = "1 2 3 4 5 6 7 8"
# Primary chassis as Sled plus secondary chassis in N slots.
OBMC_CHASSIS_INSTANCES = "0 ${OBMC_HOST_INSTANCES}"

OBMC_NIC_INSTANCES = "0 1 2 3"

FB_ETH_INTF = "eth0 eth1"

VIRTUAL-RUNTIME_obmc-host-state-manager = "phosphor-state-manager-host"
VIRTUAL-RUNTIME_obmc-chassis-state-manager = "phosphor-state-manager-chassis"

PREFERRED_PROVIDER_virtual/phosphor-led-manager-config-native = "yosemite4-led-manager-config-native"
