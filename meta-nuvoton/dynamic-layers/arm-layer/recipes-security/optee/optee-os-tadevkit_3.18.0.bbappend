SRC_URI:remove:npcm8xx = "git://github.com/OP-TEE/optee_os.git;branch=master;protocol=https"

SRC_URI:remove:npcm8xx = " \
    file://0006-allow-setting-sysroot-for-libgcc-lookup.patch \
   "

SRC_URI:append:npcm8xx = "git://github.com/Nuvoton-Israel/optee_os.git;branch=npcm_3_18;protocol=https"

SRCREV:npcm8xx = "485dc7ac4e4a3f51d86c5b6562e3720a338441c7"
