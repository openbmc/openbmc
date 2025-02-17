FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FILES:${PN}:append = " ${datadir}/swampd"
FILES:${PN}:append = " ${systemd_system_unitdir}/phosphor-pid-control.service.d/*.conf"

do_install:append() {
    override_dir="${D}${systemd_system_unitdir}/phosphor-pid-control.service.d"
    override_file="${override_dir}/yosemite4.conf"
    mkdir -p ${D}${systemd_system_unitdir}/phosphor-pid-control.service.d
    echo "[Unit]" > ${override_file}
    echo "After=" >> ${override_file}
    echo "After=multi-user.target" >> ${override_file}
}

# Temporary workaround to address the fd leak issue in phosphor-pid-control
# which causes a "Too many open files" error (EMFILE) while creating fan zones.
# This error prevents fan control from operating properly.
# By setting the LimitNOFILE value to 65536, we aim to mitigate this issue
# until a permanent solution is implemented.
# This workaround will be removed once the fd leak issue is resolved.
do_install:append() {
    echo "[Service]" >> ${override_file}
    echo "LimitNOFILE=65536" >> ${override_file}
}