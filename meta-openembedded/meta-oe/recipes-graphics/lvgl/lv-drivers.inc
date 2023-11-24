PACKAGECONFIG[drm] = ",,libdrm"
PACKAGECONFIG[fbdev] = ",,"
PACKAGECONFIG[sdl] = ",,virtual/libsdl2"
PACKAGECONFIG[wayland] = ",,libxkbcommon wayland"

LVGL_CONFIG_USE_DRM = "${@bb.utils.contains('PACKAGECONFIG', 'drm', '1', '0', d)}"
LVGL_CONFIG_DRM_CARD ?= "/dev/dri/card0"

LVGL_CONFIG_USE_EVDEV = "${@bb.utils.contains_any('PACKAGECONFIG', 'drm fbdev', '1', '0', d)}"
LVGL_CONFIG_EVDEV_INPUT ?= "/dev/input/touchscreen"

LVGL_CONFIG_USE_FBDEV = "${@bb.utils.contains('PACKAGECONFIG', 'fbdev', '1', '0', d)}"

LVGL_CONFIG_USE_SDL = "${@bb.utils.contains('PACKAGECONFIG', 'sdl', '1', '0', d)}"

LVGL_CONFIG_USE_WAYLAND = "${@bb.utils.contains('PACKAGECONFIG', 'wayland', '1', '0', d)}"
LVGL_CONFIG_WAYLAND_HOR_RES ?= "480"
LVGL_CONFIG_WAYLAND_VER_RES ?= "320"

ALLOW_EMPTY:${PN} = "1"

EXTRA_OECMAKE += "-Dinstall:BOOL=ON -DLIB_INSTALL_DIR=${baselib}"

do_configure:append() {
    # If there is a configuration template, start from that
    [ -r "${S}/lv_drv_conf_template.h" ] && cp -Lv "${S}/lv_drv_conf_template.h" "${S}/lv_drv_conf.h"

    # Configure the software using sed
    sed -e "s|#if 0 .*Set it to \"1\" to enable the content.*|#if 1 // Enabled by ${PN}|g" \
        \
        -e "s|\(^#  define USE_DRM \).*|#  define USE_DRM ${LVGL_CONFIG_USE_DRM}|g" \
        -e "s|\(^#  define DRM_CARD \).*|#  define DRM_CARD \"${LVGL_CONFIG_DRM_CARD}\"|g" \
	\
        -e "s|\(^#  define USE_EVDEV \).*|#  define USE_EVDEV ${LVGL_CONFIG_USE_EVDEV}|g" \
        -e "s|\(^#  define EVDEV_NAME \).*|#  define EVDEV_NAME \"${LVGL_CONFIG_EVDEV_INPUT}\"|g" \
        \
        -e "s|\(^#  define USE_FBDEV \).*|#  define USE_FBDEV ${LVGL_CONFIG_USE_FBDEV}|g" \
        \
        -e "s|\(^# define USE_SDL \).*|#  define USE_SDL ${LVGL_CONFIG_USE_SDL}|g" \
        -e "s|\(^#  define USE_SDL_GPU \).*|#  define USE_SDL_GPU 1|g" \
        -e "s|\(^#  define SDL_DOUBLE_BUFFERED \).*|#  define SDL_DOUBLE_BUFFERED 1|g" \
        \
        -e "s|\(^#  define USE_WAYLAND \).*|#  define USE_WAYLAND ${LVGL_CONFIG_USE_WAYLAND}|g" \
        -e "s|\(^ *# *define *WAYLAND_HOR_RES *\).*|\1${LVGL_CONFIG_WAYLAND_HOR_RES}|g" \
        -e "s|\(^ *# *define *WAYLAND_VER_RES *\).*|\1${LVGL_CONFIG_WAYLAND_VER_RES}|g" \
        \
        -i "${S}/lv_drv_conf.h"
}
