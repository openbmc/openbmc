FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

MACHINE_U-BOOT_REQUIRE ?= ""
MACHINE_U-BOOT_REQUIRE:corstone1000 = "u-boot-corstone1000.inc"
MACHINE_U-BOOT_REQUIRE:fvp-base = "u-boot-fvp-base.inc"
MACHINE_U-BOOT_REQUIRE:juno = "u-boot-juno.inc"

require ${MACHINE_U-BOOT_REQUIRE}

