# Machine specific TFAs

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
EXTRA_OEMAKE:append:corstone1000 = " DEBUG=0"
EXTRA_OEMAKE:append:corstone1000 = " LOG_LEVEL=30"
TFTF_MODE:corstone1000 = "release"
