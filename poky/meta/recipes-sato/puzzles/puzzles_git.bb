SUMMARY = "Simon Tatham's Portable Puzzle Collection"
DESCRIPTION = "Collection of small computer programs which implement one-player puzzle games."
HOMEPAGE = "http://www.chiark.greenend.org.uk/~sgtatham/puzzles/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=6e7d24cf1c949887ee9447a1e2a4a24c"

# gtk support includes a bunch of x11 headers
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://git.tartarus.org/simon/puzzles.git;branch=main \
           file://fix-compiling-failure-with-option-g-O.patch \
           file://0001-palisade-Fix-warnings-with-clang-on-arm.patch \
           file://0001-pattern.c-Change-string-lenght-parameter-to-be-size_.patch \
           file://fix-ki-uninitialized.patch \
           file://0001-malloc-Check-for-excessive-values-to-malloc.patch \
           file://0001-map-Fix-stringop-overflow-warning.patch \
           "

UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "8f3413c31ffd43c4ebde40894ac1b2f7cdf222c3"
PE = "2"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake features_check pkgconfig

DEPENDS += "gtk+3"

do_install:append () {
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

