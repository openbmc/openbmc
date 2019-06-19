FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://machine-id-commit-sync.conf \
    file://random-seed-sync.conf \
"

do_install_append () {
    for i in machine-id-commit random-seed; do
        install -d ${D}/${systemd_system_unitdir}/systemd-$i.service.d
        install -m 0644 ${WORKDIR}/$i-sync.conf ${D}/${systemd_system_unitdir}/systemd-$i.service.d
    done
}
