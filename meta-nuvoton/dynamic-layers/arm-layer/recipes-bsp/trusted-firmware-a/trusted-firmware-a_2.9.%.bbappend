SRC_URI:remove = "${SRC_URI_TRUSTED_FIRMWARE_A};name=tfa;branch=${SRCBRANCH}"
SRC_URI:append = "git://github.com/Nuvoton-Israel/arm-trusted-firmware.git;protocol=https;name=tfa;branch=nuvoton"
SRCREV_tfa = "ef86e361efaa8d59cb12bcf2dfc88c9994a8a16e" 
