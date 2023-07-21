SUMMARY = "LVGL Demo Application for Framebuffer"
HOMEPAGE = "https://github.com/lvgl/lv_port_linux_frame_buffer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=802d3d83ae80ef5f343050bf96cce3a4 \
                    file://lv_drivers/LICENSE;md5=d6fc0df890c5270ef045981b516bb8f2 \
                    file://lvgl/LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "gitsm://github.com/lvgl/lv_port_linux_frame_buffer.git;branch=master;protocol=https"
SRCREV = "adf2c4490e17a1b9ec1902cc412a24b3b8235c8e"

EXTRA_OEMAKE = "DESTDIR=${D}"

PACKAGECONFIG ??= "drm"
require lv-drivers.inc

inherit cmake

S = "${WORKDIR}/git"

TARGET_CFLAGS += "-I${STAGING_INCDIR}/libdrm"

do_configure:prepend() {
	if [ "${LVGL_CONFIG_USE_DRM}" -eq 1 ] ; then
		# Add libdrm build dependency
		sed -i '/^target_link_libraries/ s@lvgl::drivers@& drm@' "${S}/CMakeLists.txt"
		# Switch from fbdev to drm usage
		sed -i 's@fbdev@drm@g' "${S}/main.c"
		# Pull resolution from DRM instead of hardcoding it
		sed -i '/disp_drv.hor_res/ d' "${S}/main.c"
		sed -i '/disp_drv.ver_res/ s@disp_drv.ver_res.*@drm_get_sizes(\&disp_drv.hor_res, \&disp_drv.ver_res, NULL);@' "${S}/main.c"
	fi

	if [ "${LVGL_CONFIG_USE_SDL}" -eq 1 ] ; then
		# Add libsdl build dependency
		sed -i '/^target_link_libraries/ s@lvgl::drivers@& SDL2@' "${S}/CMakeLists.txt"
		# Switch from fbdev to sdl usage
		sed -i 's@fbdev_flush@sdl_display_flush@g' "${S}/main.c"
		sed -i 's@lv_drivers/display/fbdev.h@lv_drivers/sdl/sdl.h@g' "${S}/main.c"
		sed -i 's@fbdev@sdl@g' "${S}/main.c"
	fi
}

do_install:append() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/lvgl_fb ${D}${bindir}/
}
