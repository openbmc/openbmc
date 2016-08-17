SUMMARY = "Transparent Inter-Process Communication protocol"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://tipc-config/tipc-config.c;endline=32;md5=527a3d5745e1581b15a4fddfb5dfda68"

SRC_URI = "git://tipc.git.sourceforge.net/gitroot/tipc/tipcutils"
SRCREV = "292a03e17f889013fca2c7bd0aaeebd600c88f40"

DEPENDS="virtual/kernel"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools

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

