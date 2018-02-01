require mariadb.inc

EXTRA_OECMAKE += "-DSTACK_DIRECTION=-1"

DEPENDS += "mariadb-native ncurses zlib readline libaio libevent"

PROVIDES += "mysql5 libmysqlclient"

RPROVIDES_${PN} += "mysql5"
RREPLACES_${PN} += "mysql5"
RCONFLICTS_${PN} += "mysql5"

RPROVIDES_${PN}-dbg += "mysql5-dbg"
RREPLACES_${PN}-dbg += "mysql5-dbg"
RCONFLICTS_${PN}-dbg += "mysql5-dbg"

RPROVIDES_${PN}-leftovers += "mysql5-leftovers"
RREPLACES_${PN}-leftovers += "mysql5-leftovers"
RCONFLICTS_${PN}-leftovers += "mysql5-leftovers"

RPROVIDES_${PN}-client += "mysql5-client"
RREPLACES_${PN}-client += "mysql5-client"
RCONFLICTS_${PN}-client += "mysql5-client"

RPROVIDES_${PN}-server += "mysql5-server"
RREPLACES_${PN}-server += "mysql5-server"
RCONFLICTS_${PN}-server += "mysql5-server"
