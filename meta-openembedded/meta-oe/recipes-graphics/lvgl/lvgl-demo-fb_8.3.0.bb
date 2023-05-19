SUMMARY = "LVGL Demo Application for Framebuffer"
HOMEPAGE = "https://github.com/lvgl/lv_port_linux_frame_buffer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802d3d83ae80ef5f343050bf96cce3a4 \
                    file://lv_drivers/LICENSE;md5=d6fc0df890c5270ef045981b516bb8f2 \
                    file://lvgl/LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "gitsm://github.com/lvgl/lv_port_linux_frame_buffer.git;branch=master;protocol=https"
SRCREV = "adf2c4490e17a1b9ec1902cc412a24b3b8235c8e"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "fbdev"

inherit features_check

EXTRA_OEMAKE = "DESTDIR=${D}"

do_install() {
	oe_runmake install
}
