require u-boot-common.inc
require u-boot.inc

DEPENDS += "bc-native dtc-native gnutls-native python3-pyelftools-native"

# workarounds for aarch64 kvm qemu boot regressions
SRC_URI:append:qemuarm64 = " file://disable-CONFIG_BLOBLIST.cfg file://disable_CONFIG_USB.cfg"
SRC_URI:append:genericarm64 = " file://disable-CONFIG_BLOBLIST.cfg file://disable_CONFIG_USB.cfg"
