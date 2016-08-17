DESCRIPTION = "LCDproc is a client/server suite to drive all kinds of LCD (-like) devices. The client \
shipped with this package can be used to acquire various kinds of system stats."
SUMMARY = "Drivers for character-based LCD displays"
HOMEPAGE = "http://lcdproc.org"
SECTION = "utils"
LICENSE = "GPLv2+"
DEPENDS = "ncurses lirc"

LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760 \
                    file://README;beginline=60;md5=637e042cdd3671ba00e78b58ede45d3b"

SRC_URI = "${SOURCEFORGE_MIRROR}/lcdproc/${BP}.tar.gz"

inherit autotools update-rc.d

LCD_DRIVERS ?= "all"
LCD_DEFAULT_DRIVER ?= "curses"

PACKAGECONFIG ??= "usb"
PACKAGECONFIG[usb] = "--enable-libusb,--disable-libusb,virtual/libusb0"
PACKAGECONFIG[ftdi] = "--enable-libftdi,--disable-libftdi,libftdi"

EXTRA_OECONF = "--enable-drivers=${LCD_DRIVERS}"

do_install () {
    # binaries
    install -D -m 0755 server/LCDd ${D}${sbindir}/LCDd
    install -D -m 0755 clients/lcdproc/lcdproc ${D}${bindir}/lcdproc

    # init scripts
    install -d ${D}${sysconfdir}/init.d
    # so far, not fixed :-( and now even uglier :-((
    cat scripts/init-LCDd.debian | sed -e s'/--oknodo//' -e 's/ -s -f / -s 1 -f 1 /' -e 's/force-reload/force-restart/' -e 's/sleep 1/sleep 4/' > ${D}${sysconfdir}/init.d/lcdd
    chmod 0755 ${D}${sysconfdir}/init.d/lcdd
    install -m 0755 scripts/init-lcdproc.debian ${D}${sysconfdir}/init.d/lcdproc
    sed -i s'/--oknodo//' ${D}${sysconfdir}/init.d/lcdproc

    # configuration files
    install -m 0644 ${S}/LCDd.conf ${D}${sysconfdir}/LCDd.conf
    sed -i 's!^DriverPath=.*!DriverPath=${libdir}/lcdproc/!' ${D}${sysconfdir}/LCDd.conf
    sed -i 's!^Driver=.*!Driver=${LCD_DEFAULT_DRIVER}!' ${D}${sysconfdir}/LCDd.conf
    install -m 0644 ${S}/clients/lcdproc/lcdproc.conf ${D}${sysconfdir}/lcdproc.conf

    # driver library files
    install -d ${D}${libdir}/lcdproc
    for i in server/drivers/*.so; do
        install -m 0644 $i ${D}${libdir}/lcdproc/
    done
}

PACKAGES =+ "lcdd"

RRECOMMENDS_${PN} = "lcdd"

FILES_lcdd = "${sysconfdir}/LCDd.conf \
    ${sbindir}/LCDd \
    ${sysconfdir}/init.d/lcdd"

CONFFILES_lcdd = "${sysconfdir}/LCDd.conf"
CONFFILES_${PN} = "${sysconfdir}/lcdproc.conf"

# Driver packages

# USB / no USB trickery

RCONFLICTS_lcdd-driver-hd47780nousb = "lcdd-driver-hd44780"
RCONFLICTS_lcdd-driver-hd47780 = "lcdd-driver-hd44780nousb"

INITSCRIPT_PACKAGES = "lcdd lcdproc"
INITSCRIPT_NAME_lcdd = "lcdd"
INITSCRIPT_NAME_lcdproc = "lcdproc"
INITSCRIPT_PARAMS_lcdd = "defaults 70 21"
INITSCRIPT_PARAMS_lcdproc = "defaults 71 20"

python populate_packages_prepend() {
    plugindir = d.expand('${libdir}/lcdproc')
    do_split_packages(d, plugindir, '(.*)\.so$', 'lcdd-driver-%s', 'LCDd driver for %s', prepend=True)
}

PACKAGES_DYNAMIC += "^lcdd-driver-.*"

