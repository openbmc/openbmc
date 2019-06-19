FILESEXTRAPATHS_append := "${THISDIR}/${BPN}:"

SRC_URI += "file://gdmflexiserver"

do_install_append () {
    install -D -m 0755 ${WORKDIR}/gdmflexiserver ${D}${bindir}/gdmflexiserver
}
