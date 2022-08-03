# Machine specific configurations

MACHINE_OPTEE_SPDEVKIT_REQUIRE ?= ""
MACHINE_OPTEE_SPDEVKIT_REQUIRE:corstone1000 = "optee-spdevkit_corstone1000.inc"

require ${MACHINE_OPTEE_SPDEVKIT_REQUIRE}
