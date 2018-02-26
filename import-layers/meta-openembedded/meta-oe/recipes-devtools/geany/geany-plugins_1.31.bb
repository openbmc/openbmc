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
    geany \
    libxml2 \
    libsoup-2.4 \
    enchant \
    intltool-native \
    libassuan \
    gpgme \
    vte9 \
    libgit2 \
"

inherit autotools pkgconfig gtk-icon-cache

SRC_URI = "http://plugins.geany.org/${PN}/${PN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "808f9048b77fd9704569ed2ba12a56e9"
SRC_URI[sha256sum] = "76bd9e803db5a626b86669f08330cf95b8cc35057a1cdf65759bc00aef120e25"

do_configure_prepend() {
    rm -f ${S}/build/cache/glib-gettext.m4
}

FILES_${PN} += "${datadir}/icons"
FILES_${PN}-dev += "${libdir}/geany/*.la ${libdir}/${PN}/*/*.la"

PLUGINS += "${PN}-addons"
LIC_FILES_CHKSUM += "file://addons/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-addons = "${libdir}/geany/addons.so"
RDEPENDS_${PN}-addons = "${PN}"

PLUGINS += "${PN}-autoclose"
LIC_FILES_CHKSUM += "file://autoclose/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-autoclose = "${libdir}/geany/autoclose.so"
RDEPENDS_${PN}-autoclose = "${PN}"

PLUGINS += "${PN}-automark"
LIC_FILES_CHKSUM += "file://automark/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-automark = "${libdir}/geany/automark.so"
RDEPENDS_${PN}-automark = "${PN}"

PLUGINS += "${PN}-codenav"
LIC_FILES_CHKSUM += "file://codenav/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-codenav = "${libdir}/geany/codenav.so"
RDEPENDS_${PN}-codenav = "${PN}"

PLUGINS += "${PN}-commander"
LIC_FILES_CHKSUM += "file://commander/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-commander = "GPLv3"
FILES_${PN}-commander = "${libdir}/geany/commander.so"
RDEPENDS_${PN}-commander = "${PN}"

PLUGINS += "${PN}-debugger"
LIC_FILES_CHKSUM += "file://debugger/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-debugger = "${libdir}/geany/debugger.so ${datadir}/${PN}/debugger"
RDEPENDS_${PN}-debugger = "${PN}"

PLUGINS += "${PN}-defineformat"
LIC_FILES_CHKSUM += "file://defineformat/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-defineformat = "${libdir}/geany/defineformat.so"
RDEPENDS_${PN}-defineformat = "${PN}"

# no gnome devhelp in some common layer
EXTRA_OECONF += "--disable-devhelp"
#PLUGINS += "${PN}-devhelp"
#LIC_FILES_CHKSUM += "file://devhelp/COPYING;md5=d32239bcb673463ab874e80d47fae504"
#LICENSE_${PN}-devhelp = "GPLv3"
#FILES_${PN}-devhelp = "${libdir}/geany/devhelp.so"
#RDEPENDS_${PN}-devhelp = "${PN}"

PLUGINS += "${PN}-geanyctags"
LIC_FILES_CHKSUM += "file://geanyctags/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyctags = "${libdir}/geany/geanyctags.so"
RDEPENDS_${PN}-geanyctags = "${PN}"

PLUGINS += "${PN}-geanydoc"
LIC_FILES_CHKSUM += "file://geanydoc/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-geanydoc = "GPLv3"
FILES_${PN}-geanydoc = "${libdir}/geany/geanydoc.so"
RDEPENDS_${PN}-geanydoc = "${PN}"

PLUGINS += "${PN}-geanyextrasel"
LIC_FILES_CHKSUM += "file://geanyextrasel/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyextrasel = "${libdir}/geany/geanyextrasel.so"
RDEPENDS_${PN}-geanyextrasel = "${PN}"

PLUGINS += "${PN}-geanyinsertnum"
LIC_FILES_CHKSUM += "file://geanyinsertnum/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyinsertnum = "${libdir}/geany/geanyinsertnum.so"
RDEPENDS_${PN}-geanyinsertnum = "${PN}"

PLUGINS += "${PN}-geanylatex"
LIC_FILES_CHKSUM += "file://geanylatex/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanylatex = "${libdir}/geany/geanylatex.so"
RDEPENDS_${PN}-geanylatex = "${PN}"

PLUGINS += "${PN}-geanylipsum"
LIC_FILES_CHKSUM += "file://lipsum/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-geanylipsum = "${libdir}/geany/lipsum.so"
RDEPENDS_${PN}-geanylipsum = "${PN}"

# no lua: max supported version is 5.2
EXTRA_OECONF += "--disable-geanylua"
#PLUGINS += "${PN}-geanylua"
#LIC_FILES_CHKSUM += "file://geanylua/COPYING;md5=4325afd396febcb659c36b49533135d4"
#FILES_${PN}-geanylua = "${libdir}/geany/geanylua.so ${libdir}/${PN}/geanylua/*.so"
#RDEPENDS_${PN}-geanylua = "${PN}"

PLUGINS += "${PN}-geanymacro"
LIC_FILES_CHKSUM += "file://geanymacro/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanymacro = "${libdir}/geany/geanymacro.so"
RDEPENDS_${PN}-geanymacro = "${PN}"

PLUGINS += "${PN}-geanyminiscript"
LIC_FILES_CHKSUM += "file://geanyminiscript/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-geanyminiscript = "${libdir}/geany/geanyminiscript.so"
RDEPENDS_${PN}-geanyminiscript = "${PN}"

PLUGINS += "${PN}-geanynumberedbookmarks"
LIC_FILES_CHKSUM += "file://geanynumberedbookmarks/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanynumberedbookmarks = "${libdir}/geany/geanynumberedbookmarks.so"
RDEPENDS_${PN}-geanynumberedbookmarks = "${PN}"

PLUGINS += "${PN}-geanypg"
LIC_FILES_CHKSUM += "file://geanypg/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
FILES_${PN}-geanypg = "${libdir}/geany/geanypg.so"
RDEPENDS_${PN}-geanypg = "${PN}"

PLUGINS += "${PN}-geanyprj"
LIC_FILES_CHKSUM += "file://geanyprj/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-geanyprj = "GPLv3"
FILES_${PN}-geanyprj = "${libdir}/geany/geanyprj.so"
RDEPENDS_${PN}-geanyprj = "${PN}"

# no gnome pygtk
EXTRA_OECONF += "--disable-geanypy"
#PLUGINS += "${PN}-geanypy"
#LIC_FILES_CHKSUM += "file://geanypy/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
#FILES_${PN}-geanypy = "${libdir}/geany/geanypy.so"
#RDEPENDS_${PN}-geanypy = "${PN}"

PLUGINS += "${PN}-geanyvc"
LIC_FILES_CHKSUM += "file://geanyvc/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-geanyvc = "${libdir}/geany/geanyvc.so"
RDEPENDS_${PN}-geanyvc = "${PN}"

PLUGINS += "${PN}-geniuspaste"
LIC_FILES_CHKSUM += "file://geniuspaste/COPYING;md5=bfc203269f8862ebfc1198cdc809a95a"
FILES_${PN}-geniuspaste = "${libdir}/geany/geniuspaste.so ${datadir}/${PN}/geniuspaste"
RDEPENDS_${PN}-geniuspaste = "${PN}"

PLUGINS += "${PN}-git-changebar"
LIC_FILES_CHKSUM += "file://git-changebar/COPYING;md5=d32239bcb673463ab874e80d47fae504"
LICENSE_${PN}-git-changebar = "GPLv3"
FILES_${PN}-git-changebar = "${libdir}/geany/git-changebar.so"
RDEPENDS_${PN}-git-changebar = "${PN}"

PLUGINS += "${PN}-keyrecord"
LIC_FILES_CHKSUM += "file://keyrecord/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-keyrecord = "${libdir}/geany/keyrecord.so"
RDEPENDS_${PN}-keyrecord = "${PN}"

PLUGINS += "${PN}-lineoperations"
LIC_FILES_CHKSUM += "file://lineoperations/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-lineoperations = "${libdir}/geany/lineoperations.so"
RDEPENDS_${PN}-lineoperations = "${PN}"

# no markdown - avoid floating dependencies
EXTRA_OECONF += " --disable-peg-markdown"
#PLUGINS += "${PN}-markdown"
#LIC_FILES_CHKSUM += "file://markdown/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
#FILES_${PN}-markdown = "${libdir}/geany/markdown.so"
#RDEPENDS_${PN}-markdown = "${PN}"

PLUGINS += "${PN}-multiterm"
LIC_FILES_CHKSUM += "file://multiterm/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
FILES_${PN}-multiterm = "${libdir}/geany/multiterm.so"
RDEPENDS_${PN}-multiterm = "${PN}"

PLUGINS += "${PN}-overview"
LIC_FILES_CHKSUM += "file://overview/overview/overviewplugin.c;beginline=4;endline=20;md5=1aa33522916cdeb46cccac0c629da0d0"
FILES_${PN}-overview = "${libdir}/geany/overview.so ${datadir}/${PN}/overview"
RDEPENDS_${PN}-overview = "${PN}"

PLUGINS += "${PN}-pairtaghighlighter"
LICENSE_${PN}-pairtaghighlighter = "BSD-2-Clause"
LIC_FILES_CHKSUM += "file://pairtaghighlighter/COPYING;md5=d6d927525a612b3a8dbebc4b2e9b47c1"
FILES_${PN}-pairtaghighlighter = "${libdir}/geany/pairtaghighlighter.so"
RDEPENDS_${PN}-pairtaghighlighter = "${PN}"

PLUGINS += "${PN}-pohelper"
LICENSE_${PN}-pohelper = "GPLv3"
LIC_FILES_CHKSUM += "file://pohelper/COPYING;md5=d32239bcb673463ab874e80d47fae504"
FILES_${PN}-pohelper = "${libdir}/geany/pohelper.so"
RDEPENDS_${PN}-pohelper = "${PN}"

PLUGINS += "${PN}-pretty-printer"
LIC_FILES_CHKSUM += "file://pretty-printer/src/PrettyPrinter.c;beginline=1;endline=17;md5=1665115c2fadb17c1b53cdb4e43b2440"
FILES_${PN}-pretty-printer = "${libdir}/geany/pretty-printer.so"
RDEPENDS_${PN}-pretty-printer = "${PN}"

PLUGINS += "${PN}-projectorganizer"
LIC_FILES_CHKSUM += "file://projectorganizer/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-projectorganizer = "${libdir}/geany/projectorganizer.so"
RDEPENDS_${PN}-projectorganizer = "${PN}"

PLUGINS += "${PN}-scope"
LIC_FILES_CHKSUM += "file://scope/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-scope = "${libdir}/geany/scope.so"
RDEPENDS_${PN}-scope = "${PN}"

PLUGINS += "${PN}-sendmail"
LIC_FILES_CHKSUM += "file://sendmail/COPYING;md5=c107cf754550e65755c42985a5d4e9c9"
FILES_${PN}-sendmail = "${libdir}/geany/sendmail.so"
RDEPENDS_${PN}-sendmail = "${PN}"

PLUGINS += "${PN}-shiftcolumn"
LIC_FILES_CHKSUM += "file://shiftcolumn/COPYING;md5=751419260aa954499f7abaabaa882bbe"
FILES_${PN}-shiftcolumn = "${libdir}/geany/shiftcolumn.so"
RDEPENDS_${PN}-shiftcolumn = "${PN}"

PLUGINS += "${PN}-spellcheck"
LIC_FILES_CHKSUM += "file://spellcheck/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-spellcheck = "${libdir}/geany/spellcheck.so"
RDEPENDS_${PN}-spellcheck = "${PN}"

PLUGINS += "${PN}-tableconvert"
LIC_FILES_CHKSUM += "file://tableconvert/COPYING;md5=6753686878d090a1f3f9445661d3dfbc"
FILES_${PN}-tableconvert = "${libdir}/geany/tableconvert.so"
RDEPENDS_${PN}-tableconvert = "${PN}"

PLUGINS += "${PN}-treebrowser"
LIC_FILES_CHKSUM += "file://treebrowser/README;beginline=67;endline=67;md5=52f90857fd1a9672111e472dd056a0d8"
FILES_${PN}-treebrowser = "${libdir}/geany/treebrowser.so"
RDEPENDS_${PN}-treebrowser = "${PN}"

PLUGINS += "${PN}-updatechecker"
LIC_FILES_CHKSUM += "file://updatechecker/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-updatechecker = "${libdir}/geany/updatechecker.so"
RDEPENDS_${PN}-updatechecker = "${PN}"

# no webkit - lasts ages and is not properly detected
EXTRA_OECONF += " --disable-webhelper"
#PLUGINS += "${PN}-webhelper"
#LIC_FILES_CHKSUM += "file://webhelper/COPYING;md5=d32239bcb673463ab874e80d47fae504"
#LICENSE_${PN}-webhelper = "GPLv3"
#FILES_${PN}-webhelper = "${libdir}/geany/webhelper.so"
#RDEPENDS_${PN}-webhelper = "${PN}"

PLUGINS += "${PN}-xmlsnippets"
LIC_FILES_CHKSUM += "file://xmlsnippets/COPYING;md5=4325afd396febcb659c36b49533135d4"
FILES_${PN}-xmlsnippets = "${libdir}/geany/xmlsnippets.so"
RDEPENDS_${PN}-xmlsnippets = "${PN}"

PACKAGES =+ "${PLUGINS}"
RDEPENDS_${PN} = "${PLUGINS}"
ALLOW_EMPTY_${PN} = "1"
