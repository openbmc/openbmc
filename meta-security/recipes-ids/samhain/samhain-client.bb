INITSCRIPT_PARAMS = "defaults 15 85"

require samhain.inc

# Let the default Logserver be 127.0.0.1
EXTRA_OECONF += " \
        --with-logserver=${SAMHAIN_SERVER} \
        --with-port=${SAMHAIN_PORT} \
        "

RDEPENDS_${PN} = "acl zlib attr bash"
RCONFLICTS_${PN} = "samhain-standalone"
