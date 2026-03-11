# This .inc file contains functionality for automatically setting
# the the license of all plugins according to the GSTREAMER_GPL.

PACKAGESPLITFUNCS += "set_gstreamer_license"

python set_gstreamer_license () {
    import oe.utils
    pn = d.getVar('PN') + '-'
    gpl_plugins_names = [pn+plugin for plugin in d.getVar('GSTREAMER_GPL').split()]
    for pkg in oe.utils.packages_filter_out_system(d):
        if pkg in gpl_plugins_names:
            d.setVar('LICENSE:' + pkg, 'GPL-2.0-or-later')
        else:
            d.setVar('LICENSE:' + pkg, 'LGPL-2.1-or-later')
}

EXTRA_OEMESON += " \
    ${@bb.utils.contains_any('PACKAGECONFIG', "${GSTREAMER_GPL}", '-Dgpl=enabled', '-Dgpl=disabled', d)} \
    "
