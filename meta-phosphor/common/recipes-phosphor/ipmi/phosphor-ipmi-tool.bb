SUMMARY = "Phosphor ipmi tool for injecting ipmi commands"
DESCRIPTION = "IPMI Tool with dbus capabilities"
HOMEPAGE = "https://github.com/openbmc/ipmitool"
PR = "r1"


inherit obmc-phosphor-license

DEPENDS += "systemd    \
            phosphor-ipmi-host \
            "

RDEPENDS_${PN} += "libsystemd \
                   libcrypto \
                   virtual-obmc-host-ipmi-hw \
                   "


SRC_URI += "git://github.com/openbmc/ipmitool"

SRCREV = "e9b9c1a9677a3de19726d036cfb07d8d61bbccd8"


S = "${WORKDIR}/git"


do_compile() {
        ${S}/bootstrap --enable-intf-dbus
        ${S}/configure --host x86_64
        make
}

do_install() {
        install -m 0755 -d ${D}${sbindir}
        install -m 0755 ${S}/src/ipmitool ${D}${sbindir}
}
