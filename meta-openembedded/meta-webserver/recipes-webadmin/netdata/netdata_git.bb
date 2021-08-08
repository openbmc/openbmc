HOMEPAGE = "https://github.com/firehol/netdata/"
SUMMARY = "Real-time performance monitoring"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc9b848046ef54b5eaee6071947abd24"

SRC_URI = "git://github.com/firehol/netdata.git;protocol=https"
SRCREV = "1be9200ba8e11dc81a2101d85a2725137d43f766"
PV = "1.22.1"

# default netdata.conf for netdata configuration
SRC_URI += "file://netdata.conf"

# file for providing systemd service support
SRC_URI += "file://netdata.service"

S = "${WORKDIR}/git"

DEPENDS += "zlib util-linux libuv"

inherit pkgconfig autotools-brokensep useradd systemd

LIBS:toolchain-clang:x86 = "-latomic"
LIBS:riscv64 = "-latomic"
LIBS:riscv32 = "-latomic"
export LIBS

#systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "netdata.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

#User specific
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system netdata"

do_install:append() {
    #set S UID for plugins
    chmod 4755 ${D}${libexecdir}/netdata/plugins.d/apps.plugin

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        # Install systemd unit files
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/netdata.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@@datadir,${datadir_native},g' ${D}${systemd_unitdir}/system/netdata.service
    fi

    # Install default netdata.conf
    install -d ${D}${sysconfdir}/netdata
    install -m 0644 ${WORKDIR}/netdata.conf ${D}${sysconfdir}/netdata/
    sed -i -e 's,@@sysconfdir,${sysconfdir},g' ${D}${sysconfdir}/netdata/netdata.conf
    sed -i -e 's,@@libdir,${libexecdir},g' ${D}${sysconfdir}/netdata/netdata.conf
    sed -i -e 's,@@datadir,${datadir},g' ${D}${sysconfdir}/netdata/netdata.conf
}

RDEPENDS:${PN} = "bash zlib"
