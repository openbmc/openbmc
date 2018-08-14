require ttf.inc

SUMMARY = "Droid fonts - TTF Edition"
HOMEPAGE = "http://www.droidfonts.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.txt;md5=83544262a86f1f1ec761e75897df92bc"
SRCREV = "21e6e2de1f0062f949fcc52d0b4559dfa3246e0e"
PV = "0.1+gitr${SRCPV}"
PR = "r3"

SRC_URI = "git://github.com/android/platform_frameworks_base.git;branch=master"

S = "${WORKDIR}/git/data/fonts"

do_install_prepend() {
    rm ${S}/Ahem.ttf MTLc3m.ttf DroidSansArabic.ttf DroidSansThai.ttf Clockopia.ttf MTLmr3m.ttf DroidSansHebrew.ttf DroidSansFallbackLegacy.ttf # we're not packaging it
}

PACKAGES = "ttf-droid-sans ttf-droid-sans-mono \
            ttf-droid-sans-fallback ttf-droid-sans-japanese ttf-droid-serif"
FONT_PACKAGES = "ttf-droid-sans ttf-droid-sans-mono ttf-droid-sans-fallback ttf-droid-sans-japanese ttf-droid-serif"

FILES_ttf-droid-sans = "${datadir}/fonts/truetype/DroidSans.ttf ${datadir}/fonts/truetype/DroidSans-Bold.ttf"
FILES_ttf-droid-sans-mono = "${datadir}/fonts/truetype/DroidSansMono.ttf"
FILES_ttf-droid-sans-fallback = "${datadir}/fonts/truetype/DroidSansFallback.ttf"
FILES_ttf-droid-sans-japanese = "${datadir}/fonts/truetype/DroidSansJapanese.ttf"
FILES_ttf-droid-serif = "${datadir}/fonts/truetype/DroidSerif*.ttf"
