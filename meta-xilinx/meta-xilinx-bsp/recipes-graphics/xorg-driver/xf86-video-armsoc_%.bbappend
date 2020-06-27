FILESEXTRAPATHS_prepend := "${THISDIR}/xf86-video-armsoc:"

SRC_URI_append = " file://0001-src-drmmode_xilinx-Add-the-dumb-gem-support-for-Xili.patch \
                   file://0001-armsoc_driver.c-Bypass-the-exa-layer-to-free-the-roo.patch \
		"
