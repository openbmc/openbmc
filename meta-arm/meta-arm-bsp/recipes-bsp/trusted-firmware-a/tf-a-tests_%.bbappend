# Machine specific TFAs

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
SRCREV:corstone1000 = "5f591f67738a1bbe6b262c53d9dad46ed8bbcd67"
EXTRA_OEMAKE:append:corstone1000 = " DEBUG=0"
EXTRA_OEMAKE:append:corstone1000 = " LOG_LEVEL=30"
TFTF_MODE:corstone1000 = "release"

COMPATIBLE_MACHINE:n1sdp = "n1sdp"
