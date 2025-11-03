SUMMARY = "Vi IMproved - enhanced vi editor"
DESCRIPTION = "Vim is a greatly improved version of the good old UNIX editor Vi. Many new features have been added: multi-level undo, syntax highlighting, command line history, on-line help, spell checking, filename completion, block operations, script language, etc. There is also a Graphical User Interface (GUI) available."
SECTION = "console/utils"

HOMEPAGE = "https://www.vim.org/"
BUGTRACKER = "https://github.com/vim/vim/issues"

DEPENDS = "ncurses gettext-native"
# vimdiff doesn't like busybox diff
RSUGGESTS:${PN} = "diffutils"

LICENSE = "Vim"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d1a651ab770b45d41c0f8cb5a8ca930e"

SRC_URI = "git://github.com/vim/vim.git;branch=master;protocol=https \
           file://disable_acl_header_check.patch \
           file://0001-src-Makefile-improve-reproducibility.patch \
           file://no-path-adjust.patch \
           "

PV .= ".1683"
SRCREV = "b922b30cfe4c044c83bac3cc908084ed20a83598"

# Do not consider .z in x.y.z, as that is updated with every commit
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+)\.0"
# Ignore that the upstream version .z in x.y.z is always newer
UPSTREAM_VERSION_UNKNOWN = "1"

S = "${WORKDIR}/git"

VIMDIR = "vim${@d.getVar('PV').split('.')[0]}${@d.getVar('PV').split('.')[1]}"

inherit autotools-brokensep update-alternatives mime-xdg pkgconfig

CLEANBROKEN = "1"

# vim configure.in contains functions which got 'dropped' by autotools.bbclass
do_configure () {
    cd src
    rm -rf auto/*
    touch auto/config.mk
    # git timestamps aren't reliable, so touch the shipped .po files so they aren't regenerated
    touch -c po/cs.cp1250.po po/ja.euc-jp.po po/ja.sjis.po po/ko.po po/pl.UTF-8.po po/pl.cp1250.po po/ru.cp1251.po po/sk.cp1250.po po/uk.cp1251.po po/zh_CN.po po/zh_CN.cp936.po po/zh_TW.po
    # ru.cp1251.po uses CP1251 rather than cp1251, fix that
    sed -i -e s/CP1251/cp1251/ po/ru.cp1251.po
    aclocal
    autoconf
    cd ..
    oe_runconf
    touch src/auto/configure
    touch src/auto/config.mk src/auto/config.h
    # need a native tool, not a target one
    ${BUILD_CC} src/po/sjiscorr.c -o src/po/sjiscorr
}

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl selinux', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 gtkgui', '', d)} \
    nls \
"

PACKAGECONFIG[gtkgui] = "--enable-gui=gtk3,--enable-gui=no,gtk+3"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[x11] = "--with-x,--without-x,xt,"
PACKAGECONFIG[tiny] = "--with-features=tiny,--with-features=big,,"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,"
PACKAGECONFIG[elfutils] = "--enable-elf-check,,elfutils,"
PACKAGECONFIG[nls] = "--enable-nls,--disable-nls,,"
PACKAGECONFIG[sound] = "--enable-canberra,--disable-canberra,canberra,"

EXTRA_OECONF = " \
    --disable-gpm \
    --disable-gtktest \
    --disable-xim \
    --disable-netbeans \
    --disable-desktop-database-update \
    --with-tlib=ncurses \
    --with-modified-by='${MAINTAINER}' \
    --with-wayland=no \
    ac_cv_small_wchar_t=no \
    ac_cv_path_GLIB_COMPILE_RESOURCES=no \
    vim_cv_getcwd_broken=no \
    vim_cv_memmove_handles_overlap=yes \
    vim_cv_stat_ignores_slash=no \
    vim_cv_terminfo=yes \
    vim_cv_tgetent=non-zero \
    vim_cv_toupper_broken=no \
    vim_cv_tty_group=world \
    STRIP=/bin/true \
"

# Some host distros don't have it, disable consistently
EXTRA_OECONF:append:class-native = " vim_cv_timer_create=no"
EXTRA_OECONF:append:class-target = " vim_cv_timer_create=yes"

do_install() {
    autotools_do_install

    # Work around file-rdeps picking up csh, awk, perl or python as a dep
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/vim132
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/mve.awk
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/*.pl
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/*.py
    
    # Install example vimrc from runtime files
    install -m 0644 runtime/vimrc_example.vim ${D}/${datadir}/${BPN}/vimrc

    # we use --with-features=big as default
    mv ${D}${bindir}/${BPN} ${D}${bindir}/${BPN}.${BPN}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)}; then
	# The mouse being autoenabled is just annoying in xfce4-terminal (mouse
	# drag make vim go into visual mode and there is no right click menu),
	# delete the block.
	sed -i '/the mouse works just fine/,+4d' ${D}/${datadir}/${BPN}/vimrc
    fi
}

PARALLEL_MAKEINST = ""

PACKAGES =+ "${PN}-common ${PN}-syntax ${PN}-help ${PN}-tutor ${PN}-vimrc ${PN}-tools"
FILES:${PN}-syntax = "${datadir}/${BPN}/${VIMDIR}/syntax"
FILES:${PN}-help = "${datadir}/${BPN}/${VIMDIR}/doc"
FILES:${PN}-tutor = "${datadir}/${BPN}/${VIMDIR}/tutor ${bindir}/${BPN}tutor"
FILES:${PN}-vimrc = "${datadir}/${BPN}/vimrc"
FILES:${PN}-data = "${datadir}/${BPN}"

# We do not want to complain if perl or gawk are not on the target.
#
FILES:${PN}-tools = "${datadir}/${BPN}/${VIMDIR}/tools"
INSANE_SKIP:${PN}-tools = "file-rdeps"

FILES:${PN}-common = " \
    ${datadir}/${BPN}/${VIMDIR}/*.vim \
    ${datadir}/${BPN}/${VIMDIR}/autoload \
    ${datadir}/${BPN}/${VIMDIR}/colors \
    ${datadir}/${BPN}/${VIMDIR}/compiler \
    ${datadir}/${BPN}/${VIMDIR}/ftplugin \
    ${datadir}/${BPN}/${VIMDIR}/indent \
    ${datadir}/${BPN}/${VIMDIR}/keymap \
    ${datadir}/${BPN}/${VIMDIR}/lang \
    ${datadir}/${BPN}/${VIMDIR}/macros \
    ${datadir}/${BPN}/${VIMDIR}/plugin \
    ${datadir}/${BPN}/${VIMDIR}/print \
    ${datadir}/${BPN}/${VIMDIR}/spell \
    ${datadir}/icons \
"

ALTERNATIVE:${PN} = "vi vim"
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_TARGET = "${bindir}/${BPN}.${BPN}"
ALTERNATIVE_LINK_NAME[vi] = "${base_bindir}/vi"
ALTERNATIVE_LINK_NAME[vim] = "${bindir}/vim"
