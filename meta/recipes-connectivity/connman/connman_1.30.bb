require connman.inc

SRC_URI  = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
            file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
            file://0001-Detect-backtrace-API-availability-before-using-it.patch \
            file://0002-resolve-musl-does-not-implement-res_ninit.patch \
            file://0003-Fix-header-inclusions-for-musl.patch \
            file://connman \
            "
SRC_URI[md5sum] = "4a3efdbd6796922db9c6f66da57887fa"
SRC_URI[sha256sum] = "5c5e464bacc9c27ed4e7269fb9b5059f07947f5be26433b59212133663ffa991"

RRECOMMENDS_${PN} = "connman-conf"

