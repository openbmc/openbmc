require mariadb.inc
inherit native

PROVIDES += "mysql5-native"
DEPENDS = "ncurses-native zlib-native bison-native"

RDEPENDS_${PN} = ""
PACKAGES = ""
EXTRA_OEMAKE = ""

do_install() {
    oe_runmake 'DESTDIR=${D}' install

    install -d ${D}${bindir}
    install -m 0755 sql/gen_lex_hash ${D}${bindir}/
    install -m 0755 sql/gen_lex_token ${D}${bindir}/
    install -m 0755 extra/comp_err ${D}${bindir}/
    install -m 0755 scripts/comp_sql ${D}${bindir}/
}

