SUMMARY = "Firmware files for use with Linux kernel"
SECTION = "kernel"

LICENSE = "\
    Firmware-Abilis \
    & Firmware-agere \
    & Firmware-amd-ucode \
    & Firmware-atheros_firmware \
    & Firmware-broadcom_bcm43xx \
    & Firmware-ca0132 \
    & Firmware-chelsio_firmware \
    & Firmware-cw1200 \
    & Firmware-dib0700 \
    & Firmware-ene_firmware \
    & Firmware-fw_sst_0f28 \
    & Firmware-go7007 \
    & Firmware-i2400m \
    & Firmware-ibt_firmware \
    & Firmware-it913x \
    & Firmware-iwlwifi_firmware \
    & Firmware-IntcSST2 \
    & Firmware-Marvell \
    & Firmware-mwl8335 \
    & Firmware-myri10ge_firmware \
    & Firmware-OLPC \
    & Firmware-phanfw \
    & Firmware-qat_dh895xcc_firmware \
    & Firmware-qla2xxx \
    & Firmware-r8a779x_usb3 \
    & Firmware-radeon \
    & Firmware-ralink_a_mediatek_company_firmware \
    & Firmware-ralink-firmware \
    & Firmware-rtlwifi_firmware \
    & Firmware-tda7706-firmware \
    & Firmware-ti-connectivity \
    & Firmware-ueagle-atm4-firmware \
    & Firmware-via_vt6656 \
    & Firmware-wl1251 \
    & Firmware-xc4000 \
    & Firmware-xc5000 \
    & Firmware-xc5000c \
    & Firmware-siano \
"

LIC_FILES_CHKSUM = "\
    file://LICENCE.Abilis;md5=b5ee3f410780e56711ad48eadc22b8bc \
    file://LICENCE.agere;md5=af0133de6b4a9b2522defd5f188afd31 \
    file://LICENCE.atheros_firmware;md5=30a14c7823beedac9fa39c64fdd01a13 \
    file://LICENCE.broadcom_bcm43xx;md5=3160c14df7228891b868060e1951dfbc \
    file://LICENCE.ca0132;md5=209b33e66ee5be0461f13d31da392198 \
    file://LICENCE.chelsio_firmware;md5=819aa8c3fa453f1b258ed8d168a9d903 \
    file://LICENCE.cw1200;md5=f0f770864e7a8444a5c5aa9d12a3a7ed \
    file://LICENCE.ene_firmware;md5=ed67f0f62f8f798130c296720b7d3921 \
    file://LICENCE.fw_sst_0f28;md5=6353931c988ad52818ae733ac61cd293 \
    file://LICENCE.go7007;md5=c0bb9f6aaaba55b0529ee9b30aa66beb \
    file://LICENCE.i2400m;md5=14b901969e23c41881327c0d9e4b7d36 \
    file://LICENCE.ibt_firmware;md5=fdbee1ddfe0fb7ab0b2fcd6b454a366b \
    file://LICENCE.it913x;md5=1fbf727bfb6a949810c4dbfa7e6ce4f8 \
    file://LICENCE.iwlwifi_firmware;md5=3fd842911ea93c29cd32679aa23e1c88 \
    file://LICENCE.IntcSST2;md5=9e7d8bea77612d7cc7d9e9b54b623062 \
    file://LICENCE.Marvell;md5=9ddea1734a4baf3c78d845151f42a37a \
    file://LICENCE.mwl8335;md5=9a6271ee0e644404b2ff3c61fd070983 \
    file://LICENCE.myri10ge_firmware;md5=42e32fb89f6b959ca222e25ac8df8fed \
    file://LICENCE.OLPC;md5=5b917f9d8c061991be4f6f5f108719cd \
    file://LICENCE.phanfw;md5=954dcec0e051f9409812b561ea743bfa \
    file://LICENCE.qat_dh895xcc_firmware;md5=9e7d8bea77612d7cc7d9e9b54b623062 \
    file://LICENCE.qla2xxx;md5=f5ce8529ec5c17cb7f911d2721d90e91 \
    file://LICENCE.r8a779x_usb3;md5=4c1671656153025d7076105a5da7e498 \
    file://LICENCE.ralink_a_mediatek_company_firmware;md5=728f1a85fd53fd67fa8d7afb080bc435 \
    file://LICENCE.ralink-firmware.txt;md5=ab2c269277c45476fb449673911a2dfd \
    file://LICENCE.rtlwifi_firmware.txt;md5=00d06cfd3eddd5a2698948ead2ad54a5 \
    file://LICENCE.tda7706-firmware.txt;md5=835997cf5e3c131d0dddd695c7d9103e \
    file://LICENCE.ti-connectivity;md5=186e7a43cf6c274283ad81272ca218ea \
    file://LICENCE.ueagle-atm4-firmware;md5=4ed7ea6b507ccc583b9d594417714118 \
    file://LICENCE.via_vt6656;md5=e4159694cba42d4377a912e78a6e850f \
    file://LICENCE.wl1251;md5=ad3f81922bb9e197014bb187289d3b5b \
    file://LICENCE.xc4000;md5=0ff51d2dc49fce04814c9155081092f0 \
    file://LICENCE.xc5000;md5=1e170c13175323c32c7f4d0998d53f66 \
    file://LICENCE.xc5000c;md5=12b02efa3049db65d524aeb418dd87ca \
    file://LICENSE.amd-ucode;md5=3a0de451253cc1edbf30a3c621effee3 \
    file://LICENSE.dib0700;md5=f7411825c8a555a1a3e5eab9ca773431 \
    file://LICENSE.radeon;md5=6c7f97c6c62bdd9596d0238bb205118c \
    file://LICENCE.siano;md5=602c79ae3f98f1e73d880fd9f940a418 \
"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-Abilis] = "LICENCE.Abilis"
NO_GENERIC_LICENSE[Firmware-agere] = "LICENCE.agere"
NO_GENERIC_LICENSE[Firmware-atheros_firmware] = "LICENCE.atheros_firmware"
NO_GENERIC_LICENSE[Firmware-broadcom_bcm43xx] = "LICENCE.broadcom_bcm43xx"
NO_GENERIC_LICENSE[Firmware-ca0132] = "LICENCE.ca0132"
NO_GENERIC_LICENSE[Firmware-chelsio_firmware] = "LICENCE.chelsio_firmware"
NO_GENERIC_LICENSE[Firmware-cw1200] = "LICENCE.cw1200"
NO_GENERIC_LICENSE[Firmware-ene_firmware] = "LICENCE.ene_firmware"
NO_GENERIC_LICENSE[Firmware-fw_sst_0f28] = "LICENCE.fw_sst_0f28"
NO_GENERIC_LICENSE[Firmware-go7007] = "LICENCE.go7007"
NO_GENERIC_LICENSE[Firmware-i2400m] = "LICENCE.i2400m"
NO_GENERIC_LICENSE[Firmware-ibt_firmware] = "LICENCE.ibt_firmware"
NO_GENERIC_LICENSE[Firmware-it913x] = "LICENCE.it913x"
NO_GENERIC_LICENSE[Firmware-iwlwifi_firmware] = "LICENCE.iwlwifi_firmware"
NO_GENERIC_LICENSE[Firmware-IntcSST2] = "LICENCE.IntcSST2"
NO_GENERIC_LICENSE[Firmware-Marvell] = "LICENCE.Marvell"
NO_GENERIC_LICENSE[Firmware-mwl8335] = "LICENCE.mwl8335"
NO_GENERIC_LICENSE[Firmware-myri10ge_firmware] = "LICENCE.myri10ge_firmware"
NO_GENERIC_LICENSE[Firmware-OLPC] = "LICENCE.OLPC"
NO_GENERIC_LICENSE[Firmware-phanfw] = "LICENCE.phanfw"
NO_GENERIC_LICENSE[Firmware-qat_dh895xcc_firmware] = "LICENCE.qat_dh895xcc_firmware"
NO_GENERIC_LICENSE[Firmware-qla2xxx] = "LICENCE.qla2xxx"
NO_GENERIC_LICENSE[Firmware-r8a779x_usb3] = "LICENCE.r8a779x_usb3"
NO_GENERIC_LICENSE[Firmware-ralink_a_mediatek_company_firmware] = "LICENCE.ralink_a_mediatek_company_firmware"
NO_GENERIC_LICENSE[Firmware-ralink-firmware] = "LICENCE.ralink-firmware.txt"
NO_GENERIC_LICENSE[Firmware-rtlwifi_firmware] = "LICENCE.rtlwifi_firmware.txt"
NO_GENERIC_LICENSE[Firmware-tda7706-firmware] = "LICENCE.tda7706-firmware.txt"
NO_GENERIC_LICENSE[Firmware-ti-connectivity] = "LICENCE.ti-connectivity"
NO_GENERIC_LICENSE[Firmware-ueagle-atm4-firmware] = "LICENCE.ueagle-atm4-firmware"
NO_GENERIC_LICENSE[Firmware-via_vt6656] = "LICENCE.via_vt6656"
NO_GENERIC_LICENSE[Firmware-wl1251] = "LICENCE.wl1251"
NO_GENERIC_LICENSE[Firmware-xc4000] = "LICENCE.xc4000"
NO_GENERIC_LICENSE[Firmware-xc5000] = "LICENCE.xc5000"
NO_GENERIC_LICENSE[Firmware-xc5000c] = "LICENCE.xc5000c"
NO_GENERIC_LICENSE[Firmware-amd-ucode] = "LICENSE.amd-ucode"
NO_GENERIC_LICENSE[Firmware-dib0700] = "LICENSE.dib0700"
NO_GENERIC_LICENSE[Firmware-radeon] = "LICENSE.radeon"
NO_GENERIC_LICENSE[Firmware-siano] = "LICENCE.siano"

SRCREV = "75cc3ef8ba6712fd72c073b17a790282136cc743"
PE = "1"
PV = "0.0+git${SRCPV}"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git"

S = "${WORKDIR}/git"

inherit allarch update-alternatives

CLEANBROKEN = "1"

do_compile() {
	:
}

do_install() {
	install -d  ${D}/lib/firmware/
	cp -r * ${D}/lib/firmware/

	# Avoid Makefile to be deployed
	rm ${D}/lib/firmware/Makefile

	# Remove unbuild firmware which needs cmake and bash
	rm ${D}/lib/firmware/carl9170fw -rf

	# Remove pointless bash script
	rm ${D}/lib/firmware/configure

	# Libertas sd8686
	ln -sf libertas/sd8686_v9.bin ${D}/lib/firmware/sd8686.bin
	ln -sf libertas/sd8686_v9_helper.bin ${D}/lib/firmware/sd8686_helper.bin

	# fixup wl12xx location, after 2.6.37 the kernel searches a different location for it
	( cd ${D}/lib/firmware ; ln -sf ti-connectivity/* . )
}


PACKAGES =+ "${PN}-ralink-license ${PN}-ralink \
             ${PN}-radeon-license ${PN}-radeon \
             ${PN}-marvell-license ${PN}-sd8686 ${PN}-sd8787 ${PN}-sd8797 \
             ${PN}-ti-connectivity-license ${PN}-wl12xx ${PN}-wl18xx \
             ${PN}-vt6656-license ${PN}-vt6656 \
             ${PN}-rtl-license ${PN}-rtl8192cu ${PN}-rtl8192ce ${PN}-rtl8192su \
             ${PN}-broadcom-license ${PN}-bcm4329 ${PN}-bcm4330 ${PN}-bcm4334 ${PN}-bcm4354 \
             ${PN}-atheros-license ${PN}-ar9170 ${PN}-ar3k ${PN}-ath6k ${PN}-ath9k \
             \
             ${PN}-iwlwifi-license ${PN}-iwlwifi-135-6 \
             ${PN}-iwlwifi-3160-7 ${PN}-iwlwifi-3160-8 ${PN}-iwlwifi-3160-9 \
             ${PN}-iwlwifi-6000-4 ${PN}-iwlwifi-6000g2a-5 ${PN}-iwlwifi-6000g2a-6 \
             ${PN}-iwlwifi-6000g2b-5 ${PN}-iwlwifi-6000g2b-6 \
             ${PN}-iwlwifi-6050-4 ${PN}-iwlwifi-6050-5 \
             ${PN}-iwlwifi-7260-7 ${PN}-iwlwifi-7260-8 ${PN}-iwlwifi-7260-9 \
             ${PN}-iwlwifi-7265-8 ${PN}-iwlwifi-7265-9 \
             \
             ${PN}-license \
             "

# For atheros
LICENSE_${PN}-ar9170 = "Firmware-atheros_firmware"
LICENSE_${PN}-ar3k = "Firmware-atheros_firmware"
LICENSE_${PN}-ath6k = "Firmware-atheros_firmware"
LICENSE_${PN}-ath9k = "Firmware-atheros_firmware"

FILES_${PN}-atheros-license = "/lib/firmware/LICENCE.atheros_firmware"
FILES_${PN}-ar9170 = " \
  /lib/firmware/ar9170*.fw \
"
FILES_${PN}-ar3k = " \
  /lib/firmware/ar3k \
"
FILES_${PN}-ath6k = " \
  /lib/firmware/ath6k \
"
FILES_${PN}-ath9k = " \
  /lib/firmware/ar9271.fw \
  /lib/firmware/ar7010*.fw \
  /lib/firmware/htc_9271.fw \
  /lib/firmware/htc_7010.fw \
"

RDEPENDS_${PN}-ar9170 += "${PN}-atheros-license"
RDEPENDS_${PN}-ar3k += "${PN}-atheros-license"
RDEPENDS_${PN}-ath6k += "${PN}-atheros-license"
RDEPENDS_${PN}-ath9k += "${PN}-atheros-license"

# For ralink
LICENSE_${PN}-ralink = "Firmware-ralink-firmware"

FILES_${PN}-ralink-license = "/lib/firmware/LICENCE.ralink-firmware.txt"
FILES_${PN}-ralink = " \
  /lib/firmware/rt*.bin \
"

RDEPENDS_${PN}-ralink += "${PN}-ralink-license"

# For radeon
LICENSE_${PN}-radeon = "Firmware-radeon"

FILES_${PN}-radeon-license = "/lib/firmware/LICENSE.radeon"
FILES_${PN}-radeon = " \
  /lib/firmware/radeon \
"

RDEPENDS_${PN}-radeon += "${PN}-radeon-license"

# For marvell
LICENSE_${PN}-sd8686 = "Firmware-Marvell"
LICENSE_${PN}-sd8787 = "Firmware-Marvell"
LICENSE_${PN}-sd8797 = "Firmware-Marvell"

FILES_${PN}-marvell-license = "/lib/firmware/LICENCE.Marvell"
FILES_${PN}-sd8686 = " \
  /lib/firmware/libertas/sd8686_v9* \
  /lib/firmware/sd8686* \
"
FILES_${PN}-sd8787 = " \
  /lib/firmware/mrvl/sd8787_uapsta.bin \
"
FILES_${PN}-sd8797 = " \
  /lib/firmware/mrvl/sd8797_uapsta.bin \
"

RDEPENDS_${PN}-sd8686 += "${PN}-marvell-license"
RDEPENDS_${PN}-sd8787 += "${PN}-marvell-license"
RDEPENDS_${PN}-sd8797 += "${PN}-marvell-license"

# For rtl
LICENSE_${PN}-rtl8192cu = "Firmware-rtlwifi_firmware"
LICENSE_${PN}-rtl8192ce = "Firmware-rtlwifi_firmware"
LICENSE_${PN}-rtl8192su = "Firmware-rtlwifi_firmware"

FILES_${PN}-rtl-license = " \
  /lib/firmware/LICENCE.rtlwifi_firmware.txt \
"
FILES_${PN}-rtl8192cu = " \
  /lib/firmware/rtlwifi/rtl8192cufw*.bin \
"
FILES_${PN}-rtl8192ce = " \
  /lib/firmware/rtlwifi/rtl8192cfw*.bin \
"
FILES_${PN}-rtl8192su = " \
  /lib/firmware/rtlwifi/rtl8712u.bin \
"

RDEPENDS_${PN}-rtl8192ce += "${PN}-rtl-license"
RDEPENDS_${PN}-rtl8192cu += "${PN}-rtl-license"
RDEPENDS_${PN}-rtl8192su = "${PN}-rtl-license"

# For ti-connectivity
LICENSE_${PN}-wl12xx = "Firmware-ti-connectivity"
LICENSE_${PN}-wl18xx = "Firmware-ti-connectivity"

FILES_${PN}-ti-connectivity-license = "/lib/firmware/LICENCE.ti-connectivity"
FILES_${PN}-wl12xx = " \
  /lib/firmware/wl12* \
  /lib/firmware/TI* \
  /lib/firmware/ti-connectivity \
"
FILES_${PN}-wl18xx = " \
  /lib/firmware/wl18* \
  /lib/firmware/TI* \
  /lib/firmware/ti-connectivity \
"

RDEPENDS_${PN}-wl12xx = "${PN}-ti-connectivity-license"
RDEPENDS_${PN}-wl18xx = "${PN}-ti-connectivity-license"

# For vt6656
LICENSE_${PN}-vt6656 = "Firmware-via_vt6656"

FILES_${PN}-vt6656-license = "/lib/firmware/LICENCE.via_vt6656"
FILES_${PN}-vt6656 = " \
  /lib/firmware/vntwusb.fw \
"

RDEPENDS_${PN}-vt6656 = "${PN}-vt6656-license"

# For broadcom
#
# WARNING: The ALTERNATIVE_* variables are not using ${PN} because of
# a bug in bitbake; when this is fixed and bitbake learns how to proper
# pass variable flags with expansion we can rework this patch.

LICENSE_${PN}-bcm4329 = "Firmware-broadcom_bcm43xx"
LICENSE_${PN}-bcm4330 = "Firmware-broadcom_bcm43xx"
LICENSE_${PN}-bcm4334 = "Firmware-broadcom_bcm43xx"
LICENSE_${PN}-bcm4354 = "Firmware-broadcom_bcm43xx"

FILES_${PN}-broadcom-license = " \
  /lib/firmware/LICENCE.broadcom_bcm43xx \
"
FILES_${PN}-bcm4329 = " \
  /lib/firmware/brcm/brcmfmac4329-sdio.bin \
"
FILES_${PN}-bcm4330 = " \
  /lib/firmware/brcm/brcmfmac4330-sdio.bin \
"
FILES_${PN}-bcm4334 = " \
  /lib/firmware/brcm/brcmfmac4334-sdio.bin \
"
FILES_${PN}-bcm4354 = " \
  /lib/firmware/brcm/brcmfmac4354-sdio.bin \
"

ALTERNATIVE_LINK_NAME[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac-sdio.bin"

ALTERNATIVE_linux-firmware-bcm4334 = "brcmfmac-sdio.bin"
ALTERNATIVE_TARGET_linux-firmware-bcm4334[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac4334-sdio.bin"
ALTERNATIVE_linux_firmware-bcm4354 = "brcmfmac-sdio.bin"
ALTERNATIVE_TARGET_linux-firmware-bcm4354[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac4354-sdio.bin"
ALTERNATIVE_linux-firmware-bcm4329 = "brcmfmac-sdio.bin"
ALTERNATIVE_TARGET_linux-firmware-bcm4329[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac4329-sdio.bin"
ALTERNATIVE_linux-firmware-bcm4330 = "brcmfmac-sdio.bin"
ALTERNATIVE_TARGET_linux-firmware-bcm4330[brcmfmac-sdio.bin] = "/lib/firmware/brcm/brcmfmac4330-sdio.bin"

RDEPENDS_${PN}-bcm4329 += "${PN}-broadcom-license"
RDEPENDS_${PN}-bcm4330 += "${PN}-broadcom-license"
RDEPENDS_${PN}-bcm4334 += "${PN}-broadcom-license"
RDEPENDS_${PN}-bcm4354 += "${PN}-broadcom-license"

# For iwlwifi
LICENSE_${PN}-iwlwifi-135-6     = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-3160-7    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-3160-8    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-3160-9    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6000-4    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6000g2a-5 = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6000g2a-6 = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6000g2a-5 = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6000g2b-6 = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6050-4    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-6050-5    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-7260-7    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-7260-8    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-7260-9    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-7265-8    = "Firmware-iwlwifi_firmware"
LICENSE_${PN}-iwlwifi-7265-9    = "Firmware-iwlwifi_firmware"

FILES_${PN}-iwlwifi-license = "/lib/firmware/LICENCE.iwlwifi_firmware"
FILES_${PN}-iwlwifi-135-6 = "/lib/firmware/iwlwifi-135-6.ucode"
FILES_${PN}-iwlwifi-3160-7 = "/lib/firmware/iwlwifi-3160-7.ucode"
FILES_${PN}-iwlwifi-3160-8 = "/lib/firmware/iwlwifi-3160-8.ucode"
FILES_${PN}-iwlwifi-3160-9 = "/lib/firmware/iwlwifi-3160-9.ucode"
FILES_${PN}-iwlwifi-6000-4 = "/lib/firmware/iwlwifi-6000-4.ucode"
FILES_${PN}-iwlwifi-6000g2a-5 = "/lib/firmware/iwlwifi-6000g2a-5.ucode"
FILES_${PN}-iwlwifi-6000g2a-6 = "/lib/firmware/iwlwifi-6000g2a-6.ucode"
FILES_${PN}-iwlwifi-6000g2b-5 = "/lib/firmware/iwlwifi-6000g2b-5.ucode"
FILES_${PN}-iwlwifi-6000g2b-6 = "/lib/firmware/iwlwifi-6000g2b-6.ucode"
FILES_${PN}-iwlwifi-6050-4 = "/lib/firmware/iwlwifi-6050-4.ucode"
FILES_${PN}-iwlwifi-6050-5 = "/lib/firmware/iwlwifi-6050-5.ucode"
FILES_${PN}-iwlwifi-7260-7 = "/lib/firmware/iwlwifi-7260-7.ucode"
FILES_${PN}-iwlwifi-7260-8 = "/lib/firmware/iwlwifi-7260-8.ucode"
FILES_${PN}-iwlwifi-7260-9 = "/lib/firmware/iwlwifi-7260-9.ucode"
FILES_${PN}-iwlwifi-7265-8 = "/lib/firmware/iwlwifi-7265-8.ucode"
FILES_${PN}-iwlwifi-7265-9 = "/lib/firmware/iwlwifi-7265-9.ucode"

RDEPENDS_${PN}-iwlwifi-135-6     = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-3160-7    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-3160-8    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-3160-9    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6000-4    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6000g2a-5 = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6000g2a-6 = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6000g2a-5 = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6000g2b-6 = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6050-4    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-6050-5    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-7260-7    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-7260-8    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-7260-9    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-7265-8    = "${PN}-iwlwifi-license"
RDEPENDS_${PN}-iwlwifi-7265-9    = "${PN}-iwlwifi-license"

# For other firmwares
# Maybe split out to separate packages when needed.
LICENSE_${PN} = "\
    Firmware-Abilis \
    & Firmware-agere \
    & Firmware-amd-ucode \
    & Firmware-ca0132 \
    & Firmware-chelsio_firmware \
    & Firmware-cw1200 \
    & Firmware-dib0700 \
    & Firmware-ene_firmware \
    & Firmware-fw_sst_0f28 \
    & Firmware-go7007 \
    & Firmware-i2400m \
    & Firmware-ibt_firmware \
    & Firmware-it913x \
    & Firmware-mwl8335 \
    & Firmware-myri10ge_firmware \
    & Firmware-OLPC \
    & Firmware-phanfw \
    & Firmware-qat_dh895xcc_firmware \
    & Firmware-qla2xxx \
    & Firmware-r8a779x_usb3 \
    & Firmware-ralink_a_mediatek_company_firmware \
    & Firmware-tda7706-firmware \
    & Firmware-ueagle-atm4-firmware \
    & Firmware-wl1251 \
    & Firmware-xc4000 \
    & Firmware-xc5000 \
    & Firmware-xc5000c \
"
FILES_${PN}-license += "/lib/firmware/LICEN*"
FILES_${PN} += "/lib/firmware/*"
RDEPENDS_${PN} += "${PN}-license"

# Make linux-firmware depend on all of the split-out packages.
python populate_packages_prepend () {
    firmware_pkgs = oe.utils.packages_filter_out_system(d)
    d.appendVar('RDEPENDS_linux-firmware', ' ' + ' '.join(firmware_pkgs))
}
