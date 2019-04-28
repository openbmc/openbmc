#"Copyright (c) 2019-present Lenovo
#Licensed under BSD-3, see COPYING.BSD file for details."

FILESEXTRAPATHS_prepend_hr630 := "${THISDIR}/${PN}:"

EXTRA_OECONF_append_hr630 = "--disable-link-local-autoconfiguration"
