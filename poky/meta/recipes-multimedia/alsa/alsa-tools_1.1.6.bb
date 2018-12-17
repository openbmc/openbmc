SUMMARY = "Advanced tools for certain ALSA sound card drivers"
HOMEPAGE = "http://www.alsa-project.org"
BUGTRACKER = "http://alsa-project.org/main/index.php/Bug_Tracking"
SECTION = "console/utils"
LICENSE = "GPLv2 & LGPLv2+"
DEPENDS = "alsa-lib"

LIC_FILES_CHKSUM = "file://hdsploader/COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://ld10k1/COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7 \
                    "

SRC_URI = "ftp://ftp.alsa-project.org/pub/tools/${BP}.tar.bz2 \
           file://0002-Fix-clang-Wreserved-user-defined-literal-warnings.patch \
           file://musl.patch \
           "

SRC_URI[md5sum] = "5ca8c9437ae779997cd62fb2815fef19"
SRC_URI[sha256sum] = "d69c4dc2fb641a974d9903e9eb78c94cb0c7ac6c45bae664f0c9d6c0a1593227"

inherit autotools-brokensep pkgconfig
# brokensep as as10k1 (and probably more) fail out of tree
CLEANBROKEN = "1"

# Here we use PACKAGECONFIG options to pick which directories we configure/build.
# Remember on upgrades to check that no new tools have been added.
PACKAGECONFIG ??= "as10k1 hdajacksensetest hda-verb hdsploader ld10k1 mixartloader pcxhrloader \
                   sb16_csp seq--sbiload sscape_ctl us428control usx2yloader vxloader \
                   ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK2DISTROFEATURES}', 'echomixer envy24control rmedigicontrol', '', d)} \
                   ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'hdajackretask', '', d)} \
                   "

PACKAGECONFIG[as10k1] = ""
PACKAGECONFIG[echomixer] = ",,gtk+"
PACKAGECONFIG[envy24control] = ",,gtk+"
PACKAGECONFIG[hda-verb] = ""
PACKAGECONFIG[hdajackretask] = ",,gtk+3"
PACKAGECONFIG[hdajacksensetest] = ",,glib-2.0"
PACKAGECONFIG[hdspconf] = ",,fltk"
PACKAGECONFIG[hdsploader] = ""
PACKAGECONFIG[hdspmixer] = ",,fltk"
PACKAGECONFIG[hwmixvolume] = ",,,python-core python-pygtk"
PACKAGECONFIG[ld10k1] = ""
PACKAGECONFIG[mixartloader] = ""
PACKAGECONFIG[pcxhrloader] = ""
PACKAGECONFIG[qlo10k1] = ",,qt-x11-free"
PACKAGECONFIG[rmedigicontrol] = ",,gtk+"
PACKAGECONFIG[sb16_csp] = ""
PACKAGECONFIG[seq--sbiload] = ""
PACKAGECONFIG[sscape_ctl] = ""
PACKAGECONFIG[us428control] = ""
PACKAGECONFIG[usx2yloader] = ""
PACKAGECONFIG[vxloader] = ""

python do_configure() {
    for subdir in d.getVar("PACKAGECONFIG").split():
        subdir = subdir.replace("--", "/")
        bb.note("Configuring %s" % subdir)
        dd = d.createCopy()
        dd.setVar("S", os.path.join(d.getVar("S"), subdir))
        bb.build.exec_func("autotools_do_configure", dd)
}

python do_compile() {
    for subdir in d.getVar("PACKAGECONFIG").split():
        subdir = subdir.replace("--", "/")
        bb.note("Compiling %s" % subdir)
        dd = d.createCopy()
        dd.setVar("S", os.path.join(d.getVar("S"), subdir))
        bb.build.exec_func("autotools_do_compile", dd)
}

python do_install() {
    for subdir in d.getVar("PACKAGECONFIG").split():
        subdir = subdir.replace("--", "/")
        bb.note("Installing %s" % subdir)
        dd = d.createCopy()
        dd.setVar("S", os.path.join(d.getVar("S"), subdir))
        bb.build.exec_func("autotools_do_install", dd)

    # Just remove bash-needing init script that isn't installed as an init script
    try:
        os.remove(oe.path.join(d.getVar("D"), d.getVar("sbindir"), "ld10k1d"))
    except:
        pass
}

FILES_${PN} += "${datadir}"
