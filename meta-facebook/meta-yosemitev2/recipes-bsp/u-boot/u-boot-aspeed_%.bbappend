FILESEXTRAPATHS_prepend_yosemitev2 := "${THISDIR}/${PN}:"
SRC_URI_append_yosemitev2 = " file://0002-board-aspeed-Add-Mux-for-yosemitev2.patch	\
                              file://0003-spl-host-debug-console-support.patch	\
"
