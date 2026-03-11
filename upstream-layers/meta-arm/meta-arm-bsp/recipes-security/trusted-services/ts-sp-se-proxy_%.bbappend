require ts-arm-platforms.inc

EXTRA_OECMAKE:append:corstone1000 = " -DMM_COMM_BUFFER_ADDRESS="0x00000000 0x81FFF000" \
    -DMM_COMM_BUFFER_PAGE_COUNT="1" \
    -DSP_HEAP_SIZE=70*1024 \
    "

# Proxy is pointless on fvp-base as there is no dedicated security subsystem. It could be
# deployed configured to have dummy service providers for build testing purposes.
COMPATIBLE_MACHINE:remove:fvp-base = "fvp-base"
