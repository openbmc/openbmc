FILES:${PN} += "${nonarch_libdir}/sysusers.d"

# Provision mctpd system user and group via systemd-sysusers
do_install:append() {
    install -d ${D}${nonarch_libdir}/sysusers.d
    echo 'u mctpd - "mctpd daemon" / /bin/false' > ${D}${nonarch_libdir}/sysusers.d/mctpd.conf

    sed -i 's/^AmbientCapabilities=CAP_NET_BIND_SERVICE CAP_NET_ADMIN$/AmbientCapabilities=CAP_NET_BIND_SERVICE CAP_NET_ADMIN CAP_NET_RAW/' \
    ${D}${systemd_system_unitdir}/mctpd.service
}
