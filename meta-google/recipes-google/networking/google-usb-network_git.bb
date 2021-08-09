SUMMARY = "Google USB ECM Gadget Configuration Script"
DESCRIPTION = "Google USB ECM Gadget Configuration Script"
PR = "r1"
PV = "0.2"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend = "${THISDIR}/${PN}:"

inherit systemd

DEPENDS += "m4-native"
DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "usb_network.service"

BMC_IP_ADDR ??= "169.254.95.118/16"
BMC_USB_ECM_PRODUCT_ID ??= ""
BMC_USB_ECM_PRODUCT_NAME ??= "${MACHINE} BMC"
BMC_USB_ECM_HOST_MAC ??= "invalid"
BMC_USB_ECM_DEV_MAC ??= "invalid"
BMC_USB_ECM_BIND_DEV ??= ""
BMC_USB_CONFIG_PRIORITY ??= ""
BMC_USB_CONFIG_FILENAME ??= "${BMC_USB_CONFIG_PRIORITY}-bmc-usb0.network"

SRC_URI += "file://00-bmc-usb0.network.m4"
SRC_URI += "file://usb_network.service.m4"
SRC_URI += "file://usb_network.sh"

FILES:${PN} = "${bindir}/usb_network.sh"
FILES:${PN}:append = " ${systemd_unitdir}/network/${BMC_USB_CONFIG_FILENAME}"

do_compile() {
    test "X${BMC_IP_ADDR}" != "X" || bberror "Please define BMC_IP_ADDR"
    m4 -DM_BMC_IP_ADDR=${BMC_IP_ADDR} ${WORKDIR}/00-bmc-usb0.network.m4 > ${S}/00-bmc-usb0.network

    test "X${BMC_USB_ECM_PRODUCT_ID}" != "X" || bberror "Please define BMC_USB_ECM_PRODUCT_ID"
    test "X${BMC_USB_ECM_PRODUCT_NAME}" != "X" || bberror "Please define BMC_USB_ECM_PRODUCT_NAME"
    test "X${BMC_USB_ECM_BIND_DEV}" != "X" || bberror "Please define BMC_USB_ECM_BIND_DEV"

    m4 \
        -DM_BMC_USB_ECM_PRODUCT_ID="${BMC_USB_ECM_PRODUCT_ID}" \
        -DM_BMC_USB_ECM_PRODUCT_NAME="${BMC_USB_ECM_PRODUCT_NAME}" \
        -DM_BMC_USB_ECM_HOST_MAC="${BMC_USB_ECM_HOST_MAC}" \
        -DM_BMC_USB_ECM_DEV_MAC="${BMC_USB_ECM_DEV_MAC}" \
        -DM_BMC_USB_ECM_BIND_DEV="${BMC_USB_ECM_BIND_DEV}" \
        -DM_SCRIPT_INSTALL_DIR="${bindir}" \
        ${WORKDIR}/usb_network.service.m4 > ${S}/usb_network.service
}

do_install() {
  install -d ${D}/${bindir}
  install -m 0755 ${WORKDIR}/usb_network.sh ${D}/${bindir}

  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${S}/usb_network.service ${D}${systemd_system_unitdir}

  install -d ${D}${systemd_unitdir}/network
  install -m 0644 ${S}/00-bmc-usb0.network \
      ${D}${systemd_unitdir}/network/${BMC_USB_CONFIG_FILENAME}
}
