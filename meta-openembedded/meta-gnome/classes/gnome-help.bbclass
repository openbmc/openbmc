# Class to pack gnome help files or delete them during install
# There are the following cases:
#
# if 'helpfiles' not in DISTRO_FEATURES
#     delete all help contants during install
# else
#   if PACKAGE_NO_HELP_SPLIT == 1
#       pack all help files to ${PN}-help
#   else
#       pack all help files to ${PN}-help-<lingua>

# Dummy to get yelp build & PACKAGE_NO_HELP_SPLIT set 1
PACKAGES_append = " ${PN}-help"
FILES_${PN}-help = "${datadir}/help"
RRECOMMENDS_${PN}-help = "${@bb.utils.contains('DISTRO_FEATURES','helpfiles','yelp','',d)}"

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES','helpfiles','false','true',d)}; then
        rm -rf ${D}${datadir}/help/*
    fi
}

python gnome_do_split_help() {
    if bb.utils.contains('DISTRO_FEATURES', 'helpfiles', False, True, d):
        return

    if (d.getVar('PACKAGE_NO_HELP_SPLIT') == '1'):
        # all help files go to ${
        bb.debug(1, "package requested not splitting help-files")
        return

    packages = (d.getVar('PACKAGES') or "").split()
    datadir = d.getVar('datadir')
    dvar = d.getVar('PKGD')
    pn = d.getVar('PN')

    if pn + '-help' in packages:
        packages.remove(pn + '-help')

    helpdir = os.path.join(dvar + datadir, 'help')

    if not cpath.isdir(helpdir):
        bb.warn("No help files in this package - remove gnome-help from inherit?")
        return

    helps = os.listdir(helpdir)

    summary = d.getVar('SUMMARY') or pn
    description = d.getVar('DESCRIPTION') or ""
    locale_section = d.getVar('LOCALE_SECTION')
    mlprefix = d.getVar('MLPREFIX') or ""
    for l in sorted(helps):
        ln = legitimize_package_name(l)
        pkg = pn + '-help-' + ln
        packages.append(pkg)
        d.setVar('FILES_' + pkg, os.path.join(datadir, 'help', l))
        d.setVar('RRECOMMENDS_' + pkg, '%syelp' % mlprefix)
        d.setVar('SUMMARY_' + pkg, '%s - %s help' % (summary, l))
        d.setVar('DESCRIPTION_' + pkg, '%s  This package contains language help files for the %s locale.' % (description, l))
        if locale_section:
            d.setVar('SECTION_' + pkg, locale_section)

    d.setVar('PACKAGES', ' '.join(packages))
}

PACKAGESPLITFUNCS_prepend = "gnome_do_split_help "

