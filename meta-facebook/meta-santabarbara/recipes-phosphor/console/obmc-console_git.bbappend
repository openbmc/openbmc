FILESEXTRAPATHS:prepend := "${THISDIR}/obmc-console:"

inherit obmc-phosphor-systemd

def fb_get_consoles_usb(d):
    usbs = d.getVar('OBMC_USB_INSTANCES', True)
    if not usbs:
        return ""

    consoles = [f"ttyUSBdevice{int(i)}" for i in range(60)]
    return " ".join(consoles)
fb_get_consoles_usb[vardeps] += "OBMC_USB_INSTANCES"

OBMC_CONSOLE_TTYS_USB = "${@fb_get_consoles_usb(d)}"
OBMC_CONSOLE_TTYS_USB[vardeps] += "fb_get_consoles_usb"

SERVER_CONFS_USB = "${@ ' '.join([ f'file://fb-compute-tray-usb/server.{i}.conf' for i in d.getVar('OBMC_CONSOLE_TTYS_USB', True).split() ])}"


OBMC_USB_INSTANCES = "0 1 2 3"

SRC_URI += " \
    file://81-plat-obmc-console-uart.rules \
"
SRC_URI:append = " ${SERVER_CONFS_USB}"

do_install:append() {
    install -d ${D}${base_libdir}/udev/rules.d/
    install -m 0644 ${UNPACKDIR}/81-plat-obmc-console-uart.rules ${D}${base_libdir}/udev/rules.d/81-plat-obmc-console-uart.rules

    for CONSOLE_USB in ${OBMC_CONSOLE_TTYS_USB}
    do
        install -m 0644 ${UNPACKDIR}/fb-compute-tray-usb/server.${CONSOLE_USB}.conf ${D}${sysconfdir}/${BPN}/
    done
}
