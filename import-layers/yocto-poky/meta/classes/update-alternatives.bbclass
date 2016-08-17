# This class is used to help the alternatives system which is useful when
# multiple sources provide same command. You can use update-alternatives
# command directly in your recipe, but in most cases this class simplifies
# that job.
#
# To use this class a number of variables should be defined:
#
# List all of the alternatives needed by a package:
# ALTERNATIVE_<pkg> = "name1 name2 name3 ..."
#
#   i.e. ALTERNATIVE_busybox = "sh sed test bracket"
#
# The pathname of the link
# ALTERNATIVE_LINK_NAME[name] = "target"
#
#   This is the name of the binary once it's been installed onto the runtime.
#   This name is global to all split packages in this recipe, and should match
#   other recipes with the same functionality.
#   i.e. ALTERNATIVE_LINK_NAME[bracket] = "/usr/bin/["
#
# NOTE: If ALTERNATIVE_LINK_NAME is not defined, it defaults to ${bindir}/name
#
# The default link to create for all targets
# ALTERNATIVE_TARGET = "target"
#
#   This is useful in a multicall binary case
#   i.e. ALTERNATIVE_TARGET = "/bin/busybox"
#
# A non-default link to create for a target
# ALTERNATIVE_TARGET[name] = "target"
#
#   This is the name of the binary as it's been install by do_install
#   i.e. ALTERNATIVE_TARGET[sh] = "/bin/bash"
#
# A package specific link for a target
# ALTERNATIVE_TARGET_<pkg>[name] = "target"
#
#   This is useful when a recipe provides multiple alternatives for the
#   same item.
#
# NOTE: If ALTERNATIVE_TARGET is not defined, it will inherit the value
# from ALTERNATIVE_LINK_NAME.
#
# NOTE: If the ALTERNATIVE_LINK_NAME and ALTERNATIVE_TARGET are the same,
# ALTERNATIVE_TARGET will have '.{BPN}' appended to it.  If the file
# referenced has not been renamed, it will also be renamed.  (This avoids
# the need to rename alternative files in the do_install step, but still
# supports it if necessary for some reason.)
#
# The default priority for any alternatives
# ALTERNATIVE_PRIORITY = "priority"
#
#   i.e. default is ALTERNATIVE_PRIORITY = "10"
#
# The non-default priority for a specific target
# ALTERNATIVE_PRIORITY[name] = "priority"
#
# The package priority for a specific target
# ALTERNATIVE_PRIORITY_<pkg>[name] = "priority"

ALTERNATIVE_PRIORITY = "10"

# We need special processing for vardeps because it can not work on
# modified flag values.  So we aggregate the flags into a new variable
# and include that vairable in the set.
UPDALTVARS  = "ALTERNATIVE ALTERNATIVE_LINK_NAME ALTERNATIVE_TARGET ALTERNATIVE_PRIORITY"

def gen_updatealternativesvardeps(d):
    pkgs = (d.getVar("PACKAGES", True) or "").split()
    vars = (d.getVar("UPDALTVARS", True) or "").split()

    # First compute them for non_pkg versions
    for v in vars:
        for flag in (d.getVarFlags(v) or {}):
            if flag == "doc" or flag == "vardeps" or flag == "vardepsexp":
                continue
            d.appendVar('%s_VARDEPS' % (v), ' %s:%s' % (flag, d.getVarFlag(v, flag, False)))

    for p in pkgs:
        for v in vars:
            for flag in (d.getVarFlags("%s_%s" % (v,p)) or {}):
                if flag == "doc" or flag == "vardeps" or flag == "vardepsexp":
                    continue
                d.appendVar('%s_VARDEPS_%s' % (v,p), ' %s:%s' % (flag, d.getVarFlag('%s_%s' % (v,p), flag, False)))

def ua_extend_depends(d):
    if not 'virtual/update-alternatives' in d.getVar('PROVIDES', True):
        d.appendVar('DEPENDS', ' virtual/${MLPREFIX}update-alternatives')

python __anonymous() {
    # Update Alternatives only works on target packages...
    if bb.data.inherits_class('native', d) or \
       bb.data.inherits_class('cross', d) or bb.data.inherits_class('crosssdk', d) or \
       bb.data.inherits_class('cross-canadian', d):
        return

    # compute special vardeps
    gen_updatealternativesvardeps(d)

    # extend the depends to include virtual/update-alternatives
    ua_extend_depends(d)
}

def gen_updatealternativesvars(d):
    ret = []
    pkgs = (d.getVar("PACKAGES", True) or "").split()
    vars = (d.getVar("UPDALTVARS", True) or "").split()

    for v in vars:
        ret.append(v + "_VARDEPS")

    for p in pkgs:
        for v in vars:
            ret.append(v + "_" + p)
            ret.append(v + "_VARDEPS_" + p)
    return " ".join(ret)

# Now the new stuff, we use a custom function to generate the right values
populate_packages[vardeps] += "${UPDALTVARS} ${@gen_updatealternativesvars(d)}"

# We need to do the rename after the image creation step, but before
# the split and strip steps..  packagecopy seems to be the earliest reasonable
# place.
python perform_packagecopy_append () {
    # Check for deprecated usage...
    pn = d.getVar('BPN', True)
    if d.getVar('ALTERNATIVE_LINKS', True) != None:
        bb.fatal('%s: Use of ALTERNATIVE_LINKS/ALTERNATIVE_PATH/ALTERNATIVE_NAME is no longer supported, please convert to the updated syntax, see update-alternatives.bbclass for more info.' % pn)

    # Do actual update alternatives processing
    pkgdest = d.getVar('PKGD', True)
    for pkg in (d.getVar('PACKAGES', True) or "").split():
        # If the src == dest, we know we need to rename the dest by appending ${BPN}
        link_rename = {}
        for alt_name in (d.getVar('ALTERNATIVE_%s' % pkg, True) or "").split():
            alt_link     = d.getVarFlag('ALTERNATIVE_LINK_NAME', alt_name, True)
            if not alt_link:
                alt_link = "%s/%s" % (d.getVar('bindir', True), alt_name)
                d.setVarFlag('ALTERNATIVE_LINK_NAME', alt_name, alt_link)

            alt_target   = d.getVarFlag('ALTERNATIVE_TARGET_%s' % pkg, alt_name, True) or d.getVarFlag('ALTERNATIVE_TARGET', alt_name, True)
            alt_target   = alt_target or d.getVar('ALTERNATIVE_TARGET_%s' % pkg, True) or d.getVar('ALTERNATIVE_TARGET', True) or alt_link
            # Sometimes alt_target is specified as relative to the link name.
            alt_target   = os.path.join(os.path.dirname(alt_link), alt_target)

            # If the link and target are the same name, we need to rename the target.
            if alt_link == alt_target:
                src = '%s/%s' % (pkgdest, alt_target)
                alt_target_rename = '%s.%s' % (alt_target, pn)
                dest = '%s/%s' % (pkgdest, alt_target_rename)
                if os.path.lexists(dest):
                    bb.note('%s: Already renamed: %s' % (pn, alt_target_rename))
                elif os.path.lexists(src):
                    if os.path.islink(src):
                        # Delay rename of links
                        link_rename[alt_target] = alt_target_rename
                    else:
                        bb.note('%s: Rename %s -> %s' % (pn, alt_target, alt_target_rename))
                        os.rename(src, dest)
                else:
                    bb.warn("%s: alternative target (%s or %s) does not exist, skipping..." % (pn, alt_target, alt_target_rename))
                    continue
                d.setVarFlag('ALTERNATIVE_TARGET_%s' % pkg, alt_name, alt_target_rename)

        # Process delayed link names
        # Do these after other renames so we can correct broken links
        for alt_target in link_rename:
            src = '%s/%s' % (pkgdest, alt_target)
            dest = '%s/%s' % (pkgdest, link_rename[alt_target])
            link = os.readlink(src)
            link_target = oe.path.realpath(src, pkgdest, True)

            if os.path.lexists(link_target):
                # Ok, the link_target exists, we can rename
                bb.note('%s: Rename (link) %s -> %s' % (pn, alt_target, link_rename[alt_target]))
                os.rename(src, dest)
            else:
                # Try to resolve the broken link to link.${BPN}
                link_maybe = '%s.%s' % (os.readlink(src), pn)
                if os.path.lexists(os.path.join(os.path.dirname(src), link_maybe)):
                    # Ok, the renamed link target exists.. create a new link, and remove the original
                    bb.note('%s: Creating new link %s -> %s' % (pn, link_rename[alt_target], link_maybe))
                    os.symlink(link_maybe, dest)
                    os.unlink(src)
                else:
                    bb.warn('%s: Unable to resolve dangling symlink: %s' % (pn, alt_target))
}

PACKAGESPLITFUNCS_prepend = "populate_packages_updatealternatives "

python populate_packages_updatealternatives () {
    pn = d.getVar('BPN', True)

    # Do actual update alternatives processing
    pkgdest = d.getVar('PKGD', True)
    for pkg in (d.getVar('PACKAGES', True) or "").split():
        # Create post install/removal scripts
        alt_setup_links = ""
        alt_remove_links = ""
        for alt_name in (d.getVar('ALTERNATIVE_%s' % pkg, True) or "").split():
            alt_link     = d.getVarFlag('ALTERNATIVE_LINK_NAME', alt_name, True)
            alt_target   = d.getVarFlag('ALTERNATIVE_TARGET_%s' % pkg, alt_name, True) or d.getVarFlag('ALTERNATIVE_TARGET', alt_name, True)
            alt_target   = alt_target or d.getVar('ALTERNATIVE_TARGET_%s' % pkg, True) or d.getVar('ALTERNATIVE_TARGET', True) or alt_link
            # Sometimes alt_target is specified as relative to the link name.
            alt_target   = os.path.join(os.path.dirname(alt_link), alt_target)

            alt_priority = d.getVarFlag('ALTERNATIVE_PRIORITY_%s' % pkg,  alt_name, True) or d.getVarFlag('ALTERNATIVE_PRIORITY',  alt_name, True)
            alt_priority = alt_priority or d.getVar('ALTERNATIVE_PRIORITY_%s' % pkg, True) or d.getVar('ALTERNATIVE_PRIORITY', True)

            # This shouldn't trigger, as it should have been resolved earlier!
            if alt_link == alt_target:
                bb.note('alt_link == alt_target: %s == %s -- correcting, this should not happen!' % (alt_link, alt_target))
                alt_target = '%s.%s' % (alt_target, pn)

            if not os.path.lexists('%s/%s' % (pkgdest, alt_target)):
                bb.warn('%s: NOT adding alternative provide %s: %s does not exist' % (pn, alt_link, alt_target))
                continue

            # Default to generate shell script.. eventually we may want to change this...
            alt_target = os.path.normpath(alt_target)

            alt_setup_links  += '\tupdate-alternatives --install %s %s %s %s\n' % (alt_link, alt_name, alt_target, alt_priority)
            alt_remove_links += '\tupdate-alternatives --remove  %s %s\n' % (alt_name, alt_target)

        if alt_setup_links:
            # RDEPENDS setup
            provider = d.getVar('VIRTUAL-RUNTIME_update-alternatives', True)
            if provider:
                #bb.note('adding runtime requirement for update-alternatives for %s' % pkg)
                d.appendVar('RDEPENDS_%s' % pkg, ' ' + d.getVar('MLPREFIX', False) + provider)

            bb.note('adding update-alternatives calls to postinst/prerm for %s' % pkg)
            bb.note('%s' % alt_setup_links)
            postinst = d.getVar('pkg_postinst_%s' % pkg, True) or '#!/bin/sh\n'
            postinst += alt_setup_links
            d.setVar('pkg_postinst_%s' % pkg, postinst)

            bb.note('%s' % alt_remove_links)
            prerm = d.getVar('pkg_prerm_%s' % pkg, True) or '#!/bin/sh\n'
            prerm += alt_remove_links
            d.setVar('pkg_prerm_%s' % pkg, prerm)
}

python package_do_filedeps_append () {
    pn = d.getVar('BPN', True)
    pkgdest = d.getVar('PKGDEST', True)

    for pkg in packages.split():
        for alt_name in (d.getVar('ALTERNATIVE_%s' % pkg, True) or "").split():
            alt_link     = d.getVarFlag('ALTERNATIVE_LINK_NAME', alt_name, True)
            alt_target   = d.getVarFlag('ALTERNATIVE_TARGET_%s' % pkg, alt_name, True) or d.getVarFlag('ALTERNATIVE_TARGET', alt_name, True)
            alt_target   = alt_target or d.getVar('ALTERNATIVE_TARGET_%s' % pkg, True) or d.getVar('ALTERNATIVE_TARGET', True) or alt_link

            if alt_link == alt_target:
                bb.warn('%s: alt_link == alt_target: %s == %s' % (pn, alt_link, alt_target))
                alt_target = '%s.%s' % (alt_target, pn)

            if not os.path.lexists('%s/%s/%s' % (pkgdest, pkg, alt_target)):
                continue

            # Add file provide
            trans_target = oe.package.file_translate(alt_target)
            d.appendVar('FILERPROVIDES_%s_%s' % (trans_target, pkg), " " + alt_link)
            if not trans_target in (d.getVar('FILERPROVIDESFLIST_%s' % pkg, True) or ""):
                d.appendVar('FILERPROVIDESFLIST_%s' % pkg, " " + trans_target)
}

