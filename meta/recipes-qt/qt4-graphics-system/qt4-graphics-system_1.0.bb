SUMMARY = "Sets default Qt4 Graphics System to ${QT_GRAPHICS_SYSTEM}"
SECTION = "x11/base"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r1"

QT_GRAPHICS_SYSTEM ?= "raster"

def _get_extra_rdepends(d):
    gs = d.getVar('QT_GRAPHICS_SYSTEM', True)
    if gs == "opengl":
        return "qt4-plugin-graphicssystems-glgraphicssystem"

    return ""

do_install () {
	install -d ${D}/${sysconfdir}/profile.d/
	cfg_file=${D}/${sysconfdir}/profile.d/qt-graphicssystem
	echo "export QT_GRAPHICSSYSTEM=${QT_GRAPHICS_SYSTEM}" > $cfg_file
}

RDEPENDS_${PN} = "${@_get_extra_rdepends(d)}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
