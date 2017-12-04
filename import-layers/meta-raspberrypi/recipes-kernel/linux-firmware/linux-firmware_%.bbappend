FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://brcmfmac43430-sdio.txt"

do_install_append() {
	install -D -m 0644 ${WORKDIR}/brcmfmac43430-sdio.txt ${D}/lib/firmware/brcm/brcmfmac43430-sdio.txt
}

FILES_${PN}-bcm43430 += " \
  /lib/firmware/brcm/brcmfmac43430-sdio.txt \
"

