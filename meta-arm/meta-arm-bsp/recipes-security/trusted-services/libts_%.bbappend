require ts-arm-platforms.inc

EXTRA_OECMAKE:append:corstone1000 = "-DMM_COMM_BUFFER_ADDRESS=0x81FFF000 \
                                     -DMM_COMM_BUFFER_SIZE=0x1000 \
                                    "

EXTRA_OECMAKE:append:fvp-base = " -DMM_COMM_BUFFER_ADDRESS=0x81000000 \
                                  -DMM_COMM_BUFFER_SIZE=0x8000 \
    "
