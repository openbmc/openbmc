SUMMARY = "Firmware files for use with Linux kernel"
HOMEPAGE = "https://www.kernel.org/"
DESCRIPTION = "Linux firmware is a package distributed alongside the Linux kernel \
that contains firmware binary blobs necessary for partial or full functionality \
of certain hardware devices."
SECTION = "kernel"

REMOVE_UNLICENSED = ""

# For acenic - Alteon AceNIC Gigabit Ethernet card
REMOVE_UNLICENSED += "acenic/tg1.bin acenic/tg2.bin"

# For emi62 - EMI 6|2m USB Audio interface
REMOVE_UNLICENSED += "emi62/bitstream.fw emi62/loader.fw emi62/midi.fw emi62/spdif.fw"

# For snd-maestro3 - ESS Allegro Maestro3 audio device
REMOVE_UNLICENSED += "ess/maestro3_assp_kernel.fw ess/maestro3_assp_minisrc.fw"

# For s2255drv
REMOVE_UNLICENSED += "f2255usb.bin"

# For snd-korg1212 - Korg 1212 IO audio device
REMOVE_UNLICENSED += "korg/k1212.dsp"

# For lgs8gxx - Legend Silicon GB20600 demodulator driver
REMOVE_UNLICENSED += "lgs8g75.fw"

# For ti_usb_3410_5052 - Multi-Tech USB cell modems
REMOVE_UNLICENSED += "mts_cdma.fw mts_gsm.fw mts_edge.fw mts_mt9234mu.fw mts_mt9234zba.fw"

# For myri_sbus - MyriCOM Gigabit Ethernet
REMOVE_UNLICENSED += "myricom/lanai.bin"

# For qlogicpti - PTI Qlogic, ISP Driver
REMOVE_UNLICENSED += "qlogic/isp1000.bin"

# For cassini - Sun Cassini
REMOVE_UNLICENSED += "sun/cassini.bin"

# For dvb-ttusb-budget - Technotrend/Hauppauge Nova-USB devices
REMOVE_UNLICENSED += "ttusb-budget/dspbootcode.bin"

# For ueagle-atm - Driver for USB ADSL Modems based on Eagle I,II,III
REMOVE_UNLICENSED += "ueagle-atm/930-fpga.bin ueagle-atm/CMVeiWO.bin ueagle-atm/CMVepFR10.bin ueagle-atm/DSP9p.bin ueagle-atm/eagleIII.fw ueagle-atm/adi930.fw ueagle-atm/CMVep.bin ueagle-atm/CMVepFR.bin ueagle-atm/DSPei.bin ueagle-atm/CMV9i.bin ueagle-atm/CMVepES03.bin ueagle-atm/CMVepIT.bin ueagle-atm/DSPep.bin ueagle-atm/CMV9p.bin ueagle-atm/CMVepES.bin ueagle-atm/CMVepWO.bin ueagle-atm/eagleI.fw ueagle-atm/CMVei.bin ueagle-atm/CMVepFR04.bin ueagle-atm/DSP9i.bin ueagle-atm/eagleII.fw"

# For vicam - USB 3com HomeConnect (aka vicam)
REMOVE_UNLICENSED += "vicam/firmware.fw"

# For yam - YAM driver for AX.25
REMOVE_UNLICENSED += "yam/1200.bin yam/9600.bin"

# For snd-wavefront - ISA WaveFront sound card
REMOVE_UNLICENSED += "yamaha/yss225_registers.bin"

# For snd-ymfpci - Yamaha YMF724/740/744/754 audio devices
REMOVE_UNLICENSED += "yamaha/ds1_ctrl.fw yamaha/ds1_dsp.fw yamaha/ds1e_ctrl.fw"


LICENSE = "\
    Firmware-Abilis \
    & Firmware-adsp_sst \
    & Firmware-advansys \
    & Firmware-aeonsemi \
    & Firmware-agere \
    & Firmware-airoha \
    & Firmware-alacritech \
    & Firmware-amdgpu \
    & Firmware-amdisp \
    & Firmware-amdnpu \
    & Firmware-amd_pmf \
    & Firmware-amd-sev \
    & Firmware-amd-ucode \
    & Firmware-amlogic \
    & Firmware-amlogic_vdec \
    & Firmware-amphion_vpu \
    & Firmware-atheros_firmware \
    & Firmware-atmel \
    & Firmware-bfa \
    & Firmware-bmi260 \
    & Firmware-bnx2 \
    & Firmware-bnx2x \
    & Firmware-broadcom_bcm43xx \
    & Firmware-ca0132 \
    & Firmware-cadence \
    & Firmware-cavium \
    & Firmware-chelsio_firmware \
    & Firmware-cirrus \
    & Firmware-cnm \
    & Firmware-conexant \
    & Firmware-cw1200 \
    & Firmware-cw1200-sdd \
    & Firmware-cxgb3 \
    & Firmware-cypress \
    & Firmware-dabusb \
    & Firmware-dell \
    & Firmware-dib0700 \
    & Firmware-drxk \
    & Firmware-e100 \
    & Firmware-emi26 \
    & Firmware-ene_firmware \
    & Firmware-fw_sst_0f28 \
    & Firmware-go7007 \
    & Firmware-hfi1_firmware \
    & Firmware-HP \
    & Firmware-i915 \
    & Firmware-ib_qib \
    & Firmware-ibt_firmware \
    & Firmware-ice \
    & Firmware-ice_enhanced \
    & Firmware-inside-secure \
    & Firmware-intel \
    & Firmware-intel_vpu \
    & Firmware-ipu3_firmware \
    & Firmware-it913x \
    & Firmware-ivsc \
    & Firmware-iwlwifi_firmware \
    & Firmware-ixp4xx \
    & Firmware-IntcSST2 \
    & Firmware-kaweth \
    & Firmware-keyspan \
    & Firmware-lenovo \
    & Firmware-linaro \
    & Firmware-Lontium \
    & Firmware-mali_csffw \
    & Firmware-Marvell \
    & Firmware-mediatek \
    & Firmware-mellanox \
    & Firmware-mga \
    & Firmware-microchip \
    & Firmware-montage \
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
    & Firmware-qcom-2 \
    & Firmware-qcom-yamato \
    & Firmware-qed \
    & Firmware-qla1280 \
    & Firmware-qla2xxx \
    & Firmware-qualcommAthos_ar3k \
    & Firmware-qualcommAthos_ath10k \
    & Firmware-r8169 \
    & Firmware-r8a779x_usb3 \
    & Firmware-radeon \
    & Firmware-ralink_a_mediatek_company_firmware \
    & Firmware-ralink-firmware \
    & Firmware-r8a779g_pcie_phy \
    & Firmware-rockchip \
    & Firmware-rp2 \
    & Firmware-rsi \
    & Firmware-rt1320 \
    & Firmware-rtlwifi_firmware \
    & Firmware-imx-sdma_firmware \
    & Firmware-s5p-mfc \
    & Firmware-sensoray \
    & Firmware-siano \
    & Firmware-tehuti \
    & Firmware-ti-connectivity \
    & Firmware-ti-keystone \
    & Firmware-ti-tspa \
    & Firmware-tigon \
    & Firmware-tlg2300 \
    & Firmware-typhoon \
    & Firmware-ueagle-atm4-firmware \
    & Firmware-via_vt6656 \
    & Firmware-vxge \
    & Firmware-wfx \
    & Firmware-wl1251 \
    & Firmware-xc4000 \
    & Firmware-xc5000 \
    & Firmware-xc5000c \
    & Firmware-xe \
    & WHENCE \
    & GPL-1.0-only \
    & GPL-2.0-or-later \
    & GPL-2.0-only \
    & GPL-3.0-only \
    & MPL-1.1 \
    & Apache-2.0 \
    & MIT \
"

LIC_FILES_CHKSUM = "file://LICENCE.Abilis;md5=b5ee3f410780e56711ad48eadc22b8bc \
                    file://LICENCE.adsp_sst;md5=615c45b91a5a4a9fe046d6ab9a2df728 \
                    file://LICENCE.advansys;md5=bca735476602a7bcb187c7f8bf4a31d5 \
                    file://LICENSE.aeonsemi;md5=521c00bae0077c90d6ffb9ccf66905ae \
                    file://LICENCE.agere;md5=af0133de6b4a9b2522defd5f188afd31 \
                    file://LICENSE.airoha;md5=fa3dedb960e2673aea51aa509f7b537d \
                    file://LICENCE.alacritech;md5=75dabc07cc2fb59d929baa6bd6aae48a \
                    file://LICENSE.amdgpu;md5=1433dfea38c97a2e563a248a863dcb94 \
                    file://LICENSE.amdisp;md5=f040a36bf52c9643edb7c009d6f1b141 \
                    file://LICENSE.amdnpu;md5=ea42c0f38f2d42aad08bd50c822460dc \
                    file://LICENSE.amd_pmf;md5=a2589a05ea5b6bd2b7f4f623c7e7a649 \
                    file://LICENSE.amd-sev;md5=e750538791a8be0b7249c579edefb035 \
                    file://LICENSE.amd-ucode;md5=450f217aadc787514165e9568652d700 \
                    file://LICENSE.amlogic;md5=80e4e3f27def8bc4b232009c3a587c07 \
                    file://LICENSE.amlogic_vdec;md5=dc44f59bf64a81643e500ad3f39a468a \
                    file://LICENSE.amphion_vpu;md5=2bcdc00527b2d0542bd92b52aaec2b60 \
                    file://LICENCE.atheros_firmware;md5=30a14c7823beedac9fa39c64fdd01a13 \
                    file://LICENSE.atmel;md5=aa74ac0c60595dee4d4e239107ea77a3 \
                    file://LICENSE.bfa;md5=ff822aa5edf56d0159acfe5a49a5be2c \
                    file://LICENSE.bmi260;md5=0008c039ec4281e382bd0cb41b66866c \
                    file://LICENCE.bnx2;md5=d156fb810e162c4b0065ec8316efcd38 \
                    file://LICENCE.bnx2x;md5=9494ec1462e461dec5322d1a1f0adf81 \
                    file://LICENCE.broadcom_bcm43xx;md5=3160c14df7228891b868060e1951dfbc \
                    file://LICENCE.ca0132;md5=209b33e66ee5be0461f13d31da392198 \
                    file://LICENCE.cadence;md5=009f46816f6956cfb75ede13d3e1cee0 \
                    file://LICENCE.cavium;md5=c37aaffb1ebe5939b2580d073a95daea \
                    file://LICENCE.chelsio_firmware;md5=819aa8c3fa453f1b258ed8d168a9d903 \
                    file://LICENSE.cirrus;md5=662ea2c1a8888f7d79ed7f27c27472e1 \
                    file://LICENCE.cnm;md5=df3992006621b797e36de43f36336e36 \
                    file://LICENSE.conexant;md5=768b10e3fc2bbc0725174a7d9e164c26 \
                    file://LICENCE.cw1200;md5=f0f770864e7a8444a5c5aa9d12a3a7ed \
                    file://LICENCE.cw1200-sdd;md5=7e99e5e15c3668e96504a82ebd532ee4 \
                    file://LICENCE.cxgb3;md5=1cf82d9e2a4b301e20c7936e61cd0e45 \
                    file://LICENCE.cypress;md5=48cd9436c763bf873961f9ed7b5c147b \
                    file://LICENCE.dabusb;md5=fd785fc5f935c950a3423e4b1b996657 \
                    file://LICENSE.dell;md5=032c317c0483dd3364f478d2bf9d9818 \
                    file://LICENSE.dib0700;md5=f7411825c8a555a1a3e5eab9ca773431 \
                    file://LICENSE.drxk;md5=87a325e2e9740837036af3f04efa0d0f \
                    file://LICENCE.e100;md5=ec0f84136766df159a3ae6d02acdf5a8 \
                    file://LICENCE.emi26;md5=2d1cd6e732b81824fe2f0fbf595b1413 \
                    file://LICENCE.ene_firmware;md5=ed67f0f62f8f798130c296720b7d3921 \
                    file://LICENCE.fw_sst_0f28;md5=6353931c988ad52818ae733ac61cd293 \
                    file://LICENCE.go7007;md5=c0bb9f6aaaba55b0529ee9b30aa66beb \
                    file://LICENSE.hfi1_firmware;md5=5e7b6e586ce7339d12689e49931ad444 \
                    file://LICENCE.HP;md5=3506ce9cd4bedeaa4afb2d8fe24e0688 \
                    file://LICENSE.i915;md5=2b0b2e0d20984affd4490ba2cba02570 \
                    file://LICENSE.ib_qib;md5=b909c90fca84c507766601ecb6f3b9d9 \
                    file://LICENCE.ibt_firmware;md5=fdbee1ddfe0fb7ab0b2fcd6b454a366b \
                    file://LICENSE.ice;md5=742ab4850f2670792940e6d15c974b2f \
                    file://LICENSE.ice_enhanced;md5=f305cfc31b64f95f774f9edd9df0224d \
                    file://LICENCE.inside-secure;md5=71f2eb7c1d10ccbd198e2459adef6afa \
                    file://LICENCE.IntcSST2;md5=9e7d8bea77612d7cc7d9e9b54b623062 \
                    file://LICENSE.intel;md5=5c22a4ab607349c89ffcbb1595e493f8 \
                    file://LICENSE.intel_vpu;md5=1e231b7287d5a5018740041c352eb58e \
                    file://LICENSE.ipu3_firmware;md5=38fe8238c06bf7dcfd0eedbebf452c3b \
                    file://LICENCE.it913x;md5=1fbf727bfb6a949810c4dbfa7e6ce4f8 \
                    file://LICENSE.ivsc;md5=4f1f696a12c18dd058d3cc51006c640d \
                    file://LICENCE.iwlwifi_firmware;md5=2ce6786e0fc11ac6e36b54bb9b799f1b \
                    file://LICENSE.ixp4xx;md5=ddc5cd6cbc6745343926fe7ecc2cdeb2 \
                    file://LICENCE.kaweth;md5=b1d876e562f4b3b8d391ad8395dfe03f \
                    file://LICENCE.keyspan;md5=676af26017c45772c972ce4a75d467d9 \
                    file://LICENCE.lenovo;md5=7f25420b5c27211f7bf33bebb3042ce4 \
                    file://LICENCE.linaro;md5=936d91e71cf9cd30e733db4bf11661cc \
                    file://LICENSE.Lontium;md5=4ec8dc582ff7295f39e2ca6a7b0be2b6 \
                    file://LICENCE.mali_csffw;md5=e064aaec4d21ef856e1b76a6f5dc435f \
                    file://LICENCE.Marvell;md5=28b6ed8bd04ba105af6e4dcd6e997772 \
                    file://LICENCE.mediatek;md5=7c1976b63217d76ce47d0a11d8a79cf2 \
                    file://LICENSE.mellanox;md5=646741eee66a7925edc538650895b80c \
                    file://LICENSE.mga;md5=6191fc1ff8183b00515c36351ec24150 \
                    file://LICENCE.microchip;md5=db753b00305675dfbf120e3f24a47277 \
                    file://LICENSE.montage;md5=12a9f2b351f60fc9374da61c8b2f11ed \
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
                    file://LICENSE.qcom-2;md5=165287851294f2fb8ac8cbc5e24b02b0 \
                    file://LICENSE.qcom_yamato;md5=d0de0eeccaf1843a850bf7a6777eec5c \
                    file://LICENSE.qed;md5=939ee0945e6efa0ce21f6d8a21c6564c \
                    file://LICENCE.qla1280;md5=d6895732e622d950609093223a2c4f5d \
                    file://LICENCE.qla2xxx;md5=505855e921b75f1be4a437ad9b79dff0 \
                    file://LICENSE.QualcommAtheros_ar3k;md5=b5fe244fb2b532311de1472a3bc06da5 \
                    file://LICENSE.QualcommAtheros_ath10k;md5=cb42b686ee5f5cb890275e4321db60a8 \
                    file://LICENSE.r8169;md5=a9909160e6bd81b8770711918b418ef2 \
                    file://LICENCE.r8a779x_usb3;md5=4c1671656153025d7076105a5da7e498 \
                    file://LICENSE.radeon;md5=68ec28bacb3613200bca44f404c69b16 \
                    file://LICENCE.ralink_a_mediatek_company_firmware;md5=728f1a85fd53fd67fa8d7afb080bc435 \
                    file://LICENCE.ralink-firmware.txt;md5=ab2c269277c45476fb449673911a2dfd \
                    file://LICENCE.rockchip;md5=5fd70190c5ed39734baceada8ecced26 \
                    file://LICENCE.r8a779g_pcie_phy;md5=0b20e76a9a004b83c4a1c87e2153bbad \
                    file://LICENSE.rp2;md5=de5109226a643a1cdf706a633e993514 \
                    file://LICENSE.rsi;md5=a560f4b285f0733de1a3986ae847675d \
                    file://LICENSE.rt1320;md5=b44dab4314655e8f015009548dc4f962 \
                    file://LICENCE.rtlwifi_firmware.txt;md5=00d06cfd3eddd5a2698948ead2ad54a5 \
                    file://LICENSE.s5p-mfc;md5=5bdad20069b5c0268245609045374639 \
                    file://LICENSE.sdma_firmware;md5=51e8c19ecc2270f4b8ea30341ad63ce9 \
                    file://LICENCE.sensoray;md5=2273a7fed8223f6d3ef3e65f508f22eb \
                    file://LICENCE.siano;md5=4556c1bf830067f12ca151ad953ec2a5 \
                    file://LICENSE.tehuti;md5=2b0ebf8cdc4a1c4a49b8ad18c7cb2492 \
                    file://LICENCE.ti-connectivity;md5=3b1e9cf54aba8146dad4b735777d406f \
                    file://LICENCE.ti-keystone;md5=3a86335d32864b0bef996bee26cc0f2c \
                    file://LICENCE.ti-tspa;md5=d1a0eb27d0020752040190b9d51ad9be \
                    file://LICENCE.tigon;md5=49d104a32337f4a4c89478a86ce9ae4f \
                    file://LICENSE.tlg2300;md5=4b23ec9ced919a0bf2f7c56dac31b2b7 \
                    file://LICENCE.typhoon;md5=43b30243a6bda91f54c8e00600c4add5 \
                    file://LICENCE.ueagle-atm4-firmware;md5=4ed7ea6b507ccc583b9d594417714118 \
                    file://LICENCE.via_vt6656;md5=e4159694cba42d4377a912e78a6e850f \
                    file://LICENSE.vxge;md5=91e196370d9927bdf7f566e47ea2c558 \
                    file://LICENCE.wl1251;md5=ad3f81922bb9e197014bb187289d3b5b \
                    file://LICENCE.xc4000;md5=0ff51d2dc49fce04814c9155081092f0 \
                    file://LICENCE.xc5000;md5=1e170c13175323c32c7f4d0998d53f66 \
                    file://LICENCE.xc5000c;md5=12b02efa3049db65d524aeb418dd87ca \
                    file://LICENSE.xe;md5=c674d38774242bc0c528214721488118 \
                    file://wfx/LICENCE.wf200;md5=4d1beff00d902c05c9c7e95a5d8eb52d \
                    file://WHENCE;md5=${WHENCE_CHKSUM} \
                    "
# WHENCE checksum is defined separately to ease overriding it if
# class-devupstream is selected.
WHENCE_CHKSUM  = "e3897ca46d046032a2b60b8554caf61d"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-Abilis] = "LICENCE.Abilis"
NO_GENERIC_LICENSE[Firmware-adsp_sst] = "LICENCE.adsp_sst"
NO_GENERIC_LICENSE[Firmware-advansys] = "LICENCE.advansys"
NO_GENERIC_LICENSE[Firmware-aeonsemi] = "LICENSE.aeonsemi"
NO_GENERIC_LICENSE[Firmware-agere] = "LICENCE.agere"
NO_GENERIC_LICENSE[Firmware-airoha] = "LICENSE.airoha"
NO_GENERIC_LICENSE[Firmware-alacritech] = "LICENCE.alacritech"
NO_GENERIC_LICENSE[Firmware-amdgpu] = "LICENSE.amdgpu"
NO_GENERIC_LICENSE[Firmware-amdisp] = "LICENSE.amdisp"
NO_GENERIC_LICENSE[Firmware-amdnpu] = "LICENSE.amdnpu"
NO_GENERIC_LICENSE[Firmware-amd_pmf] = "LICENSE.amd_pmf"
NO_GENERIC_LICENSE[Firmware-amd-sev] = "LICENSE.amd-sev"
NO_GENERIC_LICENSE[Firmware-amd-ucode] = "LICENSE.amd-ucode"
NO_GENERIC_LICENSE[Firmware-amlogic] = "LICENSE.amlogic"
NO_GENERIC_LICENSE[Firmware-amlogic_vdec] = "LICENSE.amlogic_vdec"
NO_GENERIC_LICENSE[Firmware-amphion_vpu] = "LICENSE.amphion_vpu"
NO_GENERIC_LICENSE[Firmware-atheros_firmware] = "LICENCE.atheros_firmware"
NO_GENERIC_LICENSE[Firmware-atmel] = "LICENSE.atmel"
NO_GENERIC_LICENSE[Firmware-bfa] = "LICENSE.bfa"
NO_GENERIC_LICENSE[Firmware-bmi260] = "LICENSE.bmi260"
NO_GENERIC_LICENSE[Firmware-bnx2] = "LICENCE.bnx2"
NO_GENERIC_LICENSE[Firmware-bnx2x] = "LICENCE.bnx2x"
NO_GENERIC_LICENSE[Firmware-broadcom_bcm43xx] = "LICENCE.broadcom_bcm43xx"
NO_GENERIC_LICENSE[Firmware-ca0132] = "LICENCE.ca0132"
NO_GENERIC_LICENSE[Firmware-cadence] = "LICENCE.cadence"
NO_GENERIC_LICENSE[Firmware-cavium] = "LICENCE.cavium"
NO_GENERIC_LICENSE[Firmware-chelsio_firmware] = "LICENCE.chelsio_firmware"
NO_GENERIC_LICENSE[Firmware-cirrus] = "LICENSE.cirrus"
NO_GENERIC_LICENSE[Firmware-cnm] = "LICENCE.cnm"
NO_GENERIC_LICENSE[Firmware-conexant] = "LICENSE.conexant"
NO_GENERIC_LICENSE[Firmware-cw1200] = "LICENCE.cw1200"
NO_GENERIC_LICENSE[Firmware-cw1200-sdd] = "LICENCE.cw1200-sdd"
NO_GENERIC_LICENSE[Firmware-cxgb3] = "LICENCE.cxgb3"
NO_GENERIC_LICENSE[Firmware-cypress] = "LICENCE.cypress"
NO_GENERIC_LICENSE[Firmware-dabusb] = "LICENCE.dabusb"
NO_GENERIC_LICENSE[Firmware-dell] = "LICENSE.dell"
NO_GENERIC_LICENSE[Firmware-dib0700] = "LICENSE.dib0700"
NO_GENERIC_LICENSE[Firmware-drxk] = "LICENSE.drxk"
NO_GENERIC_LICENSE[Firmware-e100] = "LICENCE.e100"
NO_GENERIC_LICENSE[Firmware-emi26] = "LICENCE.emi26"
NO_GENERIC_LICENSE[Firmware-ene_firmware] = "LICENCE.ene_firmware"
NO_GENERIC_LICENSE[Firmware-fw_sst_0f28] = "LICENCE.fw_sst_0f28"
NO_GENERIC_LICENSE[Firmware-go7007] = "LICENCE.go7007"
NO_GENERIC_LICENSE[Firmware-hfi1_firmware] = "LICENSE.hfi1_firmware"
NO_GENERIC_LICENSE[Firmware-HP] = "LICENCE.HP"
NO_GENERIC_LICENSE[Firmware-i915] = "LICENSE.i915"
NO_GENERIC_LICENSE[Firmware-ib_qib] = "LICENSE.ib_qib"
NO_GENERIC_LICENSE[Firmware-ibt_firmware] = "LICENCE.ibt_firmware"
NO_GENERIC_LICENSE[Firmware-ice] = "LICENSE.ice"
NO_GENERIC_LICENSE[Firmware-ice_enhanced] = "LICENSE.ice_enhanced"
NO_GENERIC_LICENSE[Firmware-inside-secure] = "LICENCE.inside-secure"
NO_GENERIC_LICENSE[Firmware-IntcSST2] = "LICENCE.IntcSST2"
NO_GENERIC_LICENSE[Firmware-intel] = "LICENSE.intel"
NO_GENERIC_LICENSE[Firmware-intel_vpu] = "LICENSE.intel_vpu"
NO_GENERIC_LICENSE[Firmware-ipu3_firmware] = "LICENSE.ipu3_firmware"
NO_GENERIC_LICENSE[Firmware-it913x] = "LICENCE.it913x"
NO_GENERIC_LICENSE[Firmware-ivsc] = "LICENSE.ivsc"
NO_GENERIC_LICENSE[Firmware-iwlwifi_firmware] = "LICENCE.iwlwifi_firmware"
NO_GENERIC_LICENSE[Firmware-ixp4xx] = "LICENSE.ixp4xx"
NO_GENERIC_LICENSE[Firmware-kaweth] = "LICENCE.kaweth"
NO_GENERIC_LICENSE[Firmware-keyspan] = "LICENCE.keyspan"
NO_GENERIC_LICENSE[Firmware-lenovo] = "LICENCE.lenovo"
NO_GENERIC_LICENSE[Firmware-linaro] = "LICENCE.linaro"
NO_GENERIC_LICENSE[Firmware-Lontium] = "LICENSE.Lontium"
NO_GENERIC_LICENSE[Firmware-mali_csffw] = "LICENCE.mali_csffw"
NO_GENERIC_LICENSE[Firmware-Marvell] = "LICENCE.Marvell"
NO_GENERIC_LICENSE[Firmware-mediatek] = "LICENCE.mediatek"
NO_GENERIC_LICENSE[Firmware-mellanox] = "LICENSE.mellanox"
NO_GENERIC_LICENSE[Firmware-mga] = "LICENSE.mga"
NO_GENERIC_LICENSE[Firmware-microchip] = "LICENCE.microchip"
NO_GENERIC_LICENSE[Firmware-montage] = "LICENSE.montage"
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
NO_GENERIC_LICENSE[Firmware-qcom-2] = "LICENSE.qcom-2"
NO_GENERIC_LICENSE[Firmware-qcom-yamato] = "LICENSE.qcom_yamato"
NO_GENERIC_LICENSE[Firmware-qed] = "LICENSE.qed"
NO_GENERIC_LICENSE[Firmware-qla1280] = "LICENCE.qla1280"
NO_GENERIC_LICENSE[Firmware-qla2xxx] = "LICENCE.qla2xxx"
NO_GENERIC_LICENSE[Firmware-qualcommAthos_ar3k] = "LICENSE.QualcommAtheros_ar3k"
NO_GENERIC_LICENSE[Firmware-qualcommAthos_ath10k] = "LICENSE.QualcommAtheros_ath10k"
NO_GENERIC_LICENSE[Firmware-r8169] = "LICENSE.r8169"
NO_GENERIC_LICENSE[Firmware-r8a779x_usb3] = "LICENCE.r8a779x_usb3"
NO_GENERIC_LICENSE[Firmware-radeon] = "LICENSE.radeon"
NO_GENERIC_LICENSE[Firmware-ralink_a_mediatek_company_firmware] = "LICENCE.ralink_a_mediatek_company_firmware"
NO_GENERIC_LICENSE[Firmware-ralink-firmware] = "LICENCE.ralink-firmware.txt"
NO_GENERIC_LICENSE[Firmware-r8a779g_pcie_phy] = "LICENCE.r8a779g_pcie_phy"
NO_GENERIC_LICENSE[Firmware-rockchip] = "LICENCE.rockchip"
NO_GENERIC_LICENSE[Firmware-rp2] = "LICENSE.rp2"
NO_GENERIC_LICENSE[Firmware-rsi] = "LICENSE.rsi"
NO_GENERIC_LICENSE[Firmware-rt1320] = "LICENSE.rt1320"
NO_GENERIC_LICENSE[Firmware-rtlwifi_firmware] = "LICENCE.rtlwifi_firmware.txt"
NO_GENERIC_LICENSE[Firmware-s5p-mfc] = "LICENSE.s5p-mfc"
NO_GENERIC_LICENSE[Firmware-sensoray] = "LICENCE.sensoray"
NO_GENERIC_LICENSE[Firmware-siano] = "LICENCE.siano"
NO_GENERIC_LICENSE[Firmware-imx-sdma_firmware] = "LICENSE.sdma_firmware"
NO_GENERIC_LICENSE[Firmware-tehuti] = "LICENSE.tehuti"
NO_GENERIC_LICENSE[Firmware-ti-connectivity] = "LICENCE.ti-connectivity"
NO_GENERIC_LICENSE[Firmware-ti-keystone] = "LICENCE.ti-keystone"
NO_GENERIC_LICENSE[Firmware-ti-tspa] = "LICENCE.ti-tspa"
NO_GENERIC_LICENSE[Firmware-tigon] = "LICENCE.tigon"
NO_GENERIC_LICENSE[Firmware-tlg2300] = "LICENSE.tlg2300"
NO_GENERIC_LICENSE[Firmware-typhoon] = "LICENCE.typhoon"
NO_GENERIC_LICENSE[Firmware-ueagle-atm4-firmware] = "LICENCE.ueagle-atm4-firmware"
NO_GENERIC_LICENSE[Firmware-via_vt6656] = "LICENCE.via_vt6656"
NO_GENERIC_LICENSE[Firmware-vxge] = "LICENSE.vxge"
NO_GENERIC_LICENSE[Firmware-wfx] = "wfx/LICENCE.wf200"
NO_GENERIC_LICENSE[Firmware-wl1251] = "LICENCE.wl1251"
NO_GENERIC_LICENSE[Firmware-xc4000] = "LICENCE.xc4000"
NO_GENERIC_LICENSE[Firmware-xc5000] = "LICENCE.xc5000"
NO_GENERIC_LICENSE[Firmware-xc5000c] = "LICENCE.xc5000c"
NO_GENERIC_LICENSE[Firmware-xe] = "LICENSE.xe"
NO_GENERIC_LICENSE[WHENCE] = "WHENCE"

PE = "1"

SRC_URI = "\
  ${KERNELORG_MIRROR}/linux/kernel/firmware/${BPN}-${PV}.tar.xz \
"

BBCLASSEXTEND = "devupstream:target"
SRC_URI:class-devupstream = "git://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git;protocol=https;branch=main"
# Pin this to the 20220509 release, override this in local.conf
SRCREV:class-devupstream ?= "b19cbdca78ab2adfd210c91be15a22568e8b8cae"

SRC_URI[sha256sum] = "bd19acc4c1a02548e09d3df67f987fe6e378df735bab138c1d9e917962056d94"

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
    if compr == 'zstd':
        compr = 'zst'
    return '-' + compr

def fw_compr_file_suffix(d):
    compr = d.getVar('FIRMWARE_COMPRESSION')
    if compr == '':
        return ''
    if compr == 'zstd':
        compr = 'zst'
    return '.' + compr

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

        # Remove all unlicensed firmware
        for file in ${REMOVE_UNLICENSED}; do
                echo "Remove unlicensed firmware: $file"
                rm ${D}${nonarch_base_libdir}/firmware/$file${@fw_compr_file_suffix(d)}
                path_to_file=$(dirname $file)
                while [ "${path_to_file}" != "." ]; do
                        num_files=$(ls -A1 ${D}${nonarch_base_libdir}/firmware/$path_to_file | wc -l)
                        if [ "$num_files" = "0" ]; then
                                echo "Remove empty dir: $path_to_file"
                                rm -rf ${D}${nonarch_base_libdir}/firmware/$path_to_file
                        fi
                        path_to_file=$(dirname $path_to_file)
                done
        done
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
             ${PN}-cf8381 ${PN}-cf8385 ${PN}-gspi8682 ${PN}-gspi8686 ${PN}-gspi8688 ${PN}-sd8385 ${PN}-sd8682 \
             ${PN}-usb8388 ${PN}-usb8682 ${PN}-sd8977 ${PN}-usb8766 ${PN}-usb8797 ${PN}-usb8801 ${PN}-usb8897 ${PN}-rvu-cptpf \
             ${PN}-mwl8k ${PN}-mwlwifi\
             ${PN}-ti-connectivity-license ${PN}-wl1251-license ${PN}-wlcommon ${PN}-wl1251 ${PN}-wl12xx ${PN}-wl18xx ${PN}-cc33xx \
             ${PN}-ti-keystone-license ${PN}-ti-keystone \
             ${PN}-ti-tspa-license ${PN}-ti-tas2563 ${PN}-ti-tas2781 ${PN}-ti-tas2783 ${PN}-ti-vpe \
             ${PN}-ti-usb-3410-5052 \
             ${PN}-vt6656-license ${PN}-vt6656 \
             ${PN}-rs9113 ${PN}-rs9116 ${PN}-rsi-91x \
             ${PN}-rtl-license ${PN}-rtl8188 ${PN}-rtl8192cu ${PN}-rtl8192ce ${PN}-rtl8192su ${PN}-rtl8723 ${PN}-rtl8821 \
             ${PN}-rtl8761 \
             ${PN}-rtl8168 \
             ${PN}-rtl8822 \
             ${PN}-rtl8192 ${PN}-rtl8710 ${PN}-rtl8812 \
             ${PN}-rtl8851 ${PN}-rtl8852 ${PN}-rtl8922 \
             ${PN}-rtl8703 ${PN}-rtl8814 \
             ${PN}-rtl-nic \
             ${PN}-cypress-license \
             ${PN}-broadcom-license \
             ${PN}-bcm-0bb4-0306 \
             ${PN}-bcm-0a5c-6410 \
             ${PN}-bcm43012 \
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
             ${PN}-bcm54591 \
             ${PN}-cirrus-license ${PN}-cirrus ${PN}-cirrus-cs42l45 \
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
             ${PN}-ath11k \
             ${PN}-ath11k-ipq5018 \
             ${PN}-ath11k-ipq6018 \
             ${PN}-ath11k-ipq8074 \
             ${PN}-ath11k-qca2066 \
             ${PN}-ath11k-qca6390 \
             ${PN}-ath11k-qca6698aq \
             ${PN}-ath11k-qcn9074 \
             ${PN}-ath11k-wcn6750 \
             ${PN}-ath11k-wcn6855 \
             ${PN}-ath12k \
             ${PN}-ath12k-qcn9274 \
             ${PN}-ath12k-wcn7850 \
             ${PN}-qca \
             ${PN}-qca-qca2066 \
             ${PN}-qca-qca61x4-serial \
             ${PN}-qca-qca61x4-usb \
             ${PN}-qca-qca6390 \
             ${PN}-qca-qca6698 \
             ${PN}-qca-qcc2072 \
             ${PN}-qca-wcn3950 \
             ${PN}-qca-wcn3988 \
             ${PN}-qca-wcn399x \
             ${PN}-qca-wcn6750 \
             ${PN}-qca-wcn685x \
             ${PN}-qca-wcn7850 \
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
             ${PN}-adsp-sst-license ${PN}-adsp-sst ${PN}-snd-soc-avs \
             ${PN}-bnx2 \
             ${PN}-bnx2x \
             ${PN}-liquidio \
             ${PN}-linaro-license \
             ${PN}-mali-csffw-arch108 ${PN}-mali-csffw-arch1010 ${PN}-mali-csffw-arch1012 ${PN}-mali-csffw-arch118 ${PN}-mali-csffw-arch128 ${PN}-mali-csffw-arch138 ${PN}-mali-csffw-license \
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
             ${PN}-nxp-sr1xx \
             ${PN}-nxp-mc-license ${PN}-nxp-mc \
             ${PN}-netronome-license ${PN}-netronome \
             ${PN}-olpc-license ${PN}-olpc \
             ${PN}-phanfw-license ${PN}-phanfw \
             ${PN}-powervr-license ${PN}-powervr \
             ${PN}-prestera \
             ${PN}-qat ${PN}-qat-license \
             ${PN}-qed \
             ${PN}-qcom-license ${PN}-qcom-2-license ${PN}-qcom-yamato-license \
             ${PN}-qcom-aic100 ${PN}-qcom-qdu100 \
             ${PN}-qcom-venus-1.8 ${PN}-qcom-venus-4.2 ${PN}-qcom-venus-5.2 ${PN}-qcom-venus-5.4 ${PN}-qcom-venus-6.0 \
             ${PN}-qcom-vpu \
             ${PN}-qcom-adreno-a2xx ${PN}-qcom-adreno-a3xx ${PN}-qcom-adreno-a4xx ${PN}-qcom-adreno-a530 \
             ${PN}-qcom-adreno-a612 ${PN}-qcom-adreno-a623 ${PN}-qcom-adreno-a630 ${PN}-qcom-adreno-a640 \
             ${PN}-qcom-adreno-a650 ${PN}-qcom-adreno-a660 ${PN}-qcom-adreno-a663 \
             ${PN}-qcom-adreno-a702 ${PN}-qcom-adreno-a730 ${PN}-qcom-adreno-a740 \
             ${PN}-qcom-adreno-g705 ${PN}-qcom-adreno-g709 ${PN}-qcom-adreno-g715 \
             ${PN}-qcom-adreno-g800 ${PN}-qcom-adreno-g801 ${PN}-qcom-adreno-g802 \
             ${PN}-qcom-apq8016-modem ${PN}-qcom-apq8016-wifi \
             ${PN}-qcom-apq8096-adreno ${PN}-qcom-apq8096-audio ${PN}-qcom-apq8096-modem \
             ${PN}-qcom-glymur-adreno ${PN}-qcom-glymur-audio ${PN}-qcom-glymur-compute \
             ${PN}-qcom-kaanapali-adreno ${PN}-qcom-kaanapali-audio ${PN}-qcom-kaanapali-compute ${PN}-qcom-kaanapali-soccp \
             ${PN}-qcom-qcm2290-adreno ${PN}-qcom-qcm2290-audio ${PN}-qcom-qcm2290-modem \
             ${PN}-qcom-qcm6490-adreno ${PN}-qcom-qcm6490-audio ${PN}-qcom-qcm6490-compute \
             ${PN}-qcom-qcm6490-ipa ${PN}-qcom-qcm6490-wifi ${PN}-qcom-qcm6490-qupv3fw \
             ${PN}-qcom-qcs615-adreno ${PN}-qcom-qcs615-audio ${PN}-qcom-qcs615-compute \
             ${PN}-qcom-qcs6490-radxa-dragon-q6a-audio ${PN}-qcom-qcs6490-radxa-dragon-q6a-compute \
             ${PN}-qcom-qcs6490-thundercomm-rubikpi3-audio \
             ${PN}-qcom-qcs8300-adreno ${PN}-qcom-qcs8300-audio ${PN}-qcom-qcs8300-compute \
             ${PN}-qcom-qcs8300-generalpurpose ${PN}-qcom-qcs8300-qupv3fw \
             ${PN}-qcom-qrb4210-adreno \
             ${PN}-qcom-qrb4210-audio ${PN}-qcom-qrb4210-compute ${PN}-qcom-qrb4210-modem \
             ${PN}-qcom-sa8775p-adreno ${PN}-qcom-sa8775p-audio ${PN}-qcom-sa8775p-compute \
             ${PN}-qcom-sa8775p-generalpurpose ${PN}-qcom-sa8775p-qupv3fw \
             ${PN}-qcom-sc8280xp-lenovo-x13s-compat \
             ${PN}-qcom-sc8280xp-lenovo-x13s-audio \
             ${PN}-qcom-sc8280xp-lenovo-x13s-adreno \
             ${PN}-qcom-sc8280xp-lenovo-x13s-compute \
             ${PN}-qcom-sc8280xp-lenovo-x13s-sensors \
             ${PN}-qcom-sc8280xp-lenovo-x13s-vpu \
             ${PN}-qcom-sdm845-adreno ${PN}-qcom-sdm845-audio ${PN}-qcom-sdm845-compute \
             ${PN}-qcom-sdm845-thundercomm-db845c-sensors \
             ${PN}-qcom-sdx35-foxconn-firehose ${PN}-qcom-sdx61-foxconn-firehose \
             ${PN}-qcom-sm8150-adreno \
             ${PN}-qcom-sm8250-adreno ${PN}-qcom-sm8250-audio ${PN}-qcom-sm8250-compute \
             ${PN}-qcom-sm8250-thundercomm-rb5-sensors \
             ${PN}-qcom-sm8350-adreno \
             ${PN}-qcom-sm8450-adreno ${PN}-qcom-sm8450-audio-tplg \
             ${PN}-qcom-sm8550-adreno ${PN}-qcom-sm8550-audio-tplg \
             ${PN}-qcom-sm8650-adreno ${PN}-qcom-sm8650-audio-tplg \
             ${PN}-qcom-sm8750-adreno ${PN}-qcom-sm8750-audio ${PN}-qcom-sm8750-compute \
             ${PN}-qcom-x1e80100-asus-vivobook-16-audio-tplg \
             ${PN}-qcom-x1e80100-asus-vivobook-s15-audio-tplg \
             ${PN}-qcom-x1e80100-asus-zenbook-a14-audio-tplg \
             ${PN}-qcom-x1e80100-dell-inspiron-14-plus-7441-audio-tplg \
             ${PN}-qcom-x1e80100-dell-latitude-7455-audio-tplg \
             ${PN}-qcom-x1e80100-dell-xps13-9345-audio-tplg \
             ${PN}-qcom-x1e80100-hp-omnibook-x14-audio-tplg \
             ${PN}-qcom-x1e80100-adreno ${PN}-qcom-x1e80100-audio ${PN}-qcom-x1e80100-compute \
             ${PN}-qcom-x1e80100-lenovo-t14s-g6-adreno ${PN}-qcom-x1e80100-lenovo-t14s-g6-audio \
             ${PN}-qcom-x1e80100-lenovo-t14s-g6-compute ${PN}-qcom-x1e80100-lenovo-t14s-g6-vpu \
             ${PN}-qcom-x1e80100-lenovo-yoga-slim7x-adreno ${PN}-qcom-x1e80100-lenovo-yoga-slim7x-audio \
             ${PN}-qcom-x1e80100-lenovo-yoga-slim7x-compute ${PN}-qcom-x1e80100-lenovo-yoga-slim7x-vpu \
             ${PN}-qcom-x1e80100-qupv3fw \
             ${PN}-qcom-x1p42100-adreno \
             ${PN}-qla2xxx ${PN}-qla2xxx-license \
             ${PN}-rockchip-license ${PN}-rockchip-dptx \
             ${PN}-amlogic-vdec-license ${PN}-amlogic-vdec \
             ${PN}-lt9611uxc ${PN}-lontium-license \
             ${PN}-wfx-license ${PN}-wfx \
             ${PN}-whence-license \
             ${PN}-xc4000-license ${PN}-xc4000 \
             ${PN}-xc5000-license ${PN}-xc5000 \
             ${PN}-xc5000c-license ${PN}-xc5000c \
             ${PN}-typhoon-license ${PN}-typhoon \
             ${PN}-intel-license ${PN}-ish-lnlm \
             ${PN}-dell-license ${PN}-ish-lnlm-39ceeaf8 ${PN}-ish-ptl-39ceeaf8 \
             ${PN}-hp-license ${PN}-ish-lnlm-12128606 \
             ${PN}-lenovo-license ${PN}-ish-lnlm-53c4ffad \
             ${PN}-ish-ptl \
             ${PN}-advansys-license ${PN}-advansys \
             ${PN}-aeonsemi-license ${PN}-as21xxx \
             ${PN}-agere-license ${PN}-orinoco \
             ${PN}-airoha-license ${PN}-an8811hb ${PN}-en8811h ${PN}-airoha-npu \
             ${PN}-amd-sev-license ${PN}-ccp \
             ${PN}-amdnpu-license ${PN}-amdxdna \
             ${PN}-amd-pmf-license ${PN}-amd-pmf \
             ${PN}-amd-ucode-license ${PN}-microcode-amd \
             ${PN}-amlogic-license ${PN}-amlogic \
             ${PN}-abilis-license ${PN}-as102 \
             ${PN}-starfire \
             ${PN}-atmel-license ${PN}-wilc1000 ${PN}-wilc3000 \
             ${PN}-atusb \
             ${PN}-dvb-ttpci \
             ${PN}-bmi260-license ${PN}-bmi260 \
             ${PN}-cadence-license ${PN}-mhdp8546 \
             ${PN}-cavium-license ${PN}-cnn55xx \
             ${PN}-bfa-license ${PN}-cbfw ${PN}-ctfw ${PN}-ct2fw \
             ${PN}-pcnet-cs ${PN}-3c589-cs ${PN}-3c574-cs ${PN}-serial-cs \
             ${PN}-sw-serial \
             ${PN}-siano-license ${PN}-smsmdtv \
             ${PN}-cpia2 \
             ${PN}-ca0132-license ${PN}-ca0132 \
             ${PN}-cxgb3-license ${PN}-cxgb3 \
             ${PN}-chelsio-firmware-license ${PN}-cxgb4 \
             ${PN}-dabusb-license ${PN}-dabusb \
             ${PN}-dsp56k \
             ${PN}-dib0700-license ${PN}-dib0700 \
             ${PN}-it913x-license ${PN}-it9135 \
             ${PN}-drxk-license ${PN}-drxk \
             ${PN}-e100-license ${PN}-e100 \
             ${PN}-io-ti ${PN}-io-edgeport \
             ${PN}-emi26-license ${PN}-emi26 \
             ${PN}-ene-firmware-license ${PN}-ene-ub6250 \
             ${PN}-snd-maestro3 \
             ${PN}-sensoray-license ${PN}-go7007-s2250 \
             ${PN}-go7007-license ${PN}-go7007 \
             ${PN}-hfi1-license ${PN}-hfi1 \
             ${PN}-inside-secure-license ${PN}-inside-secure \
             ${PN}-intcsst2-license ${PN}-snd-soc-catpt \
             ${PN}-fw-sst-0f28-license ${PN}-snd-intel-sst-core \
             ${PN}-ivsc-license ${PN}-atomisp ${PN}-intel-ipu6-isys ${PN}-mei-vsc-hw \
             ${PN}-ipu3-firmware-license ${PN}-ipu3-imgu \
             ${PN}-intel-ipu7-isys \
             ${PN}-intel-vpu-license ${PN}-intel-vpu \
             ${PN}-isci \
             ${PN}-ixp4xx-license ${PN}-ixp4xx-npe \
             ${PN}-kaweth-license ${PN}-kaweth \
             ${PN}-keyspan-license ${PN}-keyspan \
             ${PN}-keyspan-pda \
             ${PN}-mga-license ${PN}-mga \
             ${PN}-myri10ge-firmware-license ${PN}-myri10ge \
             ${PN}-smc91c92-cs \
             ${PN}-qla1280-license ${PN}-qla1280 \
             ${PN}-ib-qib-license ${PN}-ib-qib \
             ${PN}-r8a779x-usb3-license ${PN}-xhci-rcar \
             ${PN}-r8a779g-pcie-phy-license ${PN}-pcie-rcar \
             ${PN}-r128 \
             ${PN}-rt1320-license ${PN}-rt1320 \
             ${PN}-rp2-license ${PN}-rp2 \
             ${PN}-s5p-mfc-license ${PN}-s5p-mfc \
             ${PN}-snd-sb16-csp \
             ${PN}-alacritech-license ${PN}-slicoss ${PN}-sxg \
             ${PN}-tehuti-license ${PN}-tehuti \
             ${PN}-tigon-license ${PN}-tg3 \
             ${PN}-tlg2300-license ${PN}-tlg2300 \
             ${PN}-montage-license ${PN}-mont-tsse \
             ${PN}-ueagle-atm4-firmware-license ${PN}-ueagle-atm \
             ${PN}-usbdux \
             ${PN}-conexant-license ${PN}-cx231xx ${PN}-cx23418 ${PN}-cx23885 ${PN}-cx23840 \
             ${PN}-vxge-license ${PN}-vxge \
             ${PN}-whiteheat \
             ${PN}-qualcommatheros-ath10k-license ${PN}-wil6210 \
             ${PN}-xe-license ${PN}-xe \
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

FILES:${PN}-cw1200 = " \
    ${nonarch_base_libdir}/firmware/sdd_sagrad_1091_1098.bin* \
    ${nonarch_base_libdir}/firmware/wsm_22.bin* \
"
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
LICENSE:${PN}-ath10k-license = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-ipq5018 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-ipq6018 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-ipq8074 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qca2066 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qca6390 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qca6698aq = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-qcn9074 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-wcn6750 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath11k-wcn6855 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k-qcn9274 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-ath12k-wcn7850 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca2066 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca61x4-serial = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca61x4-usb = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca6390 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qca6698 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-qcc2072 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn3950 = "Firmware-qcom"
LICENSE:${PN}-qca-wcn3988 = "Firmware-qcom"
LICENSE:${PN}-qca-wcn399x = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn6750 = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn685x = "Firmware-qualcommAthos_ath10k"
LICENSE:${PN}-qca-wcn7850 = "Firmware-qcom"

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
# -ath10k is a virtual package that depends upon all ath10k packages.
ALLOW_EMPTY:${PN}-ath10k = "1"

FILES:${PN}-ath11k-ipq5018 = "${nonarch_base_libdir}/firmware/ath11k/IPQ5018"
FILES:${PN}-ath11k-ipq6018 = "${nonarch_base_libdir}/firmware/ath11k/IPQ6018"
FILES:${PN}-ath11k-ipq8074 = "${nonarch_base_libdir}/firmware/ath11k/IPQ8074"
FILES:${PN}-ath11k-qca2066 = "${nonarch_base_libdir}/firmware/ath11k/QCA2066"
FILES:${PN}-ath11k-qca6390 = "${nonarch_base_libdir}/firmware/ath11k/QCA6390"
FILES:${PN}-ath11k-qca6698aq = "${nonarch_base_libdir}/firmware/ath11k/QCA6698AQ"
FILES:${PN}-ath11k-qcn9074 = "${nonarch_base_libdir}/firmware/ath11k/QCN9074"
FILES:${PN}-ath11k-wcn6750 = "${nonarch_base_libdir}/firmware/ath11k/WCN6750"
FILES:${PN}-ath11k-wcn6855 = "${nonarch_base_libdir}/firmware/ath11k/WCN6855"
# -ath11k is a virtual package that depends upon all ath11k packages.
ALLOW_EMPTY:${PN}-ath11k = "1"

FILES:${PN}-ath12k-qcn9274 = "${nonarch_base_libdir}/firmware/ath12k/QCN9274"
FILES:${PN}-ath12k-wcn7850 = "${nonarch_base_libdir}/firmware/ath12k/WCN7850"
# -ath12k is a virtual package that depends upon all ath12k packages.
ALLOW_EMPTY:${PN}-ath12k = "1"

FILES:${PN}-qca-qca2066 = " \
  ${nonarch_base_libdir}/firmware/qca/hpbtfw21.tlv* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.bin* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.bin* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.301* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.302* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.301* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.302* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.309* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.309* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.30a* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.30a* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b8c* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b9f* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba0* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba1* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba2* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba3* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.ba4* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.baa* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.bb8* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b10c* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21.b111* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b8c* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b9f* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba0* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba1* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba2* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba3* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.ba4* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.baa* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.bb8* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b10c* \
  ${nonarch_base_libdir}/firmware/qca/hpnv21g.b111* \
"
FILES:${PN}-qca-qca61x4-serial = " \
  ${nonarch_base_libdir}/firmware/qca/nvm_0*.bin* \
  ${nonarch_base_libdir}/firmware/qca/rampatch_0*.bin* \
"
FILES:${PN}-qca-qca61x4-usb = " \
  ${nonarch_base_libdir}/firmware/qca/nvm_usb_*.bin* \
  ${nonarch_base_libdir}/firmware/qca/rampatch_usb_*.bin* \
  ${nonarch_base_libdir}/firmware/qca/QCA2066/nvm_usb_00130201_030a.bin* \
  ${nonarch_base_libdir}/firmware/qca/QCA2066/nvm_usb_00130201_gf_030a.bin* \
  ${nonarch_base_libdir}/firmware/qca/QCA2066/rampatch_usb_00130201.bin* \
"
FILES:${PN}-qca-qca6390 = " \
  ${nonarch_base_libdir}/firmware/qca/htbtfw20.tlv* \
  ${nonarch_base_libdir}/firmware/qca/htnv20.bin* \
"
FILES:${PN}-qca-qca6698 = " \
  ${nonarch_base_libdir}/firmware/qca/QCA6698/hpbtfw21.tlv* \
  ${nonarch_base_libdir}/firmware/qca/QCA6698/hpnv21.b206* \
  ${nonarch_base_libdir}/firmware/qca/QCA6698/hpnv21.b207* \
  ${nonarch_base_libdir}/firmware/qca/QCA6698/hpnv21.bin* \
"
FILES:${PN}-qca-qcc2072 = " \
  ${nonarch_base_libdir}/firmware/qca/ornbtfw11.tlv* \
  ${nonarch_base_libdir}/firmware/qca/ornnv11.bin* \
"
FILES:${PN}-qca-wcn3950 = " \
  ${nonarch_base_libdir}/firmware/qca/cmbtfw12.tlv* \
  ${nonarch_base_libdir}/firmware/qca/cmbtfw13.tlv* \
  ${nonarch_base_libdir}/firmware/qca/cmnv12.bin* \
  ${nonarch_base_libdir}/firmware/qca/cmnv13.bin* \
  ${nonarch_base_libdir}/firmware/qca/cmnv13s.bin* \
  ${nonarch_base_libdir}/firmware/qca/cmnv13t.bin* \
"
FILES:${PN}-qca-wcn3988 = " \
  ${nonarch_base_libdir}/firmware/qca/apbtfw10.tlv* \
  ${nonarch_base_libdir}/firmware/qca/apbtfw11.tlv* \
  ${nonarch_base_libdir}/firmware/qca/apnv10.bin* \
  ${nonarch_base_libdir}/firmware/qca/apnv11.bin* \
"
FILES:${PN}-qca-wcn399x = " \
  ${nonarch_base_libdir}/firmware/qca/crbtfw21.tlv* \
  ${nonarch_base_libdir}/firmware/qca/crnv21.bin* \
  ${nonarch_base_libdir}/firmware/qca/crbtfw32.tlv* \
  ${nonarch_base_libdir}/firmware/qca/crnv32.bin* \
  ${nonarch_base_libdir}/firmware/qca/crnv32u.bin* \
"
FILES:${PN}-qca-wcn6750 = " \
  ${nonarch_base_libdir}/firmware/qca/msbtfw11.mbn* \
  ${nonarch_base_libdir}/firmware/qca/msbtfw11.tlv* \
  ${nonarch_base_libdir}/firmware/qca/msnv11.bin* \
  ${nonarch_base_libdir}/firmware/qca/msnv11.b0a* \
  ${nonarch_base_libdir}/firmware/qca/msnv11.b09* \
"
FILES:${PN}-qca-wcn685x = " \
  ${nonarch_base_libdir}/firmware/qca/wcnhpbtfw21.tlv* \
  ${nonarch_base_libdir}/firmware/qca/wcnhpnv21.b* \
  ${nonarch_base_libdir}/firmware/qca/wcnhpnv21g.b* \
"
FILES:${PN}-qca-wcn7850 = " \
  ${nonarch_base_libdir}/firmware/qca/hmtbtfw20.tlv* \
  ${nonarch_base_libdir}/firmware/qca/hmtnv20.b10f* \
  ${nonarch_base_libdir}/firmware/qca/hmtnv20.b112* \
  ${nonarch_base_libdir}/firmware/qca/hmtnv20.bin* \
"
ALLOW_EMPTY:${PN}-qca = "1"

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
RDEPENDS:${PN}-ath11k += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-ipq5018 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-ipq6018 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-ipq8074 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qca2066 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qca6390 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qca6698aq += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-qcn9074 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-wcn6750 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath11k-wcn6855 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k-qcn9274 += "${PN}-ath10k-license"
RDEPENDS:${PN}-ath12k-wcn7850 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca += "${PN}-ath10k-license ${PN}-qcom-license"
RDEPENDS:${PN}-qca-qca2066 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca61x4-serial += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca61x4-usb += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca6390 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qca6698 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-qcc2072 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn3950 += "${PN}-qcom-license"
RDEPENDS:${PN}-qca-wcn3988 += "${PN}-qcom-license"
RDEPENDS:${PN}-qca-wcn399x += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn6750 += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn685x += "${PN}-ath10k-license"
RDEPENDS:${PN}-qca-wcn7850 += "${PN}-qcom-license"
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
FILES:${PN}-amdgpu-aldebaran = "${nonarch_base_libdir}/firmware/amdgpu/aldebaran_*.bin*"
FILES:${PN}-amdgpu-carrizo = "${nonarch_base_libdir}/firmware/amdgpu/carrizo_*.bin*"
FILES:${PN}-amdgpu-cezanne = "${nonarch_base_libdir}/firmware/amdgpu/green_sardine_*.bin*"
FILES:${PN}-amdgpu-fiji = "${nonarch_base_libdir}/firmware/amdgpu/fiji_*.bin*"
FILES:${PN}-amdgpu-hawaii = "${nonarch_base_libdir}/firmware/amdgpu/hawaii_*.bin*"
FILES:${PN}-amdgpu-navi10 = "${nonarch_base_libdir}/firmware/amdgpu/navi10_*.bin*"
FILES:${PN}-amdgpu-navi14 = "${nonarch_base_libdir}/firmware/amdgpu/navi14_*.bin*"
FILES:${PN}-amdgpu-navi21 = "${nonarch_base_libdir}/firmware/amdgpu/sienna_cichlid_*.bin*"
FILES:${PN}-amdgpu-navi22 = "${nonarch_base_libdir}/firmware/amdgpu/navy_flounder_*.bin*"
FILES:${PN}-amdgpu-navi23 = "${nonarch_base_libdir}/firmware/amdgpu/dimgrey_cavefish_*.bin*"
FILES:${PN}-amdgpu-navi24 = "${nonarch_base_libdir}/firmware/amdgpu/beige_goby_*.bin*"
FILES:${PN}-amdgpu-navi31 = "${nonarch_base_libdir}/firmware/amdgpu/gc_11_0_0_*.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_0_sos.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_0_ta.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/smu_13_0_0.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/dcn_3_2_0_dmcub.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/sdma_6_0_0.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/vcn_4_0_0.bin* \
"
FILES:${PN}-amdgpu-navi32 = "${nonarch_base_libdir}/firmware/amdgpu/dcn_3_2_0_dmcub.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/gc_11_0_3_*.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_10_sos.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/psp_13_0_10_ta.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/sdma_6_0_3.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/smu_13_0_10.bin* \
    ${nonarch_base_libdir}/firmware/amdgpu/vcn_4_0_0.bin* \
"
FILES:${PN}-amdgpu-oland = "${nonarch_base_libdir}/firmware/amdgpu/oland_*.bin*"
FILES:${PN}-amdgpu-polaris10 = "${nonarch_base_libdir}/firmware/amdgpu/polaris10_*.bin*"
FILES:${PN}-amdgpu-polaris11 = "${nonarch_base_libdir}/firmware/amdgpu/polaris11_*.bin*"
FILES:${PN}-amdgpu-polaris12 = "${nonarch_base_libdir}/firmware/amdgpu/polaris12_*.bin*"
FILES:${PN}-amdgpu-raven = "${nonarch_base_libdir}/firmware/amdgpu/raven_*.bin*"
FILES:${PN}-amdgpu-rembrandt = "${nonarch_base_libdir}/firmware/amdgpu/yellow_carp_*.bin*"
FILES:${PN}-amdgpu-renoir = "${nonarch_base_libdir}/firmware/amdgpu/renoir_*.bin*"
FILES:${PN}-amdgpu-stoney = "${nonarch_base_libdir}/firmware/amdgpu/stoney_*.bin*"
FILES:${PN}-amdgpu-tonga = "${nonarch_base_libdir}/firmware/amdgpu/tonga_*.bin*"
FILES:${PN}-amdgpu-topaz = "${nonarch_base_libdir}/firmware/amdgpu/topaz_*.bin*"
FILES:${PN}-amdgpu-vega10 = "${nonarch_base_libdir}/firmware/amdgpu/vega10_*.bin*"
FILES:${PN}-amdgpu-vega12 = "${nonarch_base_libdir}/firmware/amdgpu/vega12_*.bin*"
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
FILES:${PN}-mali-csffw-license = "${nonarch_base_libdir}/firmware/LICENCE.mali_csffw"

LICENSE:${PN}-mali-csffw-arch108 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-arch108 = "${nonarch_base_libdir}/firmware/arm/mali/arch10.8/mali_csffw.bin*"
RDEPENDS:${PN}-mali-csffw-arch108 += "${PN}-mali-csffw-license"

LICENSE:${PN}-mali-csffw-arch1010 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-arch1010 = "${nonarch_base_libdir}/firmware/arm/mali/arch10.10/mali_csffw.bin*"
RDEPENDS:${PN}-mali-csffw-arch1010 += "${PN}-mali-csffw-license"

LICENSE:${PN}-mali-csffw-arch1012 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-arch1012 = "${nonarch_base_libdir}/firmware/arm/mali/arch10.12/mali_csffw.bin*"
RDEPENDS:${PN}-mali-csffw-arch1012 += "${PN}-mali-csffw-license"

LICENSE:${PN}-mali-csffw-arch118 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-arch118 = "${nonarch_base_libdir}/firmware/arm/mali/arch11.8/mali_csffw.bin*"
RDEPENDS:${PN}-mali-csffw-arch118 += "${PN}-mali-csffw-license"

LICENSE:${PN}-mali-csffw-arch128 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-arch128 = "${nonarch_base_libdir}/firmware/arm/mali/arch12.8/mali_csffw.bin*"
RDEPENDS:${PN}-mali-csffw-arch128 += "${PN}-mali-csffw-license"

LICENSE:${PN}-mali-csffw-arch138 = "Firmware-mali_csffw"
FILES:${PN}-mali-csffw-arch138 = "${nonarch_base_libdir}/firmware/arm/mali/arch13.8/mali_csffw.bin*"
RDEPENDS:${PN}-mali-csffw-arch138 += "${PN}-mali-csffw-license"

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
LICENSE:${PN}-cf8381 = "Firmware-Marvell"
LICENSE:${PN}-cf8385 = "Firmware-Marvell"
LICENSE:${PN}-gspi8682 = "Firmware-Marvell"
LICENSE:${PN}-gspi8686 = "Firmware-Marvell"
LICENSE:${PN}-gspi8688 = "Firmware-Marvell"
LICENSE:${PN}-sd8385 = "Firmware-Marvell"
LICENSE:${PN}-sd8682 = "Firmware-Marvell"
LICENSE:${PN}-usb8388 = "Firmware-Marvell"
LICENSE:${PN}-usb8682 = "Firmware-Marvell"
LICENSE:${PN}-sd8977 = "Firmware-Marvell"
LICENSE:${PN}-usb8766 = "Firmware-Marvell"
LICENSE:${PN}-usb8797 = "Firmware-Marvell"
LICENSE:${PN}-usb8801 = "Firmware-Marvell"
LICENSE:${PN}-usb8897 = "Firmware-Marvell"
LICENSE:${PN}-rvu-cptpf = "Firmware-Marvell"
LICENSE:${PN}-mwl8k = "Firmware-Marvell"
LICENSE:${PN}-mwlwifi = "Firmware-Marvell"
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
  ${nonarch_base_libdir}/firmware/libertas/sd8686* \
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
    ln -fs sdsd8997_combo_v4.bin${@fw_compr_file_suffix(d)} ${D}${nonarch_base_libdir}/firmware/mrvl/sd8997_uapsta.bin${@fw_compr_file_suffix(d)}
}
FILES:${PN}-sd8997 = " \
  ${nonarch_base_libdir}/firmware/mrvl/sd8997_uapsta.bin* \
  ${nonarch_base_libdir}/firmware/mrvl/sdsd8997_combo_v4.bin* \
"
FILES:${PN}-usb8997 = " \
  ${nonarch_base_libdir}/firmware/mrvl/usbusb8997_combo_v4.bin* \
"
FILES:${PN}-cf8381 = "${nonarch_base_libdir}/firmware/libertas/cf8381*"
FILES:${PN}-cf8385 = "${nonarch_base_libdir}/firmware/libertas/cf8385*"
FILES:${PN}-gspi8682 = "${nonarch_base_libdir}/firmware/libertas/gspi8682*"
FILES:${PN}-gspi8686 = "${nonarch_base_libdir}/firmware/libertas/gspi8686*"
FILES:${PN}-gspi8688 = "${nonarch_base_libdir}/firmware/libertas/gspi8688*"
FILES:${PN}-sd8385 = "${nonarch_base_libdir}/firmware/libertas/sd8385*"
FILES:${PN}-sd8682 = "${nonarch_base_libdir}/firmware/libertas/sd8682*"
FILES:${PN}-usb8388 = "${nonarch_base_libdir}/firmware/libertas/usb8388*"
FILES:${PN}-usb8682 = "${nonarch_base_libdir}/firmware/libertas/usb8682*"
FILES:${PN}-sd8977 = "${nonarch_base_libdir}/firmware/mrvl/sdsd8977*"
FILES:${PN}-usb8766 = "${nonarch_base_libdir}/firmware/mrvl/usb8766*"
FILES:${PN}-usb8797 = "${nonarch_base_libdir}/firmware/mrvl/usb8797*"
FILES:${PN}-usb8801 = "${nonarch_base_libdir}/firmware/mrvl/usb8801*"
FILES:${PN}-usb8897 = "${nonarch_base_libdir}/firmware/mrvl/usb8897*"
FILES:${PN}-rvu-cptpf = "${nonarch_base_libdir}/firmware/mrvl/cpt0*"
FILES:${PN}-mwl8k = "${nonarch_base_libdir}/firmware/mwl8k/*"
FILES:${PN}-mwlwifi = "${nonarch_base_libdir}/firmware/mwlwifi/*"

RDEPENDS:${PN}-sd8686 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8688 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8787 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8797 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8801 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8887 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8897 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8997 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8997 += "${PN}-marvell-license"
RDEPENDS:${PN}-cf8381 += "${PN}-marvell-license"
RDEPENDS:${PN}-cf8385 += "${PN}-marvell-license"
RDEPENDS:${PN}-gspi8682 += "${PN}-marvell-license"
RDEPENDS:${PN}-gspi8686 += "${PN}-marvell-license"
RDEPENDS:${PN}-gspi8688 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8385 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8682 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8388 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8682 += "${PN}-marvell-license"
RDEPENDS:${PN}-sd8977 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8766 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8797 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8801 += "${PN}-marvell-license"
RDEPENDS:${PN}-usb8897 += "${PN}-marvell-license"
RDEPENDS:${PN}-rvu-cptpf += "${PN}-marvell-license"
RDEPENDS:${PN}-mwl8k += "${PN}-marvell-license"
RDEPENDS:${PN}-mwlwifi += "${PN}-marvell-license"

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
LICENSE:${PN}-nxp-sr1xx = "Firmware-nxp"
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
FILES:${PN}-nxp-sr1xx = "${nonarch_base_libdir}/firmware/nxp/sr150_fw.bin*"
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
RDEPENDS:${PN}-nxp-sr1xx += "${PN}-nxp-license"

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
LICENSE:${PN}-rsi-91x = "WHENCE"

FILES:${PN}-rs9113 = " ${nonarch_base_libdir}/firmware/rsi/rs9113*.rps* "
FILES:${PN}-rs9116 = " ${nonarch_base_libdir}/firmware/rsi/rs9116*.rps* "
FILES:${PN}-rsi-91x = " ${nonarch_base_libdir}/firmware/rsi_91x.fw* "

RDEPENDS:${PN}-rs9113 += "${PN}-whence-license"
RDEPENDS:${PN}-rs9116 += "${PN}-whence-license"
RDEPENDS:${PN}-rsi-91x += "${PN}-whence-license"

# For rtl
LICENSE:${PN}-rtl8188 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192cu = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192ce = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192su = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8723 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8761 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8821 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8822 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8192 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8710 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8812 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8851 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8852 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8922 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8703 = "Firmware-rtlwifi_firmware"
LICENSE:${PN}-rtl8814 = "Firmware-rtlwifi_firmware"
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
  ${nonarch_base_libdir}/firmware/rtw88/README* \
  ${nonarch_base_libdir}/firmware/rtw88/rtw8821*.bin* \
  ${nonarch_base_libdir}/firmware/rtl_bt/rtl8821*.bin* \
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
FILES:${PN}-rtl8192 = " \
    ${nonarch_base_libdir}/firmware/rtlwifi/rtl8192* \
    ${nonarch_base_libdir}/firmware/rtl_bt/rtl8192* \
"
FILES:${PN}-rtl8710 = "${nonarch_base_libdir}/firmware/rtlwifi/rtl8710*"
FILES:${PN}-rtl8812 = " \
    ${nonarch_base_libdir}/firmware/rtlwifi/rtl8812* \
    ${nonarch_base_libdir}/firmware/rtl_bt/rtl8812* \
    ${nonarch_base_libdir}/firmware/rtw88/rtw8812* \
"
FILES:${PN}-rtl8851 = " \
    ${nonarch_base_libdir}/firmware/rtl_bt/rtl8851* \
    ${nonarch_base_libdir}/firmware/rtw89/rtw8851* \
"
FILES:${PN}-rtl8852 = " \
    ${nonarch_base_libdir}/firmware/rtl_bt/rtl8852* \
    ${nonarch_base_libdir}/firmware/rtw89/rtw8852* \
"
FILES:${PN}-rtl8922 = " \
    ${nonarch_base_libdir}/firmware/rtl_bt/rtl8922* \
    ${nonarch_base_libdir}/firmware/rtw89/rtw8922* \
"
FILES:${PN}-rtl8703 = "${nonarch_base_libdir}/firmware/rtw88/rtw8703*"
FILES:${PN}-rtl8814 = "${nonarch_base_libdir}/firmware/rtw88/rtw8814*"

RDEPENDS:${PN}-rtl8188 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192ce += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192cu += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192su = "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8723 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8821 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8761 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8822 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8192 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8710 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8812 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8851 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8852 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8922 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8703 += "${PN}-rtl-license"
RDEPENDS:${PN}-rtl8814 += "${PN}-rtl-license"
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

# For ti-tspa-license
LICENSE:${PN}-ti-tspa-license = "Firmware-ti-tspa"
FILES:${PN}-ti-tspa-license = "${nonarch_base_libdir}/firmware/LICENCE.ti-tspa"

# For ti-tas2563 - tas2563 firmware
LICENSE:${PN}-ti-tas2563 = "Firmware-ti-tspa"
FILES:${PN}-ti-tas2563 = "\
    ${nonarch_base_libdir}/firmware/INT8866RCA2.bin* \
    ${nonarch_base_libdir}/firmware/TAS2XXX3870.bin* \
    ${nonarch_base_libdir}/firmware/ti/audio/tas2563/* \
"
RDEPENDS:${PN}-ti-tas2563 = "${PN}-ti-tspa-license"

# For ti-tas2781 - tas2781 firmware
LICENSE:${PN}-ti-tas2781 = "Firmware-ti-tspa"
FILES:${PN}-ti-tas2781 = "\
    ${nonarch_base_libdir}/firmware/TAS2XXX*.bin* \
    ${nonarch_base_libdir}/firmware/TIAS2781*.bin* \
    ${nonarch_base_libdir}/firmware/TXNW2781*.bin* \
    ${nonarch_base_libdir}/firmware/ti/audio/tas2781/* \
"
RDEPENDS:${PN}-ti-tas2781 = "${PN}-ti-tspa-license"

# For ti-tas2783 - tas2783 firmware
LICENSE:${PN}-ti-tas2783 = "Firmware-ti-tspa"
FILES:${PN}-ti-tas2783 = "\
    ${nonarch_base_libdir}/firmware/8E[8F]*-?-?.bin* \
    ${nonarch_base_libdir}/firmware/ti/audio/tas2783/* \
"
RDEPENDS:${PN}-ti-tas2781 = "${PN}-ti-tspa-license"

# For ti-vpe - Texas Instruments V4L2 driver for Video Processing Engine
LICENSE:${PN}-ti-vpe = "Firmware-ti-tspa"
FILES:${PN}-ti-vpe = "${nonarch_base_libdir}/firmware/ti/vpdma-1b8.bin*"
RDEPENDS:${PN}-ti-vpe = "${PN}-ti-tspa-license"

# For ti_usb_3410_5052 - USB TI 3410/5052 serial device
LICENSE:${PN}-ti-usb-3410-5052 = "GPL-2.0-or-later"
FILES:${PN}-ti-usb-3410-5052 = "\
    ${nonarch_base_libdir}/firmware/ti_3410.fw* \
    ${nonarch_base_libdir}/firmware/ti_5052.fw* \
"

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
FILES:${PN}-bcm43241b4 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43241b4-sdio.*"
FILES:${PN}-bcm43241b5 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43241b5-sdio.bin*"
FILES:${PN}-bcm43242a = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43242a.bin*"
FILES:${PN}-bcm43012 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43012-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43012-sdio.* \
"
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
FILES:${PN}-bcm43570 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43570-pcie.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43570-pcie.* \
"
FILES:${PN}-bcm4358 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4358-pcie.bin*"
FILES:${PN}-bcm43602 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43602-pcie.bin* \
  ${nonarch_base_libdir}/firmware/brcm/brcmfmac43602-pcie.ap.bin* \
"
FILES:${PN}-bcm4366b = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4366b-pcie.bin*"
FILES:${PN}-bcm4366c = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4366c-pcie.bin*"
FILES:${PN}-bcm4371 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4371-pcie.bin*"

FILES:${PN}-bcm54591 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac54591-pcie.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac54591-pcie.* \
"

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
LICENSE:${PN}-bcm43012 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm43012 += "${PN}-broadcom-license"
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
LICENSE:${PN}-bcm54591 = "Firmware-broadcom_bcm43xx"
RDEPENDS:${PN}-bcm54591 += "${PN}-broadcom-license"

# For broadcom cypress

LICENSE:${PN}-cypress-license = "Firmware-cypress"
FILES:${PN}-cypress-license = "${nonarch_base_libdir}/firmware/LICENCE.cypress"

FILES:${PN}-bcm-0bb4-0306 = "${nonarch_base_libdir}/firmware/brcm/BCM-0bb4-0306.hcd*"
FILES:${PN}-bcm-0a5c-6410 = "${nonarch_base_libdir}/firmware/brcm/BCM-0a5c-6410.hcd*"
FILES:${PN}-bcm43340 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43340-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43340-sdio.*"
FILES:${PN}-bcm43362 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43362-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43362-sdio.*"
FILES:${PN}-bcm43430 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43430-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.*"
FILES:${PN}-bcm4354 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac4354-sdio.* \
  ${nonarch_base_libdir}/firmware/cypress/cyfmac4354-sdio.* \
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
LICENSE:${PN}-bcm-0a5c-6410 = "Firmware-cypress"
RDEPENDS:${PN}-bcm-0a5c-6410 += "${PN}-bcm-0bb4-0306"
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
LICENSE:${PN}-cirrus-cs42l45 = "Firmware-cirrus"
LICENSE:${PN}-cirrus-license = "Firmware-cirrus"

FILES:${PN}-cirrus = " \
    ${nonarch_base_libdir}/firmware/cs42l43.bin* \
    ${nonarch_base_libdir}/firmware/cirrus/* \
"
FILES:${PN}-cirrus-cs42l45 = " \
    ${nonarch_base_libdir}/firmware/sdca/1fa/1028/* \
    ${nonarch_base_libdir}/firmware/sdca/1fa/17aa/* \
"
FILES:${PN}-cirrus-license = "${nonarch_base_libdir}/firmware/LICENSE.cirrus"

RDEPENDS:${PN}-cirrus += "${PN}-cirrus-license"
RDEPENDS:${PN}-cirrus-cs42l45 += "${PN}-cirrus-license"

# For cnm
LICENSE:${PN}-cnm = "Firmware-cnm"
LICENSE:${PN}-cnm-license = "Firmware-cnm"

FILES:${PN}-cnm = "${nonarch_base_libdir}/firmware/cnm/*"
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
FILES:${PN}-iwlwifi-135-6 = "${nonarch_base_libdir}/firmware/iwlwifi-135-6.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-135-6.ucode*"
FILES:${PN}-iwlwifi-3160-7 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-7.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-7.ucode*"
FILES:${PN}-iwlwifi-3160-8 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-8.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-8.ucode*"
FILES:${PN}-iwlwifi-3160-9 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-9.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-9.ucode*"
FILES:${PN}-iwlwifi-3160-10 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-10.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-10.ucode*"
FILES:${PN}-iwlwifi-3160-12 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-12.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-12.ucode*"
FILES:${PN}-iwlwifi-3160-13 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-13.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-13.ucode*"
FILES:${PN}-iwlwifi-3160-16 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-16.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-16.ucode*"
FILES:${PN}-iwlwifi-3160-17 = "${nonarch_base_libdir}/firmware/iwlwifi-3160-17.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-3160-17.ucode*"
FILES:${PN}-iwlwifi-6000-4 = "${nonarch_base_libdir}/firmware/iwlwifi-6000-4.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6000-4.ucode*"
FILES:${PN}-iwlwifi-6000g2a-5 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2a-5.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6000g2a-5.ucode*"
FILES:${PN}-iwlwifi-6000g2a-6 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2a-6.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6000g2a-6.ucode*"
FILES:${PN}-iwlwifi-6000g2b-5 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2b-5.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6000g2b-5.ucode*"
FILES:${PN}-iwlwifi-6000g2b-6 = "${nonarch_base_libdir}/firmware/iwlwifi-6000g2b-6.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6000g2b-6.ucode*"
FILES:${PN}-iwlwifi-6050-4 = "${nonarch_base_libdir}/firmware/iwlwifi-6050-4.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6050-4.ucode*"
FILES:${PN}-iwlwifi-6050-5 = "${nonarch_base_libdir}/firmware/iwlwifi-6050-5.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-6050-5.ucode*"
FILES:${PN}-iwlwifi-7260   = "${nonarch_base_libdir}/firmware/iwlwifi-7260-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-7260-*.ucode*"
FILES:${PN}-iwlwifi-7265   = "${nonarch_base_libdir}/firmware/iwlwifi-7265-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-7265-*.ucode*"
FILES:${PN}-iwlwifi-7265d   = "${nonarch_base_libdir}/firmware/iwlwifi-7265D-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-7265D-*.ucode*"
FILES:${PN}-iwlwifi-8000c   = "${nonarch_base_libdir}/firmware/iwlwifi-8000C-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-8000C-*.ucode*"
FILES:${PN}-iwlwifi-8265   = "${nonarch_base_libdir}/firmware/iwlwifi-8265-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-8265-*.ucode*"
FILES:${PN}-iwlwifi-9000   = "${nonarch_base_libdir}/firmware/iwlwifi-9000-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-9000-*.ucode*"
FILES:${PN}-iwlwifi-9260   = "${nonarch_base_libdir}/firmware/iwlwifi-9260-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-9260-*.ucode*"
FILES:${PN}-iwlwifi-misc   = " \
    ${nonarch_base_libdir}/firmware/iwlwifi-*.ucode* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-*.ucode* \
    ${nonarch_base_libdir}/firmware/iwlwifi-*.pnvm* ${nonarch_base_libdir}/firmware/intel/iwlwifi/iwlwifi-*.pnvm* \
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
FILES:${PN}-adsp-sst              = "\
    ${nonarch_base_libdir}/firmware/intel/dsp_fw* \
    ${nonarch_base_libdir}/firmware/intel/avs/*/dsp_basefw.bin \
    ${nonarch_base_libdir}/firmware/intel/avs/skl/dsp_mod_7CAD0808-AB10-CD23-EF45-12AB34CD56EF.bin \
"
RDEPENDS:${PN}-adsp-sst           = "${PN}-adsp-sst-license"

# For snd_soc_avs - Intel AudioDSP driver for cAVS platforms
LICENSE:${PN}-snd-soc-avs = "Apache-2.0"
FILES:${PN}-snd-soc-avs = "${nonarch_base_libdir}/firmware/intel/avs/*"

# For QAT
LICENSE:${PN}-qat         = "Firmware-qat"
LICENSE:${PN}-qat-license = "Firmware-qat"
FILES:${PN}-qat-license   = "${nonarch_base_libdir}/firmware/LICENCE.qat_firmware"
FILES:${PN}-qat           = "${nonarch_base_libdir}/firmware/qat*.bin* ${nonarch_base_libdir}/firmware/intel/qat/qat*.bin*"
RDEPENDS:${PN}-qat        = "${PN}-qat-license"

LICENSE:${PN}-qed         = "WHENCE"
FILES:${PN}-qed           = "${nonarch_base_libdir}/firmware/qed/*"

LICENSE:${PN}-linaro-license = "Firmware-linaro"
FILES:${PN}-linaro-license   = "${nonarch_base_libdir}/firmware/LICENCE.linaro"

# For QCOM VPU/GPU and SDM845
LICENSE:${PN}-qcom-license = "Firmware-qcom"
LICENSE:${PN}-qcom-2-license = "Firmware-qcom-2"
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
LICENSE:${PN}-qcom-adreno-a612 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a623 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a630 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a640 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a650 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a660 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a663 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a702 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a730 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-a740 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g705 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g709 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g715 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g800 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g801 = "Firmware-qcom"
LICENSE:${PN}-qcom-adreno-g802 = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8016-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8016-wifi = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8096-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8096-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-apq8096-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-glymur-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-glymur-audio = "Firmware-qcom-2"
LICENSE:${PN}-qcom-glymur-compute = "Firmware-qcom-2"
LICENSE:${PN}-qcom-kaanapali-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-kaanapali-audio = "Firmware-qcom-2"
LICENSE:${PN}-qcom-kaanapali-compute = "Firmware-qcom-2"
LICENSE:${PN}-qcom-kaanapali-soccp = "Firmware-qcom-2"
LICENSE:${PN}-qcom-qcm2290-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm2290-wifi = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-qcm6490-compute  = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-ipa  = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-wifi  = "Firmware-qcom"
LICENSE:${PN}-qcom-qcm6490-qupv3fw = "Firmware-qcom"
LICENSE:${PN}-qcom-qcs615-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qcs615-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-qcs615-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-qcs6490-radxa-dragon-q6a-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-qcs6490-radxa-dragon-q6a-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-qcs6490-thundercomm-rubikpi3-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-qcs8300-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qcs8300-audio = "Firmware-qcom-2 & Firmware-linaro"
LICENSE:${PN}-qcom-qcs8300-compute = "Firmware-qcom-2"
LICENSE:${PN}-qcom-qcs8300-generalpurpose = "Firmware-qcom-2"
LICENSE:${PN}-qcom-qcs8300-qupv3fw = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-compute  = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-qrb4210-wifi = "Firmware-qcom"
LICENSE:${PN}-qcom-sa8775p-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sa8775p-audio = "Firmware-qcom-2 & Firmware-linaro"
LICENSE:${PN}-qcom-sa8775p-compute = "Firmware-qcom-2"
LICENSE:${PN}-qcom-sa8775p-generalpurpose = "Firmware-qcom-2"
LICENSE:${PN}-qcom-sa8775p-qupv3fw = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "Firmware-qcom"
LICENSE:${PN}-qcom-sc8280xp-lenovo-x13s-vpu = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-modem = "Firmware-qcom"
LICENSE:${PN}-qcom-sdm845-thundercomm-db845c-sensors = "Firmware-qcom"
LICENSE:${PN}-qcom-sdx35-foxconn-firehose = "Firmware-qcom"
LICENSE:${PN}-qcom-sdx61-foxconn-firehose = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8150-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-audio = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8250-thundercomm-rb5-sensors = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8350-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8450-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8450-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-sm8550-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8550-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-sm8650-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8650-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-sm8750-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-sm8750-audio = "Firmware-qcom-2 & Firmware-linaro"
LICENSE:${PN}-qcom-sm8750-compute = "Firmware-qcom-2"
LICENSE:${PN}-qcom-x1e80100-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-audio = "Firmware-qcom-2 & Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-asus-vivobook-16-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-asus-vivobook-s15-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-asus-zenbook-a14-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-dell-inspiron-14-plus-7441-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-dell-latitude-7455-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-dell-xps13-9345-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-hp-omnibook-x14-audio-tplg = "Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-lenovo-t14s-g6-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-lenovo-t14s-g6-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-lenovo-t14s-g6-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-lenovo-t14s-g6-vpu = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-adreno = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-audio = "Firmware-qcom & Firmware-linaro"
LICENSE:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-compute = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-vpu = "Firmware-qcom"
LICENSE:${PN}-qcom-x1e80100-qupv3fw = "Firmware-qcom"
LICENSE:${PN}-qcom-x1p42100-adreno = "Firmware-qcom"

FILES:${PN}-qcom-license   = "${nonarch_base_libdir}/firmware/LICENSE.qcom ${nonarch_base_libdir}/firmware/qcom/NOTICE.txt"
FILES:${PN}-qcom-2-license   = "${nonarch_base_libdir}/firmware/LICENSE.qcom-2"
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
FILES:${PN}-qcom-adreno-a2xx = "${nonarch_base_libdir}/firmware/qcom/leia_*.fw* ${nonarch_base_libdir}/firmware/qcom/yamato_*.fw* ${nonarch_base_libdir}/firmware/qcom/a2*_*.fw*"
FILES:${PN}-qcom-adreno-a3xx = "${nonarch_base_libdir}/firmware/qcom/a3*_*.fw* ${nonarch_base_libdir}/firmware/a300_*.fw*"
FILES:${PN}-qcom-adreno-a4xx = "${nonarch_base_libdir}/firmware/qcom/a4*_*.fw*"
FILES:${PN}-qcom-adreno-a530 = "${nonarch_base_libdir}/firmware/qcom/a530*.fw*"
FILES:${PN}-qcom-adreno-a612 = "${nonarch_base_libdir}/firmware/qcom/a612*.*"
FILES:${PN}-qcom-adreno-a623 = "${nonarch_base_libdir}/firmware/qcom/a623*.*"
FILES:${PN}-qcom-adreno-a630 = "${nonarch_base_libdir}/firmware/qcom/a630*.*"
FILES:${PN}-qcom-adreno-a640 = "${nonarch_base_libdir}/firmware/qcom/a640*.*"
FILES:${PN}-qcom-adreno-a650 = "${nonarch_base_libdir}/firmware/qcom/a650*.*"
FILES:${PN}-qcom-adreno-a660 = "${nonarch_base_libdir}/firmware/qcom/a660*.*"
FILES:${PN}-qcom-adreno-a663 = "${nonarch_base_libdir}/firmware/qcom/a663*.*"
FILES:${PN}-qcom-adreno-a702 = "${nonarch_base_libdir}/firmware/qcom/a702*.*"
FILES:${PN}-qcom-adreno-a730 = "${nonarch_base_libdir}/firmware/qcom/a730_sqe*.* ${nonarch_base_libdir}/firmware/qcom/gmu_gen70000.*"
FILES:${PN}-qcom-adreno-a740 = "${nonarch_base_libdir}/firmware/qcom/a740_sqe*.* ${nonarch_base_libdir}/firmware/qcom/gmu_gen70200.*"
FILES:${PN}-qcom-adreno-g705 = "${nonarch_base_libdir}/firmware/qcom/gen70500_*.*"
FILES:${PN}-qcom-adreno-g709 = "${nonarch_base_libdir}/firmware/qcom/gen70900_*.* ${nonarch_base_libdir}/firmware/qcom/gmu_gen70900.*"
FILES:${PN}-qcom-adreno-g715 = "${nonarch_base_libdir}/firmware/qcom/gen71500_*.*"
FILES:${PN}-qcom-adreno-g800 = "${nonarch_base_libdir}/firmware/qcom/gen80000_*.*"
FILES:${PN}-qcom-adreno-g801 = "${nonarch_base_libdir}/firmware/qcom/gen80100_*.*"
FILES:${PN}-qcom-adreno-g802 = "${nonarch_base_libdir}/firmware/qcom/gen80200_*.*"
FILES:${PN}-qcom-apq8016-modem = "${nonarch_base_libdir}/firmware/qcom/apq8016/mba.mbn* ${nonarch_base_libdir}/firmware/qcom/apq8016/modem.mbn*"
FILES:${PN}-qcom-apq8016-wifi = "${nonarch_base_libdir}/firmware/qcom/apq8016/wcnss.mbn* ${nonarch_base_libdir}/firmware/qcom/apq8016/WCNSS*"
FILES:${PN}-qcom-apq8096-adreno = "${nonarch_base_libdir}/firmware/qcom/apq8096/a530_zap.mbn* ${nonarch_base_libdir}/firmware/qcom/a530_zap.mdt*"
FILES:${PN}-qcom-apq8096-audio = "${nonarch_base_libdir}/firmware/qcom/apq8096/adsp*.*"
FILES:${PN}-qcom-apq8096-modem = "${nonarch_base_libdir}/firmware/qcom/apq8096/mba.mbn* ${nonarch_base_libdir}/firmware/qcom/apq8096/modem*.* ${nonarch_base_libdir}/firmware/qcom/apq8096/wlanmdsp.mbn*"
FILES:${PN}-qcom-glymur-adreno = "${nonarch_base_libdir}/firmware/qcom/glymur/gen80100_zap.mbn*"
FILES:${PN}-qcom-glymur-audio = "${nonarch_base_libdir}/firmware/qcom/glymur/adsp*.*"
FILES:${PN}-qcom-glymur-compute = "${nonarch_base_libdir}/firmware/qcom/glymur/cdsp*.*"
FILES:${PN}-qcom-kaanapali-adreno = "${nonarch_base_libdir}/firmware/qcom/kaanapali/gen80200_zap.mbn*"
FILES:${PN}-qcom-kaanapali-audio = "${nonarch_base_libdir}/firmware/qcom/kaanapali/adsp*.*"
FILES:${PN}-qcom-kaanapali-compute = "${nonarch_base_libdir}/firmware/qcom/kaanapali/cdsp*.*"
FILES:${PN}-qcom-kaanapali-soccp = "${nonarch_base_libdir}/firmware/qcom/kaanapali/soccp*.*"
FILES:${PN}-qcom-qcm2290-adreno = "${nonarch_base_libdir}/firmware/qcom/qcm2290/a702_zap.mbn*"
FILES:${PN}-qcom-qcm2290-audio = "${nonarch_base_libdir}/firmware/qcom/qcm2290/adsp*.*"
FILES:${PN}-qcom-qcm2290-modem = "${nonarch_base_libdir}/firmware/qcom/qcm2290/modem*.*"
FILES:${PN}-qcom-qcm2290-wifi = "${nonarch_base_libdir}/firmware/qcom/qcm2290/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/qcm2290/*"
FILES:${PN}-qcom-qcm6490-adreno = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/a660_zap.mbn*"
FILES:${PN}-qcom-qcm6490-audio = " \
    ${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/adsp*.* \
    ${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/battmgr.jsn \
    ${nonarch_base_libdir}/firmware/qcom/qcm6490/QCM6490-IDP-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/QCS6490-RB3Gen2-tplg.bin* \
"
FILES:${PN}-qcom-qcm6490-compute = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/cdsp*.*"
FILES:${PN}-qcom-qcm6490-ipa = "${nonarch_base_libdir}/firmware/qcom/qcm6490/ipa_fws.mbn*"
FILES:${PN}-qcom-qcm6490-wifi = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/wpss.mbn*"
FILES:${PN}-qcom-qcm6490-qupv3fw = "${nonarch_base_libdir}/firmware/qcom/qc[ms]6490/qupv3fw.elf*"
FILES:${PN}-qcom-qcs615-adreno = "${nonarch_base_libdir}/firmware/qcom/qcs615/a612_zap.mbn*"
FILES:${PN}-qcom-qcs615-audio = "${nonarch_base_libdir}/firmware/qcom/qcs615/adsp*.* ${nonarch_base_libdir}/firmware/qcom/qcs615/TALOS-EVK-tplg.bin*"
FILES:${PN}-qcom-qcs615-compute = "${nonarch_base_libdir}/firmware/qcom/qcs615/cdsp*.*"
FILES:${PN}-qcom-qcs6490-radxa-dragon-q6a-audio = " \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/radxa/dragon-q6a/adsp*.* \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/radxa/dragon-q6a/QCS6490-Radxa-Dragon-Q6A-tplg.bin*\
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/QCS6490-Radxa-Dragon-Q6A-tplg.bin*\
"
FILES:${PN}-qcom-qcs6490-radxa-dragon-q6a-compute = " \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/radxa/dragon-q6a/cdsp*.* \
"
FILES:${PN}-qcom-qcs6490-thundercomm-rubikpi3-audio = " \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/Thundercomm/RubikPi3/adsp*.* \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/Thundercomm/RubikPi3/QCS6490-Thundercomm-RubikPi3-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/qcs6490/QCS6490-Thundercomm-RubikPi3-tplg.bin* \
"
FILES:${PN}-qcom-qcs8300-adreno = "${nonarch_base_libdir}/firmware/qcom/qcs8300/a623_zap.mbn*"
FILES:${PN}-qcom-qcs8300-audio = "${nonarch_base_libdir}/firmware/qcom/qcs8300/adsp*.* ${nonarch_base_libdir}/firmware/qcom/qcs8300/MONACO-EVK-tplg.bin*"
FILES:${PN}-qcom-qcs8300-compute = "${nonarch_base_libdir}/firmware/qcom/qcs8300/cdsp*.*"
FILES:${PN}-qcom-qcs8300-generalpurpose = "${nonarch_base_libdir}/firmware/qcom/qcs8300/gpdsp*.*"
FILES:${PN}-qcom-qcs8300-qupv3fw = "${nonarch_base_libdir}/firmware/qcom/qcs8300/qupv3fw.elf*"
FILES:${PN}-qcom-qrb4210-adreno = "${nonarch_base_libdir}/firmware/qcom/qrb4210/a610_zap.mbn*"
FILES:${PN}-qcom-qrb4210-audio = "${nonarch_base_libdir}/firmware/qcom/qrb4210/adsp*.*"
FILES:${PN}-qcom-qrb4210-compute = "${nonarch_base_libdir}/firmware/qcom/qrb4210/cdsp*.*"
FILES:${PN}-qcom-qrb4210-modem = "${nonarch_base_libdir}/firmware/qcom/qrb4210/modem*.*"
FILES:${PN}-qcom-qrb4210-wifi = "${nonarch_base_libdir}/firmware/qcom/qrb4210/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/qrb4210/*"
FILES:${PN}-qcom-sa8775p-adreno = "${nonarch_base_libdir}/firmware/qcom/sa8775p/a663_zap.mbn*"
FILES:${PN}-qcom-sa8775p-audio = "\
    ${nonarch_base_libdir}/firmware/qcom/sa8775p/adsp*.* \
    ${nonarch_base_libdir}/firmware/qcom/sa8775p/LEMANS-EVK-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/qcs9100/LEMANS-EVK-tplg.bin* \
"
FILES:${PN}-qcom-sa8775p-compute = "${nonarch_base_libdir}/firmware/qcom/sa8775p/cdsp*.*"
FILES:${PN}-qcom-sa8775p-generalpurpose = "${nonarch_base_libdir}/firmware/qcom/sa8775p/gpdsp*.*"
FILES:${PN}-qcom-sa8775p-qupv3fw = "${nonarch_base_libdir}/firmware/qcom/sa8775p/qupv3fw.elf*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-compat = "${nonarch_base_libdir}/firmware/qcom/LENOVO/21BX"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/*adsp*.* ${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/battmgr.jsn* ${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/audioreach-tplg.bin* ${nonarch_base_libdir}/firmware/qcom/sc8280xp/SC8280XP-LENOVO-X13S-tplg.bin*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/qcdxkmsuc8280.mbn*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/*cdsp*.*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/*slpi*.*"
FILES:${PN}-qcom-sc8280xp-lenovo-x13s-vpu = "${nonarch_base_libdir}/firmware/qcom/sc8280xp/LENOVO/21BX/qcvss8280.mbn*"
FILES:${PN}-qcom-sdm845-adreno = "${nonarch_base_libdir}/firmware/qcom/sdm845/a630*.*"
FILES:${PN}-qcom-sdm845-audio = "${nonarch_base_libdir}/firmware/qcom/sdm845/adsp*.*"
FILES:${PN}-qcom-sdm845-compute = "${nonarch_base_libdir}/firmware/qcom/sdm845/cdsp*.*"
FILES:${PN}-qcom-sdm845-modem = "${nonarch_base_libdir}/firmware/qcom/sdm845/mba.mbn* ${nonarch_base_libdir}/firmware/qcom/sdm845/modem*.* ${nonarch_base_libdir}/firmware/qcom/sdm845/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/qcom/sdm845/notice.txt_wlanmdsp* \
                                 ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/wlanmdsp.mbn* ${nonarch_base_libdir}/firmware/ath10k/WCN3990/hw1.0/notice.txt_wlanmdsp"
FILES:${PN}-qcom-sdm845-thundercomm-db845c-sensors = "${nonarch_base_libdir}/firmware/qcom/sdm845/Thundercomm/db845c/slpi*.*"
FILES:${PN}-qcom-sdx35-foxconn-firehose = "${nonarch_base_libdir}/firmware/qcom/sdx35/foxconn/xbl_s_devprg_ns.melf*"
FILES:${PN}-qcom-sdx61-foxconn-firehose = "${nonarch_base_libdir}/firmware/qcom/sdx61/foxconn/prog_firehose_lite.elf*"
FILES:${PN}-qcom-sm8150-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8150/a640*.*"
FILES:${PN}-qcom-sm8250-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8250/a650*.*"
FILES:${PN}-qcom-sm8250-audio = "${nonarch_base_libdir}/firmware/qcom/sm8250/adsp*.*"
FILES:${PN}-qcom-sm8250-compute = "${nonarch_base_libdir}/firmware/qcom/sm8250/cdsp*.*"
FILES:${PN}-qcom-sm8250-thundercomm-rb5-sensors = "${nonarch_base_libdir}/firmware/qcom/sm8250/Thundercomm/RB5/slpi*.*"
FILES:${PN}-qcom-sm8350-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8350/a660_zap.mbn*"
FILES:${PN}-qcom-sm8450-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8450/a730_zap.mbn*"
FILES:${PN}-qcom-sm8450-audio-tplg = "${nonarch_base_libdir}/firmware/qcom/sm8450/*tplg.bin*"
FILES:${PN}-qcom-sm8550-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8550/a740_zap.mbn*"
FILES:${PN}-qcom-sm8550-audio-tplg = "${nonarch_base_libdir}/firmware/qcom/sm8550/*tplg.bin*"
FILES:${PN}-qcom-sm8650-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8650/gen70900_zap.mbn*"
FILES:${PN}-qcom-sm8650-audio-tplg = "${nonarch_base_libdir}/firmware/qcom/sm8650/*tplg.bin*"
FILES:${PN}-qcom-sm8750-adreno = "${nonarch_base_libdir}/firmware/qcom/sm8750/gen80000_zap.mbn*"
FILES:${PN}-qcom-sm8750-audio = "${nonarch_base_libdir}/firmware/qcom/sm8750/adsp*.* ${nonarch_base_libdir}/firmware/qcom/sm8750/*tplg.bin*"
FILES:${PN}-qcom-sm8750-compute = "${nonarch_base_libdir}/firmware/qcom/sm8750/cdsp*.*"
FILES:${PN}-qcom-x1e80100-adreno = "${nonarch_base_libdir}/firmware/qcom/x1e80100/gen70500_zap.mbn*"
FILES:${PN}-qcom-x1e80100-audio = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/adsp*.* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/battmgr.jsn \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E001DE-DEVKIT-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-CRD-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-EVK-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-Romulus-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-TUXEDO-Elite-14-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-compute = "${nonarch_base_libdir}/firmware/qcom/x1e80100/cdsp*.*"
FILES:${PN}-qcom-x1e80100-asus-vivobook-16-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/ASUSTeK/vivobook-16/X1E80100-ASUS-Vivobook-16-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-ASUS-Vivobook-16-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-asus-vivobook-s15-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/ASUSTeK/vivobook-s15/X1E80100-ASUS-Vivobook-S15-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-ASUS-Vivobook-S15-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-asus-zenbook-a14-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/ASUSTeK/zenbook-a14/X1E80100-ASUS-Zenbook-A14-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-ASUS-Zenbook-A14-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-dell-inspiron-14-plus-7441-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/dell/inspiron-14-plus-7441/X1E80100-Dell-Inspiron-14p-7441-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-Dell-Inspiron-14p-7441-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-dell-latitude-7455-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/dell/latitude-7455/X1E80100-Dell-Latitude-7455-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-Dell-Latitude-7455-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-dell-xps13-9345-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/dell/xps13-9345/X1E80100-Dell-XPS-13-9345-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-Dell-XPS-13-9345-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-hp-omnibook-x14-audio-tplg = " \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/hp/omnibook-x14/X1E80100-HP-OMNIBOOK-X14-tplg.bin* \
    ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-HP-OMNIBOOK-X14-tplg.bin* \
"
FILES:${PN}-qcom-x1e80100-lenovo-t14s-g6-adreno = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/21N1/qcdxkmsuc8380.mbn*"
FILES:${PN}-qcom-x1e80100-lenovo-t14s-g6-audio = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/21N1/*adsp*.* ${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/21N1/battmgr.jsn* ${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/21N1/X1E80100-LENOVO-Thinkpad-T14s-tplg.bin* ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-LENOVO-Thinkpad-T14s-tplg.bin*"
FILES:${PN}-qcom-x1e80100-lenovo-t14s-g6-compute = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/21N1/*cdsp*.*"
FILES:${PN}-qcom-x1e80100-lenovo-t14s-g6-vpu = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/21N1/qcvss8380.mbn*"
FILES:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-adreno = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/qcdxkmsuc8380.mbn*"
FILES:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-audio = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/*adsp*.* ${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/battmgr.jsn* ${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/X1E80100-LENOVO-Yoga-Slim7x-tplg.bin* ${nonarch_base_libdir}/firmware/qcom/x1e80100/X1E80100-LENOVO-Yoga-Slim7x-tplg.bin*"
FILES:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-compute = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/*cdsp*.*"
FILES:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-vpu = "${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/qcvss8380.mbn* ${nonarch_base_libdir}/firmware/qcom/x1e80100/LENOVO/83ED/qcav1e8380.mbn*"
FILES:${PN}-qcom-x1e80100-qupv3fw = "${nonarch_base_libdir}/firmware/qcom/x1e80100/qupv3fw.elf*"
FILES:${PN}-qcom-x1p42100-adreno = "${nonarch_base_libdir}/firmware/qcom/x1p42100/gen71500_zap.mbn*"

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
RDEPENDS:${PN}-qcom-adreno-a612 = "${PN}-qcom-license ${PN}-qcom-adreno-a630"
RDEPENDS:${PN}-qcom-adreno-a623 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a630 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a640 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a650 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a660 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a663 = "${PN}-qcom-license ${PN}-qcom-adreno-a660"
RDEPENDS:${PN}-qcom-adreno-a702 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a730 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-a740 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g705 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g709 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g715 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g800 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g801 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-adreno-g802 = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8016-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8016-wifi = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8096-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8096-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-apq8096-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-glymur-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-glymur-audio = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-glymur-compute = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-kaanapali-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-kaanapali-audio = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-kaanapali-compute = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-kaanapali-soccp = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-qcm2290-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm2290-wifi = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm6490-adreno = "${PN}-qcom-license"
RPROVIDES:${PN}-qcom-qcm6490-adreno = "${PN}-qcom-qcs6490-adreno"
RDEPENDS:${PN}-qcom-qcm6490-audio = "${PN}-qcom-license ${PN}-linaro-license"
RPROVIDES:${PN}-qcom-qcm6490-audio = "${PN}-qcom-qcs6490-audio"
RDEPENDS:${PN}-qcom-qcm6490-compute = "${PN}-qcom-license"
RPROVIDES:${PN}-qcom-qcm6490-compute = "${PN}-qcom-qcs6490-compute"
RDEPENDS:${PN}-qcom-qcm6490-ipa = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcm6490-wifi = "${PN}-qcom-license"
RPROVIDES:${PN}-qcom-qcm6490-wifi = "${PN}-qcom-qcs6490-wifi"
RDEPENDS:${PN}-qcom-qcm6490-qupv3fw = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcs615-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcs615-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-qcs615-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcs6490-radxa-dragon-q6a-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-qcs6490-radxa-dragon-q6a-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcs6490-thundercomm-rubikpi3-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-qcs8300-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qcs8300-audio = "${PN}-qcom-2-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-qcs8300-compute = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-qcs8300-generalpurpose = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-qcs8300-qupv3fw = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-qrb4210-modem = "${PN}-qcom-license"
# Only symlinks in qcom-qrb4210-wifi, firmware is in qcom-qcm2290-wifi
# c.f. https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git/commit/?id=650e88378e76d5fad3997a5398f1ade47a74d924
RDEPENDS:${PN}-qcom-qrb4210-wifi = "${PN}-qcom-license ${PN}-qcom-qcm2290-wifi"
RDEPENDS:${PN}-qcom-sa8775p-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sa8775p-audio = "${PN}-qcom-2-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sa8775p-compute = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-sa8775p-generalpurpose = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-sa8775p-qupv3fw = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sc8280xp-lenovo-x13s-vpu = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-modem = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdm845-thundercomm-db845c-sensors = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdx35-foxconn-firehose = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sdx61-foxconn-firehose = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8150-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-audio = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8250-thundercomm-rb5-sensors = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8350-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8450-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8450-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sm8550-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8550-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sm8650-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8650-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sm8750-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-sm8750-audio = "${PN}-qcom-2-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-sm8750-compute = "${PN}-qcom-2-license"
RDEPENDS:${PN}-qcom-x1e80100-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-audio = "${PN}-qcom-2-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-asus-vivobook-16-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-asus-vivobook-s15-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-asus-zenbook-a14-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-dell-inspiron-14-plus-7441-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-dell-latitude-7455-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-dell-xps13-9345-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-hp-omnibook-x14-audio-tplg = "${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-t14s-g6-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-t14s-g6-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-t14s-g6-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-adreno = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-audio = "${PN}-qcom-license ${PN}-linaro-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-compute = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-lenovo-yoga-slim7x-vpu = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1e80100-qupv3fw = "${PN}-qcom-license"
RDEPENDS:${PN}-qcom-x1p42100-adreno = "${PN}-qcom-license"

RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-audio = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-adreno = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-compute = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-sensors = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"
RRECOMMENDS:${PN}-qcom-sc8280xp-lenovo-x13s-vpu = "${PN}-qcom-sc8280xp-lenovo-x13s-compat"

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

# For 3com typhoon
LICENSE:${PN}-typhoon-license = "Firmware-typhoon"
LICENSE:${PN}-typhoon = "Firmware-typhoon"
FILES:${PN}-typhoon-license = "${nonarch_base_libdir}/firmware/LICENCE.typhoon"
FILES:${PN}-typhoon = "${nonarch_base_libdir}/firmware/3com/typhoon.bin*"
RDEPENDS:${PN}-typhoon = "${PN}-typhoon-license"

# For ish - Intel Integrated Sensor Hub
LICENSE:${PN}-intel-license = "Firmware-intel"
FILES:${PN}-intel-license = "${nonarch_base_libdir}/firmware/LICENSE.intel"

LICENSE:${PN}-ish-lnlm = "Firmware-intel"
FILES:${PN}-ish-lnlm = "${nonarch_base_libdir}/firmware/intel/ish/ish_lnlm.bin*"
RDEPENDS:${PN}-ish-lnlm = "${PN}-intel-license"

# For Dell ish - Intel Integrated Sensor Hub
LICENSE:${PN}-dell-license = "Firmware-dell"
FILES:${PN}-dell-license = "${nonarch_base_libdir}/firmware/LICENSE.dell"

LICENSE:${PN}-ish-lnlm-39ceeaf8 = "Firmware-dell"
FILES:${PN}-ish-lnlm-39ceeaf8 =  "\
    ${nonarch_base_libdir}/firmware/intel/ish/ish_lnlm_39ceeaf8.bin* \
    ${nonarch_base_libdir}/firmware/dell/ish/ish_lnlm_39ceeaf8_*.bin* \
"
RDEPENDS:${PN}-ish-lnlm-39ceeaf8 = "${PN}-dell-license"

LICENSE:${PN}-ish-ptl-39ceeaf8 = "Firmware-dell"
FILES:${PN}-ish-ptl-39ceeaf8 =  "\
    ${nonarch_base_libdir}/firmware/intel/ish/ish_ptl_39ceeaf8.bin* \
    ${nonarch_base_libdir}/firmware/dell/ish/ish_ptl_39ceeaf8_*.bin* \
"
RDEPENDS:${PN}-ish-ptl-39ceeaf8 = "${PN}-dell-license"

# For HP ish - Intel Integrated Sensor Hub
LICENSE:${PN}-hp-license = "Firmware-HP"
FILES:${PN}-hp-license = "${nonarch_base_libdir}/firmware/LICENSE.HP"

LICENSE:${PN}-ish-lnlm-12128606 = "Firmware-HP"
FILES:${PN}-ish-lnlm-12128606 = " \
    ${nonarch_base_libdir}/firmware/intel/ish/ish_lnlm_12128606.bin* \
    ${nonarch_base_libdir}/firmware/HP/ish/ish_lnlm_12128606.bin* \
"
RDEPENDS:${PN}-ish-lnlm-12128606 = "${PN}-hp-license"

# For LENOVO ish - Intel Integrated Sensor Hub
LICENSE:${PN}-lenovo-license = "Firmware-lenovo"
FILES:${PN}-lenovo-license = "${nonarch_base_libdir}/firmware/LICENCE.lenovo"

LICENSE:${PN}-ish-lnlm-53c4ffad = "Firmware-lenovo"
FILES:${PN}-ish-lnlm-53c4ffad = "\
    ${nonarch_base_libdir}/firmware/LENOVO/ish/ish_lnlm_53c4ffad_*.bin* \
    ${nonarch_base_libdir}/firmware/intel/ish/ish_lnlm_53c4ffad_*.bin* \
"
RDEPENDS:${PN}-ish-lnlm-53c4ffad = "${PN}-lenovo-license"

LICENSE:${PN}-ish-ptl = "Firmware-intel"
FILES:${PN}-ish-ptl = "${nonarch_base_libdir}/firmware/intel/ish/ish_ptl.bin*"
RDEPENDS:${PN}-ish-ptl = "${PN}-intel-license"

# For advansys - AdvanSys SCSI
LICENSE:${PN}-advansys-license = "Firmware-advansys"
FILES:${PN}-advansys-license = "${nonarch_base_libdir}/firmware/LICENCE.advansys"

LICENSE:${PN}-advansys = "Firmware-advansys"
FILES:${PN}-advansys = "${nonarch_base_libdir}/firmware/advansys/*"
RDEPENDS:${PN}-advansys = "${PN}-advansys-license"

# For as21xxx
LICENSE:${PN}-aeonsemi-license = "Firmware-aeonsemi"
FILES:${PN}-aeonsemi-license = "${nonarch_base_libdir}/firmware/LICENSE.aeonsemi"

LICENSE:${PN}-as21xxx = "Firmware-aeonsemi"
FILES:${PN}-as21xxx = "${nonarch_base_libdir}/firmware/aeonsemi/*"
RDEPENDS:${PN}-as21xxx = "${PN}-aeonsemi-license"

# For orinoco - Agere/Prism/Symbol Orinoco support
LICENSE:${PN}-agere-license = "Firmware-agere"
FILES:${PN}-agere-license = "${nonarch_base_libdir}/firmware/LICENCE.agere"

LICENSE:${PN}-orinoco = "Firmware-agere"
FILES:${PN}-orinoco = "${nonarch_base_libdir}/firmware/agere_*"
RDEPENDS:${PN}-orinoco = "${PN}-agere-license"

# For en8811h - Airoha 2.5G Ethernet Phy
LICENSE:${PN}-airoha-license = "Firmware-airoha"
FILES:${PN}-airoha-license = "${nonarch_base_libdir}/firmware/LICENSE.airoha"

LICENSE:${PN}-an8811hb = "Firmware-airoha"
FILES:${PN}-an8811hb = "${nonarch_base_libdir}/firmware/airoha/an8811hb/EthMD32*"
RDEPENDS:${PN}-an8811hb = "${PN}-airoha-license"

LICENSE:${PN}-en8811h = "Firmware-airoha"
FILES:${PN}-en8811h = "${nonarch_base_libdir}/firmware/airoha/EthMD32*"
RDEPENDS:${PN}-en8811h = "${PN}-airoha-license"

# For airoha-npu - Airoha Network Processor Unit driver
LICENSE:${PN}-airoha-npu = "Firmware-airoha"
FILES:${PN}-airoha-npu = " \
    ${nonarch_base_libdir}/firmware/airoha/an7583_npu* \
    ${nonarch_base_libdir}/firmware/airoha/en7581_npu* \
    ${nonarch_base_libdir}/firmware/airoha/en7581_MT7996_npu* \
"
RDEPENDS:${PN}-airoha-npu = "${PN}-airoha-license"

# For ccp - Platform Security Processor (PSP) device
LICENSE:${PN}-amd-sev-license = "Firmware-amd-sev"
FILES:${PN}-amd-sev-license = "${nonarch_base_libdir}/firmware/LICENSE.amd-sev"

LICENSE:${PN}-ccp = "Firmware-amd-sev"
FILES:${PN}-ccp = "${nonarch_base_libdir}/firmware/amd/amd_sev*"
RDEPENDS:${PN}-ccp = "${PN}-amd-sev-license"

# For amdxdna - AMD Inference processor
LICENSE:${PN}-amdnpu-license = "Firmware-amdnpu"
FILES:${PN}-amdnpu-license = "${nonarch_base_libdir}/firmware/LICENSE.amdnpu"

LICENSE:${PN}-amdxdna = "Firmware-amdnpu"
FILES:${PN}-amdxdna = "${nonarch_base_libdir}/firmware/amdnpu/*"
RDEPENDS:${PN}-amdxdna = "${PN}-amdnpu-license"

# For amd_pmf - AMD Platform Management Framework TA
LICENSE:${PN}-amd-pmf-license = "Firmware-amd_pmf"
FILES:${PN}-amd-pmf-license = "${nonarch_base_libdir}/firmware/LICENSE.amd_pmf"

LICENSE:${PN}-amd-pmf = "Firmware-amd_pmf"
FILES:${PN}-amd-pmf = "${nonarch_base_libdir}/firmware/amdtee/*"
RDEPENDS:${PN}-amd-pmf = "${PN}-amd-pmf-license"

# For microcode_amd - AMD CPU Microcode Update Driver for Linux
LICENSE:${PN}-amd-ucode-license = "Firmware-amd-ucode"
FILES:${PN}-amd-ucode-license = "${nonarch_base_libdir}/firmware/LICENSE.amd-ucode"

LICENSE:${PN}-microcode-amd = "Firmware-amd-ucode"
FILES:${PN}-microcode-amd = "${nonarch_base_libdir}/firmware/amd-ucode/*"
RDEPENDS:${PN}-microcode-amd = "${PN}-amd-ucode-license"

# For amlogic - Amlogic SoC Firmware
LICENSE:${PN}-amlogic-license = "Firmware-amlogic"
FILES:${PN}-amlogic-license = "${nonarch_base_libdir}/firmware/LICENSE.amlogic"

LICENSE:${PN}-amlogic = "Firmware-amlogic"
FILES:${PN}-amlogic = "${nonarch_base_libdir}/firmware/amlogic/aml_*"
RDEPENDS:${PN}-amlogic = "${PN}-amlogic-license"

# For starfire - Adaptec Starfire/DuraLAN support
LICENSE:${PN}-starfire = "GPL-2.0-only"
FILES:${PN}-starfire = "${nonarch_base_libdir}/firmware/adaptec/starfire*"

# For as102 - Abilis Systems Single DVB-T Receiver
LICENSE:${PN}-abilis-license = "Firmware-Abilis"
FILES:${PN}-abilis-license = "${nonarch_base_libdir}/firmware/LICENCE.Abilis"

LICENSE:${PN}-as102 = "Firmware-Abilis"
FILES:${PN}-as102 = "${nonarch_base_libdir}/firmware/as102_data*_st.hex*"
RDEPENDS:${PN}-as102 = "${PN}-abilis-license"

# For wilc1000 - Atmel 802.11n WLAN driver for WILC1000 & WILC3000
LICENSE:${PN}-atmel-license = "Firmware-atmel"
FILES:${PN}-atmel-license = "${nonarch_base_libdir}/firmware/LICENSE.atmel"

LICENSE:${PN}-wilc1000 = "Firmware-atmel"
FILES:${PN}-wilc1000 = "${nonarch_base_libdir}/firmware/atmel/wilc1000*"
RDEPENDS:${PN}-wilc1000 = "${PN}-atmel-license"

LICENSE:${PN}-wilc3000 = "Firmware-atmel"
FILES:${PN}-wilc3000 = "${nonarch_base_libdir}/firmware/atmel/wilc3000*"
RDEPENDS:${PN}-wilc3000 = "${PN}-atmel-license"

# For atusb - ATUSB IEEE 802.15.4 transceiver driver
FILES:${PN}-atusb = "${nonarch_base_libdir}/firmware/atusb/*"
LICENSE:${PN}-atusb = "GPL-2.0-or-later"

# For dvb-ttpci - AV7110 cards
FILES:${PN}-dvb-ttpci = "${nonarch_base_libdir}/firmware/av7110/*"
LICENSE:${PN}-dvb-ttpci = "GPL-2.0-or-later"

# For bmi260 - Bosch BMI260 IMU configuration data
LICENSE:${PN}-bmi260-license = "Firmware-bmi260"
FILES:${PN}-bmi260-license = "${nonarch_base_libdir}/firmware/LICENSE.bmi260"

LICENSE:${PN}-bmi260 = "Firmware-bmi260"
FILES:${PN}-bmi260 = "${nonarch_base_libdir}/firmware/bmi260-init-data.fw*"
RDEPENDS:${PN}-bmi260 = "${PN}-bmi260-license"

# For cdns-mhdp - Cadence MHDP8546 DP bridge
LICENSE:${PN}-cadence-license = "Firmware-cadence"
FILES:${PN}-cadence-license = "${nonarch_base_libdir}/firmware/LICENCE.cadence"

LICENSE:${PN}-mhdp8546 = "Firmware-cadence"
FILES:${PN}-mhdp8546 = "${nonarch_base_libdir}/firmware/cadence/mhdp8546.bin*"
RDEPENDS:${PN}-mhdp8546 = "${PN}-cadence-license"

# For nitrox - Cavium CNN55XX crypto driver
LICENSE:${PN}-cavium-license = "Firmware-cavium"
FILES:${PN}-cavium-license = "${nonarch_base_libdir}/firmware/LICENCE.cavium"

LICENSE:${PN}-cnn55xx = "Firmware-cavium"
FILES:${PN}-cnn55xx = "${nonarch_base_libdir}/firmware/cavium/cnn55xx*"
RDEPENDS:${PN}-cnn55xx = "${PN}-cavium-license"

# For BFA/BNA - QLogic BR-series Adapter FC/FCOE drivers
LICENSE:${PN}-bfa-license = "Firmware-bfa"
FILES:${PN}-bfa-license = "${nonarch_base_libdir}/firmware/LICENSE.bfa"

LICENSE:${PN}-cbfw = "Firmware-bfa"
FILES:${PN}-cbfw = "${nonarch_base_libdir}/firmware/cbfw-3.2.5.1.bin*"
RDEPENDS:${PN}-cbfw = "${PN}-bfa-license"

LICENSE:${PN}-ctfw = "Firmware-bfa"
FILES:${PN}-ctfw = "${nonarch_base_libdir}/firmware/ctfw-3.2.5.1.bin*"
RDEPENDS:${PN}-ctfw = "${PN}-bfa-license"

LICENSE:${PN}-ct2fw = "Firmware-bfa"
FILES:${PN}-ct2fw = "${nonarch_base_libdir}/firmware/ct2fw-3.2.5.1.bin*"
RDEPENDS:${PN}-ct2fw = "${PN}-bfa-license"

# For pcnet_cs - NE2000 compatible PCMCIA adapter
FILES:${PN}-pcnet-cs = " \
    ${nonarch_base_libdir}/firmware/cis/LA-PCM.cis* \
    ${nonarch_base_libdir}/firmware/cis/PCMLM28.cis* \
    ${nonarch_base_libdir}/firmware/cis/DP83903.cis* \
    ${nonarch_base_libdir}/firmware/cis/NE2K.cis* \
    ${nonarch_base_libdir}/firmware/cis/tamarack.cis* \
    ${nonarch_base_libdir}/firmware/cis/PE-200.cis* \
    ${nonarch_base_libdir}/firmware/cis/PE520.cis* \
"
LICENSE:${PN}-pcnet-cs = "GPL-2.0-only & MPL-1.1"

# For 3c589_cs - 3Com PCMCIA adapter
FILES:${PN}-3c589-cs = "${nonarch_base_libdir}/firmware/cis/3CXEM556.cis*"
LICENSE:${PN}-3c589-cs = "GPL-2.0-only & MPL-1.1"

# For 3c574-cs - 3Com PCMCIA adapter
FILES:${PN}-3c574-cs = "${nonarch_base_libdir}/firmware/cis/3CCFEM556.cis*"
LICENSE:${PN}-3c574-cs = "GPL-2.0-only & MPL-1.1"

# For serial_cs - Serial PCMCIA adapter (pcmcia-cs project)
FILES:${PN}-serial-cs = " \
    ${nonarch_base_libdir}/firmware/cis/MT5634ZLX.cis* \
    ${nonarch_base_libdir}/firmware/cis/RS-COM-2P.cis* \
    ${nonarch_base_libdir}/firmware/cis/COMpad2.cis* \
    ${nonarch_base_libdir}/firmware/cis/COMpad4.cis* \
"
LICENSE:${PN}-serial-cs = "GPL-2.0-only & MPL-1.1"

# For Sierra Wireless serial_cs - Serial PCMCIA adapter
FILES:${PN}-sw-serial = " \
    ${nonarch_base_libdir}/firmware/cis/SW_555_SER.cis* \
    ${nonarch_base_libdir}/firmware/cis/SW_7xx_SER.cis* \
    ${nonarch_base_libdir}/firmware/cis/SW_8xx_SER.cis* \
"
LICENSE:${PN}-sw-serial = "GPL-3.0-only"

# For smsmdtv - Siano MDTV Core module
LICENSE:${PN}-siano-license = "Firmware-siano"
FILES:${PN}-siano-license = "${nonarch_base_libdir}/firmware/LICENCE.siano"

LICENSE:${PN}-smsmdtv = "Firmware-siano"
FILES:${PN}-smsmdtv = " \
    ${nonarch_base_libdir}/firmware/cmmb_vega_12mhz.inp* \
    ${nonarch_base_libdir}/firmware/cmmb_venice_12mhz.inp* \
    ${nonarch_base_libdir}/firmware/dvb_nova_12mhz.inp* \
    ${nonarch_base_libdir}/firmware/dvb_nova_12mhz_b0.inp* \
    ${nonarch_base_libdir}/firmware/isdbt_nova_12mhz.inp* \
    ${nonarch_base_libdir}/firmware/isdbt_nova_12mhz_b0.inp* \
    ${nonarch_base_libdir}/firmware/isdbt_rio.inp* \
    ${nonarch_base_libdir}/firmware/sms1xxx-hcw-55xxx-dvbt-02.fw* \
    ${nonarch_base_libdir}/firmware/sms1xxx-hcw-55xxx-isdbt-02.fw* \
    ${nonarch_base_libdir}/firmware/sms1xxx-nova-a-dvbt-01.fw* \
    ${nonarch_base_libdir}/firmware/sms1xxx-nova-b-dvbt-01.fw* \
    ${nonarch_base_libdir}/firmware/sms1xxx-stellar-dvbt-01.fw* \
    ${nonarch_base_libdir}/firmware/tdmb_nova_12mhz.inp* \
"
RDEPENDS:${PN}-smsmdtv = "${PN}-siano-license"

# For cpia2 - cameras based on Vision's CPiA2
FILES:${PN}-cpia2 = "${nonarch_base_libdir}/firmware/cpia2/*"
LICENSE:${PN}-cpia2 = "GPL-2.0-or-later"

# For snd-hda-codec-ca0132 - Creative Sound Core3D codec
LICENSE:${PN}-ca0132-license = "Firmware-ca0132"
FILES:${PN}-ca0132-license = "${nonarch_base_libdir}/firmware/LICENCE.ca0132"

LICENSE:${PN}-ca0132 = "Firmware-ca0132"
FILES:${PN}-ca0132 = " \
    ${nonarch_base_libdir}/firmware/ctefx.bin* \
    ${nonarch_base_libdir}/firmware/ctspeq.bin* \
"
RDEPENDS:${PN}-ca0132 = "${PN}-ca0132-license"

# For cxgb3 - Chelsio Terminator 3 1G/10G Ethernet adapter
LICENSE:${PN}-cxgb3-license = "Firmware-cxgb3"
FILES:${PN}-cxgb3-license = "${nonarch_base_libdir}/firmware/LICENCE.cxgb3"

LICENSE:${PN}-cxgb3 = "Firmware-cxgb3 & GPL-2.0-only"
FILES:${PN}-cxgb3 = "${nonarch_base_libdir}/firmware/cxgb3/*"
RDEPENDS:${PN}-cxgb3 = "${PN}-cxgb3-license"

# For cxgb4 - Chelsio Terminator 4/5/6 1/10/25/40/100G Ethernet adapter
LICENSE:${PN}-chelsio-firmware-license = "Firmware-chelsio_firmware"
FILES:${PN}-chelsio-firmware-license = "${nonarch_base_libdir}/firmware/LICENCE.chelsio_firmware"

LICENSE:${PN}-cxgb4 = "Firmware-chelsio_firmware"
FILES:${PN}-cxgb4 = "${nonarch_base_libdir}/firmware/cxgb4/*"
RDEPENDS:${PN}-cxgb4 = "${PN}-chelsio-firmware-license"

# For dabusb - Digital Audio Broadcasting (DAB) Receiver for USB and Linux
LICENSE:${PN}-dabusb-license = "Firmware-dabusb"
FILES:${PN}-dabusb-license = "${nonarch_base_libdir}/firmware/LICENCE.dabusb"

LICENSE:${PN}-dabusb = "Firmware-dabusb"
FILES:${PN}-dabusb = "${nonarch_base_libdir}/firmware/dabusb/*"
RDEPENDS:${PN}-dabusb = "${PN}-dabusb-license"

# For dsp56k - Atari DSP56k support
LICENSE:${PN}-dsp56k = "GPL-2.0-or-later"
FILES:${PN}-dsp56k = "${nonarch_base_libdir}/firmware/dsp56k/*"

# For dib0700 - DiBcom dib0700 USB DVB bridge driver
LICENSE:${PN}-dib0700-license = "Firmware-dib0700"
FILES:${PN}-dib0700-license = "${nonarch_base_libdir}/firmware/LICENSE.dib0700"

LICENSE:${PN}-dib0700 = "Firmware-dib0700"
FILES:${PN}-dib0700 = "${nonarch_base_libdir}/firmware/dvb-usb-dib0700-1.20.fw*"
RDEPENDS:${PN}-dib0700 = "${PN}-dib0700-license"

# For it9135 - ITEtech IT913x DVB-T USB driver
LICENSE:${PN}-it913x-license = "Firmware-it913x"
FILES:${PN}-it913x-license = "${nonarch_base_libdir}/firmware/LICENCE.it913x"

LICENSE:${PN}-it9135 = "Firmware-it913x"
FILES:${PN}-it9135 = "${nonarch_base_libdir}/firmware/dvb-usb-it9135*"
RDEPENDS:${PN}-it9135 = "${PN}-it913x-license"

# For drxk - Micronas DRX-K demodulator driver
LICENSE:${PN}-drxk-license = "Firmware-drxk"
FILES:${PN}-drxk-license = "${nonarch_base_libdir}/firmware/LICENSE.drxk"

LICENSE:${PN}-drxk = "Firmware-drxk"
FILES:${PN}-drxk = "${nonarch_base_libdir}/firmware/dvb-usb-terratec-h5-drxk.fw*"
RDEPENDS:${PN}-drxk = "${PN}-drxk-license"

# For e100 - Intel PRO/100 Ethernet NIC
LICENSE:${PN}-e100-license = "Firmware-e100"
FILES:${PN}-e100-license = "${nonarch_base_libdir}/firmware/LICENCE.e100"

LICENSE:${PN}-e100 = "Firmware-e100"
FILES:${PN}-e100 = "${nonarch_base_libdir}/firmware/e100/*"
RDEPENDS:${PN}-e100 = "${PN}-e100-license"

# For io_ti - USB Inside Out Edgeport Serial Driver (TI Devices)
LICENSE:${PN}-io-ti = "GPL-2.0-or-later"
FILES:${PN}-io-ti = "${nonarch_base_libdir}/firmware/edgeport/down3.bin"

# For io_edgeport - USB Inside Out Edgeport Serial Driver
LICENSE:${PN}-io-edgeport = "GPL-2.0-or-later"
FILES:${PN}-io-edgeport = "${nonarch_base_libdir}/firmware/edgeport/*"

# For emi26 - EMI 2|6 USB Audio interface
LICENSE:${PN}-emi26-license = "Firmware-emi26"
FILES:${PN}-emi26-license = "${nonarch_base_libdir}/firmware/LICENCE.emi26"

LICENSE:${PN}-emi26 = "Firmware-emi26"
FILES:${PN}-emi26 = "${nonarch_base_libdir}/firmware/emi26/*"
RDEPENDS:${PN}-emi26 = "${PN}-drxk-license"

# For ene-ub6250 - ENE UB6250 SD card reader driver
LICENSE:${PN}-ene-firmware-license = "Firmware-ene_firmware"
FILES:${PN}-ene-firmware-license = "${nonarch_base_libdir}/firmware/LICENCE.ene_firmware"

LICENSE:${PN}-ene-ub6250 = "Firmware-ene_firmware"
FILES:${PN}-ene-ub6250 = "${nonarch_base_libdir}/firmware/ene-ub6250/*"
RDEPENDS:${PN}-ene-ub6250 = "${PN}-ene-firmware-license"

# For go7007-s2250
LICENSE:${PN}-sensoray-license = "Firmware-sensoray"
FILES:${PN}-sensoray-license = "${nonarch_base_libdir}/firmware/LICENCE.sensoray"

LICENSE:${PN}-go7007-s2250 = "Firmware-sensoray"
FILES:${PN}-go7007-s2250 = " \
    ${nonarch_base_libdir}/firmware/go7007/s2250* \
    ${nonarch_base_libdir}/firmware/s2250* \
"
RDEPENDS:${PN}-go7007-s2250 = "${PN}-sensoray-license"

# For go7007
LICENSE:${PN}-go7007-license = "Firmware-go7007"
FILES:${PN}-go7007-license = "${nonarch_base_libdir}/firmware/LICENCE.go7007"

LICENSE:${PN}-go7007 = "Firmware-go7007"
FILES:${PN}-go7007 = "${nonarch_base_libdir}/firmware/go7007/*"
RDEPENDS:${PN}-go7007 = "${PN}-go7007-license"

# For hfi1 - Intel OPA Gen 1 adapter
LICENSE:${PN}-hfi1-license = "Firmware-hfi1_firmware"
FILES:${PN}-hfi1-license = "${nonarch_base_libdir}/firmware/LICENSE.hfi1_firmware"

LICENSE:${PN}-hfi1 = "Firmware-hfi1_firmware"
FILES:${PN}-hfi1 = "${nonarch_base_libdir}/firmware/hfi1_*"
RDEPENDS:${PN}-hfi1 = "${PN}-hfi1-license"

# For inside-secure - Inside Secure EIP197 crypto driver
LICENSE:${PN}-inside-secure-license = "Firmware-inside-secure"
FILES:${PN}-inside-secure-license = "${nonarch_base_libdir}/firmware/LICENCE.inside-secure"

LICENSE:${PN}-inside-secure = "Firmware-inside-secure"
FILES:${PN}-inside-secure = "${nonarch_base_libdir}/firmware/inside-secure/*"
RDEPENDS:${PN}-inside-secure = "${PN}-inside-secure-license"

# For snd_soc_catpt - Intel AudioDSP driver for HSW/BDW platforms
LICENSE:${PN}-intcsst2-license = "Firmware-IntcSST2"
FILES:${PN}-intcsst2-license = "${nonarch_base_libdir}/firmware/LICENCE.IntcSST2"

LICENSE:${PN}-snd-soc-catpt = "Firmware-IntcSST2"
FILES:${PN}-snd-soc-catpt = " \
    ${nonarch_base_libdir}/firmware/intel/catpt/bdw/dsp_basefw.bin* \
    ${nonarch_base_libdir}/firmware/intel/IntcSST2.bin* \
"
RDEPENDS:${PN}-snd-soc-catpt = "${PN}-intcsst2-license"

# For snd_intel_sst_core
LICENSE:${PN}-fw-sst-0f28-license = "Firmware-fw_sst_0f28"
FILES:${PN}-fw-sst-0f28-license = "${nonarch_base_libdir}/firmware/LICENCE.fw_sst_0f28"

LICENSE:${PN}-snd-intel-sst-core = "Firmware-fw_sst_0f28"
FILES:${PN}-snd-intel-sst-core = "${nonarch_base_libdir}/firmware/intel/fw_sst_*"
RDEPENDS:${PN}-snd-intel-sst-core = "${PN}-fw-sst-0f28-license"

# For atomisp - Intel IPU2 (Image Processing Unit 2) driver
LICENSE:${PN}-ivsc-license = "Firmware-ivsc"
FILES:${PN}-ivsc-license = "${nonarch_base_libdir}/firmware/LICENSE.ivsc"

LICENSE:${PN}-atomisp = "Firmware-ivsc"
FILES:${PN}-atomisp = "${nonarch_base_libdir}/firmware/intel/ipu/shisp_240*"
RDEPENDS:${PN}-atomisp = "${PN}-ivsc-license"

# For intel-ipu6-isys - Intel IPU6 (Image Processing Unit 6) driver
LICENSE:${PN}-intel-ipu6-isys = "Firmware-ivsc"
FILES:${PN}-intel-ipu6-isys = "${nonarch_base_libdir}/firmware/intel/ipu/ipu6*"
RDEPENDS:${PN}-intel-ipu6-isys = "${PN}-ivsc-license"

# For mei-vsc-hw - Intel Visual Sensing Controller
LICENSE:${PN}-mei-vsc-hw = "Firmware-ivsc"
FILES:${PN}-mei-vsc-hw = "${nonarch_base_libdir}/firmware/intel/vsc/*"
RDEPENDS:${PN}-mei-vsc-hw = "${PN}-ivsc-license"

# For ipu3-imgu - Intel IPU3 (3rd Gen Image Processing Unit) driver
LICENSE:${PN}-ipu3-firmware-license = "Firmware-ipu3_firmware"
FILES:${PN}-ipu3-firmware-license = "${nonarch_base_libdir}/firmware/LICENSE.ipu3_firmware"

LICENSE:${PN}-ipu3-imgu = "Firmware-ipu3_firmware"
FILES:${PN}-ipu3-imgu = " \
    ${nonarch_base_libdir}/firmware/intel/ipu/irci_irci_ecr-master_20161208_0213_20170112_1500.bin* \
    ${nonarch_base_libdir}/firmware/intel/ipu3-fw.bin* \
    ${nonarch_base_libdir}/firmware/intel/irci_irci_ecr-master_20161208_0213_20170112_1500.bin* \
"
RDEPENDS:${PN}-ipu3-imgu = "${PN}-ipu3-firmware-license"

# For intel-ipu7-isys - Intel IPU7 (Image Processing Unit 7) driver
LICENSE:${PN}-intel-ipu7-isys = "Firmware-intel"
FILES:${PN}-intel-ipu7-isys = "${nonarch_base_libdir}/firmware/intel/ipu/ipu7*"
RDEPENDS:${PN}-intel-ipu7-isys = "${PN}-intel-license"

# For intel_vpu - Intel NPU driver
LICENSE:${PN}-intel-vpu-license = "Firmware-intel_vpu"
FILES:${PN}-intel-vpu-license = "${nonarch_base_libdir}/firmware/LICENSE.intel_vpu"

LICENSE:${PN}-intel-vpu = "Firmware-intel_vpu"
FILES:${PN}-intel-vpu = "${nonarch_base_libdir}/firmware/intel/vpu/*"
RDEPENDS:${PN}-intel-vpu = "${PN}-intel-vpu-license"

# For isci - Intel C600 SAS controller driver
LICENSE:${PN}-isci = "GPL-2.0-only"
FILES:${PN}-isci = "${nonarch_base_libdir}/firmware/isci/*"

# For ixp4xx-npe - Intel IXP4xx XScale Network Processing Engine (NPE) Firmware
LICENSE:${PN}-ixp4xx-license = "Firmware-ixp4xx"
FILES:${PN}-ixp4xx-license = "${nonarch_base_libdir}/firmware/LICENSE.ixp4xx"

LICENSE:${PN}-ixp4xx-npe = "Firmware-ixp4xx"
FILES:${PN}-ixp4xx-npe = "${nonarch_base_libdir}/firmware/ixp4xx/*"
RDEPENDS:${PN}-ixp4xx-npe = "${PN}-ixp4xx-license"

# For kaweth - USB KLSI KL5USB101-based Ethernet device
LICENSE:${PN}-kaweth-license = "Firmware-kaweth"
FILES:${PN}-kaweth-license = "${nonarch_base_libdir}/firmware/LICENCE.kaweth"

LICENSE:${PN}-kaweth = "Firmware-kaweth"
FILES:${PN}-kaweth = "${nonarch_base_libdir}/firmware/kaweth/*"
RDEPENDS:${PN}-kaweth = "${PN}-kaweth-license"

# For keyspan - USB Keyspan USA-xxx serial device
LICENSE:${PN}-keyspan-license = "Firmware-keyspan"
FILES:${PN}-keyspan-license = "${nonarch_base_libdir}/firmware/LICENCE.keyspan"

LICENSE:${PN}-keyspan = "Firmware-keyspan"
FILES:${PN}-keyspan = "${nonarch_base_libdir}/firmware/keyspan/*"
RDEPENDS:${PN}-keyspan = "${PN}-keyspan-license"

# For keyspan_pda - USB Keyspan PDA single-port serial device
LICENSE:${PN}-keyspan-pda = "GPL-2.0-or-later"
FILES:${PN}-keyspan-pda = "${nonarch_base_libdir}/firmware/keyspan_pda/*"

# For mga - Matrox G200/G400/G550
LICENSE:${PN}-mga-license = "Firmware-mga"
FILES:${PN}-mga-license = "${nonarch_base_libdir}/firmware/LICENSE.mga"

LICENSE:${PN}-mga = "Firmware-mga"
FILES:${PN}-mga = "${nonarch_base_libdir}/firmware/matrox/*"
RDEPENDS:${PN}-mga = "${PN}-mga-license"

# For myri10ge - Myri10GE 10GbE NIC driver
LICENSE:${PN}-myri10ge-firmware-license = "Firmware-myri10ge_firmware"
FILES:${PN}-myri10ge-firmware-license = "${nonarch_base_libdir}/firmware/LICENCE.myri10ge_firmware"

LICENSE:${PN}-myri10ge = "Firmware-myri10ge_firmware"
FILES:${PN}-myri10ge = "${nonarch_base_libdir}/firmware/myri10ge_*"
RDEPENDS:${PN}-myri10ge = "${PN}-myri10ge-firmware-license"

# For smc91c92_cs - SMC 91Cxx PCMCIA
LICENSE:${PN}-smc91c92-cs = "GPL-1.0-only"
FILES:${PN}-smc91c92-cs = "${nonarch_base_libdir}/firmware/ositech/Xilinx7OD.bin*"

# For qla1280 - Qlogic QLA 1240/1x80/1x160 SCSI support
LICENSE:${PN}-qla1280-license = "Firmware-qla1280"
FILES:${PN}-qla1280-license = "${nonarch_base_libdir}/firmware/LICENCE.qla1280"

LICENSE:${PN}-qla1280 = "Firmware-qla1280"
FILES:${PN}-qla1280 = " \
    ${nonarch_base_libdir}/firmware/qlogic/1040.bin* \
    ${nonarch_base_libdir}/firmware/qlogic/1280.bin* \
    ${nonarch_base_libdir}/firmware/qlogic/12160.bin* \
"
RDEPENDS:${PN}-qla1280 = "${PN}-qla1280-license"

# For ib_qib - QLogic Infiniband
LICENSE:${PN}-ib-qib-license = "Firmware-ib_qib"
FILES:${PN}-ib-qib-license = "${nonarch_base_libdir}/firmware/LICENSE.ib_qib"

LICENSE:${PN}-ib-qib = "Firmware-ib_qib"
FILES:${PN}-ib-qib = "${nonarch_base_libdir}/firmware/qlogic/sd7220.fw*"
RDEPENDS:${PN}-ib-qib = "${PN}-ib-qib-license"

# For xhci-rcar - Renesas R-Car Gen2/3 USB 3.0 host controller driver
LICENSE:${PN}-r8a779x-usb3-license = "Firmware-r8a779x_usb3"
FILES:${PN}-r8a779x-usb3-license = "${nonarch_base_libdir}/firmware/LICENCE.r8a779x_usb3"

LICENSE:${PN}-xhci-rcar = "Firmware-r8a779x_usb3"
FILES:${PN}-xhci-rcar = "${nonarch_base_libdir}/firmware/r8a779x_usb3_*"
RDEPENDS:${PN}-xhci-rcar = "${PN}-r8a779x-usb3-license"

# For pcie-rcar-gen4 - Renesas R-Car Gen4 PCIe driver
LICENSE:${PN}-r8a779g-pcie-phy-license = "Firmware-r8a779g_pcie_phy"
FILES:${PN}-r8a779g-pcie-phy-license = "${nonarch_base_libdir}/firmware/LICENCE.r8a779g_pcie_phy"

LICENSE:${PN}-pcie-rcar = "Firmware-r8a779g_pcie_phy"
FILES:${PN}-pcie-rcar = "${nonarch_base_libdir}/firmware/rcar_gen4_pcie.bin*"
RDEPENDS:${PN}-pcie-rcar = "${PN}-r8a779g-pcie-phy-license"

# For r128 - ATI Rage 128
LICENSE:${PN}-r128 = "MIT"
FILES:${PN}-r128 = "${nonarch_base_libdir}/firmware/r128/*"

# For rt1320 - Realtek rt1320 ASoC audio driver.
LICENSE:${PN}-rt1320-license = "Firmware-rt1320"
FILES:${PN}-rt1320-license = "${nonarch_base_libdir}/firmware/LICENSE.rt1320"

LICENSE:${PN}-rt1320 = "Firmware-rt1320"
FILES:${PN}-rt1320 = "${nonarch_base_libdir}/firmware/realtek/rt1320/*"
RDEPENDS:${PN}-rt1320 = "${PN}-rt1320-license"

# For rp2 - Comtrol RocketPort 2 serial driver
LICENSE:${PN}-rp2-license = "Firmware-rp2"
FILES:${PN}-rp2-license = "${nonarch_base_libdir}/firmware/LICENSE.rp2"

LICENSE:${PN}-rp2 = "Firmware-rp2"
FILES:${PN}-rp2 = "${nonarch_base_libdir}/firmware/rp2.fw*"
RDEPENDS:${PN}-rp2 = "${PN}-rp2-license"

# For s5p-mfc - Samsung MFC video encoder/decoder driver
LICENSE:${PN}-s5p-mfc-license = "Firmware-s5p-mfc"
FILES:${PN}-s5p-mfc-license = "${nonarch_base_libdir}/firmware/LICENSE.s5p-mfc"

LICENSE:${PN}-s5p-mfc = "Firmware-s5p-mfc"
FILES:${PN}-s5p-mfc = "${nonarch_base_libdir}/firmware/s5p-mfc*"
RDEPENDS:${PN}-s5p-mfc = "${PN}-s5p-mfc-license"

# For snd-sb16-csp - Sound Blaster 16/AWE CSP support
LICENSE:${PN}-snd-sb16-csp = "GPL-2.0-or-later"
FILES:${PN}-snd-sb16-csp = "${nonarch_base_libdir}/firmware/sb16/*"

# For slicoss - Alacritech IS-NIC products
LICENSE:${PN}-alacritech-license = "Firmware-alacritech"
FILES:${PN}-alacritech-license = "${nonarch_base_libdir}/firmware/LICENCE.alacritech"

LICENSE:${PN}-slicoss = "Firmware-alacritech"
FILES:${PN}-slicoss = "${nonarch_base_libdir}/firmware/slicoss/*"
RDEPENDS:${PN}-slicoss = "${PN}-alacritech-license"

LICENSE:${PN}-sxg = "Firmware-alacritech"
FILES:${PN}-sxg = "${nonarch_base_libdir}/firmware/sxg/*"
RDEPENDS:${PN}-sxg = "${PN}-alacritech-license"

# For tehuti - Tehuti Networks 10G Ethernet
LICENSE:${PN}-tehuti-license = "Firmware-tehuti"
FILES:${PN}-tehuti-license = "${nonarch_base_libdir}/firmware/LICENSE.tehuti"

LICENSE:${PN}-tehuti = "Firmware-tehuti"
FILES:${PN}-tehuti = "${nonarch_base_libdir}/firmware/tehuti/*"
RDEPENDS:${PN}-tehuti = "${PN}-tehuti-license"

# For tg3 - Broadcom Tigon3 based gigabit Ethernet cards
LICENSE:${PN}-tigon-license = "Firmware-tigon"
FILES:${PN}-tigon-license = "${nonarch_base_libdir}/firmware/LICENCE.tigon"

LICENSE:${PN}-tg3 = "Firmware-tigon"
FILES:${PN}-tg3 = "${nonarch_base_libdir}/firmware/tigon/*"
RDEPENDS:${PN}-tg3 = "${PN}-tigon-license"

# For tlg2300 - Telgent 2300 V4L/DVB driver.
LICENSE:${PN}-tlg2300-license = "Firmware-tlg2300"
FILES:${PN}-tlg2300-license = "${nonarch_base_libdir}/firmware/LICENSE.tlg2300"

LICENSE:${PN}-tlg2300 = "Firmware-tlg2300"
FILES:${PN}-tlg2300 = "${nonarch_base_libdir}/firmware/tlg2300*"
RDEPENDS:${PN}-tlg2300 = "${PN}-tlg2300-license"

# For Mont-TSSE - Mont-TSSE(TM) Crypto Algorithm Accelerator Driver
LICENSE:${PN}-montage-license = "Firmware-montage"
FILES:${PN}-montage-license = "${nonarch_base_libdir}/firmware/LICENSE.montage"

LICENSE:${PN}-mont-tsse = "Firmware-montage"
FILES:${PN}-mont-tsse = "${nonarch_base_libdir}/firmware/tsse_firmware.bin*"
RDEPENDS:${PN}-mont-tsse = "${PN}-montage-license"

# For ueagle-atm - Driver for USB ADSL Modems based on Eagle IV Chipset
LICENSE:${PN}-ueagle-atm4-firmware-license = "Firmware-ueagle-atm4-firmware"
FILES:${PN}-ueagle-atm4-firmware-license = "${nonarch_base_libdir}/firmware/LICENCE.ueagle-atm4-firmware"

LICENSE:${PN}-ueagle-atm = "Firmware-ueagle-atm4-firmware"
FILES:${PN}-ueagle-atm = " \
    ${nonarch_base_libdir}/firmware/ueagle-atm/CMV4p.bin.v2* \
    ${nonarch_base_libdir}/firmware/ueagle-atm/DSP4p.bin* \
    ${nonarch_base_libdir}/firmware/ueagle-atm/eagleIV.fw* \
"
RDEPENDS:${PN}-ueagle-atm = "${PN}-ueagle-atm4-firmware-license"

# For usbdux/usbduxfast/usbduxsigma - usbdux data acquisition cards
LICENSE:${PN}-usbdux = "GPL-2.0-or-later"
FILES:${PN}-usbdux = "${nonarch_base_libdir}/firmware/usbdux*"

# For cx231xx - Conexant Cx23100/101/102 USB broadcast A/V decoder
LICENSE:${PN}-conexant-license = "Firmware-conexant"
FILES:${PN}-conexant-license = "${nonarch_base_libdir}/firmware/LICENSE.conexant"

LICENSE:${PN}-cx231xx = "Firmware-conexant"
FILES:${PN}-cx231xx = "${nonarch_base_libdir}/firmware/v4l-cx231xx*"
RDEPENDS:${PN}-cx231xx = "${PN}-conexant-license"

LICENSE:${PN}-cx23418 = "Firmware-conexant"
FILES:${PN}-cx23418 = "${nonarch_base_libdir}/firmware/v4l-cx23418*"
RDEPENDS:${PN}-cx23418 = "${PN}-conexant-license"

LICENSE:${PN}-cx23885 = "Firmware-conexant"
FILES:${PN}-cx23885 = "${nonarch_base_libdir}/firmware/v4l-cx23885*"
RDEPENDS:${PN}-cx23885 = "${PN}-conexant-license"

LICENSE:${PN}-cx23840 = "Firmware-conexant"
FILES:${PN}-cx23840 = "${nonarch_base_libdir}/firmware/v4l-cx25840*"
RDEPENDS:${PN}-cx23840 = "${PN}-conexant-license"

# For vxge - Exar X3100 Series 10GbE PCIe I/O Virtualized Server Adapter
LICENSE:${PN}-vxge-license = "Firmware-vxge"
FILES:${PN}-vxge-license = "${nonarch_base_libdir}/firmware/LICENSE.vxge"

LICENSE:${PN}-vxge = "Firmware-vxge"
FILES:${PN}-vxge = "${nonarch_base_libdir}/firmware/vxge/*"
RDEPENDS:${PN}-vxge = "${PN}-vxge-license"

# For whiteheat - USB ConnectTech WhiteHEAT serial device
LICENSE:${PN}-whiteheat = "GPL-2.0-only"
FILES:${PN}-whiteheat = "${nonarch_base_libdir}/firmware/whiteheat*"

LICENSE:${PN}-wil6210 = "Firmware-qualcommAthos_ath10k"
FILES:${PN}-wil6210 = "${nonarch_base_libdir}/firmware/wil6210*"
RDEPENDS:${PN}-wil6210 = "${PN}-ath10k-license"

# For xe - Intel Graphics driver
LICENSE:${PN}-xe-license = "Firmware-xe"
FILES:${PN}-xe-license = "${nonarch_base_libdir}/firmware/LICENSE.xe"

LICENSE:${PN}-xe = "Firmware-xe"
FILES:${PN}-xe = "${nonarch_base_libdir}/firmware/xe/*"
RDEPENDS:${PN}-xe = "${PN}-xe-license"

# For other firmwares
# Maybe split out to separate packages when needed.
LICENSE:${PN} = "\
    Firmware-Abilis \
    & Firmware-aeonsemi \
    & Firmware-agere \
    & Firmware-airoha \
    & Firmware-alacritech \
    & Firmware-amdgpu \
    & Firmware-amdisp \
    & Firmware-amdnpu \
    & Firmware-amd_pmf \
    & Firmware-amd-sev \
    & Firmware-amd-ucode \
    & Firmware-amlogic \
    & Firmware-amlogic_vdec \
    & Firmware-atmel \
    & Firmware-bfa \
    & Firmware-bmi260 \
    & Firmware-bnx2 \
    & Firmware-bnx2x \
    & Firmware-ca0132 \
    & Firmware-cavium \
    & Firmware-chelsio_firmware \
    & Firmware-cirrus \
    & Firmware-cnm \
    & Firmware-conexant \
    & Firmware-cw1200 \
    & Firmware-cw1200-sdd \
    & Firmware-cxgb3 \
    & Firmware-dabusb \
    & Firmware-dell \
    & Firmware-dib0700 \
    & Firmware-drxk \
    & Firmware-e100 \
    & Firmware-emi26 \
    & Firmware-ene_firmware \
    & Firmware-fw_sst_0f28 \
    & Firmware-go7007 \
    & Firmware-hfi1_firmware \
    & Firmware-HP \
    & Firmware-ib_qib \
    & Firmware-ibt_firmware \
    & Firmware-inside-secure \
    & Firmware-intel \
    & Firmware-intel_vpu \
    & Firmware-ipu3_firmware \
    & Firmware-it913x \
    & Firmware-ivsc \
    & Firmware-ixp4xx \
    & Firmware-IntcSST2 \
    & Firmware-kaweth \
    & Firmware-keyspan \
    & Firmware-lenovo \
    & Firmware-mellanox \
    & Firmware-mga \
    & Firmware-montage \
    & Firmware-moxa \
    & Firmware-myri10ge_firmware \
    & Firmware-nvidia \
    & Firmware-nxp \
    & Firmware-OLPC \
    & Firmware-ath9k-htc \
    & Firmware-phanfw \
    & Firmware-qat \
    & Firmware-qcom \
    & Firmware-qed \
    & Firmware-qla1280 \
    & Firmware-qla2xxx \
    & Firmware-r8169 \
    & Firmware-r8a779x_usb3 \
    & Firmware-radeon \
    & Firmware-ralink_a_mediatek_company_firmware \
    & Firmware-ralink-firmware \
    & Firmware-rp2 \
    & Firmware-rsi \
    & Firmware-rt1320 \
    & Firmware-imx-sdma_firmware \
    & Firmware-s5p-mfc \
    & Firmware-sensoray \
    & Firmware-siano \
    & Firmware-tehuti \
    & Firmware-ti-connectivity \
    & Firmware-ti-keystone \
    & Firmware-ti-tspa \
    & Firmware-tigon \
    & Firmware-tlg2300 \
    & Firmware-typhoon \
    & Firmware-ueagle-atm4-firmware \
    & Firmware-vxge \
    & Firmware-wl1251 \
    & Firmware-xc4000 \
    & Firmware-xc5000 \
    & Firmware-xc5000c \
    & Firmware-xe \
    & WHENCE \
"

# The goal for this recipe is to creata bunch of breakout packages for the
# firmware so that you can choose which files to include and not have to pull
# them all in.  To that end, we do want any files to be part of the
# linux-firmware package.  So set it to "".  Do not change this.
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"

FILES:${PN}-license += "${nonarch_base_libdir}/firmware/LICEN*"
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
INSANE_SKIP += "arch"

# They can also be ELF files, but obviously won't have the linkage we expect
INSANE_SKIP += "ldflags"

# Don't warn about already stripped files
INSANE_SKIP:${PN} = "already-stripped"

# No need to put firmware into the sysroot
SYSROOT_DIRS_IGNORE += "${nonarch_base_libdir}/firmware"
