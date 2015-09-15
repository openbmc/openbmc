require pseudo.inc

SRC_URI = " \
    http://downloads.yoctoproject.org/releases/pseudo/${BPN}-${PV}.tar.bz2 \
    file://fallback-passwd \
    file://fallback-group \
"

SRC_URI[md5sum] = "4cd39502f9bd0e734dee80e08b28a5f1"
SRC_URI[sha256sum] = "9f2caca5f1579a376a509cd81a81156fc208650add9f0af275da9e911f18f291"

PSEUDO_EXTRA_OPTS ?= "--enable-force-async --without-passwd-fallback"

do_install_append_class-native () {
	install -d ${D}${sysconfdir}
	# The fallback files should never be modified
	install -m 444 ${WORKDIR}/fallback-passwd ${D}${sysconfdir}/passwd
	install -m 444 ${WORKDIR}/fallback-group ${D}${sysconfdir}/group
}
