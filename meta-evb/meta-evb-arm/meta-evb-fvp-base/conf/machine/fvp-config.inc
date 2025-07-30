# Armv8-A Base Platform FVP Specific Parameters

FVP_PROVIDER ?= "fvp-base-a-aem-native"
FVP_EXE ?= "FVP_Base_RevC-2xAEMvA"

# Network
FVP_CONFIG[bp.hostbridge.interfaceName] = "enp97s0"
FVP_CONFIG[bp.hostbridge.userNetworking] = "1"
FVP_CONFIG[bp.virtio_net.hostbridge.interfaceName] = "tap1"

# Ethernet controller
FVP_CONFIG[bp.smsc_91c111.enabled] = "1"
FVP_CONFIG[bp.virtio_net.enabled] = "1"

# Disable dc4 device for uart 1-3
FVP_CONFIG[bp.pl011_uart1.enable_dc4] = "0"
FVP_CONFIG[bp.pl011_uart2.enable_dc4] = "0"
FVP_CONFIG[bp.pl011_uart3.enable_dc4] = "0"

# Set the baseline to ARMv8.4, as the default is 8.0.
FVP_CONFIG[cluster0.has_arm_v8-4] = "1"
FVP_CONFIG[cluster1.has_arm_v8-4] = "1"

# FVP Terminal/console outpot settings
FVP_CONFIG[bp.hostbridge.userNetPorts] = "4222=22,4223=443,4224=80"
FVP_CONSOLES[default] = "terminal_0"
FVP_TERMINALS[bp.terminal_0] ?= "Console"
FVP_TERMINALS[bp.terminal_1] ?= ""
FVP_TERMINALS[bp.terminal_2] ?= ""
FVP_TERMINALS[bp.terminal_3] ?= ""
# user can update ports as needed
FVP_CONFIG[bp.terminal_0.start_port] = "5064"
FVP_CONFIG[bp.terminal_1.start_port] = "5065"
FVP_CONFIG[bp.terminal_2.start_port] = "5066"
FVP_CONFIG[bp.terminal_3.start_port] = "5067"

# FIP image settings
# fip images can be inspected with 'fiptool' from TF-A
FVP_CONFIG[bp.secureflashloader.fname] = "bl1-fvp.bin"
FVP_CONFIG[bp.flashloader0.fname] = "fip-fvp.bin"
# enable randomness which is needed for various security related applications
FVP_CONFIG[bp.virtio_rng.enabled] = "1"
FVP_CONFIG[bp.virtio_rng.diagnostics] = "1"
FVP_CONFIG[cache_state_modelled] ?= "0"

# Enable MMC support
FVP_CONFIG[bp.mmc.p_mmc_file] = "obmc-phosphor-image-fvp.wic"
# use pad instead of silently truncate emmc image
FVP_CONFIG[bp.mmc.support_unpadded_images] = "1"
FVP_CONFIG[bp.mmc.diagnostics] = "0x1"
