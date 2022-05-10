NUVOTON_FLASH_PCIMBOX1 = "0xF0848000"
NUVOTON_FLASH_PCIMBOX2 = "0xF0868000"
NUVOTON_FLASH_LPC     = "0xC0008000"

#PACKAGECONFIG:append:evb-npcm845 = " nuvoton-lpc static-bmc reboot-update"
PACKAGECONFIG:append:evb-npcm845 = " nuvoton-p2a-mbox static-bmc reboot-update"

IMAGE_PATH = "/run/initramfs/image-bmc"
EXTRA_OECONF:append:evb-npcm845 = " STATIC_HANDLER_STAGED_NAME=${IMAGE_PATH}"
IPMI_FLASH_BMC_ADDRESS:evb-npcm845 = "${NUVOTON_FLASH_PCIMBOX1}"
