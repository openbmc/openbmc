INITSCRIPT_PARAMS = "defaults 15 85"

require samhain.inc

# Let the default Logserver be 127.0.0.1
EXTRA_OECONF += " \
        --with-logserver=${SAMHAIN_SERVER} \
        --with-port=${SAMHAIN_PORT} \
        "

MODE_NAME = "client"
SAMHAIN_MODE = "client"

RDEPENDS:${PN} = "acl zlib attr bash"
RCONFLICTS:${PN} = "samhain-standalone"
