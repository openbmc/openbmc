SUMMARY = "Portable Puzzle Collection"
HOMEPAGE = "http://o-hand.com/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=f56ec6772dd1c7c367067bbea8ea1675 \
                    file://src/tree234.h;endline=28;md5=a188e6d250430ca094a54a82f48472a7 \
                    file://src/tree234.c;endline=28;md5=b4feb1976feebf8f1379093ed52f2945"

SECTION = "x11"
DEPENDS = "gtk+ gconf intltool-native librsvg gettext-native"

# libowl requires x11 in DISTRO_FEATURES
DEPENDS_append_poky = " ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libowl', '', d)}"

# Requires gdk/gdkx.h which is provided by gtk when x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

SRCREV = "92f1a20e4b72eed7a35b00984d9793b51dc2fb3b"
PV = "0.2+git${SRCPV}"
PR = "r10"

SRC_URI = "git://git.yoctoproject.org/${BPN}"
SRC_URI_append_poky = " file://oh-puzzles-owl-menu.patch;striplevel=0 "

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

bindir = "/usr/games"

EXTRA_OEMAKE += "GCONF_DISABLE_MAKEFILE_SCHEMA_INSTALL=1"

do_install_append () {
    install -d ${D}/${datadir}/applications/

    cd ${D}/${prefix}/games
    for prog in *; do
	if [ -x $prog ]; then
            # Convert prog to Title Case
            title=$(echo $prog | sed 's/\(^\| \)./\U&/g')
	    echo "making ${D}/${datadir}/applications/$prog.desktop"
	    cat <<STOP > ${D}/${datadir}/applications/$prog.desktop
[Desktop Entry]
Name=$title
Exec=${prefix}/games/$prog
Icon=applications-games
Terminal=false
Type=Application
Categories=Game;
StartupNotify=true
X-MB-SingleInstance=true
Comment=Play $title.
STOP
        fi
    done
}

PACKAGES += "${PN}-extra"
RDEPENDS_${PN}-extra += "oh-puzzles"

FILES_${PN} = "/usr/share/pixmaps /usr/share/oh-puzzles/"
FILES_${PN}-extra = "/usr/games/ /usr/share/applications /etc/gconf/schemas"

python __anonymous () {
    var = bb.data.expand("FILES_${PN}", d, 1)
    data = d.getVar(var, True)
    for name in ("bridges", "fifteen", "inertia", "map", "samegame", "slant"):
        data = data + " /usr/games/%s" % name
        data = data + " /usr/share/applications/%s.desktop" % name
        data = data + " /etc/gconf/schemas/%s.schemas" % name
    d.setVar(var, data)
}
