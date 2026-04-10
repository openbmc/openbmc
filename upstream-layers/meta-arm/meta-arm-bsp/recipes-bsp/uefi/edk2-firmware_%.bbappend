# Include machine specific configurations for UEFI EDK2

MACHINE_EDK2_REQUIRE ?= ""

MACHINE_EDK2_REQUIRE:fvp-base = "edk2-firmware-fvp-base.inc"
MACHINE_EDK2_REQUIRE:juno = "edk2-firmware-juno.inc"
MACHINE_EDK2_REQUIRE:rdn2 = "edk2-firmware-rdn2.inc"
MACHINE_EDK2_REQUIRE:rdv2 = "edk2-firmware-rdv2.inc"
MACHINE_EDK2_REQUIRE:sbsa-ref = "edk2-firmware-sbsa-ref.inc"

require ${MACHINE_EDK2_REQUIRE}
