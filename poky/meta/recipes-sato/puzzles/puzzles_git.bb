SUMMARY = "Simon Tatham's Portable Puzzle Collection"
HOMEPAGE = "http://www.chiark.greenend.org.uk/~sgtatham/puzzles/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=da6110d4ed1225a287eab2bf0ac0193b"

DEPENDS = "libxt"

# The libxt requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://git.tartarus.org/simon/puzzles.git \
           file://fix-compiling-failure-with-option-g-O.patch \
           file://0001-Use-labs-instead-of-abs.patch \
           file://0001-palisade-Fix-warnings-with-clang-on-arm.patch \
           file://0001-Use-Wno-error-format-overflow-if-the-compiler-suppor.patch \
           "

UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "c6e0161dd475415316ed66dc82794d68e52f0025"
PE = "2"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools distro_features_check pkgconfig

PACKAGECONFIG ??= "gtk3"
PACKAGECONFIG[gtk2] = "--with-gtk=2,,gtk+,"
PACKAGECONFIG[gtk3] = "--with-gtk=3,,gtk+3,"

CFLAGS_append = " -Wno-deprecated-declarations"

ASNEEDED = ""

do_configure_prepend () {
    cd ${S}
    ./mkfiles.pl
    cd ${B}
}

do_install_append () {
    # net conflicts with Samba, so rename it
    mv ${D}${bindir}/net ${D}${bindir}/puzzles-net

    # Create desktop shortcuts
    install -d ${D}/${datadir}/applications/
    cd ${D}/${prefix}/bin
    for prog in *; do
	if [ -x $prog ]; then
            # Convert prog to Title Case
            title=$(echo $prog | sed 's/puzzles-//' | sed 's/\(^\| \)./\U&/g')
	    echo "making ${D}/${datadir}/applications/$prog.desktop"
	    cat <<STOP > ${D}/${datadir}/applications/$prog.desktop
[Desktop Entry]
Name=$title
Exec=${bindir}/$prog
Icon=applications-games
Terminal=false
Type=Application
Categories=Game;
StartupNotify=true
STOP
        fi
    done
}

PACKAGES += "${PN}-extra"

FILES_${PN} = ""
FILES_${PN}-extra = "${prefix}/bin ${datadir}/applications"

python __anonymous () {
    var = d.expand("FILES_${PN}")
    data = d.getVar(var, False)
    for name in ("bridges", "fifteen", "inertia", "map", "samegame", "slant"):
        data = data + " ${bindir}/%s" % name
        data = data + " ${datadir}/applications/%s.desktop" % name
    d.setVar(var, data)
}
