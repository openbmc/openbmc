require connman.inc

SRC_URI  = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
            file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
            file://0001-connman.service-stop-systemd-resolved-when-we-use-co.patch \
            file://connman \
            file://no-version-scripts.patch \
            file://0001-Fix-various-issues-which-cause-problems-under-musl.patch \
"

SRC_URI_append_libc-musl = " file://0002-resolve-musl-does-not-implement-res_ninit.patch"

SRC_URI[md5sum] = "dae77d9c904d2c223ae849e32079d57e"
SRC_URI[sha256sum] = "c789db41cc443fa41e661217ea321492ad59a004bebcd1aa013f3bc10a6e0074"

RRECOMMENDS_${PN} = "connman-conf"
