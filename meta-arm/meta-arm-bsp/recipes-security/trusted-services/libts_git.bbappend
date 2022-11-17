MACHINE_TS_REQUIRE ?= ""
MACHINE_TS_REQUIRE:corstone1000 = "ts-corstone1000.inc"

require ${MACHINE_TS_REQUIRE}


EXTRA_OECMAKE:append:corstone1000 = "-DMM_COMM_BUFFER_ADDRESS=0x02000000 \
                                    -DMM_COMM_BUFFER_PAGE_COUNT=1 \
                                    "

