FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
	file://brcmfmac43430-sdio.bin \
	file://brcmfmac43430-sdio.txt \
	"

do_install_append() {
	# Overwrite v7.45.41.26 by the one we currently provide in this layer
	# (v7.45.41.46)
	local _firmware="brcmfmac43430-sdio.bin"
	local _oldmd5=9258986488eca9fe5343b0d6fe040f8e
	if [ "$(md5sum ${D}/lib/firmware/brcm/$_firmware | awk '{print $1}')" != "$_oldmd5" ]; then
		_firmware=""
		bbwarn "linux-firmware stopped providing brcmfmac43430 v7.45.41.26."
	else
		_firmware="${WORKDIR}/$_firmware"
	fi

	mkdir -p ${D}/lib/firmware/brcm
	install -m 0644 $_firmware ${WORKDIR}/brcmfmac43430-sdio.txt ${D}/lib/firmware/brcm
}

FILES_${PN}-bcm43430 += " \
	/lib/firmware/brcm/brcmfmac43430-sdio.txt \
"
