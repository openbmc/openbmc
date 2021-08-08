DEPENDS:append:class-target = " bash-completion"

PACKAGES += "${PN}-bash-completion"

FILES:${PN}-bash-completion = "${datadir}/bash-completion ${sysconfdir}/bash_completion.d"

RDEPENDS:${PN}-bash-completion = "bash-completion"
