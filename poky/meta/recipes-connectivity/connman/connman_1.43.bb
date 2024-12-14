require connman.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
           file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
           file://0001-connman.service-stop-systemd-resolved-when-we-use-co.patch \
           file://connman \
           file://no-version-scripts.patch \
           file://0002-resolve-musl-does-not-implement-res_ninit.patch \
           "


SRC_URI[sha256sum] = "1257cebe327e7900b7e2b84c0fb330aa90815e455898cd2f941f4308ed2be3bc"

RRECOMMENDS:${PN} = "connman-conf"
RCONFLICTS:${PN} = "networkmanager"
