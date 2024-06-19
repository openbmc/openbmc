LICENSE = "CLOSED"
INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://file1 \
           file://file2"

SRC_URI:append:class-native = " file://file3"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

EXCLUDE_FROM_WORLD = "1"
BBCLASSEXTEND = "native"
