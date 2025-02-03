DEPENDS:append:p10bmc = " pdbg ipl"
EXTRA_OEMESON:append:p10bmc = " -Dmax-cpus=4 -Dwith-host-communication-protocol=pldm -Dpower10-support=enabled -Dread-occ-sensors=enabled"
EXTRA_OEMESON:append:witherspoon = " -Dmax-cpus=4 -Dwith-host-communication-protocol= -Dpower10-support=disabled -Dread-occ-sensors=disabled"
