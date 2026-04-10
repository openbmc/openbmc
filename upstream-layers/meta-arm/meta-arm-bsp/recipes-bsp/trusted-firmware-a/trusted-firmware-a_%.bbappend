# Machine specific TFAs

MACHINE_TFA_REQUIRE ?= ""
MACHINE_TFA_REQUIRE:corstone1000 = "trusted-firmware-a-corstone1000.inc"
MACHINE_TFA_REQUIRE:fvp-base = "trusted-firmware-a-fvp-base.inc"
MACHINE_TFA_REQUIRE:juno = "trusted-firmware-a-juno.inc"
MACHINE_TFA_REQUIRE:rdn2 = "trusted-firmware-a-rdn2.inc"
MACHINE_TFA_REQUIRE:rdv2 = "trusted-firmware-a-rdv2.inc"
MACHINE_TFA_REQUIRE:sbsa-ref = "trusted-firmware-a-sbsa-ref.inc"

require ${MACHINE_TFA_REQUIRE}
