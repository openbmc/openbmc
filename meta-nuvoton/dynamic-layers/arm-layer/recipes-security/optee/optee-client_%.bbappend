FILESEXTRAPATHS:append:npcm8xx := "${THISDIR}/${PN}:"

SRC_URI:append:npcm8xx = " file://tee-supplicant.service"

EXTRA_OECMAKE:append:npcm8xx = " \
    -DCFG_TEE_FS_PARENT_PATH='/var/tee' \
    -DRPMB_EMU=OFF \
    "
