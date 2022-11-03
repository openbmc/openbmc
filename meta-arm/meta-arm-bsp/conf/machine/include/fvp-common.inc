# FVP common parameters

#
# Capturing FVP common configurations (Armv8-A Base Platform FVP,
# Armv8-A Foundation Platform and Armv7-A Base Platform FVP).
#

MACHINE_FEATURES = "optee"

IMAGE_FSTYPES += "wic"
WKS_FILE ?= "fvp-base.wks"

SERIAL_CONSOLES = "115200;ttyAMA0"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-yocto"

KERNEL_DEVICETREE = "arm/fvp-base-revc.dtb"

EXTRA_IMAGEDEPENDS += "trusted-firmware-a u-boot"

# As this is a virtual target that will not be used in the real world there is
# no need for real SSH keys.  Disable rng-tools (which takes too long to
# initialise) and install the pre-generated keys.
PACKAGECONFIG:remove:pn-openssh = "rng-tools"
MACHINE_EXTRA_RRECOMMENDS += "ssh-pregen-hostkeys"

TEST_TARGET = "OEFVPTarget"
TEST_TARGET_IP = "127.0.0.1:8022"

FVP_PROVIDER ?= "fvp-base-a-aem-native"
FVP_EXE ?= "FVP_Base_RevC-2xAEMvA"
FVP_CONFIG[bp.ve_sysregs.exit_on_shutdown] ?= "1"
FVP_CONFIG[bp.virtio_net.enabled] ?= "1"
FVP_CONFIG[bp.virtio_net.hostbridge.userNetworking] ?= "1"
# Tell testimage to connect to localhost:8022, and forward that to SSH in the FVP.
FVP_CONFIG[bp.virtio_net.hostbridge.userNetPorts] = "8022=22"
FVP_CONFIG[cache_state_modelled] ?= "0"
FVP_CONFIG[bp.secureflashloader.fname] ?= "${DEPLOY_DIR_IMAGE}/bl1-fvp.bin"
FVP_CONFIG[bp.flashloader0.fname] ?= "${DEPLOY_DIR_IMAGE}/fip-fvp.bin"
FVP_CONFIG[bp.virtioblockdevice.image_path] ?= "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.wic"
# Set the baseline to ARMv8.4, as the default is 8.0.
FVP_CONFIG[cluster0.has_arm_v8-4] = "1"
FVP_CONFIG[cluster1.has_arm_v8-4] = "1"
FVP_CONSOLE ?= "terminal_0"
FVP_DATA ?= "cluster0.cpu0=${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}@0x80080000 \
             cluster0.cpu0=${DEPLOY_DIR_IMAGE}/fvp-base-revc.dtb@0x8fc00000"
FVP_TERMINALS[bp.terminal_0] ?= "Console"
FVP_TERMINALS[bp.terminal_1] ?= ""
FVP_TERMINALS[bp.terminal_2] ?= ""
FVP_TERMINALS[bp.terminal_3] ?= ""
