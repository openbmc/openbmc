require dfu-util_${PV}.bb

inherit native deploy

DEPENDS = "libusb1-native"

PACKAGECONFIG ??= ""

PACKAGECONFIG[static] = "CFLAGS='${CFLAGS} -pthread -static',,"

do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_TOOLS}"
do_deploy() {
    install -m 0755 src/dfu-util ${DEPLOYDIR}/dfu-util-${PV}
    rm -f ${DEPLOYDIR}/dfu-util
    ln -sf ./dfu-util-${PV} ${DEPLOYDIR}/dfu-util
}

addtask deploy before do_package after do_install
