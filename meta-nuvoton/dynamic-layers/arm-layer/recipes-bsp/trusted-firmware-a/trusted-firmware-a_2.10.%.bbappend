SRC_URI:remove = "${SRC_URI_TRUSTED_FIRMWARE_A};name=tfa;branch=${SRCBRANCH}"
SRC_URI:append = "git://github.com/Nuvoton-Israel/arm-trusted-firmware.git;protocol=https;name=tfa;branch=npcm_2_10"
SRCREV_tfa = "113ec57b1cd7a2889151d06f0040a573a013fcb0"
