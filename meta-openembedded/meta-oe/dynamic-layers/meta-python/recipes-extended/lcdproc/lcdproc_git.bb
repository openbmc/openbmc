DESCRIPTION = "LCDproc is a client/server suite to drive all kinds of LCD (-like) devices. The client \
shipped with this package can be used to acquire various kinds of system stats."
SUMMARY = "Drivers for character-based LCD displays"
HOMEPAGE = "http://lcdproc.org"
SECTION = "utils"
LICENSE = "GPL-2.0-or-later"
DEPENDS = "ncurses lirc"

LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760 \
                    file://README.md;beginline=107;md5=5db392f043253a2d64b1737068ce6b58"

PV = "0.5.9+git${SRCPV}"
SRCREV = "0e2ce9b9c46c47363436f9ee730f7c71bf455f0f"
SRC_URI = "git://github.com/lcdproc/lcdproc;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig update-rc.d

LCD_DRIVERS ?= "all,!irman,!svga${SERIALVFD}"
SERIALVFD ?= ""
SERIALVFD:libc-musl = ",!serialVFD"
SERIALVFD:libc-musl:x86 = ""
SERIALVFD:libc-musl:x86-64 = ""

LCD_DEFAULT_DRIVER ?= "curses"

PACKAGECONFIG ??= "usb"
PACKAGECONFIG[usb] = "--enable-libusb,--disable-libusb,virtual/libusb0"
PACKAGECONFIG[ftdi] = "--enable-libftdi,--disable-libftdi,libftdi"
PACKAGECONFIG[g15] = ",,libg15 g15daemon libg15render,"
PACKAGECONFIG[hid] = "--enable-libhid,--disable-libhid,libhid"
PACKAGECONFIG[png] = "--enable-libpng,--disable-libpng,libpng"

LCD_DRIVERS:append = "${@bb.utils.contains('PACKAGECONFIG', 'g15', '', ',!g15', d)}"

EXTRA_OECONF = "--enable-drivers='${LCD_DRIVERS}'"

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
    # binaries
    install -D -m 0755 clients/lcdvc/lcdvc ${D}${sbindir}/lcdvc

    # configuration files
    install -D -m 0644 ${S}/clients/lcdvc/lcdvc.conf ${D}${sysconfdir}/lcdvc.conf
}

PACKAGES =+ "lcdd lcdvc"

RRECOMMENDS:${PN} = "lcdd"

FILES:lcdd = "${sysconfdir}/LCDd.conf \
    ${sbindir}/LCDd \
    ${sysconfdir}/init.d/lcdd"

CONFFILES:lcdd = "${sysconfdir}/LCDd.conf"
CONFFILES:${PN} = "${sysconfdir}/lcdproc.conf"
CONFFILES:lcdvc = "${sysconfdir}/lcdvc.conf"
FILES:lcdvc = "${sysconfdir}/lcdvc.conf ${sbindir}/lcdvc"

# Driver packages

# USB / no USB trickery

RCONFLICTS:lcdd-driver-hd47780nousb = "lcdd-driver-hd44780"
RCONFLICTS:lcdd-driver-hd47780 = "lcdd-driver-hd44780nousb"

INITSCRIPT_PACKAGES = "lcdd lcdproc"
INITSCRIPT_NAME:lcdd = "lcdd"
INITSCRIPT_NAME:lcdproc = "lcdproc"
INITSCRIPT_PARAMS:lcdd = "defaults 70 21"
INITSCRIPT_PARAMS:lcdproc = "defaults 71 20"

python populate_packages:prepend() {
    plugindir = d.expand('${libdir}/lcdproc')
    do_split_packages(d, plugindir, r'(.*)\.so$', 'lcdd-driver-%s', 'LCDd driver for %s', prepend=True)
}

PACKAGES_DYNAMIC += "^lcdd-driver-.*"
