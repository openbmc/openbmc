require connman.inc

SRC_URI  = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
            file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
            file://connman \
            file://no-version-scripts.patch \
            file://includes.patch \
            "
SRC_URI_append_libc-musl = " file://0002-resolve-musl-does-not-implement-res_ninit.patch"

SRC_URI[md5sum] = "c51903fd3e7a6a371d12ac5d72a1fa01"
SRC_URI[sha256sum] = "bc8946036fa70124d663136f9f6b6238d897ca482782df907b07a428b09df5a0"

RRECOMMENDS_${PN} = "connman-conf"
