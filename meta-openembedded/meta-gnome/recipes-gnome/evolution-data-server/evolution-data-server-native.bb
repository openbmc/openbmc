require ${BPN}.inc

inherit_defer native

DEPENDS = "glib-2.0-native"

# build native helpers
do_configure[noexec] = "1"

do_compile() {
    cd ${S}/src/camel
    sed -i 's:#include "evolution-data-server-config.h"::g' camel-gen-tables.c
    ${CC} -o ${B}/camel-gen-tables camel-gen-tables.c ${CFLAGS} ${LDFLAGS}

    LDFLAGS_glib=`pkg-config glib-2.0 --libs`
    CFLAGS_glib=`pkg-config glib-2.0 --cflags`
    cd ${S}/src/addressbook/libebook-contacts
    sed -i 's:#include "evolution-data-server-config.h"::g' gen-western-table.c
    ${CC} -o ${B}/gen-western-table gen-western-table.c ${CFLAGS} ${CFLAGS_glib} ${LDFLAGS} ${LDFLAGS_glib}
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${B}/* ${D}${bindir}
}
