FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_vesnin = "\
        file://vesnin.cfg \
        file://0002-vesnin-remap-aspeed-uart.patch \
"
