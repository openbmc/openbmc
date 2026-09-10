SUMMARY = "LVGL Demo Application for Framebuffer"
HOMEPAGE = "https://github.com/lvgl/lv_port_linux_frame_buffer"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802d3d83ae80ef5f343050bf96cce3a4 \
                    file://lvgl/LICENCE.txt;md5=4570b6241b4fced1d1d18eb691a0e083"

DEPENDS = "python3-pcpp-native"

PV .= "+git"

SRC_URI = "\
    git://github.com/lvgl/lv_port_linux_frame_buffer.git;protocol=https;branch=release/v9.5;name=demo \
    git://github.com/lvgl/lvgl;protocol=https;branch=release/v9.5;tag=v9.5.0;name=lvgl;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/lvgl \
"

SRCREV_demo = "045137cf0fa1781483cb796261a11b5eb21e99d9"
SRCREV_lvgl = "85aa60d18b3d5e5588d7b247abf90198f07c8a63"
SRCREV_FORMAT = "demo_lvgl"
# The version check runs against the first SRC_URI (the demo repo), whose tags
# are "vX.Y.Z" but top out at v9.2.2 -- below PV 9.5.0+git, which follows the
# bundled lvgl release. Regex is correct; marked unknown until the demo repo
# tags a >=9.5.0 release.
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"
UPSTREAM_VERSION_UNKNOWN = "1"

inherit cmake pkgconfig

require lv-conf.inc

KCONFIG_CONFIG_ROOTDIR = "${S}/lvgl"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/bin/lvglsim ${D}${bindir}
}
