
SRC_URI_append_minized-zynq7 = " \
       git://github.com/murata-wireless/cyw-fmac-nvram;protocol=git;branch=orga;destsuffix=cyw-fmac-nvram;name=cyw-fmac-nvram \
       git://github.com/murata-wireless/cyw-bt-patch;protocol=git;branch=morty-orga;destsuffix=cyw-bt-patch;name=cyw-bt-patch \
       git://github.com/murata-wireless/cyw-fmac-utils-imx32;protocol=git;branch=orga;destsuffix=cyw-fmac-utils-imx32;name=cyw-fmac-utils-imx32 \
"

SRCREV_cyw-fmac-nvram = "d12c2ac1b93941eaa03063bb7cb3c1ee1aa1b7d0"
SRCREV_cyw-bt-patch = "9216e0d9f778009b5667d032886dfd49174c4b3a"
SRCREV_cyw-fmac-utils-imx32 = "060688dfe76df98751207c8146268ce7fd80b6ab"
SRCREV_FORMAT = "default+cyw-fmac-nvram+cyw-bt-patch+cyw-fmac-utils-imx32"

do_install_append_minized-zynq7() {

       install -d ${D}${bindir}

       install -m 0644 ${WORKDIR}/cyw-fmac-nvram/brcmfmac43430-sdio.txt ${D}${nonarch_base_libdir}/firmware/brcm/
       install -m 0644 ${WORKDIR}/cyw-bt-patch/CYW43430A1.1DX.hcd ${D}${nonarch_base_libdir}/firmware/brcm/
       install -m 0644 ${WORKDIR}/cyw-fmac-utils-imx32/wl ${D}${bindir}
}

PACKAGES_prepend_minized-zynq7 = "\
    ${PN}-bcm43430a1-hcd \
    ${PN}-mfgtest \
"

FILES_${PN}-mfgtest_minized-zynq7 = " \
       ${bindir}/wl \
"

FILES_${PN}-bcm43430_append_minized-zynq7 = " \
       ${nonarch_base_libdir}/firmware/brcm/ brcmfmac43430-sdio.txt \
"

LICENSE_${PN}-bcm43430a1-hcd = "Firmware-cypress"

FILES_${PN}-bcm43430a1-hcd = " \
  ${nonarch_base_libdir}/firmware/brcm/BCM43430A1.1DX.hcd \
"

RDEPENDS_${PN}-bcm43430a1-hcd += "${PN}-cypress-license"
