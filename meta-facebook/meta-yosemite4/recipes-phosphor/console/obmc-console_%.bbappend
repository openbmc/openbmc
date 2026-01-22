FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://plat-80-obmc-console-uart.rules \
    file://99-terminus-usb-serial.rules \
    file://10-host-console-dependency.conf.in \
"

do_install:append() {
        install -d ${D}${base_libdir}/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/plat-80-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/80-obmc-console-uart.rules

        install -d ${D}${sysconfdir}/udev/rules.d/
        install -m 0644 ${UNPACKDIR}/99-terminus-usb-serial.rules ${D}${sysconfdir}/udev/rules.d/99-terminus-usb-serial.rules

        host=1
        for console in ${OBMC_CONSOLE_TTYS}; do
            dstdir=${D}${libdir}/systemd/system/obmc-console@${console}.service.d
            install -d "${dstdir}"

            sed "s/@HOST_ID@/${host}/g" ${UNPACKDIR}/10-host-console-dependency.conf.in > ${dstdir}/10-host-console-dependency@${console}.conf
            chmod 0644 ${dstdir}/10-host-console-dependency@${console}.conf

            wantedby_units="obmc-host-starting@${host}.target \
                            obmc-host-reboot@${host}.target \
                            slot-plug-in@${host}.service"
            for wantedby in ${wantedby_units}; do
                wants_dir="${D}${sysconfdir}/systemd/system/${wantedby}.wants"
                install -d "${wants_dir}"
                ln -sf "${systemd_system_unitdir}/obmc-console@${console}.service" \
                       "${wants_dir}/obmc-console@${console}.service"
            done

            host=$(expr ${host} + 1)
        done
}

FILES:${PN} += "${libdir}/systemd/system/obmc-console@*.service.d/*"
