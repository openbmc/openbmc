# anonymous support class from originally from angstrom
# 
# To use the blacklist, a distribution should include this
# class in the INHERIT_DISTRO
#
# No longer use ANGSTROM_BLACKLIST, instead use a table of
# recipes in PNBLACKLIST
#
# Features:
#
# * To add a package to the blacklist, set:
#   PNBLACKLIST[pn] = "message"
#

# Cope with PNBLACKLIST flags for multilib case
addhandler blacklist_multilib_eventhandler
blacklist_multilib_eventhandler[eventmask] = "bb.event.ConfigParsed"
python blacklist_multilib_eventhandler() {
    multilibs = e.data.getVar('MULTILIBS', True)
    if not multilibs:
        return

    # this block has been copied from base.bbclass so keep it in sync
    prefixes = []
    for ext in multilibs.split():
        eext = ext.split(':')
        if len(eext) > 1 and eext[0] == 'multilib':
            prefixes.append(eext[1])

    blacklists = e.data.getVarFlags('PNBLACKLIST') or {}
    for pkg, reason in blacklists.items():
        if pkg.endswith(("-native", "-crosssdk")) or pkg.startswith(("nativesdk-", "virtual/nativesdk-")) or 'cross-canadian' in pkg:
            continue
        for p in prefixes:
            newpkg = p + "-" + pkg
            if not e.data.getVarFlag('PNBLACKLIST', newpkg, True):
                e.data.setVarFlag('PNBLACKLIST', newpkg, reason)
}

python () {
    blacklist = d.getVarFlag('PNBLACKLIST', d.getVar('PN', True), True)

    if blacklist:
        raise bb.parse.SkipPackage("Recipe is blacklisted: %s" % (blacklist))
}
