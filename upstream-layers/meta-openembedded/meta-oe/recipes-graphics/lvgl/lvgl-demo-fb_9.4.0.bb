SUMMARY = "LVGL Demo Application for Framebuffer"
HOMEPAGE = "https://github.com/lvgl/lv_port_linux_frame_buffer"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802d3d83ae80ef5f343050bf96cce3a4 \
                    file://lvgl/LICENCE.txt;md5=4570b6241b4fced1d1d18eb691a0e083"

DEPENDS = "python3-pcpp-native"

PV .= "+git"

SRC_URI = "\
    git://github.com/lvgl/lv_port_linux_frame_buffer.git;protocol=https;branch=release/v9.4;name=demo \
    git://github.com/lvgl/lvgl;protocol=https;branch=release/v9.4;tag=v9.4.0;name=lvgl;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/lvgl \
"

SRCREV_demo = "71050624acd8a52ab7d365d0d12acf9bf5fe41db"
SRCREV_lvgl = "c016f72d4c125098287be5e83c0f1abed4706ee5"
SRCREV_FORMAT = "demo_lvgl"

inherit cmake pkgconfig

require lv-conf.inc

KCONFIG_CONFIG_ROOTDIR = "${S}/lvgl"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/bin/lvglsim ${D}${bindir}
}
