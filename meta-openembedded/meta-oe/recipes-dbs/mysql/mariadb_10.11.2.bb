require mariadb.inc

DEPENDS += "mariadb-native bison-native boost libpcre2 curl ncurses \
            zlib libaio libedit libevent libxml2 gnutls fmt lzo zstd"

PROVIDES += "mysql5 libmysqlclient"

RPROVIDES:${PN} += "mysql5"
RREPLACES:${PN} += "mysql5"
RCONFLICTS:${PN} += "mysql5"

RPROVIDES:${PN}-dbg += "mysql5-dbg"
RREPLACES:${PN}-dbg += "mysql5-dbg"
RCONFLICTS:${PN}-dbg += "mysql5-dbg"

RPROVIDES:${PN}-leftovers += "mysql5-leftovers"
RREPLACES:${PN}-leftovers += "mysql5-leftovers"
RCONFLICTS:${PN}-leftovers += "mysql5-leftovers"

RPROVIDES:${PN}-client += "mysql5-client"
RREPLACES:${PN}-client += "mysql5-client"
RCONFLICTS:${PN}-client += "mysql5-client"

RPROVIDES:${PN}-server += "mysql5-server"
RREPLACES:${PN}-server += "mysql5-server"
RCONFLICTS:${PN}-server += "mysql5-server"
