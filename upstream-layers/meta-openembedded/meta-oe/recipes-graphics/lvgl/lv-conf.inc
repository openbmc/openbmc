# SPDX-FileCopyrightText: Axis Communication AB <https://www.axis.com/>
# SPDX-License-Identifier: MIT

DEPENDS += "python3-kconfiglib-native python3-pcpp-native"

SRC_URI += " \
    file://defconfig \
    ${@oe.utils.vartrue('DEBUG_BUILD', 'file://debug.cfg', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'drm', 'file://drm.cfg', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'fbdev', 'file://fbdev.cfg', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'freetype', 'file://freetype.cfg', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gridnav', 'file://gridnav.cfg', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'sdl', 'file://sdl.cfg', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'thorvg', 'file://thorvg.cfg', '', d)} \
"

PACKAGECONFIG ??= "drm"

PACKAGECONFIG[drm] = ",,libdrm libevdev"
PACKAGECONFIG[fbdev] = ",,libevdev"
PACKAGECONFIG[freetype] = ",,freetype"
PACKAGECONFIG[gridnav] = ",,"
PACKAGECONFIG[thorvg] = ",,"
PACKAGECONFIG[sdl] = ",,virtual/libsdl2 libsdl2-image"

inherit cml1 python3native

EXTRA_OECMAKE += "-DLV_BUILD_USE_KCONFIG=ON"

LVGL_FLAGS += " \
    ${@bb.utils.contains('PACKAGECONFIG', 'drm', '-I${STAGING_INCDIR}/libdrm', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'freetype', '-I${STAGING_INCDIR}/freetype2', '', d)} \
"
CFLAGS += "${LVGL_FLAGS}"
CXXFLAGS += "${LVGL_FLAGS}"

KCONFIG_CONFIG_ROOTDIR ?= "${S}"
KCONFIG_FILE ?= "${KCONFIG_CONFIG_ROOTDIR}/Kconfig"
export KCONFIG_CONFIG ?= "${KCONFIG_CONFIG_ROOTDIR}/.config"
KCONFIG_CONFIG_COMMAND ?= "menuconfig ${KCONFIG_FILE}"

do_configure() {
    cat ${UNPACKDIR}/defconfig ${@" ".join(find_cfgs(d))} > ${B}/defconfig
    defconfig --kconfig ${KCONFIG_FILE} ${B}/defconfig

    cmake_do_configure
}

# Copied from cml1.bbclass. The only modification is that
# ${KCONFIG_CONFIG_COMMAND} is not prefixed with "make" when called by
# oe_terminal.
python do_menuconfig() {
    import shutil

    if not bb.utils.to_boolean(d.getVar("KCONFIG_CONFIG_ENABLE_MENUCONFIG")):
        bb.fatal("do_menuconfig is disabled, please check KCONFIG_CONFIG_ENABLE_MENUCONFIG variable.")
        return

    config = os.path.join(d.getVar('KCONFIG_CONFIG_ROOTDIR'), ".config")
    configorig = os.path.join(d.getVar('KCONFIG_CONFIG_ROOTDIR'), ".config.orig")

    try:
        mtime = os.path.getmtime(config)
        shutil.copy(config, configorig)
    except OSError:
        mtime = 0

    # setup native pkg-config variables (kconfig scripts call pkg-config directly, cannot generically be overriden to pkg-config-native)
    d.setVar("PKG_CONFIG_DIR", "${STAGING_DIR_NATIVE}${libdir_native}/pkgconfig")
    d.setVar("PKG_CONFIG_PATH", "${PKG_CONFIG_DIR}:${STAGING_DATADIR_NATIVE}/pkgconfig")
    d.setVar("PKG_CONFIG_LIBDIR", "${PKG_CONFIG_DIR}")
    d.setVarFlag("PKG_CONFIG_SYSROOT_DIR", "unexport", "1")
    # ensure that environment variables are overwritten with this tasks 'd' values
    d.appendVar("OE_TERMINAL_EXPORTS", " PKG_CONFIG_DIR PKG_CONFIG_PATH PKG_CONFIG_LIBDIR PKG_CONFIG_SYSROOT_DIR")

    oe_terminal("sh -c '%s; if [ $? -ne 0 ]; then echo \"Command failed.\"; printf \"Press any key to continue... \"; read r; fi'" % d.getVar('KCONFIG_CONFIG_COMMAND'),
                d.getVar('PN') + ' Configuration', d)

    try:
        newmtime = os.path.getmtime(config)
    except OSError:
        newmtime = 0

    if newmtime > mtime:
        bb.plain("Changed configuration saved at:\n %s\nRecompile will be forced" % config)
        bb.build.write_taint('do_compile', d)
}

do_savedefconfig() {
    bbplain "Saving defconfig to:\n${B}/defconfig"
    savedefconfig --kconfig ${KCONFIG_FILE} --out ${B}/defconfig
}
