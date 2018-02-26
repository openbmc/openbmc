HOMEPAGE = "https://github.com/firehol/netdata/"
SUMMARY = "Real-time performance monitoring"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a4a3b650ea3f74269cdfd45a3550e219 \
                    file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                   "

SRC_URI = "git://github.com/firehol/netdata.git;protocol=https \
           file://0001-makefile-Do-not-build-contrib-dir.patch \
"
SRCREV = "f5fa346a188e906a8f2cce3c2cf32a88ce81c666"
PV = "1.6.0+git${SRCPV}"

# patch to disable timeout because timeout are not available with actual version
# of core-utils
SRC_URI += "file://0001-Correct-Timeout-issue.patch"

# default netdata.conf for netdata configuration
SRC_URI += "file://netdata.conf"

# file for providing systemd service support
SRC_URI += "file://netdata.service"

S = "${WORKDIR}/git"

DEPENDS += "zlib util-linux"

inherit pkgconfig autotools useradd systemd

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
