FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", " features/apparmor/apparmor.scc", "" ,d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "smack", " features/smack/smack.scc", "" ,d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("IMAGE_CLASSES", "dm-verity-img", " features/device-mapper/dm-verity.scc", "" ,d)}"
KERNEL_FEATURES:append = " features/ecryptfs/ecryptfs.scc"
SRC_URI += " ${@bb.utils.contains("DISTRO_FEATURES", "lkrg", "file://lkrg.scc", "" ,d)}"
