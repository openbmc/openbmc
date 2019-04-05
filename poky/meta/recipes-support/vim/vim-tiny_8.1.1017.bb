require vim_${PV}.bb

SUMMARY += " (with tiny features)"

PROVIDES_remove = "xxd"
ALTERNATIVE_${PN}_remove = "xxd"

PACKAGECONFIG += "tiny"

do_install() {
    install -D -m 0755 ${S}/vim ${D}/${bindir}/vim.tiny
}

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_TARGET = "${bindir}/vim.tiny"
