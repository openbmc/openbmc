require connman.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
           file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
           file://0001-connman.service-stop-systemd-resolved-when-we-use-co.patch \
           file://connman \
           file://no-version-scripts.patch \
           "

SRC_URI:append:libc-musl = " file://0002-resolve-musl-does-not-implement-res_ninit.patch"

SRC_URI[sha256sum] = "1a57ae7ce234aa3a1744aac3be5c2121d98dce999440ef8ab9cc4edfd5edcb12"

RRECOMMENDS:${PN} = "connman-conf"
RCONFLICTS:${PN} = "networkmanager"
