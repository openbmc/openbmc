FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

LICENSE_append = " & Firmware-broadcom_brcm80211"

LIC_FILES_CHKSUM_append = " file://${WORKDIR}/LICENSE.broadcom_brcm80211;md5=8cba1397cda6386db37210439a0da3eb"

NO_GENERIC_LICENSE[Firmware-broadcom_brcm80211] = "LICENSE.broadcom_brcm80211"

SRC_URI_append = " \
    file://brcmfmac43430-sdio.bin \
    file://brcmfmac43430-sdio.txt \
    file://LICENSE.broadcom_brcm80211 \
    "

do_install_append() {
    cp ${WORKDIR}/brcmfmac43430-sdio.* ${D}/lib/firmware/brcm/
    cp ${WORKDIR}/LICENSE.broadcom_brcm80211 ${D}/lib/firmware/
}

PACKAGES =+ "${PN}-brcm43430 ${PN}-brcm43430-license"

LICENSE_${PN}-brcm43430 = "Firmware-broadcom_brcm80211"
FILES_${PN}-brcm43430-license = "/lib/firmware/LICENSE.broadcom_brcm80211"
FILES_${PN}-brcm43430 = " \
    /lib/firmware/brcm/brcmfmac43430-sdio.bin \
    /lib/firmware/brcm/brcmfmac43430-sdio.txt \
    "

RDEPENDS_${PN}-brcm43430 += "${PN}-brcm43430-license"

ALTERNATIVE_LINK_NAME[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac-sdio.bin"
ALTERNATIVE_TARGET_linux-firmware-brcm43430[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac43430-sdio.bin"
ALTERNATIVE_linux-firmware-brcm43430 = "brcmfmac-sdio.bin"
