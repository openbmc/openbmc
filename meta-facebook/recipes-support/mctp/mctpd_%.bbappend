FILES:${PN} += "${nonarch_libdir}/sysusers.d"

# Provision mctpd system user and group via systemd-sysusers
do_install:append() {
    install -d ${D}${nonarch_libdir}/sysusers.d
    echo 'u mctpd - "mctpd daemon" / /bin/false' > ${D}${nonarch_libdir}/sysusers.d/mctpd.conf
}
