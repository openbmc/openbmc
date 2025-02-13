SUMMARY = "Firmware files for use with Linux kernel"
HOMEPAGE = "https://www.kernel.org/"
DESCRIPTION = "Linux firmware is a package distributed alongside the Linux kernel \
that contains firmware binary blobs necessary for partial or full functionality \
of certain hardware devices."
SECTION = "kernel"

LICENSE = "\
    Firmware-Abilis \
    & Firmware-adsp_sst \
    & Firmware-agere \
    & Firmware-amdgpu \
    & Firmware-amd-ucode \
    & Firmware-amlogic_vdec \
    & Firmware-amphion_vpu \
    & Firmware-atheros_firmware \
    & Firmware-atmel \
    & Firmware-broadcom_bcm43xx \
    & Firmware-ca0132 \
    & Firmware-cavium \
    & Firmware-chelsio_firmware \
    & Firmware-cirrus \
    & Firmware-cnm \
    & Firmware-cw1200 \
    & Firmware-cypress \
    & Firmware-dib0700 \
    & Firmware-e100 \
    & Firmware-ene_firmware \
    & Firmware-fw_sst_0f28 \
    & Firmware-go7007 \
    & Firmware-hfi1_firmware \
    & Firmware-i915 \
    & Firmware-ibt_firmware \
    & Firmware-ice \
    & Firmware-ice_enhanced \
    & Firmware-it913x \
    & Firmware-iwlwifi_firmware \
    & Firmware-IntcSST2 \
    & Firmware-kaweth \
    & Firmware-linaro \
    & Firmware-Lontium \
    & Firmware-mali_csffw \
    & Firmware-Marvell \
    & Firmware-mediatek \
    & Firmware-microchip \
    & Firmware-moxa \
    & Firmware-myri10ge_firmware \
    & Firmware-netronome \
    & Firmware-nvidia \
    & Firmware-nxp \
    & Firmware-nxp_mc_firmware \
    & Firmware-OLPC \
    & Firmware-ath9k-htc \
    & Firmware-phanfw \
    & Firmware-powervr \
    & Firmware-qat \
    & Firmware-qcom \
    & Firmware-qcom-yamato \
    & Firmware-qla1280 \
    & Firmware-qla2xxx \
    & Firmware-qualcommAthos_ar3k \
    & Firmware-qualcommAthos_ath10k \
    & Firmware-r8a779x_usb3 \
    & Firmware-radeon \
    & Firmware-ralink_a_mediatek_company_firmware \
    & Firmware-ralink-firmware \
    & Firmware-rockchip \
    & Firmware-rtlwifi_firmware \
    & Firmware-imx-sdma_firmware \
    & Firmware-siano \
    & Firmware-ti-connectivity \
    & Firmware-ti-keystone \
    & Firmware-ueagle-atm4-firmware \
    & Firmware-via_vt6656 \
    & Firmware-wfx \
    & Firmware-wl1251 \
    & Firmware-xc4000 \
    & Firmware-xc5000 \
    & Firmware-xc5000c \
    & WHENCE \
    & GPL-2.0-or-later \
"

LIC_FILES_CHKSUM = "file://LICENCE.Abilis;md5=b5ee3f410780e56711ad48eadc22b8bc \
                    file://LICENCE.adsp_sst;md5=615c45b91a5a4a9fe046d6ab9a2df728 \
                    file://LICENCE.agere;md5=af0133de6b4a9b2522defd5f188afd31 \
                    file://LICENSE.amdgpu;md5=1433dfea38c97a2e563a248a863dcb94 \
                    file://LICENSE.amd-ucode;md5=6ca90c57f7b248de1e25c7f68ffc4698 \
                    file://LICENSE.amlogic_vdec;md5=dc44f59bf64a81643e500ad3f39a468a \
                    file://LICENSE.amphion_vpu;md5=2bcdc00527b2d0542bd92b52aaec2b60 \
                    file://LICENCE.atheros_firmware;md5=30a14c7823beedac9fa39c64fdd01a13 \
                    file://LICENSE.atmel;md5=aa74ac0c60595dee4d4e239107ea77a3 \
                    file://LICENCE.broadcom_bcm43xx;md5=3160c14df7228891b868060e1951dfbc \
                    file://LICENCE.ca0132;md5=209b33e66ee5be0461f13d31da392198 \
                    file://LICENCE.cadence;md5=009f46816f6956cfb75ede13d3e1cee0 \
                    file://LICENCE.cavium;md5=c37aaffb1ebe5939b2580d073a95daea \
                    file://LICENCE.chelsio_firmware;md5=819aa8c3fa453f1b258ed8d168a9d903 \
                    file://LICENSE.cirrus;md5=662ea2c1a8888f7d79ed7f27c27472e1 \
                    file://LICENCE.cnm;md5=93b67e6bac7f8fec22b96b8ad0a1a9d0 \
                    file://LICENCE.cw1200;md5=f0f770864e7a8444a5c5aa9d12a3a7ed \
                    file://LICENCE.cypress;md5=48cd9436c763bf873961f9ed7b5c147b \
                    file://LICENSE.dib0700;md5=f7411825c8a555a1a3e5eab9ca773431 \
                    file://LICENCE.e100;md5=ec0f84136766df159a3ae6d02acdf5a8 \
                    file://LICENCE.ene_firmware;md5=ed67f0f62f8f798130c296720b7d3921 \
                    file://LICENCE.fw_sst_0f28;md5=6353931c988ad52818ae733ac61cd293 \
                    file://LICENCE.go7007;md5=c0bb9f6aaaba55b0529ee9b30aa66beb \
                    file://LICENSE.hfi1_firmware;md5=5e7b6e586ce7339d12689e49931ad444 \
                    file://LICENSE.i915;md5=2b0b2e0d20984affd4490ba2cba02570 \
                    file://LICENCE.ibt_firmware;md5=fdbee1ddfe0fb7ab0b2fcd6b454a366b \
                    file://LICENSE.ice;md5=742ab4850f2670792940e6d15c974b2f \
                    file://LICENSE.ice_enhanced;md5=f305cfc31b64f95f774f9edd9df0224d \
                    file://LICENCE.IntcSST2;md5=9e7d8bea77612d7cc7d9e9b54b623062 \
                    file://LICENCE.it913x;md5=1fbf727bfb6a949810c4dbfa7e6ce4f8 \
                    file://LICENCE.iwlwifi_firmware;md5=2ce6786e0fc11ac6e36b54bb9b799f1b \
                    file://LICENCE.kaweth;md5=b1d876e562f4b3b8d391ad8395dfe03f \
                    file://LICENCE.linaro;md5=936d91e71cf9cd30e733db4bf11661cc \
                    file://LICENSE.Lontium;md5=4ec8dc582ff7295f39e2ca6a7b0be2b6 \
                    file://LICENCE.mali_csffw;md5=e064aaec4d21ef856e1b76a6f5dc435f \
                    file://LICENCE.Marvell;md5=28b6ed8bd04ba105af6e4dcd6e997772 \
                    file://LICENCE.mediatek;md5=7c1976b63217d76ce47d0a11d8a79cf2 \
                    file://LICENCE.microchip;md5=db753b00305675dfbf120e3f24a47277 \
                    file://LICENCE.moxa;md5=1086614767d8ccf744a923289d3d4261 \
                    file://LICENCE.myri10ge_firmware;md5=42e32fb89f6b959ca222e25ac8df8fed \
                    file://LICENCE.Netronome;md5=4add08f2577086d44447996503cddf5f \
                    file://LICENCE.nvidia;md5=4428a922ed3ba2ceec95f076a488ce07 \
                    file://LICENCE.NXP;md5=58bb8ba632cd729b9ba6183bc6aed36f \
                    file://LICENSE.nxp;md5=cca321ca1524d6a1e4fed87486cd82dc \
                    file://LICENSE.nxp_mc_firmware;md5=9dc97e4b279b3858cae8879ae2fe5dd7 \
                    file://LICENCE.OLPC;md5=5b917f9d8c061991be4f6f5f108719cd \
                    file://LICENCE.open-ath9k-htc-firmware;md5=1b33c9f4d17bc4d457bdb23727046837 \
                    file://LICENCE.phanfw;md5=954dcec0e051f9409812b561ea743bfa \
                    file://LICENSE.powervr;md5=83045ed2a2cda15b4eaff682c98c9533 \
                    file://LICENCE.qat_firmware;md5=72de83dfd9b87be7685ed099a39fbea4 \
                    file://LICENSE.qcom;md5=164e3362a538eb11d3ac51e8e134294b \
                    file://LICENSE.qcom_yamato;md5=d0de0eeccaf1843a850bf7a6777eec5c \
                    file://LICENCE.qla1280;md5=d6895732e622d950609093223a2c4f5d \
                    file://LICENCE.qla2xxx;md5=505855e921b75f1be4a437ad9b79dff0 \
                    file://LICENSE.QualcommAtheros_ar3k;md5=b5fe244fb2b532311de1472a3bc06da5 \
                    file://LICENSE.QualcommAtheros_ath10k;md5=cb42b686ee5f5cb890275e4321db60a8 \
                    file://LICENCE.r8a779x_usb3;md5=4c1671656153025d7076105a5da7e498 \
                    file://LICENSE.radeon;md5=68ec28bacb3613200bca44f404c69b16 \
                    file://LICENCE.ralink_a_mediatek_company_firmware;md5=728f1a85fd53fd67fa8d7afb080bc435 \
                    file://LICENCE.ralink-firmware.txt;md5=ab2c269277c45476fb449673911a2dfd \
                    file://LICENCE.rockchip;md5=5fd70190c5ed39734baceada8ecced26 \
                    file://LICENCE.rtlwifi_firmware.txt;md5=00d06cfd3eddd5a2698948ead2ad54a5 \
                    file://LICENSE.sdma_firmware;md5=51e8c19ecc2270f4b8ea30341ad63ce9 \
                    file://LICENCE.siano;md5=4556c1bf830067f12ca151ad953ec2a5 \
                    file://LICENCE.ti-connectivity;md5=3b1e9cf54aba8146dad4b735777d406f \
                    file://LICENCE.ti-keystone;md5=3a86335d32864b0bef996bee26cc0f2c \
                    file://LICENCE.ueagle-atm4-firmware;md5=4ed7ea6b507ccc583b9d594417714118 \
                    file://LICENCE.via_vt6656;md5=e4159694cba42d4377a912e78a6e850f \
                    file://LICENCE.wl1251;md5=ad3f81922bb9e197014bb187289d3b5b \
                    file://LICENCE.xc4000;md5=0ff51d2dc49fce04814c9155081092f0 \
                    file://LICENCE.xc5000;md5=1e170c13175323c32c7f4d0998d53f66 \
                    file://LICENCE.xc5000c;md5=12b02efa3049db65d524aeb418dd87ca \
                    file://wfx/LICENCE.wf200;md5=4d1beff00d902c05c9c7e95a5d8eb52d \
                    file://WHENCE;md5=${WHENCE_CHKSUM} \
                    "
# WHENCE checksum is defined separately to ease overriding it if
# class-devupstream is selected.
WHENCE_CHKSUM  = "c90a25ccbd027eb8d61b2943ab68447c"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-Abilis] = "LICENCE.Abilis"
NO_GENERIC_LICENSE[Firmware-adsp_sst] = "LICENCE.adsp_sst"
NO_GENERIC_LICENSE[Firmware-agere] = "LICENCE.agere"
NO_GENERIC_LICENSE[Firmware-amdgpu] = "LICENSE.amdgpu"
NO_GENERIC_LICENSE[Firmware-amd-ucode] = "LICENSE.amd-ucode"
NO_GENERIC_LICENSE[Firmware-amlogic_vdec] = "LICENSE.amlogic_vdec"
NO_GENERIC_LICENSE[Firmware-amphion_vpu] = "LICENSE.amphion_vpu"
NO_GENERIC_LICENSE[Firmware-atheros_firmware] = "LICENCE.atheros_firmware"
NO_GENERIC_LICENSE[Firmware-atmel] = "LICENSE.atmel"
NO_GENERIC_LICENSE[Firmware-broadcom_bcm43xx] = "LICENCE.broadcom_bcm43xx"
NO_GENERIC_LICENSE[Firmware-ca0132] = "LICENCE.ca0132"
NO_GENERIC_LICENSE[Firmware-cadence] = "LICENCE.cadence"
NO_GENERIC_LICENSE[Firmware-cavium] = "LICENCE.cavium"
NO_GENERIC_LICENSE[Firmware-chelsio_firmware] = "LICENCE.chelsio_firmware"
NO_GENERIC_LICENSE[Firmware-cirrus] = "LICENSE.cirrus"
NO_GENERIC_LICENSE[Firmware-cnm] = "LICENCE.cnm"
NO_GENERIC_LICENSE[Firmware-cw1200] = "LICENCE.cw1200"
NO_GENERIC_LICENSE[Firmware-cypress] = "LICENCE.cypress"
NO_GENERIC_LICENSE[Firmware-dib0700] = "LICENSE.dib0700"
NO_GENERIC_LICENSE[Firmware-e100] = "LICENCE.e100"
NO_GENERIC_LICENSE[Firmware-ene_firmware] = "LICENCE.ene_firmware"
NO_GENERIC_LICENSE[Firmware-fw_sst_0f28] = "LICENCE.fw_sst_0f28"
NO_GENERIC_LICENSE[Firmware-go7007] = "LICENCE.go7007"
NO_GENERIC_LICENSE[Firmware-hfi1_firmware] = "LICENSE.hfi1_firmware"
NO_GENERIC_LICENSE[Firmware-i915] = "LICENSE.i915"
NO_GENERIC_LICENSE[Firmware-ibt_firmware] = "LICENCE.ibt_firmware"
NO_GENERIC_LICENSE[Firmware-ice] = "LICENSE.ice"
NO_GENERIC_LICENSE[Firmware-ice_enhanced] = "LICENSE.ice_enhanced"
NO_GENERIC_LICENSE[Firmware-IntcSST2] = "LICENCE.IntcSST2"
NO_GENERIC_LICENSE[Firmware-it913x] = "LICENCE.it913x"
NO_GENERIC_LICENSE[Firmware-iwlwifi_firmware] = "LICENCE.iwlwifi_firmware"
NO_GENERIC_LICENSE[Firmware-kaweth] = "LICENCE.kaweth"
NO_GENERIC_LICENSE[Firmware-linaro] = "LICENCE.linaro"
NO_GENERIC_LICENSE[Firmware-Lontium] = "LICENSE.Lontium"
NO_GENERIC_LICENSE[Firmware-mali_csffw] = "LICENCE.mali_csffw"
NO_GENERIC_LICENSE[Firmware-Marvell] = "LICENCE.Marvell"
NO_GENERIC_LICENSE[Firmware-mediatek] = "LICENCE.mediatek"
NO_GENERIC_LICENSE[Firmware-microchip] = "LICENCE.microchip"
NO_GENERIC_LICENSE[Firmware-moxa] = "LICENCE.moxa"
NO_GENERIC_LICENSE[Firmware-myri10ge_firmware] = "LICENCE.myri10ge_firmware"
NO_GENERIC_LICENSE[Firmware-netronome] = "LICENCE.Netronome"
NO_GENERIC_LICENSE[Firmware-nvidia] = "LICENCE.nvidia"
NO_GENERIC_LICENSE[Firmware-nxp] = "LICENSE.nxp"
NO_GENERIC_LICENSE[Firmware-nxp_mc_firmware] = "LICENSE.nxp_mc_firmware"
NO_GENERIC_LICENSE[Firmware-OLPC] = "LICENCE.OLPC"
NO_GENERIC_LICENSE[Firmware-ath9k-htc] = "LICENCE.open-ath9k-htc-firmware"
NO_GENERIC_LICENSE[Firmware-phanfw] = "LICENCE.phanfw"
NO_GENERIC_LICENSE[Firmware-powervr] = "LICENSE.powervr"
NO_GENERIC_LICENSE[Firmware-qat] = "LICENCE.qat_firmware"
NO_GENERIC_LICENSE[Firmware-qcom] = "LICENSE.qcom"
NO_GENERIC_LICENSE[Firmware-qcom-yamato] = "LICENSE.qcom_yamato"
NO_GENERIC_LICENSE[Firmware-qla1280] = "LICENCE.qla1280"
NO_GENERIC_LICENSE[Firmware-qla2xxx] = "LICENCE.qla2xxx"
NO_GENERIC_LICENSE[Firmware-qualcommAthos_ar3k] = "LICENSE.QualcommAtheros_ar3k"
NO_GENERIC_LICENSE[Firmware-qualcommAthos_ath10k] = "LICENSE.QualcommAtheros_ath10k"
NO_GENERIC_LICENSE[Firmware-r8a779x_usb3] = "LICENCE.r8a779x_usb3"
NO_GENERIC_LICENSE[Firmware-radeon] = "LICENSE.radeon"
NO_GENERIC_LICENSE[Firmware-ralink_a_mediatek_company_firmware] = "LICENCE.ralink_a_mediatek_company_firmware"
NO_GENERIC_LICENSE[Firmware-ralink-firmware] = "LICENCE.ralink-firmware.txt"
NO_GENERIC_LICENSE[Firmware-rockchip] = "LICENCE.rockchip"
NO_GENERIC_LICENSE[Firmware-rtlwifi_firmware] = "LICENCE.rtlwifi_firmware.txt"
NO_GENERIC_LICENSE[Firmware-siano] = "LICENCE.siano"
NO_GENERIC_LICENSE[Firmware-imx-sdma_firmware] = "LICENSE.sdma_firmware"
NO_GENERIC_LICENSE[Firmware-ti-connectivity] = "LICENCE.ti-connectivity"
NO_GENERIC_LICENSE[Firmware-ti-keystone] = "LICENCE.ti-keystone"
NO_GENERIC_LICENSE[Firmware-ueagle-atm4-firmware] = "LICENCE.ueagle-atm4-firmware"
NO_GENERIC_LICENSE[Firmware-via_vt6656] = "LICENCE.via_vt6656"
NO_GENERIC_LICENSE[Firmware-wfx] = "wfx/LICENCE.wf200"
NO_GENERIC_LICENSE[Firmware-wl1251] = "LICENCE.wl1251"
NO_GENERIC_LICENSE[Firmware-xc4000] = "LICENCE.xc4000"
NO_GENERIC_LICENSE[Firmware-xc5000] = "LICENCE.xc5000"
NO_GENERIC_LICENSE[Firmware-xc5000c] = "LICENCE.xc5000c"
NO_GENERIC_LICENSE[WHENCE] = "WHENCE"

PE = "1"

SRC_URI = "\
  ${KERNELORG_MIRROR}/linux/kernel/firmware/${BPN}-${PV}.tar.xz \
"

BBCLASSEXTEND = "devupstream:target"
SRC_URI:class-devupstream = "git://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git;protocol=https;branch=main"
# Pin this to the 20220509 release, override this in local.conf
SRCREV:class-devupstream ?= "b19cbdca78ab2adfd210c91be15a22568e8b8cae"

SRC_URI[sha256sum] = "a12cb38f4793394bf56c6702be29b6e4f09deca0714943bba98a492030651570"

inherit allarch

CLEANBROKEN = "1"

PACKAGECONFIG ??= ""
PACKAGECONFIG[deduplicate] = ",,rdfind-native"

# Possible values are "xz" and "zst".
FIRMWARE_COMPRESSION ?= ""

# Specifying -j requires GNU parallel, which is a part of meta-oe
PARALLEL_MAKE = ""

def fw_compr_suffix(d):
    compr = d.getVar('FIRMWARE_COMPRESSION')
    if compr == '':
        return ''
    return '-' + compr

do_compile() {
	:
}

do_install() {
        sed -i 's:^./check_whence.py:#./check_whence.py:' ${S}/copy-firmware.sh

        oe_runmake 'DESTDIR=${D}' 'FIRMWAREDIR=${nonarch_base_libdir}/firmware' install${@fw_compr_suffix(d)}
        if [ "${@bb.utils.contains('PACKAGECONFIG', 'deduplicate', '1', '0', d)}" = "1" ]; then
                oe_runmake 'DESTDIR=${D}' 'FIRMWAREDIR=${nonarch_base_libdir}/firmware' dedup
        fi
        cp LICEN[CS]E.* WHENCE ${D}${nonarch_base_libdir}/firmware/
        cp wfx/LICEN[CS]E.* ${D}${nonarch_base_libdir}/firmware/wfx/
}


PACKAGES =+ "${PN}-amphion-vpu-license ${PN}-amphion-vpu \
             ${PN}-cw1200-license ${PN}-cw1200 \
             ${PN}-ralink-license ${PN}-ralink \
             ${PN}-mt76x-license ${PN}-mt7601u ${PN}-mt7650 ${PN}-mt76x2 \
             ${PN}-radeon-license ${PN}-radeon \
             ${PN}-amdgpu-license ${PN}-amdgpu \
             ${PN}-amdgpu-aldebaran \
             ${PN}-amdgpu-carrizo \
             ${PN}-amdgpu-cezanne \
             ${PN}-amdgpu-fiji \
             ${PN}-amdgpu-hawaii \
             ${PN}-amdgpu-navi10 \
             ${PN}-amdgpu-navi14 \
             ${PN}-amdgpu-navi21 \
             ${PN}-amdgpu-navi22 \
             ${PN}-amdgpu-navi23 \
             ${PN}-amdgpu-navi24 \
             ${PN}-amdgpu-navi31 \
             ${PN}-amdgpu-navi32 \
             ${PN}-amdgpu-oland \
             ${PN}-amdgpu-polaris10 \
             ${PN}-amdgpu-polaris11 \
             ${PN}-amdgpu-polaris12 \
             ${PN}-amdgpu-raven \
             ${PN}-amdgpu-rembrandt \
             ${PN}-amdgpu-renoir \
             ${PN}-amdgpu-stoney \
             ${PN}-amdgpu-tonga \
             ${PN}-amdgpu-topaz \
             ${PN}-amdgpu-vega10 \
             ${PN}-amdgpu-vega12 \
             ${PN}-amdgpu-misc \
             ${PN}-marvell-license ${PN}-pcie8897 ${PN}-pcie8997 \
             ${PN}-mediatek-license ${PN}-mediatek \
             ${PN}-microchip-license ${PN}-microchip \
             ${PN}-moxa-license ${PN}-moxa \
             ${PN}-sd8686 ${PN}-sd8688 ${PN}-sd8787 ${PN}-sd8797 ${PN}-sd8801 \
             ${PN}-sd8887 ${PN}-sd8897 ${PN}-sd8997 ${PN}-usb8997 \
             ${PN}-ti-connectivity-license ${PN}-wlcommon ${PN}-wl12xx ${PN}-wl18xx ${PN}-cc33xx \
             ${PN}-ti-keystone-license ${PN}-ti-keystone \
             ${PN}-vt6656-license ${PN}-vt6656 \
             ${PN}-rs9113 ${PN}-rs9116 \
             ${PN}-rtl-license ${PN}-rtl8188 ${PN}-rtl8192cu ${PN}-rtl8192ce ${PN}-rtl8192su ${PN}-rtl8723 ${PN}-rtl8821 \
             ${PN}-rtl8761 \
             ${PN}-rtl8168 \
             ${PN}-rtl8822 \
             ${PN}-rtl-nic \
             ${PN}-cypress-license \
             ${PN}-broadcom-license \
             ${PN}-bcm-0bb4-0306 \
             ${PN}-bcm43143 \
             ${PN}-bcm43236b \
             ${PN}-bcm43241b0 \
             ${PN}-bcm43241b4 \
             ${PN}-bcm43241b5 \
             ${PN}-bcm43242a \
             ${PN}-bcm4329 \
             ${PN}-bcm4329-fullmac \
             ${PN}-bcm4330 \
             ${PN}-bcm4334 \
             ${PN}-bcm43340 \
             ${PN}-bcm4335 \
             ${PN}-bcm43362 \
             ${PN}-bcm4339 \
             ${PN}-bcm43430 \
             ${PN}-bcm43430a0 \
             ${PN}-bcm43455 \
             ${PN}-bcm4350 \
             ${PN}-bcm4350c2 \
             ${PN}-bcm4354 \
             ${PN}-bcm4356 \
             ${PN}-bcm4356-pcie \
             ${PN}-bcm43569 \
             ${PN}-bcm43570 \
             ${PN}-bcm4358 \
             ${PN}-bcm43602 \
             ${PN}-bcm4366b \
             ${PN}-bcm4366c \
             ${PN}-bcm4371 \
             ${PN}-bcm4373 \
             ${PN}-bcm43xx \
             ${PN}-bcm43xx-hdr \
             ${PN}-cirrus-license ${PN}-cirrus \
             ${PN}-cnm-license ${PN}-cnm \
             ${PN}-atheros-license ${PN}-ar5523 ${PN}-ar9170 ${PN}-ath6k ${PN}-ath9k ${PN}-ath3k \
             ${PN}-carl9170 \
             ${PN}-qcom-qcm2290-wifi ${PN}-qcom-qrb4210-wifi ${PN}-qcom-sdm845-modem \
             ${PN}-ar3k-license ${PN}-ar3k \
             ${PN}-ath10k-license ${PN}-ath10k \
             ${PN}-ath10k-qca4019 \
             ${PN}-ath10k-qca6174 \
             ${PN}-ath10k-qca9377 \
             ${PN}-ath10k-qca9887 \
             ${PN}-ath10k-qca9888 \
             ${PN}-ath10k-qca988x \
             ${PN}-ath10k-qca9984 \
             ${PN}-ath10k-qca99x0 \
             ${PN}-ath10k-wcn3990 \
             ${PN}-ath10k-misc \
             ${PN}-ath11k \
             ${PN}-ath11k-ipq5018 \
             ${PN}-ath11k-ipq6018 \
             ${PN}-ath11k-ipq8074 \
             ${PN}-ath11k-qca2066 \
             ${PN}-ath11k-qca6390 \
             ${PN}-ath11k-qcn9074 \
             ${PN}-ath11k-wcn6750 \
             ${PN}-ath11k-wcn6855 \
             ${PN}-ath11k-misc \
             ${PN}-ath12k \
             ${PN}-ath12k-qcn9274 \
             ${PN}-ath12k-wcn7850 \
             ${PN}-ath12k-misc \
             ${PN}-qca \
             ${PN}-qca-qca61x4-serial \
             ${PN}-qca-qca61x4-usb \
             ${PN}-qca-qca6390 \
             ${PN}-qca-wcn3988 \
             ${PN}-qca-wcn399x \
             ${PN}-qca-wcn6750 \
             ${PN}-qca-qca2066 \
             ${PN}-qca-wcn7850 \
             ${PN}-qca-misc \
             \
             ${PN}-imx-sdma-license ${PN}-imx-sdma-imx6q ${PN}-imx-sdma-imx7d \
             \
             ${PN}-iwlwifi-license ${PN}-iwlwifi \
             ${PN}-iwlwifi-135-6 \
             ${PN}-iwlwifi-3160-7 ${PN}-iwlwifi-3160-8 ${PN}-iwlwifi-3160-9 \
             ${PN}-iwlwifi-3160-10 ${PN}-iwlwifi-3160-12 ${PN}-iwlwifi-3160-13 \
             ${PN}-iwlwifi-3160-16 ${PN}-iwlwifi-3160-17 \
             ${PN}-iwlwifi-6000-4 ${PN}-iwlwifi-6000g2a-5 ${PN}-iwlwifi-6000g2a-6 \
             ${PN}-iwlwifi-6000g2b-5 ${PN}-iwlwifi-6000g2b-6 \
             ${PN}-iwlwifi-6050-4 ${PN}-iwlwifi-6050-5 \
             ${PN}-iwlwifi-7260 \
             ${PN}-iwlwifi-7265 \
             ${PN}-iwlwifi-7265d ${PN}-iwlwifi-8000c ${PN}-iwlwifi-8265 \
             ${PN}-iwlwifi-9000 \
             ${PN}-iwlwifi-9260 \
             ${PN}-iwlwifi-misc \
             ${PN}-ibt-license ${PN}-ibt \
             ${PN}-ibt-11-5 ${PN}-ibt-12-16 ${PN}-ibt-hw-37-7 ${PN}-ibt-hw-37-8 \
             ${PN}-ibt-17 \
             ${PN}-ibt-18 \
             ${PN}-ibt-20 \
             ${PN}-ibt-misc \
             ${PN}-i915-license ${PN}-i915 \
             ${PN}-ice-license ${PN}-ice \
             ${PN}-ice-enhanced-license ${PN}-ice-enhanced \
             ${PN}-adsp-sst-license ${PN}-adsp-sst \
             ${PN}-bnx2 \
             ${PN}-bnx2x \
             ${PN}-liquidio \
             ${PN}-linaro-license \
             ${PN}-mali-csffw-arch108 ${PN}-mali-csffw-license \
             ${PN}-mellanox \
             ${PN}-nvidia-license \
             ${PN}-nvidia-tegra-k1 ${PN}-nvidia-tegra \
             ${PN}-nvidia-gpu \
             ${PN}-nxp-license \
             ${PN}-nxp8987-sdio \
             ${PN}-nxp8997-common \
             ${PN}-nxp8997-pcie \
             ${PN}-nxp8997-sdio \
             ${PN}-nxp9098-common \
             ${PN}-nxp9098-pcie \
             ${PN}-nxp9098-sdio \
             ${PN}-nxpiw416-sdio \
             ${PN}-nxpiw612-sdio \
             ${PN}-nxp-mc-license ${PN}-nxp-mc \
             ${PN}-netronome-license ${PN}-netronome \
             ${PN}-olpc-license ${PN}-olpc \
             ${PN}-phanfw-license ${PN}-phanfw \
             ${PN}-powervr-license ${PN}-powervr \
             ${PN}-prestera \
             ${PN}-qat ${PN}-qat-license \
             ${PN}-qed \
             ${PN}-qcom-license ${PN}-qcom-yamato-license \
             ${PN}-qcom-aic100 ${PN}-qcom-qdu100 \
             ${PN}-qcom-venus-1.8 ${PN}-qcom-venus-4.2 ${PN}-qcom-venus-5.2 ${PN}-qcom-venus-5.4 ${PN}-qcom-venus-6.0 \
             ${PN}-qcom-vpu \
             ${PN}-qcom-adreno-a2xx ${PN}-qcom-adreno-a3xx ${PN}-qcom-adreno-a4xx ${PN}-qcom-adreno-a530 \
             ${PN}-qcom-adreno-a630 ${PN}-qcom-adreno-a650 ${PN}-qcom-adreno-a660 ${PN}-qcom-adreno-a663 \
             ${PN}-qcom-adreno-a702 ${PN}-qcom-adreno-g750 \
             ${PN}-qcom-apq8016-modem ${PN}-qcom-apq8016-wifi \
             ${PN}-qcom-apq8096-adreno ${PN}-qcom-apq8096-audio ${PN}-qcom-apq8096-modem \
             ${PN}-qcom-qcm2290-adreno ${PN}-qcom-qcm2290-audio ${PN}-qcom-qcm2290-modem \
             ${PN}-qcom-qcm6490-adreno \
             ${PN}-qcom-qcm6490-audio ${PN}-qcom-qcm6490-compute ${PN}-qcom-qcm6490-ipa \
             ${PN}-qcom-qcs615-adreno \
             ${PN}-qcom-qrb4210-adreno \
             ${PN}-qcom-qrb4210-audio ${PN}-qcom-qrb4210-compute ${PN}-qcom-qrb4210-modem \
             ${PN}-qcom-sa8775p-adreno \
             ${PN}-qcom-sc8280xp-lenovo-x13s-compat \
             ${PN}-qcom-sc8280xp-lenovo-x13s-audio \
             ${PN}-qcom-sc8280xp-lenovo-x13s-adreno \
             ${PN}-qcom-sc8280xp-lenovo-x13s-compute \
             ${PN}-qcom-sc8280xp-lenovo-x13s-sensors \
             ${PN}-qcom-sdm845-adreno ${PN}-qcom-sdm845-audio ${PN}-qcom-sdm845-compute \
             ${PN}-qcom-sdm845-thundercomm-db845c-sensors \
             ${PN}-qcom-sm8250-adreno ${PN}-qcom-sm8250-audio ${PN}-qcom-sm8250-compute \
             ${PN}-qcom-sm8250-thundercomm-rb5-sensors \
             ${PN}-qcom-sm8550-audio-tplg \
             ${PN}-qcom-sm8650-audio-tplg \
             ${PN}-qcom-x1e80100-adreno ${PN}-qcom-x1e80100-audio \
             ${PN}-qla2xxx ${PN}-qla2xxx-license \
             ${PN}-rockchip-license ${PN}-rockchip-dptx \
             ${PN}-amlogic-vdec-license ${PN}-amlogic-vdec \
             ${PN}-lt9611uxc ${PN}-lontium-license \
             ${PN}-wfx-license ${PN}-wfx \
             ${PN}-whence-license \
             ${PN}-wl1251-license ${PN}-wl1251 \
             ${PN}-xc4000-license ${PN}-xc4000 \
             ${PN}-xc5000-license ${PN}-xc5000 \
             ${PN}-xc5000c-license ${PN}-xc5000c \
             ${PN}-license \
             "

# For Amphion VPU
LICENSE:${PN}-amphion-vpu = "Firmware-amphion_vpu"
LICENSE:${PN}-amphion-vpu-license = "Firmware-amphion_vpu"

FILES:${PN}-amphion-vpu = "${nonarch_base_libdir}/firmware/amphion/*"
FILES:${PN}-amphion-vpu-license = " \
  ${nonarch_base_libdir}/firmware/LICENSE.amphion_vpu \
"
RDEPENDS:${PN}-amphion-vpu += "${PN}-amphion-vpu-license"

# For cw1200
LICENSE:${PN}-cw1200 = "Firmware-cw1200"
LICENSE:${PN}-cw1200-license = "Firmware-cw1200"

FILES:${PN}-cw1200 = "${nonarch_base_libdir}/firmware/wsm_22.bin*"
FILES:${PN}-cw1200-license = "${nonarch_base_libdir}/firmware/LICENCE.cw1200"

RDEPENDS:${PN}-cw1200 += "${PN}-cw1200-license"

# For atheros
LICENSE:${PN}-ar5523 = "Firmware-atheros_firmware"
LICENSE:${PN}-ar9170 = "Firmware-atheros_firmware"
LICENSE:${PN}-ath3k = "Firmware-atheros_firmware"
LICENSE:${PN}-ath6k = "Firmware-atheros_firmware"
LICENSE:${PN}-ath9k = "Firmware-atheros_firmware"
LICENSE:${PN}-atheros-license = "Firmware-atheros_firmware"

FILES:${PN}-atheros-license = "${nonarch_base_libdir}/firmware/LICENCE.atheros_firmware"
FILES:${PN}-ar5523 = " \
  ${nonarch_base_libdir}/firmware/ar5523.bin* \
"
FILES:${PN}-ar9170 = " \
  ${nonarch_base_libdir}/firmware/ar9170*.fw* \
"
FILES:${PN}-ath3k = " \
  ${nonarch_base_libdir}/firmware/ath3k*fw* \
"
FILES:${PN}-ath6k = " \
  ${nonarch_base_libdir}/firmware/ath6k \
"
FILES:${PN}-ath9k = " \
  ${nonarch_base_libdir}/firmware/ar9271.fw* \
  ${nonarch_base_libdir}/firmware/ar7010*.fw* \
  ${nonarch_base_libdir}/firmware/htc_9271.fw* \
  ${nonarch_base_libdir}/firmware/htc_7010.fw* \
  ${nonarch_base_libdir}/firmware/ath9k_htc/htc_7010-1.4.0.fw* \
  ${nonarch_base_libdir}/firmware/ath9k_htc/htc_9271-1.4.0.fw* \
"

RDEPENDS:${PN}-ar5523 += "${PN}-atheros-license"
RDEPENDS:${PN}-ar9170 += "${PN}-atheros-license"
RDEPENDS:${PN}-ath6k += "${PN}-atheros-license"
RDEPENDS:${PN}-ath9k += "${PN}-atheros-license"

# For carl9170

FILES:${PN}-carl9170 = " \
  ${nonarch_base_libdir}/firmware/carl9170*.fw* \
"
LICENSE:${PN}-carl9170 = "GPL-2.0-or-later"

# For QualCommAthos
LICENSE:${PN}-ar3k = "Firmware-qualcommAthos_ar3k & Firmware-atheros_firmware"
LICENSE:${PN}-ar3k-license = "Firmware-qualcommAthos_ar3k"
LICENSE:${PN}-ath10k = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca4019 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca6174 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca9377 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca9887 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca9888 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca988x = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca9984 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-qca99x0 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-wcn3990 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-misc    = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath10k-license = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-ipq5018 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-ipq6018 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-ipq8074 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qca2066 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qca6390 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qcn9074 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-wcn6750 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-wcn6855 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-misc    = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k-qcn9274 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k-wcn7850 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k-misc = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca61x4-serial = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca61x4-usb = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca6390 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn3988 = "Firmware-qcom"
LICENSE:${PN}-qca-wcn399x = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn6750 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca2066 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn7850 = "Firmware-qcom"
LICENSE:${PN}-qca-misc = "Firmware-qualcommAthos_ath10k & Firmware-qcom"

FILES:${PN}-ar3k-license = "${nonarch_base_libdir}/firmware/LICENSE.QualcommAtheros_ar3k"
FILES:${PN}-ar3k = " \
  ${nonarch_base_libdir}/firmware/ar3k \
"

FILES:${PN}-ath10k-license = "${nonarch_base_libdir}/firmware/LICENSE.QualcommAtheros_ath10k"
FILES:${PN}-ath10k-qca4019 = "${nonarch_base_libdir}/firmware/ath10k/QCA4019"
FILES:${PN}-ath10k-qca6174 = "${nonarch_base_libdir}/firmware/ath10k/QCA6174"
FILES:${PN}-ath10k-qca9377 = "${nonarch_base_libdir}/firmware/ath10k/QCA9377"
FILES:${PN}-ath10k-qca9887 = "${nonarch_base_libdir}/firmware/ath10k/QCA9887"
FILES:${PN}-ath10k-qca9888 = "${nonarch_base_libdir}/firmware/ath10k/QCA9888"
FILES:${PN}-ath10k-qca988x = "${nonarch_base_libdir}/firmware/ath10k/QCA988X"
FILES:${PN}-ath10k-qca9984 = "${nonarch_base_libdir}/firmware/ath10k/QCA9984"
FILES:${PN}-ath10k-qca99x0 = "${nonarch_base_libdir}/firmware/ath10k/QCA99X0"
FILES:${PN}-ath10k-wcn3990 = "${nonarch_base_libdir}/firmware/ath10k/WCN3990"
FILES:${PN}-ath10k-misc = "${nonarch_base_libdir}/firmware/ath10k/*"
# -ath10k is a virtual package that depends upon all ath10k packages.
ALLOW_EMPTY:${PN}-ath10k = "1"
# -ath10k-misc is a catch all package that includes all the ath10k
# firmwares that are not already included in other -ath10k- packages.
ALLOW_EMPTY:${PN}-ath10k-misc = "1"

FILES:${PN}-ath11k-ipq5018 = "${nonarch_base_libdir}/firmware/ath11k/IPQ5018"
FILES:${PN}-ath11k-ipq6018 = "${nonarch_base_libdir}/firmware/ath11k/IPQ6018"
FILES:${PN}-ath11k-ipq8074 = "${nonarch_base_libdir}/firmware/ath11k/IPQ8074"
FILES:${PN}-ath11k-qca2066 = "${nonarch_base_libdir}/firmware/ath11k/QCA2066"
FILES:${PN}-ath11k-qca6390 = "${nonarch_base_libdir}/firmware/ath11k/QCA6390"
FILES:${PN}-ath11k-qcn9074 = "${nonarch_base_libdir}/firmware/ath11k/QCN9074"
FILES:${PN}-ath11k-wcn6750 = "${nonarch_base_libdir}/firmware/ath11k/WCN6750"
FILES:${PN}-ath11k-wcn6855 = "${nonarch_base_libdir}/firmware/ath11k/WCN6855"
FILES:${PN}-ath11k-misc = "${nonarch_base_libdir}/firmware/ath11k/*"
# -ath11k is a virtual package that depends upon all ath11k packages.
ALLOW_EMPTY:${PN}-ath11k = "1"
# -ath11k-misc is a catch all package that includes all the ath11k
# firmwares that are not already included in other -ath11k- packages.
ALLOW_EMPTY:${PN}-ath11k-misc = "1"

FILES:${PN}-ath12k-qcn9274 = "${nonarch_base_libdir}/firmware/ath12k/QCN9274"
FILES:${PN}-ath12k-wcn7850 = "${nonarch_base_libdir}/firmware/ath12k/WCN7850"
FILES:${PN}-ath12k-misc = "${nonarch_base_libdir}/firmware/ath12k/*"
# -ath12k is a virtual package that depends upon all ath12k packages.
ALLOW_EMPTY:${PN}-ath12k = "1"
# -ath12k-misc is a catch all package that includes all the ath12k
# firmwares that are not already included in other -ath12k- packages.
ALLOW_EMPTY:${PN}-ath12k-misc = "1"

FILES:${PN}-qca-qca61x4-serial = " \
  ${nonarch_base_libdir}/firmware/qca/nvm_0*.bin \
  ${nonarch_base_libdir}/firmware/qca/rampatch_0*.bin \
"
FILES:${PN}-qca-qca61x4-usb = " \
  ${nonarch_base_libdir}/firmware/qca/rampatch_usb_*.bin \
  ${nonarch_base_libdir}/firmware/qca/rampatch_usb_*.bin \
"
FILES:${PN}-qca-qca6390 = " \
  ${nonarch_base_libdir}/firmware/qca/htbtfw20.tlv \
  ${nonarch_base_libdir}/firmware/qca/htnv20.bin \
"
FILES:${PN}-qca-wcn3988 = " \
  ${nonarch_base_libdir}/firmware/qca/apbtfw10.tlv \
  ${nonarch_base_libdir}/firmware/qca/apbtfw11.tlv \
  ${nonarch_base_libdir}/firmware/qca/apnv10.bin \
  ${nonarch_base_libdir}/firmware/qca/apnv11.bin \
"
FILES:${PN}-qca-wcn399x = " \
  ${nonarch_base_libdir}/firmware/qca/crbtfw21.tlv \
  ${nonarch_base_libdir}/firmware/qca/crnv21.bin \
  ${nonarch_base_libdir}/firmware/qca/crbtfw32.tlv \
  ${nonarch_base_libdir}/firmware/qca/crnv32.bin \
  ${nonarch_base_libdir}/firmware/qca/crnv32u.bin \
"
FILES:${PN}-qca-wcn6750 = " \
  ${nonarch_base_libdir}/firmware/qca/msbtfw11.mbn \
  ${nonarch_base_libdir}/firmware/qca/msbtfw11.tlv \
  ${nonarch_base_libdir}/firmware/qca/msnv11.bin \
  ${nonarch_base_libdir}/firmware/qca/msnv11.b0a \
  ${nonarch_base_libdir}/firmware/qca/msnv11.b09 \
"
FILES:${PN}-qca-qca2066 = " \
  ${nonarch_base_libdir}/firmware/qca/hpbtfw21.tlv \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.bin \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.bin \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.301 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.302 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.301 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.302 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.309 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.309 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b8c \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b9f \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba0 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba1 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba2 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba3 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba4 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.baa \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.bb8 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b10c \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b111 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b8c \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b9f \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba0 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba1 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba2 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba3 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba4 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.baa \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.bb8 \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b10c \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b111 \
"
FILES:${PN}-qca-wcn7850 = " \
  ${nonarch_base_libdir}/firmware/qca/hmtbtfw20.tlv \
  ${nonarch_base_libdir}/firmware/qca/hmtnv20.b10f \
  ${nonarch_base_libdir}/firmware/qca/hmtnv20.b112 \
  ${nonarch_base_libdir}/firmware/qca/hmtnv20.bin \
"
FILES:${PN}-qca-misc = "${nonarch_base_libdir}/firmware/qca/*"
# -qca is a virtual package that depends upon all qca packages.
ALLOW_EMPTY:${PN}-qca = "1"
# -qca-misc is a catch all package that includes all the qca
# firmwares that are not already included in other -qca- packages.
ALLOW_EMPTY:${PN}-qca-misc = "1"


RDEPENDS:${PN}-ar3k += "${PN}-ar3k-license ${PN}-atheros-license"
RDEPENDS:${PN}-ath10k += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca4019 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca6174 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca9377 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca9887 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca9888 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca988x += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca9984 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-qca99x0 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-wcn3990 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath10k-misc += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-ipq5018 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-ipq6018 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-ipq8074 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qca2066 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qca6390 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qcn9074 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-wcn6750 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-wcn6855 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-misc += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k-qcn9274 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k-wcn7850 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k-misc += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca += "${PN}-ath10k-license ${PN}-qcom-license"
RDEPENDS:${PN}-qca-qca61x4-serial += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca61x4-usb += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca6390 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn3988 += "${PN}-qcom-license"
RDEPENDS:${PN}-qca-wcn399x += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn6750 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca2066 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn7850 += "${PN}-qcom-license"
RDEPENDS:${PN}-qca-misc += "${PN}-ath10k-license ${PN}-qcom-license"
# For ralink
LICENSE:${PN}-ralink = "Firmware-ralink-firmware"
LICENSE:${PN}-ralink-license = "Firmware-ralink-firmware"

FILES:${PN}-ralink-license = "${nonarch_base_libdir}/firmware/LICENCE.ralink-firmware.txt"
FILES:${PN}-ralink = " \
  ${nonarch_base_libdir}/firmware/rt*.bin* \
"

RDEPENDS:${PN}-ralink += "${PN}-ralink-license"

# For mediatek MT7601U
LICENSE:${PN}-mt76x-license = "Firmware-ralink_a_mediatek_company_firmware"
FILES:${PN}-mt76x-license = "${nonarch_base_libdir}/firmware/LICENCE.ralink_a_mediatek_company_firmware"

LICENSE:${PN}-mt7601u = "Firmware-ralink_a_mediatek_company_firmware"

FILES:${PN}-mt7601u = " \
  ${nonarch_base_libdir}/firmware/mediatek/mt7601u.bin* \
  ${nonarch_base_libdir}/firmware/mt7601u.bin* \
"
RDEPENDS:${PN}-mt7601u += "${PN}-mt76x-license"

# For MediaTek Bluetooth USB driver 7650
LICENSE:${PN}-mt7650 = "Firmware-ralink_a_mediatek_company_firmware"

FILES:${PN}-mt7650 = " \
  ${nonarch_base_libdir}/firmware/mediatek/mt7650.bin* \
  ${nonarch_base_libdir}/firmware/mt7650.bin* \
"
RDEPENDS:${PN}-mt7650 += "${PN}-mt76x-license"

# For MediaTek MT76x2 Wireless MACs
LICENSE:${PN}-mt76x2 = "Firmware-ralink_a_mediatek_company_firmware"

FILES:${PN}-mt76x2 = " \
  ${nonarch_base_libdir}/firmware/mediatek/mt7662.bin* \
  ${nonarch_base_libdir}/firmware/mt7662.bin* \
  ${nonarch_base_libdir}/firmware/mediatek/mt7662_rom_patch.bin* \
  ${nonarch_base_libdir}/firmware/mt7662_rom_patch.bin* \
"
RDEPENDS:${PN}-mt76x2 += "${PN}-mt76x-license"

# For MediaTek
LICENSE:${PN}-mediatek = "Firmware-mediatek"
LICENSE:${PN}-mediatek-license = "Firmware-mediatek"

FILES:${PN}-mediatek = " \
  ${nonarch_base_libdir}/firmware/mediatek/* \
  ${nonarch_base_libdir}/firmware/vpu_d.bin* \
  ${nonarch_base_libdir}/firmware/vpu_p.bin* \
"
FILES:${PN}-mediatek-license = " \
  ${nonarch_base_libdir}/firmware/LICENCE.mediatek \
"
RDEPENDS:${PN}-mediatek += "${PN}-mediatek-license"

# For Microchip
LICENSE:${PN}-microchip = "Firmware-microchip"
LICENSE:${PN}-microchip-license = "Firmware-microchip"

FILES:${PN}-microchip = "${nonarch_base_libdir}/firmware/microchip/*"
FILES:${PN}-microchip-license = " \
  ${nonarch_base_libdir}/firmware/LICENCE.microchip \
"
RDEPENDS:${PN}-microchip += "${PN}-microchip-license"

# For MOXA
LICENSE:${PN}-moxa = "Firmware-moxa"
LICENSE:${PN}-moxa-license = "Firmware-moxa"

FILES:${PN}-moxa = "${nonarch_base_libdir}/firmware/moxa"
FILES:${PN}-moxa-license = "${nonarch_base_libdir}/firmware/LICENCE.moxa"

RDEPENDS:${PN}-moxa += "${PN}-moxa-license"

# For radeon

LICENSE:${PN}-radeon = "Firmware-radeon"
LICENSE:${PN}-radeon-license = "Firmware-radeon"

FILES:${PN}-radeon-license = "${nonarch_base_libdir}/firmware/LICENSE.radeon"
FILES:${PN}-radeon = " \
  ${nonarch_base_libdir}/firmware/radeon \
"

RDEPENDS:${PN}-radeon += "${PN}-radeon-license"

# For amdgpu
LICENSE:${PN}-amdgpu = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-license = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-aldebaran = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-carrizo = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-cezanne = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-fiji = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-hawaii = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi10 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi14 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi21 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi22 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi23 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi24 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi31 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-navi32 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-oland = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-polaris10 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-polaris11 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-polaris12 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-raven = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-rembrandt = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-renoir = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-stoney = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-tonga = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-topaz = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-vega10 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-vega12 = "Firmware-amdgpu"
LICENSE:${PN}-amdgpu-misc = "Firmware-amdgpu"

FILES:${PN}-amdgpu-license = "${nonarch_base_libdir}/firmware/LICENSE.amdgpu"
FILES:${PN}-amdgpu-aldebaran = "${nonarch_base_libdir}/firmware/amdgpu/aldebaran_*.bin"
FILES:${PN}-amdgpu-carrizo = "${nonarch_base_libdir}/firmware/amdgpu/carrizo_*.bin"
FILES:${PN}-amdgpu-cezanne = "${nonarch_base_libdir}/firmware/amdgpu/green_sardine_*.bin"
FILES:${PN}-amdgpu-fiji = "${nonarch_base_libdir}/firmware/amdgpu/fiji_*.bin"
FILES:${PN}-amdgpu-hawaii = "${nonarch_base_libdir}/firmware/amdgpu/hawaii_*.bin"
FILES:${PN}-amdgpu-navi10 = "${nonarch_base_libdir}/firmware/amdgpu/navi10_*.bin"
FILES:${PN}-amdgpu-navi14 = "${nonarch_base_libdir}/firmware/amdgpu/navi14_*.bin"
FILES:${PN}-amdgpu-navi21 = "${nonarch_base_libdir}/firmware/amdgpu/sienna_cichlid_*.bin"
FILES:${PN}-amdgpu-navi22 = "${nonarch_base_libdir}/firmware/amdgpu/navy_flounder_*.bin"
FILES:${PN}-amdgpu-navi23 = "${nonarch_base_libdir}/firmware/amdgpu/dimgrey_cavefish_*.bin"
FILES:${PN}-amdgpu-navi24 = "${nonarch_base_libdir}/firmware/amdgpu/beige_goby_*.bin"
FILES:${PN}-amdgpu-navi31 = "${nonarch_base_libdir}/firmware/amdgpu/gc_11_0_0_*.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_0_sos.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_0_ta.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/smu_13_0_0.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/dcn_3_2_0_dmcub.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/sdma_6_0_0.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/vcn_4_0_0.bin \
"
FILES:${PN}-amdgpu-navi32 = "${nonarch_base_libdir}/firmware/amdgpu/dcn_3_2_0_dmcub.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/gc_11_0_3_*.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_10_sos.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_10_ta.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/sdma_6_0_3.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/smu_13_0_10.bin \
    ${nonarch_base_libdir}/firmware/amdgpu/vcn_4_0_0.bin \
"
FILES:${PN}-amdgpu-oland = "${nonarch_base_libdir}/firmware/amdgpu/oland_*.bin"
FILES:${PN}-amdgpu-polaris10 = "${nonarch_base_libdir}/firmware/amdgpu/polaris10_*.bin"
FILES:${PN}-amdgpu-polaris11 = "${nonarch_base_libdir}/firmware/amdgpu/polaris11_*.bin"
FILES:${PN}-amdgpu-polaris12 = "${nonarch_base_libdir}/firmware/amdgpu/polaris12_*.bin"
FILES:${PN}-amdgpu-raven = "${nonarch_base_libdir}/firmware/amdgpu/raven_*.bin"
FILES:${PN}-amdgpu-rembrandt = "${nonarch_base_libdir}/firmware/amdgpu/yellow_carp_*.bin"
FILES:${PN}-amdgpu-renoir = "${nonarch_base_libdir}/firmware/amdgpu/renoir_*.bin"
FILES:${PN}-amdgpu-stoney = "${nonarch_base_libdir}/firmware/amdgpu/stoney_*.bin"
FILES:${PN}-amdgpu-tonga = "${nonarch_base_libdir}/firmware/amdgpu/tonga_*.bin"
FILES:${PN}-amdgpu-topaz = "${nonarch_base_libdir}/firmware/amdgpu/topaz_*.bin"
FILES:${PN}-amdgpu-vega10 = "${nonarch_base_libdir}/firmware/amdgpu/vega10_*.bin"
FILES:${PN}-amdgpu-vega12 = "${nonarch_base_libdir}/firmware/amdgpu/vega12_*.bin"
FILES:${PN}-amdgpu-misc = "${nonarch_base_libdir}/firmware/amdgpu/*"
# -amdgpu is a virtual package that depends upon all amdgpu packages.
ALLOW_EMPTY:${PN}-amdgpu = "1"
# -amdgpu-misc is a catch all package that includes all the amdgpu
# firmwares that are not already included in other -amdgpu- packages.
ALLOW_EMPTY:${PN}-amdgpu-misc = "1"

RDEPENDS:${PN}-amdgpu += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-aldebaran += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-carrizo += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-cezanne += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-fiji += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-hawaii += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi10 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi14 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi21 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi22 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi23 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi24 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi31 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-navi32 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-oland += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-polaris10 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-polaris11 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-polaris12 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-raven += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-rembrandt += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-renoir += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-stoney += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-tonga += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-topaz += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-vega10 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-vega12 += "${PN}-amdgpu-license"
RDEPENDS:${PN}-amdgpu-misc += "${PN}-amdgpu-license"

# For lontium
LICENSE:${PN}-lt9611uxc = "Firmware-Lontium"

FILES:${PN}-lontium-license = "${nonarch_base_libdir}/firmware/LICENSE.Lontium"
FILES:${PN}-lt9611uxc = "${nonarch_base_libdir}/firmware/lt9611uxc_fw.bin*"

# For Arm Mali
LICENSE:${PN}-mali-csffw-arch108 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-license = "${nonarch_base_libdir}/firmware/LICENCE.mali_csffw"
FILES:${PN}-mali-csffw-arch108 = "${nonarch_base_libdir}/firmware/arm/mali/arch10.8/mali_csffw.bin*"

RDEPENDS:${PN}-mali-csffw-arch108 += "${PN}-mali-csffw-license"

# For marvell
LICENSE:${PN}-pcie8897 = "Firmware-Marvell"
LICENSE:${PN}-pcie8997 = "Firmware-Marvell"
LICENSE:${PN}-sd8686 = "Firmware-Marvell"
LICENSE:${PN}-sd8688 = "Firmware-Marvell"
LICENSE:${PN}-sd8787 = "Firmware-Marvell"
LICENSE:${PN}-sd8797 = "Firmware-Marvell"
LICENSE:${PN}-sd8801 = "Firmware-Marvell"
LICENSE:${PN}-sd8887 = "Firmware-Marvell"
LICENSE:${PN}-sd8897 = "Firmware-Marvell"
LICENSE:${PN}-sd8997 = "Firmware-Marvell"
LICENSE:${PN}-usb8997 = "Firmware-Marvell"
LICENSE:${PN}-marvell-license = "Firmware-Marvell"

FILES:${PN}-marvell-license = "${nonarch_base_libdir}/firmware/LICENCE.Marvell"
FILES:${PN}-pcie8897 = " \
  ${nonarch_base_libdir}/firmware/mrvl/pcie8897_uapsta.bin* \
"
FILES:${PN}-pcie8997 = " \
  ${nonarch_base_libdir}/firmware/mrvl/pcie8997_wlan_v4.bin* \
  ${nonarch_base_libdir}/firmware/mrvl/pcieuart8997_combo_v4.bin* \
  ${nonarch_base_libdir}/firmware/mrvl/pcieusb8997_combo_v4.bin* \
"
FILES:${PN}-sd8686 = " \
  ${nonarch_base_libdir}/firmware/libertas/sd8686_v9* \
  ${nonarch_base_libdir}/firmware/sd8686* \
"
FILES:${PN}-sd8688 = " \
  ${nonarch_base_libdir}/firmware/libertas/sd8688* \
  ${nonarch_base_libdir}/firmware/mrvl/sd8688* \
"
FILES:${PN}-sd8787 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8787_uapsta.bin* \
"
FILES:${PN}-sd8797 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8797_uapsta.bin* \
"
FILES:${PN}-sd8801 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8801_uapsta.bin* \
"
FILES:${PN}-sd8887 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8887_uapsta.bin* \
"
FILES:${PN}-sd8897 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8897_uapsta.bin* \
"
do_install:append() {
    # The kernel 5.6.x driver still uses the old name, provide a symlink for
    # older kernels
    COMPR=$(echo ${@fw_compr_suffix(d)} | tr -d '-')
    if [ -n "$COMPR" ]; then
        COMPR=".$COMPR"
    fi
    ln -fs sdsd8997_combo_v4.bin$COMPR ${D}${nonarch_base_libdir}/firmware/mrvl/sd8997_uapsta.bin$COMPR
}
FILES:${PN}-sd8997 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8997_uapsta.bin* \
  ${nonarch_base_libdir}/firmware/mrvl/sdsd8997_combo_v4.bin* \
"
FILES:${PN}-usb8997 = " \
  ${nonarch_base_libdir}/firmware/mrvl/usbusb8997_combo_v4.bin* \
"

RDEPENDS:${PN}-sd8686 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8688 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8787 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8797 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8801 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8887 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8897 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8997 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8997 += "${PN}-marvell-license"

# For netronome
LICENSE:${PN}-netronome = "Firmware-netronome"

FILES:${PN}-netronome-license = " \
  ${nonarch_base_libdir}/firmware/LICENCE.Netronome \
"
FILES:${PN}-netronome = " \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0081*.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0096*.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0097*.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0099*.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0058-0011_2x40.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0058-0012_2x40.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/nic_AMDA0078-0011_1x100.nffw* \
  ${nonarch_base_libdir}/firmware/netronome/bpf \
  ${nonarch_base_libdir}/firmware/netronome/flower \
  ${nonarch_base_libdir}/firmware/netronome/nic \
  ${nonarch_base_libdir}/firmware/netronome/nic-sriov \
"

RDEPENDS:${PN}-netronome += "${PN}-netronome-license"

# For NXP
LICENSE:${PN}-nxp8987-sdio = "Firmware-nxp"
LICENSE:${PN}-nxp8997-common = "Firmware-nxp"
LICENSE:${PN}-nxp8997-pcie = "Firmware-nxp"
LICENSE:${PN}-nxp8997-sdio = "Firmware-nxp"
LICENSE:${PN}-nxp9098-common = "Firmware-nxp"
LICENSE:${PN}-nxp9098-pcie = "Firmware-nxp"
LICENSE:${PN}-nxp9098-sdio = "Firmware-nxp"
LICENSE:${PN}-nxpiw416-sdio = "Firmware-nxp"
LICENSE:${PN}-nxpiw612-sdio = "Firmware-nxp"
LICENSE:${PN}-nxp-license = "Firmware-nxp"

FILES:${PN}-nxp8987-sdio = "${nonarch_base_libdir}/firmware/nxp/*8987*"
FILES:${PN}-nxp8997-common = " \
    ${nonarch_base_libdir}/firmware/nxp/uartuart8997_bt_v4.bin* \
    ${nonarch_base_libdir}/firmware/nxp/helper_uart_3000000.bin* \
"
ALLOW_EMPTY:${PN}-nxp8997-pcie = "1"
ALLOW_EMPTY:${PN}-nxp8997-sdio = "1"
FILES:${PN}-nxp9098-common = "${nonarch_base_libdir}/firmware/nxp/uartuart9098_bt_v1.bin*"
ALLOW_EMPTY:${PN}-nxp9098-pcie = "1"
ALLOW_EMPTY:${PN}-nxp9098-sdio = "1"
FILES:${PN}-nxpiw416-sdio = "${nonarch_base_libdir}/firmware/nxp/*iw416*"
FILES:${PN}-nxpiw612-sdio = "${nonarch_base_libdir}/firmware/nxp/uartspi_n61x_v1.bin.se*"
FILES:${PN}-nxp-license = "${nonarch_base_libdir}/firmware/LICENSE.nxp"

RDEPENDS:${PN}-nxp8987-sdio += "${PN}-nxp-license"
RDEPENDS:${PN}-nxp8997-common += "${PN}-nxp-license"
RDEPENDS:${PN}-nxp8997-pcie += "${PN}-nxp8997-common"
RDEPENDS:${PN}-nxp8997-sdio += "${PN}-nxp8997-common"
RDEPENDS:${PN}-nxp9098-common += "${PN}-nxp-license"
RDEPENDS:${PN}-nxp9098-pcie += "${PN}-nxp9098-common"
RDEPENDS:${PN}-nxp9098-sdio += "${PN}-nxp9098-common"
RDEPENDS:${PN}-nxpiw416-sdio += "${PN}-nxp-license"
RDEPENDS:${PN}-nxpiw612-sdio += "${PN}-nxp-license"

# For nxp-mc
LICENSE:${PN}-nxp-mc = "Firmware-nxp_mc_firmware"
LICENSE:${PN}-nxp-mc-license = "Firmware-nxp_mc_firmware"

FILES:${PN}-nxp-mc = "${nonarch_base_libdir}/firmware/dpaa2/mc/*"
FILES:${PN}-nxp-mc-license = " \
  ${nonarch_base_libdir}/firmware/LICENSE.nxp_mc_firmware \
"
RDEPENDS:${PN}-nxp-mc += "${PN}-nxp-mc-license"

# For Nvidia
LICENSE:${PN}-nvidia-gpu = "Firmware-nvidia"
LICENSE:${PN}-nvidia-tegra = "Firmware-nvidia"
LICENSE:${PN}-nvidia-tegra-k1 = "Firmware-nvidia"
LICENSE:${PN}-nvidia-license = "Firmware-nvidia"

FILES:${PN}-nvidia-gpu = "${nonarch_base_libdir}/firmware/nvidia"
FILES:${PN}-nvidia-tegra = " \
  ${nonarch_base_libdir}/firmware/nvidia/tegra* \
  ${nonarch_base_libdir}/firmware/nvidia/gm20b \
  ${nonarch_base_libdir}/firmware/nvidia/gp10b \
"
FILES:${PN}-nvidia-tegra-k1 = " \
  ${nonarch_base_libdir}/firmware/nvidia/tegra124 \
  ${nonarch_base_libdir}/firmware/nvidia/gk20a \
"
FILES:${PN}-nvidia-license = "${nonarch_base_libdir}/firmware/LICENCE.nvidia"

RDEPENDS:${PN}-nvidia-gpu += "${PN}-nvidia-license"
RDEPENDS:${PN}-nvidia-tegra += "${PN}-nvidia-license"
RDEPENDS:${PN}-nvidia-tegra-k1 += "${PN}-nvidia-license"

# For OLPC
LICENSE:${PN}-olpc = "Firmware-OLPC"
LICENSE:${PN}-olpc-license = "Firmware-OLPC"

FILES:${PN}-olpc = " \
  ${nonarch_base_libdir}/firmware/libertas/lbtf_sdio.bin*	\
  ${nonarch_base_libdir}/firmware/lbtf_usb.bin*			\
  ${nonarch_base_libdir}/firmware/libertas/usb8388_olpc.bin*	\
"
FILES:${PN}-olpc-license = "${nonarch_base_libdir}/firmware/LICENCE.OLPC"

RDEPENDS:${PN}-olpc += "${PN}-olpc-license"

# For phanfw
LICENSE:${PN}-phanfw = "Firmware-phanfw"
LICENSE:${PN}-phanfw-license = "Firmware-phanfw"

FILES:${PN}-phanfw = "${nonarch_base_libdir}/firmware/phanfw.bin*"
FILES:${PN}-phanfw-license = "${nonarch_base_libdir}/firmware/LICENCE.phanfw"

RDEPENDS:${PN}-phanfw += "${PN}-phanfw-license"

# For PowerVR
LICENSE:${PN}-powervr = "Firmware-powervr"
LICENSE:${PN}-powervr-license = "Firmware-powervr"

FILES:${PN}-powervr = "${nonarch_base_libdir}/firmware/powervr"
FILES:${PN}-powervr-license = "${nonarch_base_libdir}/firmware/LICENSE.powervr"

RDEPENDS:${PN}-powervr += "${PN}-powervr-license"

# For qla2xxx
LICENSE:${PN}-qla2xxx = "Firmware-qla2xxx"
LICENSE:${PN}-qla2xxx-license = "Firmware-qla2xxx"

FILES:${PN}-qla2xxx = "${nonarch_base_libdir}/firmware/ql2*"
FILES:${PN}-qla2xxx-license = "${nonarch_base_libdir}/firmware/LICENCE.qla2xxx"

RDEPENDS:${PN}-qla2xxx += "${PN}-qla2xxx-license"

# For RSI RS911x WiFi
LICENSE:${PN}-rs9113 = "WHENCE"
LICENSE:${PN}-rs9116 = "WHENCE"

FILES:${PN}-rs9113 = " ${nonarch_base_libdir}/firmware/rsi/rs9113*.rps* "
FILES:${PN}-rs9116 = " ${nonarch_base_libdir}/firmware/rsi/rs9116*.rps* "

RDEPENDS:${PN}-rs9113 += "${PN}-whence-license"
RDEPENDS:${PN}-rs9116 += "${PN}-whence-license"

# For rtl
LICENSE:${PN}-rtl8188 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192cu = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192ce = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192su = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8723 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8761 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8821 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8822 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl-license = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl-nic = "WHENCE"
LICENSE:${PN}-rtl8168 = "WHENCE"

FILES:${PN}-rtl-license = " \
  ${nonarch_base_libdir}/firmware/LICENCE.rtlwifi_firmware.txt \
"
FILES:${PN}-rtl8188 = " \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8188*.bin* \
"
FILES:${PN}-rtl8192cu = " \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8192cufw*.bin* \
"
FILES:${PN}-rtl8192ce = " \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8192cfw*.bin* \
"
FILES:${PN}-rtl8192su = " \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8712u.bin* \
"
FILES:${PN}-rtl8723 = " \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8723*.bin* \
  ${nonarch_base_libdir}/firmware/rtw88/rtw8723*.bin* \
  ${nonarch_base_libdir}/firmware/rtl_bt/rtl8723*.bin* \
"
FILES:${PN}-rtl8821 = " \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8821*.bin* \
  ${nonarch_base_libdir}/firmware/rtw88/rtw8821*.bin* \
"
FILES:${PN}-rtl8761 = " \
  ${nonarch_base_libdir}/firmware/rtl_bt/rtl8761*.bin* \
"
FILES:${PN}-rtl8168 = " \
  ${nonarch_base_libdir}/firmware/rtl_nic/rtl8168*.fw* \
"
FILES:${PN}-rtl8822 = " \
  ${nonarch_base_libdir}/firmware/rtl_bt/rtl8822*.bin* \
  ${nonarch_base_libdir}/firmware/rtw88/rtw8822*.bin* \
  ${nonarch_base_libdir}/firmware/rtlwifi/rtl8822*.bin* \
"
FILES:${PN}-rtl-nic = " \
  ${nonarch_base_libdir}/firmware/rtl_nic/*.fw* \
"

RDEPENDS:${PN}-rtl8188 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192ce += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192cu += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192su = "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8723 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8821 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8761 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8822 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8168 += "${PN}-whence-license"
RDEPENDS:${PN}-rtl-nic += "${PN}-whence-license"

# For Silabs
LICENSE:${PN}-wfx = "Firmware-wfx"
LICENSE:${PN}-wfx-license = "Firmware-wfx"

FILES:${PN}-wfx = " \
  ${nonarch_base_libdir}/firmware/wfx/*.pds*        \
  ${nonarch_base_libdir}/firmware/wfx/*.sec*        \
"
FILES:${PN}-wfx-license = "${nonarch_base_libdir}/firmware/wfx/LICENCE.wf200"

RDEPENDS:${PN}-wfx += "${PN}-wfx-license"

# For TI wl1251
LICENSE:${PN}-wl1251 = "Firmware-wl1251"
LICENSE:${PN}-wl1251-license = "Firmware-wl1251"

FILES:${PN}-wl1251 = " \
  ${nonarch_base_libdir}/firmware/ti-connectivity/wl1251-fw.bin*         \
  ${nonarch_base_libdir}/firmware/ti-connectivity/wl1251-nvs.bin*        \
"
FILES:${PN}-wl1251-license = "${nonarch_base_libdir}/firmware/LICENCE.wl1251"

RDEPENDS:${PN}-wl1251 += "${PN}-wl1251-license"

# For ti-connectivity
LICENSE:${PN}-wlcommon = "Firmware-ti-connectivity"
LICENSE:${PN}-wl12xx = "Firmware-ti-connectivity"
LICENSE:${PN}-wl18xx = "Firmware-ti-connectivity"
LICENSE:${PN}-cc33xx = "Firmware-ti-connectivity"
LICENSE:${PN}-ti-connectivity-license = "Firmware-ti-connectivity"

FILES:${PN}-ti-connectivity-license = "${nonarch_base_libdir}/firmware/LICENCE.ti-connectivity"
# wl18xx optionally needs wl1271-nvs.bin (which itself is a symlink to
# wl127x-nvs.bin) - see linux/drivers/net/wireless/ti/wlcore/sdio.c
# and drivers/net/wireless/ti/wlcore/spi.c.
# While they're optional and actually only used to override the MAC
# address on wl18xx, driver loading will delay (by udev timout - 60s)
# if not there. So let's make it available always. Because it's a
# symlink, both need to go to wlcommon.
FILES:${PN}-wlcommon = " \
  ${nonarch_base_libdir}/firmware/ti-connectivity/TI* \
  ${nonarch_base_libdir}/firmware/ti-connectivity/wl127x-nvs.bin* \
  ${nonarch_base_libdir}/firmware/ti-connectivity/wl1271-nvs.bin* \
"
FILES:${PN}-wl12xx = " \
  ${nonarch_base_libdir}/firmware/ti-connectivity/wl12* \
"
FILES:${PN}-wl18xx = " \
  ${nonarch_base_libdir}/firmware/ti-connectivity/wl18* \
"

FILES:${PN}-cc33xx = " \
  ${nonarch_base_libdir}/firmware/ti-connectivity/cc33* \
"

RDEPENDS:${PN}-wl12xx = "${PN}-ti-connectivity-license ${PN}-wlcommon"
RDEPENDS:${PN}-wl18xx = "${PN}-ti-connectivity-license ${PN}-wlcommon"
RDEPENDS:${PN}-cc33xx = "${PN}-ti-connectivity-license"

# For ti-keystone
LICENSE:${PN}-ti-keystone = "Firmware-ti-keystone"
LICENSE:${PN}-ti-keystone-license = "Firmware-ti-keystone"

FILES:${PN}-ti-keystone = "${nonarch_base_libdir}/firmware/ti-keystone/*"
FILES:${PN}-ti-keystone-license = " \
  ${nonarch_base_libdir}/firmware/LICENCE.ti-keystone \
"
RDEPENDS:${PN}-ti-keystone += "${PN}-ti-keystone-license"

# For vt6656
LICENSE:${PN}-vt6656 = "Firmware-via_vt6656"
LICENSE:${PN}-vt6656-license = "Firmware-via_vt6656"

FILES:${PN}-vt6656-license = "${nonarch_base_libdir}/firmware/LICENCE.via_vt6656"
FILES:${PN}-vt6656 = " \
  ${nonarch_base_libdir}/firmware/vntwusb.fw* \
"

RDEPENDS:${PN}-vt6656 = "${PN}-vt6656-license"

# For xc4000
LICENSE:${PN}-xc4000 = "Firmware-xc4000"
LICENSE:${PN}-xc4000-license = "Firmware-xc4000"

FILES:${PN}-xc4000 = "${nonarch_base_libdir}/firmware/dvb-fe-xc4000-1.4.1.fw*"
FILES:${PN}-xc4000-license = "${nonarch_base_libdir}/firmware/LICENCE.xc4000"

RDEPENDS:${PN}-xc4000 += "${PN}-xc4000-license"

# For xc5000
LICENSE:${PN}-xc5000 = "Firmware-xc5000"
LICENSE:${PN}-xc5000-license = "Firmware-xc5000"

FILES:${PN}-xc5000 = "${nonarch_base_libdir}/firmware/dvb-fe-xc5000-1.6.114.fw*"
FILES:${PN}-xc5000-license = "${nonarch_base_libdir}/firmware/LICENCE.xc5000"

RDEPENDS:${PN}-xc5000 += "${PN}-xc5000-license"

# For xc5000c
LICENSE:${PN}-xc5000c = "Firmware-xc5000c"
LICENSE:${PN}-xc5000c-license = "Firmware-xc5000c"

FILES:${PN}-xc5000c = " \
  ${nonarch_base_libdir}/firmware/dvb-fe-xc5000c-4.1.30.7.fw* \
"
FILES:${PN}-xc5000c-license = "${nonarch_base_libdir}/firmware/LICENCE.xc5000c"

RDEPENDS:${PN}-xc5000c += "${PN}-xc5000c-license"

# For broadcom

# for i in `grep brcm WHENCE  | grep ^File | sed 's/File: brcm.//g'`; do pkg=`echo $i | sed 's/-[sp40].*//g; s/\.bin//g; s/brcmfmac/bcm/g; s/_hdr/-hdr/g; s/BCM/bcm-0bb4-0306/g'`; echo -e "             \${PN}-$pkg \\"; done  | sort -u

LICENSE:${PN}-broadcom-license = "Firmware-broadcom_bcm43xx"
FILES:${PN}-broadcom-license = "${nonarch_base_libdir}/firmware/LICENCE.broadcom_bcm43xx"

# for i in `grep brcm WHENCE  | grep ^File | sed 's/File: brcm.//g'`; do pkg=`echo $i | sed 's/-[sp40].*//g; s/\.bin//g; s/brcmfmac/bcm/g; s/_hdr/-hdr/g; s/BCM/bcm-0bb4-0306/g'`; echo "$i - $pkg"; echo -e "FILES:\${PN}-$pkg = \"\${nonarch_base_libdir}/firmware/brcm/$i\""; done | grep ^FILES

FILES:${PN}-bcm43xx = "${nonarch_base_libdir}/firmware/brcm/bcm43xx-0.fw*"
FILES:${PN}-bcm43xx-hdr = "${nonarch_base_libdir}/firmware/brcm/bcm43xx_hdr-0.fw*"
FILES:${PN}-bcm4329-fullmac = "${nonarch_base_libdir}/firmware/brcm/bcm4329-fullmac-4.bin*"
FILES:${PN}-bcm43236b = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43236b.bin*"
FILES:${PN}-bcm4329 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4329-sdio.bin*"
FILES:${PN}-bcm4330 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4330-sdio.*"
FILES:${PN}-bcm4334 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4334-sdio.bin*"
FILES:${PN}-bcm4335 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4335-sdio.bin*"
FILES:${PN}-bcm4339 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4339-sdio.bin* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4339-sdio.bin* \
"
FILES:${PN}-bcm43241b0 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43241b0-sdio.bin*"
FILES:${PN}-bcm43241b4 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43241b4-sdio.bin*"
FILES:${PN}-bcm43241b5 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43241b5-sdio.bin*"
FILES:${PN}-bcm43242a = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43242a.bin*"
FILES:${PN}-bcm43143 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43143.bin* \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43143-sdio.bin* \
"
FILES:${PN}-bcm43430a0 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43430a0-sdio.*"
FILES:${PN}-bcm43455 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43455-sdio.* \
"
FILES:${PN}-bcm4350c2 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4350c2-pcie.bin*"
FILES:${PN}-bcm4350 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4350-pcie.bin*"
FILES:${PN}-bcm4356 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4356-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4356-sdio.* \
"
FILES:${PN}-bcm43569 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43569.bin*"
FILES:${PN}-bcm43570 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43570-pcie.bin* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43570-pcie.bin* \
"
FILES:${PN}-bcm4358 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4358-pcie.bin*"
FILES:${PN}-bcm43602 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43602-pcie.bin* \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43602-pcie.ap.bin* \
"
FILES:${PN}-bcm4366b = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4366b-pcie.bin*"
FILES:${PN}-bcm4366c = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4366c-pcie.bin*"
FILES:${PN}-bcm4371 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4371-pcie.bin*"

# for i in `grep brcm WHENCE  | grep ^File | sed 's/File: brcm.//g'`; do pkg=`echo $i | sed 's/-[sp40].*//g; s/\.bin//g; s/brcmfmac/bcm/g; s/_hdr/-hdr/g; s/BCM/bcm-0bb4-0306/g'`; echo -e "LICENSE:\${PN}-$pkg = \"Firmware-broadcom_bcm43xx\"\nRDEPENDS_\${PN}-$pkg += \"\${PN}-broadcom-license\""; done
# Currently 1st one and last 6 have cypress LICENSE

LICENSE:${PN}-bcm43xx = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43xx += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43xx-hdr = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43xx-hdr += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4329-fullmac = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4329-fullmac += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43236b = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43236b += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4329 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4329 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4330 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4330 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4334 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4334 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4335 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4335 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4339 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4339 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43241b0 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43241b0 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43241b4 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43241b4 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43241b5 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43241b5 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43242a = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43242a += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43143 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43143 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43430a0 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43430a0 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43455 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43455 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4350c2 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4350c2 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4350 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4350 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4356 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4356 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43569 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43569 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43570 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43570 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4358 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4358 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm43602 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43602 += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4366b = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4366b += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4366c = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4366c += "${PN}-broadcom-license"
LICENSE:${PN}-bcm4371 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm4371 += "${PN}-broadcom-license"

# For broadcom cypress

LICENSE:${PN}-cypress-license = "Firmware-cypress"
FILES:${PN}-cypress-license = "${nonarch_base_libdir}/firmware/LICENCE.cypress"

FILES:${PN}-bcm-0bb4-0306 = "${nonarch_base_libdir}/firmware/brcm/BCM-0bb4-0306.hcd*"
FILES:${PN}-bcm43340 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43340-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43340-sdio.*"
FILES:${PN}-bcm43362 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43362-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43362-sdio.*"
FILES:${PN}-bcm43430 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.*"
FILES:${PN}-bcm4354 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4354-sdio.bin \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4354-sdio.bin \
"
FILES:${PN}-bcm4356-pcie = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4356-pcie.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4356-pcie.* \
"
FILES:${PN}-bcm4373 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.bin* \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac4373.bin* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4373-sdio.bin* \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac4373-sdio.clm_blob* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4373-sdio.clm_blob* \
"

LICENSE:${PN}-bcm-0bb4-0306 = "Firmware-cypress"
RDEPENDS:${PN}-bcm-0bb4-0306 += "${PN}-cypress-license"
LICENSE:${PN}-bcm43340 = "Firmware-cypress"
RDEPENDS:${PN}-bcm43340 += "${PN}-cypress-license"
LICENSE:${PN}-bcm43362 = "Firmware-cypress"
RDEPENDS:${PN}-bcm43362 += "${PN}-cypress-license"
LICENSE:${PN}-bcm43430 = "Firmware-cypress"
RDEPENDS:${PN}-bcm43430 += "${PN}-cypress-license"
LICENSE:${PN}-bcm4354 = "Firmware-cypress"
RDEPENDS:${PN}-bcm4354 += "${PN}-cypress-license"
LICENSE:${PN}-bcm4356-pcie = "Firmware-cypress"
RDEPENDS:${PN}-bcm4356-pcie += "${PN}-cypress-license"
LICENSE:${PN}-bcm4373 = "Firmware-cypress"
RDEPENDS:${PN}-bcm4373 += "${PN}-cypress-license"

# For Broadcom bnx2
#
# which is a separate case to the other Broadcom firmwares since its
# license is contained in the shared WHENCE file.

LICENSE:${PN}-bnx2 = "WHENCE"
LICENSE:${PN}-whence-license = "WHENCE"

FILES:${PN}-bnx2 = " \
    ${nonarch_base_libdir}/firmware/bnx2/bnx2-mips*.fw* \
    ${nonarch_base_libdir}/firmware/bnx2/bnx2-rv2p*.fw* \
"
FILES:${PN}-whence-license = "${nonarch_base_libdir}/firmware/WHENCE"

RDEPENDS:${PN}-bnx2 += "${PN}-whence-license"
RPROVIDES:${PN}-bnx2 = "${PN}-bnx2-mips"

LICENSE:${PN}-bnx2x = "WHENCE"

FILES:${PN}-bnx2x = "${nonarch_base_libdir}/firmware/bnx2x/bnx2x*.fw*"

RDEPENDS:${PN}-bnx2x += "${PN}-whence-license"

# For cirrus
LICENSE:${PN}-cirrus = "Firmware-cirrus"
LICENSE:${PN}-cirrus-license = "Firmware-cirrus"

FILES:${PN}-cirrus = "${nonarch_base_libdir}/firmware/cirrus/*"
FILES:${PN}-cirrus-license = "${nonarch_base_libdir}/firmware/LICENSE.cirrus"

RDEPENDS:${PN}-cirrus += "${PN}-cirrus-license"

# For cnm
LICENSE:${PN}-cnm = "Firmware-cnm"
LICENSE:${PN}-cnm-license = "Firmware-cnm"

FILES:${PN}-cnm = "${nonarch_base_libdir}/firmware/cnm/wave521c_k3_codec_fw.bin*"
FILES:${PN}-cnm-license = "${nonarch_base_libdir}/firmware/LICENCE.cnm"

RDEPENDS:${PN}-cnm += "${PN}-cnm-license"

# For imx-sdma
LICENSE:${PN}-imx-sdma-imx6q       = "Firmware-imx-sdma_firmware"
LICENSE:${PN}-imx-sdma-imx7d       = "Firmware-imx-sdma_firmware"
LICENSE:${PN}-imx-sdma-license       = "Firmware-imx-sdma_firmware"

FILES:${PN}-imx-sdma-imx6q = "${nonarch_base_libdir}/firmware/imx/sdma/sdma-imx6q.bin*"

RPROVIDES:${PN}-imx-sdma-imx6q = "firmware-imx-sdma-imx6q"
RREPLACES:${PN}-imx-sdma-imx6q = "firmware-imx-sdma-imx6q"
RCONFLICTS:${PN}-imx-sdma-imx6q = "firmware-imx-sdma-imx6q"

FILES:${PN}-imx-sdma-imx7d = "${nonarch_base_libdir}/firmware/imx/sdma/sdma-imx7d.bin*"

FILES:${PN}-imx-sdma-license = "${nonarch_base_libdir}/firmware/LICENSE.sdma_firmware"

RDEPENDS:${PN}-imx-sdma-imx6q += "${PN}-imx-sdma-license"
RDEPENDS:${PN}-imx-sdma-imx7d += "${PN}-imx-sdma-license"

# For iwlwifi
LICENSE:${PN}-iwlwifi           = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-135-6     = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-7    = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-8    = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-9    = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-10   = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-12   = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-13   = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-16   = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-3160-17   = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6000-4    = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6000g2a-5 = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6000g2a-6 = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6000g2b-5 = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6000g2b-6 = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6050-4    = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-6050-5    = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-7260      = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-7265      = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-7265d     = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-8000c     = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-8265      = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-9000      = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-9260      = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-misc      = "Firmware-iwlwifi_firmware"
LICENSE:${PN}-iwlwifi-license   = "Firmware-iwlwifi_firmware"


FILES:${PN}-iwlwifi-license = "${nonarch_base_libdir}/firmware/LICENCE.iwlwifi_firmware"
FILES:${PN}-iwlwifi-135-6 = "${nonarch_base_libdir}/firmware/iwlwifi-135-6.ucode*"
FILES:${PN}-iwlwifi-3160-7 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-7.ucode*"
FILES:${PN}-iwlwifi-3160-8 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-8.ucode*"
FILES:${PN}-iwlwifi-3160-9 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-9.ucode*"
FILES:${PN}-iwlwifi-3160-10 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-10.ucode*"
FILES:${PN}-iwlwifi-3160-12 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-12.ucode*"
FILES:${PN}-iwlwifi-3160-13 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-13.ucode*"
FILES:${PN}-iwlwifi-3160-16 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-16.ucode*"
FILES:${PN}-iwlwifi-3160-17 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-17.ucode*"
FILES:${PN}-iwlwifi-6000-4 = "${nonarch_base_libdir}/firmware/iwlwifi-6000-4.ucode*"
FILES:${PN}-iwlwifi-6000g2a-5 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2a-5.ucode*"
FILES:${PN}-iwlwifi-6000g2a-6 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2a-6.ucode*"
FILES:${PN}-iwlwifi-6000g2b-5 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2b-5.ucode*"
FILES:${PN}-iwlwifi-6000g2b-6 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2b-6.ucode*"
FILES:${PN}-iwlwifi-6050-4 = "${nonarch_base_libdir}/firmware/iwlwifi-6050-4.ucode*"
FILES:${PN}-iwlwifi-6050-5 = "${nonarch_base_libdir}/firmware/iwlwifi-6050-5.ucode*"
FILES:${PN}-iwlwifi-7260   = "${nonarch_base_libdir}/firmware/iwlwifi-7260-*.ucode*"
FILES:${PN}-iwlwifi-7265   = "${nonarch_base_libdir}/firmware/iwlwifi-7265-*.ucode*"
FILES:${PN}-iwlwifi-7265d   = "${nonarch_base_libdir}/firmware/iwlwifi-7265D-*.ucode*"
FILES:${PN}-iwlwifi-8000c   = "${nonarch_base_libdir}/firmware/iwlwifi-8000C-*.ucode*"
FILES:${PN}-iwlwifi-8265   = "${nonarch_base_libdir}/firmware/iwlwifi-8265-*.ucode*"
FILES:${PN}-iwlwifi-9000   = "${nonarch_base_libdir}/firmware/iwlwifi-9000-*.ucode*"
FILES:${PN}-iwlwifi-9260   = "${nonarch_base_libdir}/firmware/iwlwifi-9260-*.ucode*"
FILES:${PN}-iwlwifi-misc   = " \
    ${nonarch_base_libdir}/firmware/iwlwifi-*.ucode* \
    ${nonarch_base_libdir}/firmware/iwlwifi-*.pnvm* \
"

RDEPENDS:${PN}-iwlwifi-135-6     = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-7    = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-8    = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-9    = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-10   = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-12   = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-13   = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-16   = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-3160-17   = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6000-4    = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6000g2a-5 = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6000g2a-6 = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6000g2b-5 = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6000g2b-6 = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6050-4    = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-6050-5    = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-7260      = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-7265      = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-7265d     = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-8000c     = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-8265      = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-9000      = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-9260      = "${PN}-iwlwifi-license"
RDEPENDS:${PN}-iwlwifi-misc      = "${PN}-iwlwifi-license"

# -iwlwifi-misc is a "catch all" package that includes all the iwlwifi
# firmwares that are not already included in other -iwlwifi- packages.
# -iwlwifi is a virtual package that depends upon all iwlwifi packages.
# These are distinct in order to allow the -misc firmwares to be installed
# without pulling in every other iwlwifi package.
ALLOW_EMPTY:${PN}-iwlwifi = "1"
ALLOW_EMPTY:${PN}-iwlwifi-misc = "1"

# Handle package updating for the newly merged iwlwifi groupings
RPROVIDES:${PN}-iwlwifi-7265 = "${PN}-iwlwifi-7265-8 ${PN}-iwlwifi-7265-9"
RREPLACES:${PN}-iwlwifi-7265 = "${PN}-iwlwifi-7265-8 ${PN}-iwlwifi-7265-9"
RCONFLICTS:${PN}-iwlwifi-7265 = "${PN}-iwlwifi-7265-8 ${PN}-iwlwifi-7265-9"

RPROVIDES:${PN}-iwlwifi-7260 = "${PN}-iwlwifi-7260-7 ${PN}-iwlwifi-7260-8 ${PN}-iwlwifi-7260-9"
RREPLACES:${PN}-iwlwifi-7260 = "${PN}-iwlwifi-7260-7 ${PN}-iwlwifi-7260-8 ${PN}-iwlwifi-7260-9"
RCONFLICTS:${PN}-iwlwifi-7260 = "${PN}-iwlwifi-7260-7 ${PN}-iwlwifi-7260-8 ${PN}-iwlwifi-7260-9"

# For ibt
LICENSE:${PN}-ibt-license = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-hw-37-7 = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-hw-37-8 = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-11-5    = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-12-16   = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-17 = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-18    = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-20 = "Firmware-ibt_firmware"
LICENSE:${PN}-ibt-misc    = "Firmware-ibt_firmware"

FILES:${PN}-ibt-license = "${nonarch_base_libdir}/firmware/LICENCE.ibt_firmware"
FILES:${PN}-ibt-hw-37-7 = "${nonarch_base_libdir}/firmware/intel/ibt-hw-37.7*.bseq*"
FILES:${PN}-ibt-hw-37-8 = "${nonarch_base_libdir}/firmware/intel/ibt-hw-37.8*.bseq*"
FILES:${PN}-ibt-11-5    = "${nonarch_base_libdir}/firmware/intel/ibt-11-5.sfi* ${nonarch_base_libdir}/firmware/intel/ibt-11-5.ddc*"
FILES:${PN}-ibt-12-16   = "${nonarch_base_libdir}/firmware/intel/ibt-12-16.sfi* ${nonarch_base_libdir}/firmware/intel/ibt-12-16.ddc*"
FILES:${PN}-ibt-17 = "${nonarch_base_libdir}/firmware/intel/ibt-17-*.sfi* ${nonarch_base_libdir}/firmware/intel/ibt-17-*.ddc*"
FILES:${PN}-ibt-18      = "${nonarch_base_libdir}/firmware/intel/ibt-18-*.sfi* ${nonarch_base_libdir}/firmware/intel/ibt-18-*.ddc*"
FILES:${PN}-ibt-20 = "${nonarch_base_libdir}/firmware/intel/ibt-20-*.sfi* ${nonarch_base_libdir}/firmware/intel/ibt-20-*.ddc*"
FILES:${PN}-ibt-misc    = "${nonarch_base_libdir}/firmware/intel/ibt-*"

RDEPENDS:${PN}-ibt-hw-37-7 = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-hw-37.8 = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-11-5    = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-12-16   = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-17 = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-18      = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-20 = "${PN}-ibt-license"
RDEPENDS:${PN}-ibt-misc    = "${PN}-ibt-license"

ALLOW_EMPTY:${PN}-ibt = "1"
ALLOW_EMPTY:${PN}-ibt-misc = "1"

LICENSE:${PN}-i915       = "Firmware-i915"
LICENSE:${PN}-i915-license = "Firmware-i915"
FILES:${PN}-i915-license = "${nonarch_base_libdir}/firmware/LICENSE.i915"
FILES:${PN}-i915         = "${nonarch_base_libdir}/firmware/i915"
RDEPENDS:${PN}-i915      = "${PN}-i915-license"

# For ice-enhanced
LICENSE:${PN}-ice-enhanced         = "Firmware-ice_enhanced"
LICENSE:${PN}-ice-enhanced-license = "Firmware-ice_enhanced"

FILES:${PN}-ice-enhanced           = " \
  ${nonarch_base_libdir}/firmware/intel/ice/ddp-comms/* \
  ${nonarch_base_libdir}/firmware/intel/ice/ddp-wireless_edge/* \
"
FILES:${PN}-ice-enhanced-license   = " \
  ${nonarch_base_libdir}/firmware/LICENSE.ice_enhanced \
"
RDEPENDS:${PN}-ice-enhanced        = "${PN}-ice-enhanced-license"

LICENSE:${PN}-ice       = "Firmware-ice"
LICENSE:${PN}-ice-license = "Firmware-ice"
FILES:${PN}-ice-license = "${nonarch_base_libdir}/firmware/LICENSE.ice"
FILES:${PN}-ice         = " \
  ${nonarch_base_libdir}/firmware/intel/ice/ddp/* \
  ${nonarch_base_libdir}/firmware/intel/ice/ddp-lag/* \
"
RDEPENDS:${PN}-ice      = "${PN}-ice-license"

FILES:${PN}-adsp-sst-license      = "${nonarch_base_libdir}/firmware/LICENCE.adsp_sst"
LICENSE:${PN}-adsp-sst            = "Firmware-adsp_sst"
LICENSE:${PN}-adsp-sst-license    = "Firmware-adsp_sst"
FILES:${PN}-adsp-sst              = "${nonarch_base_libdir}/firmware/intel/dsp_fw*"
RDEPENDS:${PN}-adsp-sst           = "${PN}-adsp-sst-license"

# For QAT
LICENSE:${PN}-qat         = "Firmware-qat"
LICENSE:${PN}-qat-license = "Firmware-qat"
FILES:${PN}-qat-license   = "${nonarch_base_libdir}/firmware/LICENCE.qat_firmware"
FILES:${PN}-qat           = "${nonarch_base_libdir}/firmware/qat*.bin*"
RDEPENDS:${PN}-qat        = "${PN}-qat-license"

LICENSE:${PN}-qed         = "WHENCE"
FILES:${PN}-qed           = "${nonarch_base_libdir}/firmware/qed/*"

LICENSE:${PN}-linaro-license = "Firmware-linaro"
FILES:${PN}-linaro-license   = "${nonarch_base_libdir}/firmware/LICENCE.linaro"

# For QCOM VPU/GPU and SDM845
LICENSE:${PN}-qcom-license = "Firmware-qcom"
LICENSE:${PN}-qcom-yamato-license = "Firmware-qcom-yamato"
LICENSE:${PN}-qcom-aic100 = "Firmware-qcom"
LICENSE:${PN}-qcom-qdu100 = "Firmware-qcom"
LICENSE:${PN}-qcom-venus-1.8 = "Firmware-qcom"
LICENSE:${PN}-qcom-venus-4.2 = "Firmware-qcom"
LICENSE:${PN}-qcom-venus-5.2 = "Firmware-qcom"
LICENSE:${PN}-qcom-venus-5.4 = "Firmware-qcom"
LICENSE:${PN}-qcom-venus-6.0 = "Firmware-qcom"
LICENSE:${PN}-qcom-vpu = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a2xx = "Firmware-qcom Firmware-qcom-yamato"
LICENSE:${PN}-qcom-adreno-a3xx = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a4xx = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a530 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a630 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a650 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a660 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a663 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a702 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g750 = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8016-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8016-wifi = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8096-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8096-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8096-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-wifi = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-compute  = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-ipa  = "Firmware-qcom"
LICENSE:${PN}-qcom-qcs615-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-compute  = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-wifi = "Firmware-qcom"
LICENSE:${PN}-qcom-sa8775p-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-thundercomm-db845c-sensors = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-thundercomm-rb5-sensors = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8550-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-sm8650-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-audio = "Firmware-qcom"

FILES:${PN}-qcom-license   = "${nonarch_base_libdir}/firmware/LICENSE.qcom ${nonarch_base_libdir}/firmware/qcom/NOTICE.txt"
FILES:${PN}-qcom-yamato-license = "${nonarch_base_libdir}/firmware/LICENSE.qcom_yamato"

FILES:${PN}-qcom-aic100 = "${nonarch_base_libdir}/firmware/qcom/aic100/*"
FILES:${PN}-qcom-qdu100 = "${nonarch_base_libdir}/firmware/qcom/qdu100/*"

FILES:${PN}-qcom-venus-1.8 = "${nonarch_base_libdir}/firmware/qcom/venus-1.8/*"
FILES:${PN}-qcom-venus-4.2 = "${nonarch_base_libdir}/firmware/qcom/venus-4.2/*"
FILES:${PN}-qcom-venus-5.2 = "${nonarch_base_libdir}/firmware/qcom/venus-5.2/*"
FILES:${PN}-qcom-venus-5.4 = "${nonarch_base_libdir}/firmware/qcom/venus-5.4/*"
FILES:${PN}-qcom-venus-6.0 = "${nonarch_base_libdir}/firmware/qcom/venus-6.0/*"
FILES:${PN}-qcom-vpu = " \
    ${nonarch_base_libdir}/firmware/qcom/vpu/* \
    ${nonarch_base_libdir}/firmware/qcom/vpu-1.0/* \
    ${nonarch_base_libdir}/firmware/qcom/vpu-2.0/* \
"
FILES:${PN}-qcom-adreno-a2xx = "${nonarch_base_libdir}/firmware/qcom/leia_*.fw* ${nonarch_base_libdir}/firmware/qcom/yamato_*.fw*"
FILES:${PN}-qcom-adreno-a3xx = "${nonarch_base_libdir}/firmware/qcom/a3*_*.fw* ${nonarch_base_libdir}/firmware/a300_*.fw*"
FILES:${PN}-qcom-adreno-a4xx = "${nonarch_base_libdir}/firmware/qcom/a4*_*.fw*"
FILES:${PN}-qcom-adreno-a530 = "${nonarch_base_libdir}/firmware/qcom/a530*.fw*"
FILES:${PN}-qcom-adreno-a630 = "${nonarch_base_libdir}/firmware/qcom/a630*.*"
FILES:${PN}-qcom-adreno-a650 = "${nonarch_base_libdir}/firmware/qcom/a650*.*"
FILES:${PN}-qcom-adreno-a660 = "${nonarch_base_libdir}/firmware/qcom/a660*.*"
FILES:${PN}-qcom-adreno-a663 = "${nonarch_base_libdir}/firmware/qcom/a663*.*"
FILES:${PN}-qcom-adreno-a702 = "${nonarch_base_libdir}/firmware/qcom/a702*.*"
FILES:${PN}-qcom-adreno-g750 = "${nonarch_base_libdir}/firmware/qcom/gen70500_*.*"
FILES:${PN}-qcom-apq8016-modem = "${nonarch_base_libdir}/firmware/qcom/apq8016/mba.mbn* ${nonarch_base_libdir}/firmware/qcom/apq8016/modem.mbn*"
FILES:${PN}-qcom-apq8016-wifi = "${nonarch_base_libdir}/firmware/qcom/apq8016/wcnss.mbn* ${nonarch_base_libdir}/firmware/qcom/apq8016/WCNSS*"
FILES:${PN}-qcom-apq8096-adreno = "${nonarch_base_libdir}/firmware/qcom/apq8096/a530_zap.mbn* ${nonarch_base_libdir}/firmware/qcom/a530_zap.mdt*"
FILES:${PN}-qcom-apq8096-audio = "${nonarch_base_libdir}/firmware/qcom/apq8096/adsp*.*"
FILES:${PN}-qcom-apq8096-modem = "${nonarch_base_libdir}/firmware/qcom/apq8096/mba.mbn* ${nonarch_base_libdir}/firmware/qcom/apq8096/modem*.* ${nonarch_base_libdir}/firmware/qcom/apq8096/wlanmdsp.mbn*"
FILES:${PN}-qcom-qcm2290-adreno = "${nonarch_base_libdir}/firmware/qcom/qcm2290/a702_zap.mbn*"
FILES:${PN}-qcom-qcm2290-audio = "${nonarch_base_libdir}/firmware/qcom/qcm2290/adsp*.*"
FILES:${PN}-qcom-qcm2290-modem = "${nonarch_base_libdir}/firmware/qcom/qcm2290/modem*.*"
FILES:${PN}-qcom-qcm2290-wifi = "${nonarch_base_libdir}/firmware/qcom/qcm2290/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/qcm2290/*"
FILES:${PN}-qcom-qcm6490-adreno = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/a660_zap.mbn*"
FILES:${PN}-qcom-qcm6490-audio = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/adsp*.* ${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/battmgr.jsn"
FILES:${PN}-qcom-qcm6490-compute = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/cdsp*.*"
FILES:${PN}-qcom-qcm6490-ipa = "${nonarch_base_libdir}/firmware/qcom/qcm6490/ipa_fws.mbn"
FILES:${PN}-qcom-qcs615-adreno = "${nonarch_base_libdir}/firmware/qcom/qcs615/a612_zap.mbn*"
FILES:${PN}-qcom-qrb4210-adreno = "${nonarch_base_libdir}/firmware/qcom/qrb4210/a610_zap.mbn*"
FILES:${PN}-qcom-qrb4210-audio = "${nonarch_base_libdir}/firmware/qcom/qrb4210/adsp*.*"
FILES:${PN}-qcom-qrb4210-compute = "${nonarch_base_libdir}/firmware/qcom/qrb4210/cdsp*.*"
FILES:${PN}-qcom-qrb4210-modem = "${nonarch_base_libdir}/firmware/qcom/qrb4210/modem*.*"
FILES:${PN}-qcom-qrb4210-wifi = "${nonarch_base_libdir}/firmware/qcom/qrb4210/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/qrb4210/*"
FILES:${PN}-qcom-sa8775p-adreno = "${nonarch_base_libdir}/firmware/qcom/sa8775p/a663_zap.mbn*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-compat = "${nonarch_base_libdir}/firmware/qcom/LENOVO/21BX"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/*adsp*.* ${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/battmgr.jsn* ${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/audioreach-tplg.bin* ${nonarch_base_libdir}/firmware/qcom/sc8280xp/SC8280XP-LENOVO-X13S-tplg.bin*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/qcdxkmsuc8280.mbn*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/*cdsp*.*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/*slpi*.*"
FILES:${PN}-qcom-sdm845-adreno = "${nonarch_base_libdir}/firmware/qcom/sdm845/a630*.*"
FILES:${PN}-qcom-sdm845-audio = "${nonarch_base_libdir}/firmware/qcom/sdm845/adsp*.*"
FILES:${PN}-qcom-sdm845-compute = "${nonarch_base_libdir}/firmware/qcom/sdm845/cdsp*.*"
FILES:${PN}-qcom-sdm845-modem = "${nonarch_base_libdir}/firmware/qcom/sdm845/mba.mbn* ${nonarch_base_libdir}/firmware/qcom/sdm845/modem*.* ${nonarch_base_libdir}/firmware/qcom/sdm845/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/qcom/sdm845/notice.txt_wlanmdsp* \
                                 ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/notice.txt_wlanmdsp"
FILES:${PN}-qcom-sdm845-thundercomm-db845c-sensors = "${nonarch_base_libdir}/firmware/qcom/sdm845/Thundercomm/db845c/slpi*.*"
FILES:${PN}-qcom-sm8250-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8250/a650*.*"
FILES:${PN}-qcom-sm8250-audio = "${nonarch_base_libdir}/firmware/qcom/sm8250/adsp*.*"
FILES:${PN}-qcom-sm8250-compute = "${nonarch_base_libdir}/firmware/qcom/sm8250/cdsp*.*"
FILES:${PN}-qcom-sm8250-thundercomm-rb5-sensors = "${nonarch_base_libdir}/firmware/qcom/sm8250/Thundercomm/RB5/slpi*.*"
FILES:${PN}-qcom-sm8550-audio-tplg = "${nonarch_base_libdir}/firmware/qcom/sm8550/*tplg.bin*"
FILES:${PN}-qcom-sm8650-audio-tplg = "${nonarch_base_libdir}/firmware/qcom/sm8650/*tplg.bin*"
FILES:${PN}-qcom-x1e80100-adreno = "${nonarch_base_libdir}/firmware/qcom/x1e80100/gen70500_zap.mbn*"
FILES:${PN}-qcom-x1e80100-audio = "${nonarch_base_libdir}/firmware/qcom/x1e80100/adsp*.* ${nonarch_base_libdir}/firmware/qcom/x1e80100/battmgr.jsn"

RDEPENDS:${PN}-qcom-aic100 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qdu100 = "${PN}-qcom-license"

RDEPENDS:${PN}-qcom-venus-1.8 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-venus-4.2 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-venus-5.2 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-venus-5.4 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-venus-6.0 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-vpu = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a2xx = "${PN}-qcom-license ${PN}-qcom-yamato-license"
RDEPENDS:${PN}-qcom-adreno-a3xx = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a4xx = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a530 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a630 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a650 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a660 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a663 = "${PN}-qcom-license ${PN}-qcom-adreno-a660"
RDEPENDS:${PN}-qcom-adreno-a702 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g750 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8016-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8016-wifi = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8096-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8096-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8096-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-wifi = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm6490-adreno = "${PN}-qcom-license"
RPROVIDES:${PN}-qcom-qcm6490-adreno = "${PN}-qcom-qcs6490-adreno"
RDEPENDS:${PN}-qcom-qcm6490-audio = "${PN}-qcom-license"
RPROVIDES:${PN}-qcom-qcm6490-audio = "${PN}-qcom-qcs6490-audio"
RDEPENDS:${PN}-qcom-qcm6490-compute = "${PN}-qcom-license"
RPROVIDES:${PN}-qcom-qcm6490-compute = "${PN}-qcom-qcs6490-compute"
RDEPENDS:${PN}-qcom-qcm6490-ipa = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcs615-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-modem = "${PN}-qcom-license"
# Only symlinks in qcom-qrb4210-wifi, firmware is in qcom-qcm2290-wifi
# c.f. https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git/commit/?id=650e88378e76d5fad3997a5398f1ade47a74d924
RDEPENDS:${PN}-qcom-qrb4210-wifi = "${PN}-qcom-license ${PN}-qcom-qcm2290-wifi"
RDEPENDS:${PN}-qcom-sa8775p-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-thundercomm-db845c-sensors = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-thundercomm-rb5-sensors = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8550-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sm8650-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-audio = "${PN}-qcom-license"

RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"

LICENSE:${PN}-liquidui = "Firmware-cavium_liquidio"
FILES:${PN}-liquidio = "${nonarch_base_libdir}/firmware/liquidio"

LICENSE:${PN}-mellanox = "WHENCE"
FILES:${PN}-mellanox = "${nonarch_base_libdir}/firmware/mellanox"

LICENSE:${PN}-prestera = "Firmware-Marvell"
FILES:${PN}-prestera = "${nonarch_base_libdir}/firmware/mrvl/prestera"
RDEPENDS:${PN}-prestera = "${PN}-marvell-license"

# For Rockchip
LICENSE:${PN}-rockchip-dptx = "Firmware-rockchip"
FILES:${PN}-rockchip-license = "${nonarch_base_libdir}/firmware/LICENCE.rockchip"
FILES:${PN}-rockchip-dptx = "${nonarch_base_libdir}/firmware/rockchip/dptx.bin*"
RDEPENDS:${PN}-rockchip-dptx = "${PN}-rockchip-license"

# For Amlogic VDEC
LICENSE:${PN}-amlogic-vdec = "Firmware-amlogic_vdec"
FILES:${PN}-amlogic-vdec-license = "${nonarch_base_libdir}/firmware/LICENSE.amlogic_vdec"
FILES:${PN}-amlogic-vdec = "${nonarch_base_libdir}/firmware/meson/vdec/*"
RDEPENDS:${PN}-amlogic-vdec = "${PN}-amlogic-vdec-license"

# For other firmwares
# Maybe split out to separate packages when needed.
LICENSE:${PN} = "\
    Firmware-Abilis \
    & Firmware-agere \
    & Firmware-amdgpu \
    & Firmware-amd-ucode \
    & Firmware-amlogic_vdec \
    & Firmware-atmel \
    & Firmware-ca0132 \
    & Firmware-cavium \
    & Firmware-chelsio_firmware \
    & Firmware-cirrus \
    & Firmware-cnm \
    & Firmware-cw1200 \
    & Firmware-dib0700 \
    & Firmware-e100 \
    & Firmware-ene_firmware \
    & Firmware-fw_sst_0f28 \
    & Firmware-go7007 \
    & Firmware-hfi1_firmware \
    & Firmware-ibt_firmware \
    & Firmware-it913x \
    & Firmware-IntcSST2 \
    & Firmware-kaweth \
    & Firmware-moxa \
    & Firmware-myri10ge_firmware \
    & Firmware-nvidia \
    & Firmware-nxp \
    & Firmware-OLPC \
    & Firmware-ath9k-htc \
    & Firmware-phanfw \
    & Firmware-qat \
    & Firmware-qcom \
    & Firmware-qla1280 \
    & Firmware-qla2xxx \
    & Firmware-r8a779x_usb3 \
    & Firmware-radeon \
    & Firmware-ralink_a_mediatek_company_firmware \
    & Firmware-ralink-firmware \
    & Firmware-imx-sdma_firmware \
    & Firmware-siano \
    & Firmware-ti-connectivity \
    & Firmware-ti-keystone \
    & Firmware-ueagle-atm4-firmware \
    & Firmware-wl1251 \
    & Firmware-xc4000 \
    & Firmware-xc5000 \
    & Firmware-xc5000c \
    & WHENCE \
"

FILES:${PN}-license += "${nonarch_base_libdir}/firmware/LICEN*"
FILES:${PN} += "${nonarch_base_libdir}/firmware/*"
RDEPENDS:${PN} += "${PN}-license"
RDEPENDS:${PN} += "${PN}-whence-license"

# Make linux-firmware depend on all of the split-out packages.
# Make linux-firmware-iwlwifi depend on all of the split-out iwlwifi packages.
# Make linux-firmware-ibt depend on all of the split-out ibt packages.
# Make linux-firmware-ath10k depend on all of the split-out ath10k packages.
# Make linux-firmware-ath11k depend on all of the split-out ath11k packages.
# Make linux-firmware-ath12k depend on all of the split-out ath12k packages.
# Make linux-firmware-qca depend on all of the split-out qca packages.
# Make linux-firmware-amdgpu depend on all of the split-out amdgpu packages.
python populate_packages:prepend () {
    firmware_pkgs = oe.utils.packages_filter_out_system(d)
    d.appendVar('RRECOMMENDS:linux-firmware', ' ' + ' '.join(firmware_pkgs))

    iwlwifi_pkgs = filter(lambda x: x.find('-iwlwifi-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-iwlwifi', ' ' + ' '.join(iwlwifi_pkgs))

    ibt_pkgs = filter(lambda x: x.find('-ibt-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-ibt', ' ' + ' '.join(ibt_pkgs))

    ath10k_pkgs = filter(lambda x: x.find('-ath10k-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-ath10k', ' ' + ' '.join(ath10k_pkgs))

    ath11k_pkgs = filter(lambda x: x.find('-ath11k-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-ath11k', ' ' + ' '.join(ath11k_pkgs))

    ath12k_pkgs = filter(lambda x: x.find('-ath12k-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-ath12k', ' ' + ' '.join(ath12k_pkgs))

    qca_pkgs = filter(lambda x: x.find('-qca-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-qca', ' ' + ' '.join(qca_pkgs))

    amdgpu_pkgs = filter(lambda x: x.find('-amdgpu-') != -1, firmware_pkgs)
    d.appendVar('RRECOMMENDS:linux-firmware-amdgpu', ' ' + ' '.join(amdgpu_pkgs))
}

# Firmware files are generally not ran on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"

# Don't warn about already stripped files
INSANE_SKIP:${PN} = "already-stripped"

# No need to put firmware into the sysroot
SYSROOT_DIRS_IGNORE += "${nonarch_base_libdir}/firmware"
