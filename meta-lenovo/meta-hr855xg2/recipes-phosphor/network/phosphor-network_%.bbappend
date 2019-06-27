#"Copyright (c) 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

FILESEXTRAPATHS_prepend_hr855xg2 := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_hr855xg2 = " --disable-link-local-autoconfiguration"
