require ts-arm-platforms.inc

EXTRA_OECMAKE:append:corstone1000 = " -DMM_COMM_BUFFER_ADDRESS="0x00000000 0x02000000" \
    -DMM_COMM_BUFFER_PAGE_COUNT="1" \
    "
