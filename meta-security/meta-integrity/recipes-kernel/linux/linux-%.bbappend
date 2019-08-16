KERNEL_FEATURES_append = " ${@bb.utils.contains("DISTRO_FEATURES", "ima", " features/ima/ima.scc", "" ,d)}"

KERNEL_FEATURES_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'modsign', ' features/ima/modsign.scc', '', d)}"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'modsign', 'kernel-modsign', '', d)}
