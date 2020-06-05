LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://${BPN}.tar.gz \
           file://0001-I-ll-patch-you-only-if-devtool-lets-me-to-do-it-corr.patch"

S = "${WORKDIR}/${BPN}"

EXCLUDE_FROM_WORLD = "1"
