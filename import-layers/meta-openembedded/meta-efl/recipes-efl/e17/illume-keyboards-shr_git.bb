SECTION = "x11/data"

SRC_URI = "git://git.shr-project.org/repo/illume-keyboards.git;protocol=http;branch=master \
           file://0001-Makefile-update-KEYBOARDS_DIR-for-new-illume2.patch \
           file://LICENSE"
S = "${WORKDIR}/git"

SRCREV = "4064489f359a1addf572089b582f317dff5f50e1"
PV = "0.0+gitr${SRCPV}"
PE = "1"
PR = "r5"
LICENSE = "MIT & BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=f523ab5986cc79b52a90d2ac3d5454a2"

CLEANBROKEN = "1"

PACKAGES = "\
illume-keyboard-alpha \
illume-keyboard-arabic \
illume-keyboard-browse \
illume-keyboard-danish \
illume-keyboard-default-alt \
illume-keyboard-dutch \
illume-keyboard-dvorak \
illume-keyboard-finnish \
illume-keyboard-french \
illume-keyboard-german \
illume-keyboard-hebrew \
illume-keyboard-numeric-alt \
illume-keyboard-russian \
illume-keyboard-russian-terminal \
illume-keyboard-persian \
"

inherit allarch
KEYBOARDS_DIR="${libdir}/enlightenment/modules/illume-keyboard/keyboards"

AUTHOR_illume-keyboard-alpha = "Jesus McCloud"
DESCRIPTION_illume-keyboard-alpha = "Illume keyboard with phone-like layout"
FILES_illume-keyboard-alpha = "${KEYBOARDS_DIR}/Alpha.kbd"

AUTHOR_illume-keyboard-arabic = "Mohammad Fahmi / Tom Hacohen"
DESCRIPTION_illume-keyboard-arabic = "Illume keyboard with arabic layout"
FILES_illume-keyboard-arabic = "${KEYBOARDS_DIR}/Arabic.kbd ${KEYBOARDS_DIR}/Arabic.png"

AUTHOR_illume-keyboard-browse = "Pander"
DESCRIPTION_illume-keyboard-browse = "Illume keyboard with a layout optimized for browsing"
FILES_illume-keyboard-browse = "${KEYBOARDS_DIR}/Browse.kbd ${KEYBOARDS_DIR}/end-browse.png ${KEYBOARDS_DIR}/pagedown-browse.png ${KEYBOARDS_DIR}/browse.png ${KEYBOARDS_DIR}/home-browse.png ${KEYBOARDS_DIR}/pageup-browse.png ${KEYBOARDS_DIR}/space-browse.png"

AUTHOR_illume-keyboard-danish = "Esben Damgaard"
DESCRIPTION_illume-keyboard-danish = "Illume keyboard with danish layout"
FILES_illume-keyboard-danish = "${KEYBOARDS_DIR}/Danish.kbd ${KEYBOARDS_DIR}/danish.png"

AUTHOR_illume-keyboard-default-alt = "Pander"
DESCRIPTION_illume-keyboard-default-alt = "Illume keyboard with an alternative default layout"
FILES_illume-keyboard-default-alt = "${KEYBOARDS_DIR}/Default-alt.kbd"

AUTHOR_illume-keyboard-dutch = "Pander"
DESCRIPTION_illume-keyboard-dutch = "Illume keyboard with dutch layout"
FILES_illume-keyboard-dutch = "${KEYBOARDS_DIR}/capslock-negative.png ${KEYBOARDS_DIR}/end.png ${KEYBOARDS_DIR}/pagedown.png ${KEYBOARDS_DIR}/qwerty-dutch-nl.png ${KEYBOARDS_DIR}/Terminal-dutch-nl.kbd ${KEYBOARDS_DIR}/capslock.png ${KEYBOARDS_DIR}/home.png ${KEYBOARDS_DIR}/pageup.png ${KEYBOARDS_DIR}/space.png"

AUTHOR_illume-keyboard-dvorak = "Gabor Adam TOTH"
DESCRIPTION_illume-keyboard-dvorak = "Illume keyboard with dvorak layout"
FILES_illume-keyboard-dvorak = "${KEYBOARDS_DIR}/Dvorak.kbd ${KEYBOARDS_DIR}/dvorak.png"

AUTHOR_illume-keyboard-finnish = "Olof Sjöbergh / Jussi Timperi"
DESCRIPTION_illume-keyboard-finnish = "Illume keyboard with finnish layout"
FILES_illume-keyboard-finnish = "${KEYBOARDS_DIR}/Finnish.kbd ${KEYBOARDS_DIR}/Finnish.png"

DESCRIPTION_illume-keyboard-french = "Illume keyboard with french layout"
FILES_illume-keyboard-french = "${KEYBOARDS_DIR}/Azerty.kbd ${KEYBOARDS_DIR}/Azerty.png"

AUTHOR_illume-keyboard-german = "Florian Hackenberger"
DESCRIPTION_illume-keyboard-german = "Illume keyboard with german layout"
FILES_illume-keyboard-german = "${KEYBOARDS_DIR}/German.kbd ${KEYBOARDS_DIR}/German.png"

AUTHOR_illume-keyboard-hebrew = "Tom Hacohen"
DESCRIPTION_illume-keyboard-hebrew = "Illume keyboard with hebrew layout"
FILES_illume-keyboard-hebrew = "${KEYBOARDS_DIR}/Hebrew.kbd ${KEYBOARDS_DIR}/Alpha-hebrew-il.png"

AUTHOR_illume-keyboard-numeric-alt = "Pander"
DESCRIPTION_illume-keyboard-numeric-alt = "Illume keyboard with an alternative numeric layout"
FILES_illume-keyboard-numeric-alt = "${KEYBOARDS_DIR}/Numbers-alt.kbd ${KEYBOARDS_DIR}/numeric-alt.png"

AUTHOR_illume-keyboard-russian = "lucky"
DESCRIPTION_illume-keyboard-russian = "Illume keyboard with russian layout"
FILES_illume-keyboard-russian = "${KEYBOARDS_DIR}/X8_Russian.kbd ${KEYBOARDS_DIR}/X8-russian-ru.png"

AUTHOR_illume-keyboard-russian-terminal = "lucky"
DESCRIPTION_illume-keyboard-russian-terminal = "Illume keyboard with russian layout for the Terminal"
FILES_illume-keyboard-russian-terminal = "${KEYBOARDS_DIR}/Terminal_Russian.kbd ${KEYBOARDS_DIR}/Terminal-russian-ru.png"

AUTHOR_illume-keyboard-persian = "slave"
DESCRIPTION_illume-keyboard-persian = "Illume keyboard with persian layout"
FILES_illume-keyboard-persian = "${KEYBOARDS_DIR}/Persian.kbd ${KEYBOARDS_DIR}/Persian.png"

do_install() {
    make DESTDIR=${D} install
}

