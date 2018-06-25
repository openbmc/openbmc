# Handle U-Boot config for a machine
#
# The format to specify it, in the machine, is:
#
# UBOOT_CONFIG ??= <default>
# UBOOT_CONFIG[foo] = "config,images,binary"
#
# or
#
# UBOOT_MACHINE = "config"
#
# Copyright 2013, 2014 (C) O.S. Systems Software LTDA.

UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"

python () {
    ubootmachine = d.getVar("UBOOT_MACHINE")
    ubootconfigflags = d.getVarFlags('UBOOT_CONFIG')
    ubootbinary = d.getVar('UBOOT_BINARY')
    ubootbinaries = d.getVar('UBOOT_BINARIES')
    # The "doc" varflag is special, we don't want to see it here
    ubootconfigflags.pop('doc', None)
    ubootconfig = (d.getVar('UBOOT_CONFIG') or "").split()

    if not ubootmachine and not ubootconfig:
        PN = d.getVar("PN")
        FILE = os.path.basename(d.getVar("FILE"))
        bb.debug(1, "To build %s, see %s for instructions on \
                 setting up your machine config" % (PN, FILE))
        raise bb.parse.SkipRecipe("Either UBOOT_MACHINE or UBOOT_CONFIG must be set in the %s machine configuration." % d.getVar("MACHINE"))

    if ubootmachine and ubootconfig:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_MACHINE and UBOOT_CONFIG at the same time.")

    if ubootconfigflags and ubootbinaries:
        raise bb.parse.SkipRecipe("You cannot use UBOOT_BINARIES as it is internal to uboot_config.bbclass.")

    if len(ubootconfig) > 0:
        for config in ubootconfig:
            for f, v in ubootconfigflags.items():
                if config == f: 
                    items = v.split(',')
                    if items[0] and len(items) > 3:
                        raise bb.parse.SkipRecipe('Only config,images,binary can be specified!')
                    d.appendVar('UBOOT_MACHINE', ' ' + items[0])
                    # IMAGE_FSTYPES appending
                    if len(items) > 1 and items[1]:
                        bb.debug(1, "Appending '%s' to IMAGE_FSTYPES." % items[1])
                        d.appendVar('IMAGE_FSTYPES', ' ' + items[1])
                    if len(items) > 2 and items[2]:
                        bb.debug(1, "Appending '%s' to UBOOT_BINARIES." % items[2])
                        d.appendVar('UBOOT_BINARIES', ' ' + items[2])
                    else:
                        bb.debug(1, "Appending '%s' to UBOOT_BINARIES." % ubootbinary)
                        d.appendVar('UBOOT_BINARIES', ' ' + ubootbinary)
                    break
}
