SUMMARY = "Vi IMproved - enhanced vi editor"
DESCRIPTION = "Vim is a greatly improved version of the good old UNIX editor Vi. Many new features have been added: multi-level undo, syntax highlighting, command line history, on-line help, spell checking, filename completion, block operations, script language, etc. There is also a Graphical User Interface (GUI) available."
SECTION = "console/utils"

HOMEPAGE = "https://www.vim.org/"
BUGTRACKER = "https://github.com/vim/vim/issues"

DEPENDS = "ncurses gettext-native"
# vimdiff doesn't like busybox diff
RSUGGESTS:${PN} = "diffutils"

LICENSE = "Vim"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6b30ea4fa660c483b619924bc709ef99"

SRC_URI = "git://github.com/vim/vim.git;branch=master;protocol=https \
           file://disable_acl_header_check.patch \
           file://vim-add-knob-whether-elf.h-are-checked.patch \
           file://0001-src-Makefile-improve-reproducibility.patch \
           file://no-path-adjust.patch \
           "

PV .= ".1592"
SRCREV = "29b4c513b11deb37f0e0538df53d195f602fa42c"

# Remove when 8.3 is out
UPSTREAM_VERSION_UNKNOWN = "1"

# Do not consider .z in x.y.z, as that is updated with every commit
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+)\.0"

S = "${WORKDIR}/git"

VIMDIR = "vim${@d.getVar('PV').split('.')[0]}${@d.getVar('PV').split('.')[1]}"

inherit autotools-brokensep update-alternatives mime-xdg pkgconfig

CLEANBROKEN = "1"

# vim configure.in contains functions which got 'dropped' by autotools.bbclass
do_configure () {
    cd src
    rm -f auto/*
    touch auto/config.mk
    aclocal
    autoconf
    cd ..
    oe_runconf
    touch src/auto/configure
    touch src/auto/config.mk src/auto/config.h
}

do_compile() {
    # We do not support fully / correctly the following locales.  Attempting
    # to use these with msgfmt in order to update the ".desktop" files exposes
    # this problem and leads to the compile failing.
    for LOCALE in cs fr ko pl sk zh_CN zh_TW;do
        echo -n > src/po/${LOCALE}.po
    done
    autotools_do_compile
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
