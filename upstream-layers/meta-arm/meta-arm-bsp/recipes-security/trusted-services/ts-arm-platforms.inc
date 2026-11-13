MACHINE_TS_REQUIRE ?= ""
MACHINE_TS_REQUIRE:corstone1000 = "ts-arm-platforms-corstone1000.inc"
MACHINE_TS_REQUIRE:fvp-base = "ts-arm-platforms-fvp-base.inc"

require ${MACHINE_TS_REQUIRE}
