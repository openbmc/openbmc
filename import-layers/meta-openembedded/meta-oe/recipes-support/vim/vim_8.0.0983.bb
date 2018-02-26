SUMMARY = "Vi IMproved - enhanced vi editor"
SECTION = "console/utils"
DEPENDS = "ncurses gettext-native"
# vimdiff doesn't like busybox diff
RSUGGESTS_${PN} = "diffutils"
LICENSE = "vim"
LIC_FILES_CHKSUM = "file://../runtime/doc/uganda.txt;md5=eea32ac1424bba14096736a494ae9045"

SRC_URI = "git://github.com/vim/vim.git \
           file://disable_acl_header_check.patch;patchdir=.. \
           file://vim-add-knob-whether-elf.h-are-checked.patch;patchdir=.. \
"
SRCREV = "3f9a1ff141412e9e85f7dff47d02946cb9be9228"

S = "${WORKDIR}/git/src"

VIMDIR = "vim${@d.getVar('PV').split('.')[0]}${@d.getVar('PV').split('.')[1]}"

inherit autotools update-alternatives
inherit autotools-brokensep

# vim configure.in contains functions which got 'dropped' by autotools.bbclass
do_configure () {
    rm -f auto/*
    touch auto/config.mk
    aclocal
    autoconf
    oe_runconf
    touch auto/configure
    touch auto/config.mk auto/config.h
}

#Available PACKAGECONFIG options are gtkgui, acl, x11, tiny
PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'acl selinux', d)}"

PACKAGECONFIG[gtkgui] = "--enable-gtk2-test --enable-gui=gtk2,--enable-gui=no,gtk+,"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[x11] = "--with-x,--without-x,xt,"
PACKAGECONFIG[tiny] = "--with-features=tiny,--with-features=big,,"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,"
PACKAGECONFIG[elfutils] = "--enable-elf-check,,elfutils,"

EXTRA_OECONF = " \
    --disable-gpm \
    --disable-gtktest \
    --disable-xim \
    --disable-netbeans \
    --with-tlib=ncurses \
    ac_cv_small_wchar_t=no \
    vim_cv_getcwd_broken=no \
    vim_cv_memmove_handles_overlap=yes \
    vim_cv_stat_ignores_slash=no \
    vim_cv_terminfo=yes \
    vim_cv_tgent=non-zero \
    vim_cv_toupper_broken=no \
    vim_cv_tty_group=world \
    STRIP=/bin/true \
"

do_install() {
    autotools_do_install

    # Work around file-rdeps picking up csh, awk, perl or python as a dep
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/vim132
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/mve.awk
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/*.pl
    chmod -x ${D}${datadir}/${BPN}/${VIMDIR}/tools/*.py

    # Install example vimrc from runtime files
    install -m 0644 ../runtime/vimrc_example.vim ${D}/${datadir}/${BPN}/vimrc

    # we use --with-features=big as default
    mv ${D}${bindir}/${BPN} ${D}${bindir}/${BPN}.${BPN}
}

PARALLEL_MAKEINST = ""

PACKAGES =+ "${PN}-common ${PN}-syntax ${PN}-help ${PN}-tutor ${PN}-vimrc ${PN}-tools"
FILES_${PN}-syntax = "${datadir}/${BPN}/${VIMDIR}/syntax"
FILES_${PN}-help = "${datadir}/${BPN}/${VIMDIR}/doc"
FILES_${PN}-tutor = "${datadir}/${BPN}/${VIMDIR}/tutor ${bindir}/${BPN}tutor"
FILES_${PN}-vimrc = "${datadir}/${BPN}/vimrc"
FILES_${PN}-data = "${datadir}/${BPN}"
FILES_${PN}-tools = "${datadir}/${BPN}/${VIMDIR}/tools"
FILES_${PN}-common = " \
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

RDEPENDS_${BPN} = "ncurses-terminfo-base"
# Recommend that runtime data is installed along with vim
RRECOMMENDS_${BPN} = "${PN}-syntax ${PN}-help ${PN}-tutor ${PN}-vimrc ${PN}-common"

ALTERNATIVE_${PN} = "vi vim"
ALTERNATIVE_TARGET = "${bindir}/${BPN}.${BPN}"
ALTERNATIVE_LINK_NAME[vi] = "${base_bindir}/vi"
ALTERNATIVE_LINK_NAME[vim] = "${bindir}/vim"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
