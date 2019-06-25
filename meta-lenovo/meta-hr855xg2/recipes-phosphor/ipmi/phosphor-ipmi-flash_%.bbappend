#"Copyright (c) 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

FILESEXTRAPATHS_prepend_hr855xg2 := "${THISDIR}/${PN}:"
EXTRA_OECONF_append_hr855xg2 = " --enable-static-layout --enable-lpc-bridge --enable-aspeed-lpc --enable-reboot-update MAPPED_ADDRESS=0x98000000"
