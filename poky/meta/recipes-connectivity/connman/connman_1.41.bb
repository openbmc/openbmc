require connman.inc

SRC_URI = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
           file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
           file://0001-connman.service-stop-systemd-resolved-when-we-use-co.patch \
           file://connman \
           file://no-version-scripts.patch \
           file://CVE-2022-32293_p1.patch \
           file://CVE-2022-32293_p2.patch \
           file://CVE-2022-32292.patch \
           file://CVE-2023-28488.patch \
           "

SRC_URI:append:libc-musl = " file://0002-resolve-musl-does-not-implement-res_ninit.patch"

SRC_URI[sha256sum] = "79fb40f4fdd5530c45aa8e592fb16ba23d3674f3a98cf10b89a6576f198de589"

RRECOMMENDS:${PN} = "connman-conf"
RCONFLICTS:${PN} = "networkmanager"
