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

python () {
    blacklist = d.getVarFlag('PNBLACKLIST', d.getVar('PN'))

    if blacklist:
        raise bb.parse.SkipPackage("Recipe is blacklisted: %s" % (blacklist))
}
