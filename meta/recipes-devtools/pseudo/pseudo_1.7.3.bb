require pseudo.inc

SRC_URI = " \
    http://downloads.yoctoproject.org/releases/pseudo/${BPN}-${PV}.tar.bz2 \
    file://fallback-passwd \
    file://fallback-group \
"

SRC_URI[md5sum] = "2bd0a44eadd4713e90ad8c152eea77aa"
SRC_URI[sha256sum] = "e9fc3922f8feb97839b50d14eb1987afdc8f22cdcac93119323cccd5f8444652"

PSEUDO_EXTRA_OPTS ?= "--enable-force-async --without-passwd-fallback"

do_install_append_class-native () {
	install -d ${D}${sysconfdir}
	# The fallback files should never be modified
	install -m 444 ${WORKDIR}/fallback-passwd ${D}${sysconfdir}/passwd
	install -m 444 ${WORKDIR}/fallback-group ${D}${sysconfdir}/group
}
