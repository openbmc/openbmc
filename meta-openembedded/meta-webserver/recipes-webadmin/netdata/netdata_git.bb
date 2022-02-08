HOMEPAGE = "https://github.com/firehol/netdata/"
SUMMARY = "Real-time performance monitoring"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc9b848046ef54b5eaee6071947abd24"

SRC_URI = "git://github.com/firehol/netdata.git;protocol=https;branch=master \
           file://0002-Makefiles-does-not-build-contrib-dir.patch \
"
SRCREV = "588ce5a7b18999dfa66698cd3a2f005f7a3c31cf"
PV = "1.17.0"

# patch to disable timeout because timeout are not available with actual version
# of core-utils
SRC_URI += "file://0001-Correct-timeout-issue.patch"

# default netdata.conf for netdata configuration
SRC_URI += "file://netdata.conf"

# file for providing systemd service support
SRC_URI += "file://netdata.service"

S = "${WORKDIR}/git"

DEPENDS += "zlib util-linux"

inherit pkgconfig autotools-brokensep useradd systemd

LIBS_toolchain-clang_x86 = "-latomic"
LIBS_riscv64 = "-latomic"
LIBS_riscv32 = "-latomic"
export LIBS

#systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "netdata.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

#User specific
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system netdata"

do_install_append() {
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

FILES_${PN}-dbg += "${libexecdir}/netdata/plugins.d/.debug"
RDEPENDS_${PN} = "bash zlib"
