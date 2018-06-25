require connman.inc

SRC_URI  = "${KERNELORG_MIRROR}/linux/network/${BPN}/${BP}.tar.xz \
            file://0001-plugin.h-Change-visibility-to-default-for-debug-symb.patch \
            file://0001-connman.service-stop-systemd-resolved-when-we-use-co.patch \
            file://connman \
            file://no-version-scripts.patch \
            file://includes.patch \
            file://0001-session-Keep-track-of-addr-in-fw_snat-session.patch \
            file://0001-giognutls-Fix-a-crash-using-wispr-over-TLS.patch \
            file://0001-inet-Add-prefixlen-to-iproute_default_function.patch \
            file://0002-inet-Implement-subnet-route-creation-deletion-in-ipr.patch \
            file://0003-inet-Implement-APIs-for-creating-and-deleting-subnet.patch \
            file://0004-session-Use-subnet-route-creation-and-deletion-APIs.patch \
            "
SRC_URI_append_libc-musl = " file://0002-resolve-musl-does-not-implement-res_ninit.patch \
                             "

SRC_URI[md5sum] = "bae37b45ee9b3db5ec8115188f8a7652"
SRC_URI[sha256sum] = "66d7deb98371545c6e417239a9b3b3e3201c1529d08eedf40afbc859842cf2aa"

RRECOMMENDS_${PN} = "connman-conf"
