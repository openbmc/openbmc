SUMMARY = "Google USB EEM Gadget Configuration Script"
DESCRIPTION = "Google USB EEM Gadget Configuration Script"
PR = "r1"
PV = "0.2"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend = "${THISDIR}/${PN}:"

inherit systemd

DEPENDS += "m4-native"
DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

BMC_USB_ECM_PRODUCT_ID ??= ""
BMC_USB_ECM_PRODUCT_NAME ??= "${MACHINE} BMC"
BMC_USB_ECM_HOST_MAC ??= "invalid"
BMC_USB_ECM_DEV_MAC ??= "invalid"
BMC_USB_ECM_BIND_DEV ??= ""
BMC_USB_TYPE ??= "eem"
BMC_USB_IFACE ??= "gusb0"

SRC_URI += "file://usb_network.service.m4"
SRC_URI += "file://usb_network.sh"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${@'usb_network.service' if d.getVar('BMC_USB_ECM_PRODUCT_ID') else ''}"

do_compile:append() {
  if [ -n "${BMC_USB_ECM_PRODUCT_ID}" ]; then
    test "X${BMC_USB_ECM_PRODUCT_NAME}" != "X" || bberror "Please define BMC_USB_ECM_PRODUCT_NAME"
    test "X${BMC_USB_ECM_BIND_DEV}" != "X" || bberror "Please define BMC_USB_ECM_BIND_DEV"

    m4 \
        -DM_BMC_USB_PRODUCT_ID="${BMC_USB_ECM_PRODUCT_ID}" \
        -DM_BMC_USB_PRODUCT_NAME="${BMC_USB_ECM_PRODUCT_NAME}" \
        -DM_BMC_USB_TYPE="${BMC_USB_TYPE}" \
        -DM_BMC_USB_HOST_MAC="${BMC_USB_ECM_HOST_MAC}" \
        -DM_BMC_USB_DEV_MAC="${BMC_USB_ECM_DEV_MAC}" \
        -DM_BMC_USB_IFACE="${BMC_USB_IFACE}" \
        -DM_BMC_USB_BIND_DEV="${BMC_USB_ECM_BIND_DEV}" \
        -DM_SCRIPT_INSTALL_DIR="${bindir}" \
        ${UNPACKDIR}/usb_network.service.m4 > ${UNPACKDIR}/usb_network.service
  fi
}

do_install:append() {
  install -d ${D}/${bindir}
  install -m 0755 ${UNPACKDIR}/usb_network.sh ${D}/${bindir}

  if [ -n "${BMC_USB_ECM_PRODUCT_ID}" ]; then
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/usb_network.service ${D}${systemd_system_unitdir}
  fi
}
