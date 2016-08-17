PACKAGES += "${PN}-bash-completion"

FILES_${PN}-bash-completion = "${datadir}/bash-completion ${sysconfdir}/bash_completion.d"

RDEPENDS_${PN}-bash-completion = "bash-completion"
