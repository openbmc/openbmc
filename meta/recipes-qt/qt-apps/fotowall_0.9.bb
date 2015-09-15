SUMMARY = "Creative photo display application"
DESCRIPTION = "Fotowall is a creative tool that allows you to layout your photos or pictures \
in a personal way. You can add pictures, then resize, move, change colors, text, shadows, etc.."

HOMEPAGE = "http://www.enricoros.com/opensource/fotowall"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://GPL_V2;md5=79808397c3355f163c012616125c9e26 \
                    file://main.cpp;beginline=6;endline=11;md5=b569acc2bf8974a3082b58fc53b9d8dc"
SECTION = "x11/apps"

PR = "r4"

SRCREV = "06d5a4142c599604d9a9fd64727b6945eb8cd3da"
SRC_URI = "git://github.com/enricoros/fotowall.git \
           file://ExportWizard-depends-on-ui_wizard.patch \
           "

S = "${WORKDIR}/git"

inherit qt4x11

EXTRA_QMAKEVARS_PRE = "CONFIG+=no-webcam"

do_install() {
	oe_runmake INSTALL_ROOT=${D} install
}

# Ensure we have some plugins for some useful image formats
RRECOMMENDS_${PN} += "qt4-plugin-imageformat-gif qt4-plugin-imageformat-jpeg qt4-plugin-imageformat-tiff"
