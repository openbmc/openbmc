require connman.inc

SRC_URI  = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
            file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
            file://0001-Detect-backtrace-API-availability-before-using-it.patch \
            file://0002-resolve-musl-does-not-implement-res_ninit.patch \
            file://0003-Fix-header-inclusions-for-musl.patch \
            file://0001-iptables-Add-missing-function-item-of-xtables-to-mat.patch \
            file://connman \
            "
SRC_URI[md5sum] = "cb1c413fcc4f49430294bbd7a92f5f3c"
SRC_URI[sha256sum] = "88fcf0b6df334796b90e2fd2e434d6f5b36cd6f13b886a119b8c90276b72b8e2"

RRECOMMENDS_${PN} = "connman-conf"

