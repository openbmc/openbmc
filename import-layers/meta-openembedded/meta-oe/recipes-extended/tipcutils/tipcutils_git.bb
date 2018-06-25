SUMMARY = "Transparent Inter-Process Communication protocol"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://tipclog/tipc.h;endline=35;md5=985b6ea8735818511d276c1b466cce98"

SRC_URI = "git://git.code.sf.net/p/tipc/tipcutils \
           file://0001-include-sys-select.h-for-FD_-definitions.patch \
           file://0002-replace-non-standard-uint-with-unsigned-int.patch \
           file://0001-multicast_blast-tipcc-Fix-struct-type-for-TIPC_GROUP.patch \
           "
SRCREV = "7ab2211b87414ba240b0b2e4af219c1057c9cf9a"
PV = "2.2.0+git${SRCPV}"

DEPENDS="virtual/kernel"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools pkgconfig

DEPENDS += "libdaemon"

RDEPENDS_${PN} = "iproute2-tipc"

S = "${WORKDIR}/git"

do_configure_prepend() {
    ( cd ${S}; ${S}/bootstrap )
}

do_install_append() {
    demos="benchmark hello_world topology_subscr_demo connection_demo \
           multicast_demo stream_demo"
    for i in $demos;do
        install -d ${D}/opt/tipcutils/demos/$i
        install ${B}/demos/$i/client_tipc ${D}/opt/tipcutils/demos/$i/
        install ${B}/demos/$i/server_tipc ${D}/opt/tipcutils/demos/$i/
    done
    install -d ${D}/opt/tipcutils/demos/inventory_sim
    install ${B}/demos/inventory_sim/inventory_sim ${D}/opt/tipcutils/demos/inventory_sim/

    install -d ${D}/opt/tipcutils/ptts
    install ${B}/ptts/tipcTS ${D}/opt/tipcutils/ptts/
    install ${B}/ptts/tipcTC ${D}/opt/tipcutils/ptts/

    install -d ${D}${sysconfdir}
    cp -R --no-dereference --preserve=mode,links -v ${S}/scripts/etc/* ${D}${sysconfdir}/
    chown -R root:root ${D}${sysconfdir}
}

PACKAGES += "${PN}-demos"
FILES_${PN}-dbg += "/opt/tipcutils/demos/*/.debug /opt/tipcutils/ptts/.debug"
FILES_${PN}-demos = "/opt/tipcutils/*"

