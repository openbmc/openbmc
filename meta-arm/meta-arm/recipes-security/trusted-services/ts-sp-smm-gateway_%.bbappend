
# Update MM communication buffer address for qemuarm64 machine
EXTRA_OECMAKE:append:qemuarm64-secureboot = "-DMM_COMM_BUFFER_ADDRESS="0x00000000 0x42000000" \
                                             -DMM_COMM_BUFFER_PAGE_COUNT="1" \
"
