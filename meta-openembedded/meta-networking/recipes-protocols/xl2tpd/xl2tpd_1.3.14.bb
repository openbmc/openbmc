SUMMARY = "Xelerance version of the Layer 2 Tunneling Protocol (L2TP) daemon"
HOMEPAGE = "http://www.xelerance.com/software/xl2tpd/"
SECTION = "net"
DEPENDS = "ppp virtual/kernel"

PACKAGE_ARCH = "${MACHINE_ARCH}"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/xelerance/xl2tpd.git"
SRCREV = "ba619c79c4790c78c033df0abde4a9a5de744a08"

S = "${WORKDIR}/git"

inherit update-rc.d

do_compile () {
    oe_runmake CFLAGS="${CFLAGS} -DLINUX" LDFLAGS="${LDFLAGS}" PREFIX="${prefix}" KERNELSRC=${STAGING_KERNEL_DIR} all
}

do_install () {
    oe_runmake PREFIX="${D}${prefix}" install

    install -d ${D}${sysconfdir}/init.d
    touch ${D}${sysconfdir}/xl2tpd.conf
    install -m 0755 debian/xl2tpd.init ${D}${sysconfdir}/init.d/xl2tpd
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/xl2tpd
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/xl2tpd
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/xl2tpd
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${base_bindir}:${sbindir}:${bindir}!' ${D}${sysconfdir}/init.d/xl2tpd

    install -d ${D}${sysconfdir}/default
    install -m 0644 debian/xl2tpd.default ${D}${sysconfdir}/default/xl2tpd
}

CONFFILES_${PN} += "${sysconfdir}/xl2tpd.conf ${sysconfdir}/default/xl2tpd"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "xl2tpd"
