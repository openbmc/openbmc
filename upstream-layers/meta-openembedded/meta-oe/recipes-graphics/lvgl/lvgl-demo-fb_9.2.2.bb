SUMMARY = "LVGL Demo Application for Framebuffer"
HOMEPAGE = "https://github.com/lvgl/lv_port_linux_frame_buffer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802d3d83ae80ef5f343050bf96cce3a4 \
                    file://lvgl/LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "\
	git://github.com/lvgl/lv_port_linux_frame_buffer.git;protocol=https;branch=release/v9.2;name=demo \
	git://github.com/lvgl/lvgl;protocol=https;branch=release/v9.2;name=lvgl;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/lvgl \
	file://0001-thorvg-fix-build-with-gcc-15.patch;patchdir=lvgl \
	"
SRCREV_demo = "c924e24c7aa55317521bcd9dd75ce9337508f5a5"
SRCREV_lvgl = "7f07a129e8d77f4984fff8e623fd5be18ff42e74"
SRCREV_FORMAT = "demo_lvgl"

EXTRA_OEMAKE = "DESTDIR=${D}"

LVGL_CONFIG_DRM_CARD ?= "/dev/dri/card0"
LVGL_CONFIG_LV_USE_LOG    = "1"
LVGL_CONFIG_LV_LOG_PRINTF = "1"
LVGL_CONFIG_LV_MEM_SIZE = "(256 * 1024U)"
LVGL_CONFIG_LV_USE_FONT_COMPRESSED = "1"
require lv-conf.inc

inherit cmake


do_configure:prepend() {
	if [ "${LVGL_CONFIG_USE_SDL}" -eq 1 ] ; then
		# Add libsdl build dependency, SDL2_image has no cmake file
		sed -i '/^target_link_libraries/ s@pthread@& SDL2_image@' "${S}/CMakeLists.txt"
	fi
}

do_install:append() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/bin/main ${D}${bindir}/lvgl
}
