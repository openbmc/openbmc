SUMMARY = "Simon Tatham's Portable Puzzle Collection"
DESCRIPTION = "Collection of small computer programs which implement one-player puzzle games."
HOMEPAGE = "http://www.chiark.greenend.org.uk/~sgtatham/puzzles/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=6099f4981f9461d7f411091e69a7f07a"

DEPENDS = "libxt"

# The libxt requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://git.tartarus.org/simon/puzzles.git \
           file://fix-compiling-failure-with-option-g-O.patch \
           file://0001-palisade-Fix-warnings-with-clang-on-arm.patch \
           file://0001-Use-Wno-error-format-overflow-if-the-compiler-suppor.patch \
           file://0001-pattern.c-Change-string-lenght-parameter-to-be-size_.patch \
           file://fix-ki-uninitialized.patch \
           file://0001-malloc-Check-for-excessive-values-to-malloc.patch \
           file://0001-map-Fix-stringop-overflow-warning.patch \
           "

UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "84cb4c6701e027090ff3fd955ce08065e20121b2"
PE = "2"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools features_check pkgconfig

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
