# Machine specific TFAs

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
EXTRA_OEMAKE:append:corstone1000 = " DEBUG=0"
EXTRA_OEMAKE:append:corstone1000 = " LOG_LEVEL=30"
TFTF_MODE:corstone1000 = "release"

COMPATIBLE_MACHINE:n1sdp = "n1sdp"
EXTRA_OEMAKE:append:n1sdp = " DEBUG=1"
EXTRA_OEMAKE:append:n1sdp = " LOG_LEVEL=50"
TFTF_MODE:n1sdp = "debug"
SRC_URI:append:n1sdp = " \
			file://0001-n1sdp-tftf-tests-to-skip.patch \
			"
