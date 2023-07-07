SRC_URI:remove = "${SRC_URI_TRUSTED_FIRMWARE_A};name=tfa;branch=${SRCBRANCH}"
SRC_URI:append = "git://github.com/Nuvoton-Israel/arm-trusted-firmware.git;protocol=https;name=tfa;branch=nuvoton"
SRCREV_tfa = "009fd73ba58ac744bf9168e425afa19357ea331d"

