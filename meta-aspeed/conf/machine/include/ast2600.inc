SOC_FAMILY = "aspeed-g6"

# Enable SPL signing by default
SOCSEC_SIGN_ENABLE ?= "1"
SOCSEC_SIGN_SOC ?= "2600"

include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed.inc
include conf/machine/include/arm/arch-armv7a.inc
