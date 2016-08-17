require pseudo.inc

SRC_URI = " \
    http://downloads.yoctoproject.org/releases/pseudo/${BPN}-${PV}.tar.bz2 \
    file://0001-configure-Prune-PIE-flags.patch \
    file://fallback-passwd \
    file://fallback-group \
    file://moreretries.patch \
    file://handle-remove-xattr.patch \
"

SRC_URI[md5sum] = "c10209938f03128d0c193f041ff3596d"
SRC_URI[sha256sum] = "fd89cadec984d3b8202aca465898b1bb4350e0d63ba9aa9ac899f6f50270e688"

PSEUDO_EXTRA_OPTS ?= "--enable-force-async --without-passwd-fallback"

do_install_append_class-native () {
	install -d ${D}${sysconfdir}
	# The fallback files should never be modified
	install -m 444 ${WORKDIR}/fallback-passwd ${D}${sysconfdir}/passwd
	install -m 444 ${WORKDIR}/fallback-group ${D}${sysconfdir}/group
}
