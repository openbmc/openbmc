SUMMARY = "GNOME themes"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2b543dc97eb7594d026af39d9b54204b"

PR = "r3"

SECTION = "x11/gnome"
DEPENDS = "gtk-engines icon-naming-utils icon-naming-utils-native \
           glib-2.0 intltool-native libxml-simple-perl-native \
           glib-2.0-native \
"
RDEPENDS_${PN} = "adwaita-icon-theme"

EXTRA_OECONF += "--enable-all-themes \
    --with-iconmap=${STAGING_LIBEXECDIR_NATIVE}/icon-name-mapping \
"

inherit gnomebase perlnative

PACKAGES =+ " gnome-theme-crux gnome-theme-highcontrast gnome-theme-highcontrastinverse gnome-theme-highcontrastlargeprint gnome-theme-highcontrastlargeprintinverse gnome-theme-largeprint gnome-theme-mist"
FILES_gnome-theme-crux = "${datadir}/themes/Crux ${datadir}/icons/Crux"
FILES_gnome-theme-highcontrast = "${datadir}/themes/HighContrast ${datadir}/icons/HighContrast"
FILES_gnome-theme-highcontrastinverse = "${datadir}/themes/HighContrastInverse ${datadir}/icons/HighContrastInverse"
FILES_gnome-theme-highcontrastlargeprint = "${datadir}/themes/HighContrastLargePrint ${datadir}/icons/HighContrastLargePrint"
FILES_gnome-theme-highcontrastlargeprintinverse = "${datadir}/themes/HighContrastLargePrintInverse ${datadir}/icons/HighContrastLargePrintInverse"
FILES_gnome-theme-largeprint = "${datadir}/themes/LargePrint ${datadir}/icons/LargePrint"
FILES_gnome-theme-mist = "${datadir}/themes/Mist ${datadir}/icons/Mist"

FILES_${PN} += "${datadir}/themes ${datadir}/icons"

SRC_URI += "file://iconpath-option.patch \
    file://drop-localedir-override.patch \
"
SRC_URI[archive.md5sum] = "41db9e3cb25d35af2675c599e67944d1"
SRC_URI[archive.sha256sum] = "8601ee24c2e096593221cbd6ebdb6686042225a03c02a01c0d67c163f9febd1a"
GNOME_COMPRESS_TYPE="bz2"

