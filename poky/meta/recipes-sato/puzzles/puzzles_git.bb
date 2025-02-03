SUMMARY = "Simon Tatham's Portable Puzzle Collection"
DESCRIPTION = "Collection of small computer programs which implement one-player puzzle games."
HOMEPAGE = "http://www.chiark.greenend.org.uk/~sgtatham/puzzles/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=191542b32377bde254e9799e0a46f18b"

# gtk support includes a bunch of x11 headers
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://git.tartarus.org/simon/puzzles.git;branch=main;protocol=https"

UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "79be403101d055d2fde3611a5f133135d4094ee9"
PE = "2"
PV = "0.0+git"

S = "${WORKDIR}/git"

inherit cmake features_check pkgconfig

DEPENDS += "gtk+3"

do_install:append () {
    # net conflicts with Samba, so rename it
    mv ${D}${bindir}/net ${D}${bindir}/puzzles-net
    rm ${D}/${datadir}/applications/net.desktop

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

