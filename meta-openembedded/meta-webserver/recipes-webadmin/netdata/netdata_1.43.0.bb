SUMMARY = "Real-time performance monitoring"
DESCRIPTION = "Netdata is high-fidelity infrastructure monitoring and troubleshooting. \
               Open-source, free, preconfigured, opinionated, and always real-time."
HOMEPAGE = "https://github.com/netdata/netdata/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc9b848046ef54b5eaee6071947abd24"

DEPENDS += "libuv util-linux zlib libyaml json-c"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BPN}-v${PV}.tar.gz \
"
SRC_URI[sha256sum] = "d3c66273d8eb62290d412c870dcde45ea214ad187df9d5350f682d9e6d19d426"

# default netdata.conf for netdata configuration
SRC_URI += "file://netdata.conf"

# file for providing systemd service support
SRC_URI += "file://netdata.service"

UPSTREAM_CHECK_URI = "https://github.com/netdata/netdata/releases"

S = "${WORKDIR}/${BPN}-v${PV}"

# Stop sending anonymous statistics to Google Analytics
NETDATA_ANONYMOUS ??= "enabled"

inherit pkgconfig autotools-brokensep useradd systemd

LIBS:toolchain-clang:x86 = "-latomic"
LIBS:riscv64 = "-latomic"
LIBS:riscv32 = "-latomic"
LIBS:mips = "-latomic"
export LIBS

#systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "netdata.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

#User specific
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --home-dir ${localstatedir}/run/netdata --user-group netdata"

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[cloud] = "--enable-cloud, --disable-cloud,"
PACKAGECONFIG[lz4] = "--enable-lz4, --disable-lz4, lz4"
PACKAGECONFIG[openssl] = "--enable-openssl, --disable-openssl, openssl"

# ebpf doesn't compile (or detect) the cross compilation well
EXTRA_OECONF += "--disable-ebpf"

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

    if [ "${NETDATA_ANONYMOUS}" = "enabled" ]; then
        touch ${D}${sysconfdir}/netdata/.opt-out-from-anonymous-statistics
    fi

    install --group netdata --owner netdata --directory ${D}${localstatedir}/cache/netdata
    install --group netdata --owner netdata --directory ${D}${localstatedir}/lib/netdata

    chown -R netdata:netdata ${D}${datadir}/netdata/web
}

FILES:${PN} += "${localstatedir}/cache/netdata/ ${localstatedir}/lib/netdata/"

RDEPENDS:${PN} = "bash zlib"
