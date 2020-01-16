DESCRIPTION = "A fast and lightweight IDE"
HOMEPAGE = "http://plugins.geany.org/"

LICENSE_DEFAULT = "GPLv2"
LICENSE = "${LICENSE_DEFAULT} & BSD-2-Clause & GPLv3"

python () {
    for plugin in d.getVar('PLUGINS').split():
        if 'LICENSE_%s' % plugin not in d:
            d.setVar('LICENSE_' + plugin, '${LICENSE_DEFAULT}')
}

DEPENDS = " \
    vala-native \
    fribidi \
    geany \
    libxml2 \
    libsoup-2.4 \
    enchant2 \
    intltool-native \
    libassuan \
    gpgme \
    vte \
    libgit2 \
"

inherit features_check autotools pkgconfig gtk-icon-cache

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = " \
    https://plugins.geany.org/${BPN}/${BP}.tar.bz2 \
    file://0001-Use-pkg-config-to-find-gpgme.patch \
"
SRC_URI[md5sum] = "91fb4634953702f914d9105da7048a33"
SRC_URI[sha256sum] = "ebe18dd699292174622e8cb8745b020ada8a5be3b604ab980af36e8518df7ce6"

do_configure_prepend() {
    rm -f ${S}/build/cache/glib-gettext.m4
}

FILES_${PN} += "${datadir}/icons"
FILES_${PN}-dev += "${libdir}/geany/*.la ${libdir}/${BPN}/*/*.la"

PLUGINS += "${PN}-addons"
LIC_FILES_CHKSUM += "file://addons/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-addons = "${libdir}/geany/addons.so"

PLUGINS += "${PN}-autoclose"
LIC_FILES_CHKSUM += "file://autoclose/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-autoclose = "${libdir}/geany/autoclose.so"

PLUGINS += "${PN}-automark"
LIC_FILES_CHKSUM += "file://automark/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-automark = "${libdir}/geany/automark.so"

PLUGINS += "${PN}-codenav"
LIC_FILES_CHKSUM += "file://codenav/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-codenav = "${libdir}/geany/codenav.so"

PLUGINS += "${PN}-commander"
LIC_FILES_CHKSUM += "file://commander/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-commander = "GPLv3"
FILES_${PN}-commander = "${libdir}/geany/commander.so"

# | checking whether the GTK version in use is compatible with plugin Debugger... no
EXTRA_OECONF += "--disable-debugger"
#PLUGINS += "${PN}-debugger"
#LIC_FILES_CHKSUM += "file://debugger/COPYING;md5=4325afd396febcb659c36b49533135d4"
#FILES_${PN}-debugger = "${libdir}/geany/debugger.so ${datadir}/${PN}/debugger"

PLUGINS += "${PN}-defineformat"
LIC_FILES_CHKSUM += "file://defineformat/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-defineformat = "${libdir}/geany/defineformat.so"

# no gnome devhelp in some common layer
EXTRA_OECONF += "--disable-devhelp"
#PLUGINS += "${PN}-devhelp"
#LIC_FILES_CHKSUM += "file://devhelp/COPYING;md5=d32239bcb673463ab874e80d47fae504"
#LICENSE_${PN}-devhelp = "GPLv3"
#FILES_${PN}-devhelp = "${libdir}/geany/devhelp.so"

PLUGINS += "${PN}-geanyctags"
LIC_FILES_CHKSUM += "file://geanyctags/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyctags = "${libdir}/geany/geanyctags.so"

PLUGINS += "${PN}-geanydoc"
LIC_FILES_CHKSUM += "file://geanydoc/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-geanydoc = "GPLv3"
FILES_${PN}-geanydoc = "${libdir}/geany/geanydoc.so"

PLUGINS += "${PN}-geanyextrasel"
LIC_FILES_CHKSUM += "file://geanyextrasel/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyextrasel = "${libdir}/geany/geanyextrasel.so"

PLUGINS += "${PN}-geanyinsertnum"
LIC_FILES_CHKSUM += "file://geanyinsertnum/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyinsertnum = "${libdir}/geany/geanyinsertnum.so"

# no lua: max supported version is 5.2
EXTRA_OECONF += "--disable-geanylua"
#PLUGINS += "${PN}-geanylua"
#LIC_FILES_CHKSUM += "file://geanylua/COPYING;md5=4325afd396febcb659c36b49533135d4"
#FILES_${PN}-geanylua = "${libdir}/geany/geanylua.so ${libdir}/${PN}/geanylua/*.so"

PLUGINS += "${PN}-geanymacro"
LIC_FILES_CHKSUM += "file://geanymacro/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanymacro = "${libdir}/geany/geanymacro.so"

PLUGINS += "${PN}-geanyminiscript"
LIC_FILES_CHKSUM += "file://geanyminiscript/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-geanyminiscript = "${libdir}/geany/geanyminiscript.so"

PLUGINS += "${PN}-geanynumberedbookmarks"
LIC_FILES_CHKSUM += "file://geanynumberedbookmarks/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanynumberedbookmarks = "${libdir}/geany/geanynumberedbookmarks.so"

PLUGINS += "${PN}-geanypg"
LIC_FILES_CHKSUM += "file://geanypg/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
FILES_${PN}-geanypg = "${libdir}/geany/geanypg.so"

PLUGINS += "${PN}-geanyprj"
LIC_FILES_CHKSUM += "file://geanyprj/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-geanyprj = "GPLv3"
FILES_${PN}-geanyprj = "${libdir}/geany/geanyprj.so"

#PLUGINS += "${PN}-geanypy"
#LIC_FILES_CHKSUM += "file://geanypy/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
#FILES_${PN}-geanypy = "${libdir}/geany/geanypy.so"

PLUGINS += "${PN}-geanyvc"
LIC_FILES_CHKSUM += "file://geanyvc/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyvc = "${libdir}/geany/geanyvc.so"

PLUGINS += "${PN}-geniuspaste"
LIC_FILES_CHKSUM += "file://geniuspaste/COPYING;md5=bfc203269f8862ebfc1198cdc809a95a"
FILES_${PN}-geniuspaste = "${libdir}/geany/geniuspaste.so ${datadir}/${PN}/geniuspaste"

PLUGINS += "${PN}-git-changebar"
LIC_FILES_CHKSUM += "file://git-changebar/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-git-changebar = "GPLv3"
FILES_${PN}-git-changebar = "${datadir}/${BPN}/git-changebar ${libdir}/geany/git-changebar.so"

PLUGINS += "${PN}-keyrecord"
LIC_FILES_CHKSUM += "file://keyrecord/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-keyrecord = "${libdir}/geany/keyrecord.so"

PLUGINS += "${PN}-latex"
LIC_FILES_CHKSUM += "file://latex/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-latex = "${libdir}/geany/latex.so"

PLUGINS += "${PN}-lineoperations"
LIC_FILES_CHKSUM += "file://lineoperations/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-lineoperations = "${libdir}/geany/lineoperations.so"

PLUGINS += "${PN}-lipsum"
LIC_FILES_CHKSUM += "file://lipsum/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-lipsum = "${libdir}/geany/lipsum.so"

# no markdown - avoid floating dependencies
EXTRA_OECONF += "--disable-peg-markdown"
#PLUGINS += "${PN}-markdown"
#LIC_FILES_CHKSUM += "file://markdown/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
#FILES_${PN}-markdown = "${libdir}/geany/markdown.so"

# | checking whether the GTK version in use is compatible with plugin multiterm... no
EXTRA_OECONF += "--disable-multiterm"
#PLUGINS += "${PN}-multiterm"
#LIC_FILES_CHKSUM += "file://multiterm/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
#FILES_${PN}-multiterm = "${libdir}/geany/multiterm.so"

PLUGINS += "${PN}-overview"
LIC_FILES_CHKSUM += "file://overview/overview/overviewplugin.c;beginline=4;endline=20;md5=1aa33522916cdeb46cccac0c629da0d0"
FILES_${PN}-overview = "${libdir}/geany/overview.so ${datadir}/${PN}/overview"

PLUGINS += "${PN}-pairtaghighlighter"
LICENSE_${PN}-pairtaghighlighter = "BSD-2-Clause"
LIC_FILES_CHKSUM += "file://pairtaghighlighter/COPYING;md5=d6d927525a612b3a8dbebc4b2e9b47c1"
FILES_${PN}-pairtaghighlighter = "${libdir}/geany/pairtaghighlighter.so"

PLUGINS += "${PN}-pohelper"
LICENSE_${PN}-pohelper = "GPLv3"
LIC_FILES_CHKSUM += "file://pohelper/COPYING;md5=d32239bcb673463ab874e80d47fae504"
FILES_${PN}-pohelper = "${datadir}/${BPN}/pohelper ${libdir}/geany/pohelper.so"

PLUGINS += "${PN}-pretty-printer"
LIC_FILES_CHKSUM += "file://pretty-printer/src/PrettyPrinter.c;beginline=1;endline=17;md5=1665115c2fadb17c1b53cdb4e43b2440"
FILES_${PN}-pretty-printer = "${libdir}/geany/pretty-printer.so"

PLUGINS += "${PN}-projectorganizer"
LIC_FILES_CHKSUM += "file://projectorganizer/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-projectorganizer = "${libdir}/geany/projectorganizer.so"

PLUGINS += "${PN}-scope"
LIC_FILES_CHKSUM += "file://scope/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-scope = "${datadir}/${BPN}/scope ${libdir}/geany/scope.so"

PLUGINS += "${PN}-sendmail"
LIC_FILES_CHKSUM += "file://sendmail/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-sendmail = "${libdir}/geany/sendmail.so"

PLUGINS += "${PN}-shiftcolumn"
LIC_FILES_CHKSUM += "file://shiftcolumn/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-shiftcolumn = "${libdir}/geany/shiftcolumn.so"

PLUGINS += "${PN}-spellcheck"
LIC_FILES_CHKSUM += "file://spellcheck/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-spellcheck = "${libdir}/geany/spellcheck.so"

PLUGINS += "${PN}-tableconvert"
LIC_FILES_CHKSUM += "file://tableconvert/COPYING;md5=6753686878d090a1f3f9445661d3dfbc"
FILES_${PN}-tableconvert = "${libdir}/geany/tableconvert.so"

PLUGINS += "${PN}-treebrowser"
LIC_FILES_CHKSUM += "file://treebrowser/README;beginline=67;endline=67;md5=1f17f0f2abb88e0fa0f1b342112d871c"
FILES_${PN}-treebrowser = "${libdir}/geany/treebrowser.so"

PLUGINS += "${PN}-updatechecker"
LIC_FILES_CHKSUM += "file://updatechecker/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-updatechecker = "${libdir}/geany/updatechecker.so"

PLUGINS += "${PN}-vimode"
LIC_FILES_CHKSUM += "file://vimode/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-vimode = "${libdir}/geany/vimode.so"

# no webkit - lasts ages and is not properly detected
EXTRA_OECONF += " --disable-webhelper"
#PLUGINS += "${PN}-webhelper"
#LIC_FILES_CHKSUM += "file://webhelper/COPYING;md5=d32239bcb673463ab874e80d47fae504"
#LICENSE_${PN}-webhelper = "GPLv3"
#FILES_${PN}-webhelper = "${libdir}/geany/webhelper.so"

PLUGINS += "${PN}-workbench"
LIC_FILES_CHKSUM += "file://workbench/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-workbench = "${libdir}/geany/workbench.so"

PLUGINS += "${PN}-xmlsnippets"
LIC_FILES_CHKSUM += "file://xmlsnippets/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-xmlsnippets = "${libdir}/geany/xmlsnippets.so"

PACKAGES =+ "${PN}-common ${PLUGINS}"
FILES_${PN}-common = "${libdir}/libgeanypluginutils${SOLIBS}"

# geany-plugins is meta package for all plugins
RDEPENDS_${PN} = "${PLUGINS}"
ALLOW_EMPTY_${PN} = "1"
