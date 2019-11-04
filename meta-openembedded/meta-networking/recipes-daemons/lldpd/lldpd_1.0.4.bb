SUMMARY = "A 802.1ab implementation (LLDP) to help you locate neighbors of all your equipments"
SECTION = "net/misc"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d"

DEPENDS = "libbsd libevent"

SRC_URI = "\
    http://media.luffy.cx/files/${BPN}/${BPN}-${PV}.tar.gz \
    file://lldpd.init.d \
    file://lldpd.default \
    "

SRC_URI[md5sum] = "33e8d58623f99184e4e709cbbfe45db3"
SRC_URI[sha256sum] = "5319bc032fabf1008d5d91e280276aa7f1bbfbb70129d8526cd4526d7c22724f"

inherit autotools update-rc.d useradd systemd pkgconfig bash-completion

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system -g lldpd --shell /bin/false lldpd"
GROUPADD_PARAM_${PN} = "--system lldpd"

EXTRA_OECONF += "--without-embedded-libevent \
                 --disable-oldies \
                 --with-privsep-user=lldpd \
                 --with-privsep-group=lldpd \
                 --with-systemdsystemunitdir=${systemd_system_unitdir} \
                 --without-sysusersdir \
"

PACKAGECONFIG ??= "cdp fdp edp sonmp lldpmed dot1 dot3"
PACKAGECONFIG[xml] = "--with-xml,--without-xml,libxm2"
PACKAGECONFIG[snmp] = "--with-snmp,--without-snmp,net-snmp"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[seccomp] = "--with-seccomp,--without-seccomp,libseccomp"
PACKAGECONFIG[cdp] = "--enable-cdp,--disable-cdp"
PACKAGECONFIG[fdp] = "--enable-fdp,--disable-fdp"
PACKAGECONFIG[edp] = "--enable-edp,--disable-edp"
PACKAGECONFIG[sonmp] = "--enable-sonmp,--disable-sonmp"
PACKAGECONFIG[lldpmed] = "--enable-lldpmed,--disable-lldpmed"
PACKAGECONFIG[dot1] = "--enable-dot1,--disable-dot1"
PACKAGECONFIG[dot3] = "--enable-dot3,--disable-dot3"
PACKAGECONFIG[custom] = "--enable-custom,--disable-custom"

INITSCRIPT_NAME = "lldpd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "lldpd.service"

do_install_append() {
    install -Dm 0755 ${WORKDIR}/lldpd.init.d ${D}${sysconfdir}/init.d/lldpd
    install -Dm 0644 ${WORKDIR}/lldpd.default ${D}${sysconfdir}/default/lldpd
    # Make an empty configuration file
    touch ${D}${sysconfdir}/lldpd.conf
}

PACKAGES =+ "${PN}-zsh-completion"

FILES_${PN} += "${libdir}/sysusers.d"
RDEPENDS_${PN} += "os-release"

FILES_${PN}-zsh-completion += "${datadir}/zsh/"
# FIXME: zsh is broken in meta-oe so this cannot be enabled for now
#RDEPENDS_${PN}-zsh-completion += "zsh"
