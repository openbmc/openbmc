FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " file://etcd-new.conf.sample"

do_install:append() {
    install -m 0644 ${UNPACKDIR}/etcd-new.conf.sample -D -t ${D}${sysconfdir}
}

FILES:${PN}:append = " ${sysconfdir}/etcd-new.conf.sample"
