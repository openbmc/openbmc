FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_vesnin = "\
        file://vesnin.cfg \
        file://0001-vesnin-dts-add-mbox-and-lpc_host.patch \
        file://0002-vesnin-remap-aspeed-uart.patch \
"
