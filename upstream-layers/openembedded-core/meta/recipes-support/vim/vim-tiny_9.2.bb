require vim.inc

SUMMARY += " (with tiny features)"

PACKAGECONFIG += "tiny"

do_install() {
    install -D -m 0755 ${S}/src/vim ${D}/${bindir}/vim.tiny
}

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_TARGET = "${bindir}/vim.tiny"
