require connman.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
           file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
           file://0001-connman.service-stop-systemd-resolved-when-we-use-co.patch \
           file://connman \
           file://no-version-scripts.patch \
           file://0001-vpn-Adding-support-for-latest-pppd-2.5.0-release.patch \
           file://0001-src-log.c-Include-libgen.h-for-basename-API.patch \
           file://0002-resolve-musl-does-not-implement-res_ninit.patch \
           "


SRC_URI[sha256sum] = "a3e6bae46fc081ef2e9dae3caa4f7649de892c3de622c20283ac0ca81423c2aa"

RRECOMMENDS:${PN} = "connman-conf"
RCONFLICTS:${PN} = "networkmanager"
