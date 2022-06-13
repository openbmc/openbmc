BPN = "qemu"

DEPENDS = "glib-2.0-native zlib-native ninja-native meson-native"

require qemu-native.inc

EXTRA_OECONF:append = " --target-list=${@get_qemu_usermode_target_list(d)} --disable-tools --disable-blobs --disable-guest-agent"

PACKAGECONFIG ??= "pie"
