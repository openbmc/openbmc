require ttf.inc

SUMMARY = "Droid fonts - TTF Edition"
HOMEPAGE = "http://www.droidfonts.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.txt;md5=83544262a86f1f1ec761e75897df92bc"
SRCREV = "21e6e2de1f0062f949fcc52d0b4559dfa3246e0e"
PV = "0.1+git"

SRC_URI = "git://github.com/android/platform_frameworks_base.git;branch=master;protocol=https"

S = "${WORKDIR}/git/data/fonts"

do_install:append() {
    for f in Ahem.ttf MTLc3m.ttf DroidSansArabic.ttf DroidSansThai.ttf \
             Clockopia.ttf MTLmr3m.ttf DroidSansHebrew.ttf \
             DroidSansFallbackLegacy.ttf; do
        rm -f ${D}${datadir}/fonts/truetype/$f
    done
}

PACKAGES = "ttf-droid-sans ttf-droid-sans-mono \
            ttf-droid-sans-fallback ttf-droid-sans-japanese ttf-droid-serif"
FONT_PACKAGES = "ttf-droid-sans ttf-droid-sans-mono ttf-droid-sans-fallback ttf-droid-sans-japanese ttf-droid-serif"

FILES:ttf-droid-sans = "${datadir}/fonts/truetype/DroidSans.ttf ${datadir}/fonts/truetype/DroidSans-Bold.ttf"
FILES:ttf-droid-sans-mono = "${datadir}/fonts/truetype/DroidSansMono.ttf"
FILES:ttf-droid-sans-fallback = "${datadir}/fonts/truetype/DroidSansFallback.ttf"
FILES:ttf-droid-sans-japanese = "${datadir}/fonts/truetype/DroidSansJapanese.ttf"
FILES:ttf-droid-serif = "${datadir}/fonts/truetype/DroidSerif*.ttf"
