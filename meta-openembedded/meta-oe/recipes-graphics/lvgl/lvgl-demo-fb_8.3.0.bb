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
PACKAGECONFIG[drm] = ",,libdrm"
PACKAGECONFIG[fbdev] = ",,"
PACKAGECONFIG[sdl] = ",,virtual/libsdl2"
LVGL_CONFIG_USE_DRM = "${@bb.utils.contains('PACKAGECONFIG', 'drm', '1', '0', d)}"
LVGL_CONFIG_DRM_CARD ?= "/dev/dri/card0"
LVGL_CONFIG_USE_FBDEV = "${@bb.utils.contains('PACKAGECONFIG', 'fbdev', '1', '0', d)}"
LVGL_CONFIG_USE_SDL = "${@bb.utils.contains('PACKAGECONFIG', 'sdl', '1', '0', d)}"

inherit cmake

S = "${WORKDIR}/git"

EXTRA_OECMAKE += "-Dinstall:BOOL=ON -DLIB_INSTALL_DIR=${baselib}"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/libdrm"

do_configure:prepend() {
	sed -i -e "s|\(^#  define USE_FBDEV \).*|#  define USE_FBDEV ${LVGL_CONFIG_USE_FBDEV}|g" \
		-e "s|\(^#  define USE_DRM \).*|#  define USE_DRM ${LVGL_CONFIG_USE_DRM}|g" \
		-e "s|\(^#  define DRM_CARD \).*|#  define DRM_CARD \"${LVGL_CONFIG_DRM_CARD}\"|g" \
		-e "s|\(^# define USE_SDL \).*|#  define USE_SDL ${LVGL_CONFIG_USE_SDL}|g" \
		-e "s|\(^#  define USE_SDL_GPU \).*|#  define USE_SDL_GPU 1|g" \
		-e "s|\(^#  define SDL_DOUBLE_BUFFERED \).*|#  define SDL_DOUBLE_BUFFERED 1|g" \
	"${S}/lv_drv_conf.h"

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
