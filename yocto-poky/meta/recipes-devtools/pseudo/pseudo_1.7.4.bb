require pseudo.inc

SRC_URI = " \
    http://downloads.yoctoproject.org/releases/pseudo/${BPN}-${PV}.tar.bz2 \
    file://fallback-passwd \
    file://fallback-group \
"

SRC_URI[md5sum] = "6e4b59a346d08d4a29133c335ea12052"
SRC_URI[sha256sum] = "f33ff84da328f943155f22cfd49030ef4ad85ad35fc2d9419a203521b65c384c"

PSEUDO_EXTRA_OPTS ?= "--enable-force-async --without-passwd-fallback"

do_install_append_class-native () {
	install -d ${D}${sysconfdir}
	# The fallback files should never be modified
	install -m 444 ${WORKDIR}/fallback-passwd ${D}${sysconfdir}/passwd
	install -m 444 ${WORKDIR}/fallback-group ${D}${sysconfdir}/group
}
