require qemu-xilinx-native.inc
BPN = "qemu-xilinx"

EXTRA_OECONF_append = " --target-list=${@get_qemu_usermode_target_list(d)} --disable-tools --disable-blobs --disable-guest-agent"


SRC_URI_append = "file://0001-linux-user-disable-qemu-bridge-helper-and-socket_scm.patch"
