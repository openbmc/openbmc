FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"

# Machine specific TFAs

MACHINE_TFA_REQUIRE ?= ""
MACHINE_TFA_REQUIRE:corstone500 = "trusted-firmware-a-corstone500.inc"
MACHINE_TFA_REQUIRE:corstone1000 = "trusted-firmware-a-corstone1000.inc"
MACHINE_TFA_REQUIRE:fvp-base = "trusted-firmware-a-fvp.inc"
MACHINE_TFA_REQUIRE:fvp-base-arm32 = "trusted-firmware-a-fvp-arm32.inc"
MACHINE_TFA_REQUIRE:juno = "trusted-firmware-a-juno.inc"
MACHINE_TFA_REQUIRE:n1sdp = "trusted-firmware-a-n1sdp.inc"
MACHINE_TFA_REQUIRE:sgi575 = "trusted-firmware-a-sgi575.inc"
MACHINE_TFA_REQUIRE:tc = "trusted-firmware-a-tc.inc"

require ${MACHINE_TFA_REQUIRE}
