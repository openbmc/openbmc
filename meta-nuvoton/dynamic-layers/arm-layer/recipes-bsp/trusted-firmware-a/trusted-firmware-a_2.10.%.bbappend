SRC_URI:remove = "${SRC_URI_TRUSTED_FIRMWARE_A};name=tfa;branch=${SRCBRANCH}"
SRC_URI:append = "git://github.com/Nuvoton-Israel/arm-trusted-firmware.git;protocol=https;name=tfa;branch=npcm_2_10"
SRCREV_tfa = "bd1389a26c3db06353b24aaaf8276b3d6723a801"
