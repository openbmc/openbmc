FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append_microblaze = " file://lock-obj-pub.microblazeel-unknown-linux-gnu.h"

do_configure_append_microblaze () {
	cp ${WORKDIR}/lock-obj-pub.microblazeel-unknown-linux-gnu.h ${S}/src/syscfg/
}

