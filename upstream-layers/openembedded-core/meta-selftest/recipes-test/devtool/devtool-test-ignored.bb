LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://${BPN}.tar.gz \
           file://${BPN}.patch"

S = "${UNPACKDIR}/${BPN}"

EXCLUDE_FROM_WORLD = "1"
