#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# returns all the elements from the src uri that are .cfg files
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return sources_list

cml1_do_configure() {
	set -e
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	yes '' | oe_runmake oldconfig
}

EXPORT_FUNCTIONS do_configure

inherit terminal

OE_TERMINAL_EXPORTS += "HOST_EXTRACFLAGS HOSTLDFLAGS TERMINFO CROSS_CURSES_LIB CROSS_CURSES_INC"
HOST_EXTRACFLAGS = "${BUILD_CFLAGS} ${BUILD_LDFLAGS}"
HOSTLDFLAGS = "${BUILD_LDFLAGS}"
CROSS_CURSES_LIB = "-lncurses -ltinfo"
CROSS_CURSES_INC = '-DCURSES_LOC="<curses.h>"'
TERMINFO = "${STAGING_DATADIR_NATIVE}/terminfo"

KCONFIG_CONFIG_COMMAND ??= "menuconfig"
KCONFIG_CONFIG_ENABLE_MENUCONFIG ??= "true"
KCONFIG_CONFIG_ROOTDIR ??= "${B}"
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

    oe_terminal("sh -c 'make %s; if [ \\$? -ne 0 ]; then echo \"Command failed.\"; printf \"Press any key to continue... \"; read r; fi'" % d.getVar('KCONFIG_CONFIG_COMMAND'),
                d.getVar('PN') + ' Configuration', d)

    try:
        newmtime = os.path.getmtime(config)
    except OSError:
        newmtime = 0

    if newmtime > mtime:
        bb.plain("Changed configuration saved at:\n %s\nRecompile will be forced" % config)
        bb.build.write_taint('do_compile', d)
}
do_menuconfig[depends] += "ncurses-native:do_populate_sysroot"
do_menuconfig[nostamp] = "1"
do_menuconfig[dirs] = "${KCONFIG_CONFIG_ROOTDIR}"
addtask menuconfig after do_configure

python do_diffconfig() {
    import shutil
    import subprocess

    workdir = d.getVar('WORKDIR')
    fragment = workdir + '/fragment.cfg'
    configorig = os.path.join(d.getVar('KCONFIG_CONFIG_ROOTDIR'), ".config.orig")
    config = os.path.join(d.getVar('KCONFIG_CONFIG_ROOTDIR'), ".config")

    try:
        md5newconfig = bb.utils.md5_file(configorig)
        md5config = bb.utils.md5_file(config)
        isdiff = md5newconfig != md5config
    except IOError as e:
        bb.fatal("No config files found. Did you do menuconfig ?\n%s" % e)

    if isdiff:
        statement = 'diff --unchanged-line-format= --old-line-format= --new-line-format="%L" ' + configorig + ' ' + config + '>' + fragment
        # No need to check the exit code as we know it's going to be
        # non-zero, but that's what we expect.
        subprocess.call(statement, shell=True)

        bb.plain("Config fragment has been dumped into:\n %s" % fragment)
    else:
        if os.path.exists(fragment):
            os.unlink(fragment)
}

do_diffconfig[nostamp] = "1"
do_diffconfig[dirs] = "${KCONFIG_CONFIG_ROOTDIR}"
addtask diffconfig

do_showconfig() {
    bbplain "Config file written to ${KCONFIG_CONFIG_ROOTDIR}/.config"
}
do_showconfig[nostamp] = "1"
addtask showconfig after do_configure

do_savedefconfig() {
	bbplain "Saving defconfig to:\n${B}/defconfig"
	oe_runmake -C ${B} savedefconfig
}
do_savedefconfig[nostamp] = "1"
addtask savedefconfig after do_configure
