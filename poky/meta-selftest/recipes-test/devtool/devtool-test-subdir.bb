LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://devtool-test-subdir.tar.gz \
           file://testfile;subdir=${BPN}"

S = "${WORKDIR}/${BPN}"

EXCLUDE_FROM_WORLD = "1"
