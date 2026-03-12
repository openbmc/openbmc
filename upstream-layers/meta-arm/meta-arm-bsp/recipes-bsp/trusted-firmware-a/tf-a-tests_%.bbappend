# Machine specific TFAs

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
EXTRA_OEMAKE:append:corstone1000 = " DEBUG=0"
EXTRA_OEMAKE:append:corstone1000 = " LOG_LEVEL=30"

# Add Cortex-A320 specific configurations
EXTRA_OEMAKE:append:cortexa320 = " \
        CORSTONE1000_CORTEX_A320=1 \
        "
TFTF_MODE:corstone1000 = "release"

FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/files/corstone1000/tf-a-tests:"
SRC_URI:append:corstone1000 = " \
        file://0001-plat-corstone1000-Add-Cortex-A320-support.patch \
        "
