# Include machine specific configurations for UEFI EDK2

MACHINE_EDK2_REQUIRE ?= ""

MACHINE_EDK2_REQUIRE:fvp-base = "edk2-firmware-fvp-base.inc"
MACHINE_EDK2_REQUIRE:juno = "edk2-firmware-juno.inc"
MACHINE_EDK2_REQUIRE:sbsa-ref = "edk2-firmware-sbsa-ref.inc"
MACHINE_EDK2_REQUIRE:sgi575 = "edk2-firmware-sgi575.inc"

require ${MACHINE_EDK2_REQUIRE}
