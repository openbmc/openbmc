require vim_${PV}.bb

SUMMARY += " (with tiny features)"

PACKAGECONFIG += "tiny"

do_install() {
    install -d ${D}/${bindir}
    install -m 0755 ${S}/vim ${D}/${bindir}/vim.tiny
}

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_TARGET = "${bindir}/vim.tiny"
