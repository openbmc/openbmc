SUMMARY = "LVGL Demo Application for Framebuffer"
HOMEPAGE = "https://github.com/lvgl/lv_port_linux_frame_buffer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802d3d83ae80ef5f343050bf96cce3a4 \
                    file://lvgl/LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "\
	git://github.com/lvgl/lv_port_linux_frame_buffer.git;protocol=https;branch=master;name=demo \
	git://github.com/lvgl/lvgl;protocol=https;branch=master;name=lvgl;subdir=git/lvgl \
	file://0002-fix-sdl-handle-both-LV_IMAGE_SRC_FILE-and-LV_IMAGE_S.patch;patchdir=lvgl \
	file://0003-Make-fbdev-device-node-runtime-configurable-via-envi.patch \
	file://0004-Factor-out-fbdev-initialization-code.patch \
	file://0005-Add-DRM-KMS-example-support.patch \
	file://0006-Add-SDL2-example-support.patch \
	file://0007-fix-cmake-generate-versioned-shared-libraries.patch;patchdir=lvgl \
	file://0008-fix-fbdev-set-resolution-prior-to-buffer.patch;patchdir=lvgl \
	"
SRCREV_demo = "dccc6a1ca48372aa993dbea7a8e17dec6f42df6a"
SRCREV_lvgl = "e1c0b21b2723d391b885de4b2ee5cc997eccca91"
SRCREV_FORMAT = "demo_lvgl"

EXTRA_OEMAKE = "DESTDIR=${D}"

LVGL_CONFIG_DRM_CARD ?= "/dev/dri/card0"
LVGL_CONFIG_LV_USE_LOG    = "1"
LVGL_CONFIG_LV_LOG_PRINTF = "1"
LVGL_CONFIG_LV_MEM_SIZE = "(256 * 1024U)"
LVGL_CONFIG_LV_USE_FONT_COMPRESSED = "1"
require lv-conf.inc

inherit cmake

S = "${WORKDIR}/git"

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
