KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", " features/apparmor/apparmor.scc", "" ,d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "smack", " features/smack/smack.scc", "" ,d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "yama", " features/yama/yama.scc", "" ,d)}"
KERNEL_FEATURES_append = " ${@bb.utils.contains("IMAGE_CLASSES", "dm-verity-img", " features/device-mapper/dm-verity.scc", "" ,d)}"
