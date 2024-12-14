# Utility functions for reading and modifying recipes
#
# Some code borrowed from the OE layer index
#
# Copyright (C) 2013-2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import sys
import os
import os.path
import tempfile
import textwrap
import difflib
from . import utils
import shutil
import re
import fnmatch
import glob
import bb.tinfoil

from collections import OrderedDict, defaultdict
from bb.utils import vercmp_string

# Help us to find places to insert values
recipe_progression = ['SUMMARY', 'DESCRIPTION', 'HOMEPAGE', 'BUGTRACKER', 'SECTION', 'LICENSE', 'LICENSE_FLAGS', 'LIC_FILES_CHKSUM', 'PROVIDES', 'DEPENDS', 'PR', 'PV', 'SRCREV', 'SRC_URI', 'S', 'do_fetch()', 'do_unpack()', 'do_patch()', 'EXTRA_OECONF', 'EXTRA_OECMAKE', 'EXTRA_OESCONS', 'do_configure()', 'EXTRA_OEMAKE', 'do_compile()', 'do_install()', 'do_populate_sysroot()', 'INITSCRIPT', 'USERADD', 'GROUPADD', 'PACKAGES', 'FILES', 'RDEPENDS', 'RRECOMMENDS', 'RSUGGESTS', 'RPROVIDES', 'RREPLACES', 'RCONFLICTS', 'ALLOW_EMPTY', 'populate_packages()', 'do_package()', 'do_deploy()', 'BBCLASSEXTEND']
# Variables that sometimes are a bit long but shouldn't be wrapped
nowrap_vars = ['SUMMARY', 'HOMEPAGE', 'BUGTRACKER', r'SRC_URI\[(.+\.)?md5sum\]', r'SRC_URI\[(.+\.)?sha[0-9]+sum\]']
list_vars = ['SRC_URI', 'LIC_FILES_CHKSUM']
meta_vars = ['SUMMARY', 'DESCRIPTION', 'HOMEPAGE', 'BUGTRACKER', 'SECTION']


def simplify_history(history, d):
    """
    Eliminate any irrelevant events from a variable history
    """
    ret_history = []
    has_set = False
    # Go backwards through the history and remove any immediate operations
    # before the most recent set
    for event in reversed(history):
        if 'flag' in event or not 'file' in event:
            continue
        if event['op'] == 'set':
            if has_set:
                continue
            has_set = True
        elif event['op'] in ('append', 'prepend', 'postdot', 'predot'):
            # Reminder: "append" and "prepend" mean += and =+ respectively, NOT :append / :prepend
            if has_set:
                continue
        ret_history.insert(0, event)
    return ret_history


def get_var_files(fn, varlist, d):
    """Find the file in which each of a list of variables is set.
    Note: requires variable history to be enabled when parsing.
    """
    varfiles = {}
    for v in varlist:
        files = []
        if '[' in v:
            varsplit = v.split('[')
            varflag = varsplit[1].split(']')[0]
            history = d.varhistory.variable(varsplit[0])
            for event in history:
                if 'file' in event and event.get('flag', '') == varflag:
                    files.append(event['file'])
        else:
            history = d.varhistory.variable(v)
            for event in history:
                if 'file' in event and not 'flag' in event:
                    files.append(event['file'])
        if files:
            actualfile = files[-1]
        else:
            actualfile = None
        varfiles[v] = actualfile

    return varfiles


def split_var_value(value, assignment=True):
    """
    Split a space-separated variable's value into a list of items,
    taking into account that some of the items might be made up of
    expressions containing spaces that should not be split.
    Parameters:
        value:
            The string value to split
        assignment:
            True to assume that the value represents an assignment
            statement, False otherwise. If True, and an assignment
            statement is passed in the first item in
            the returned list will be the part of the assignment
            statement up to and including the opening quote character,
            and the last item will be the closing quote.
    """
    inexpr = 0
    lastchar = None
    out = []
    buf = ''
    for char in value:
        if char == '{':
            if lastchar == '$':
                inexpr += 1
        elif char == '}':
            inexpr -= 1
        elif assignment and char in '"\'' and inexpr == 0:
            if buf:
                out.append(buf)
            out.append(char)
            char = ''
            buf = ''
        elif char.isspace() and inexpr == 0:
            char = ''
            if buf:
                out.append(buf)
            buf = ''
        buf += char
        lastchar = char
    if buf:
        out.append(buf)

    # Join together assignment statement and opening quote
    outlist = out
    if assignment:
        assigfound = False
        for idx, item in enumerate(out):
            if '=' in item:
                assigfound = True
            if assigfound:
                if '"' in item or "'" in item:
                    outlist = [' '.join(out[:idx+1])]
                    outlist.extend(out[idx+1:])
                    break
    return outlist


def patch_recipe_lines(fromlines, values, trailing_newline=True):
    """Update or insert variable values into lines from a recipe.
       Note that some manual inspection/intervention may be required
       since this cannot handle all situations.
    """

    import bb.utils

    if trailing_newline:
        newline = '\n'
    else:
        newline = ''

    nowrap_vars_res = []
    for item in nowrap_vars:
        nowrap_vars_res.append(re.compile('^%s$' % item))

    recipe_progression_res = []
    recipe_progression_restrs = []
    for item in recipe_progression:
        if item.endswith('()'):
            key = item[:-2]
        else:
            key = item
        restr = r'%s(_[a-zA-Z0-9-_$(){}]+|\[[^\]]*\])?' % key
        if item.endswith('()'):
            recipe_progression_restrs.append(restr + '()')
        else:
            recipe_progression_restrs.append(restr)
        recipe_progression_res.append(re.compile('^%s$' % restr))

    def get_recipe_pos(variable):
        for i, p in enumerate(recipe_progression_res):
            if p.match(variable):
                return i
        return -1

    remainingnames = {}
    for k in values.keys():
        remainingnames[k] = get_recipe_pos(k)
    remainingnames = OrderedDict(sorted(remainingnames.items(), key=lambda x: x[1]))

    modifying = False

    def outputvalue(name, lines, rewindcomments=False):
        if values[name] is None:
            return
        if isinstance(values[name], tuple):
            op, value = values[name]
            if op == '+=' and value.strip() == '':
                return
        else:
            value = values[name]
            op = '='
        rawtext = '%s %s "%s"%s' % (name, op, value, newline)
        addlines = []
        nowrap = False
        for nowrap_re in nowrap_vars_res:
            if nowrap_re.match(name):
                nowrap = True
                break
        if nowrap:
            addlines.append(rawtext)
        elif name in list_vars:
            splitvalue = split_var_value(value, assignment=False)
            if len(splitvalue) > 1:
                linesplit = ' \\\n' + (' ' * (len(name) + 4))
                addlines.append('%s %s "%s%s"%s' % (name, op, linesplit.join(splitvalue), linesplit, newline))
            else:
                addlines.append(rawtext)
        else:
            wrapped = textwrap.wrap(rawtext)
            for wrapline in wrapped[:-1]:
                addlines.append('%s \\%s' % (wrapline, newline))
            addlines.append('%s%s' % (wrapped[-1], newline))

        # Split on newlines - this isn't strictly necessary if you are only
        # going to write the output to disk, but if you want to compare it
        # (as patch_recipe_file() will do if patch=True) then it's important.
        addlines = [line for l in addlines for line in l.splitlines(True)]
        if rewindcomments:
            # Ensure we insert the lines before any leading comments
            # (that we'd want to ensure remain leading the next value)
            for i, ln in reversed(list(enumerate(lines))):
                if not ln.startswith('#'):
                    lines[i+1:i+1] = addlines
                    break
            else:
                lines.extend(addlines)
        else:
            lines.extend(addlines)

    existingnames = []
    def patch_recipe_varfunc(varname, origvalue, op, newlines):
        if modifying:
            # Insert anything that should come before this variable
            pos = get_recipe_pos(varname)
            for k in list(remainingnames):
                if remainingnames[k] > -1 and pos >= remainingnames[k] and not k in existingnames:
                    outputvalue(k, newlines, rewindcomments=True)
                    del remainingnames[k]
            # Now change this variable, if it needs to be changed
            if varname in existingnames and op in ['+=', '=', '=+']:
                if varname in remainingnames:
                    outputvalue(varname, newlines)
                    del remainingnames[varname]
                return None, None, 0, True
        else:
            if varname in values:
                existingnames.append(varname)
        return origvalue, None, 0, True

    # First run - establish which values we want to set are already in the file
    varlist = [re.escape(item) for item in values.keys()]
    bb.utils.edit_metadata(fromlines, varlist, patch_recipe_varfunc)
    # Second run - actually set everything
    modifying = True
    varlist.extend(recipe_progression_restrs)
    changed, tolines = bb.utils.edit_metadata(fromlines, varlist, patch_recipe_varfunc, match_overrides=True)

    if remainingnames:
        if tolines and tolines[-1].strip() != '':
            tolines.append('\n')
        for k in remainingnames.keys():
            outputvalue(k, tolines)

    return changed, tolines


def patch_recipe_file(fn, values, patch=False, relpath='', redirect_output=None):
    """Update or insert variable values into a recipe file (assuming you
       have already identified the exact file you want to update.)
       Note that some manual inspection/intervention may be required
       since this cannot handle all situations.
    """

    with open(fn, 'r') as f:
        fromlines = f.readlines()

    _, tolines = patch_recipe_lines(fromlines, values)

    if redirect_output:
        with open(os.path.join(redirect_output, os.path.basename(fn)), 'w') as f:
            f.writelines(tolines)
        return None
    elif patch:
        relfn = os.path.relpath(fn, relpath)
        diff = difflib.unified_diff(fromlines, tolines, 'a/%s' % relfn, 'b/%s' % relfn)
        return diff
    else:
        with open(fn, 'w') as f:
            f.writelines(tolines)
        return None


def localise_file_vars(fn, varfiles, varlist):
    """Given a list of variables and variable history (fetched with get_var_files())
    find where each variable should be set/changed. This handles for example where a
    recipe includes an inc file where variables might be changed - in most cases
    we want to update the inc file when changing the variable value rather than adding
    it to the recipe itself.
    """
    fndir = os.path.dirname(fn) + os.sep

    first_meta_file = None
    for v in meta_vars:
        f = varfiles.get(v, None)
        if f:
            actualdir = os.path.dirname(f) + os.sep
            if actualdir.startswith(fndir):
                first_meta_file = f
                break

    filevars = defaultdict(list)
    for v in varlist:
        f = varfiles[v]
        # Only return files that are in the same directory as the recipe or in some directory below there
        # (this excludes bbclass files and common inc files that wouldn't be appropriate to set the variable
        # in if we were going to set a value specific to this recipe)
        if f:
            actualfile = f
        else:
            # Variable isn't in a file, if it's one of the "meta" vars, use the first file with a meta var in it
            if first_meta_file:
                actualfile = first_meta_file
            else:
                actualfile = fn

        actualdir = os.path.dirname(actualfile) + os.sep
        if not actualdir.startswith(fndir):
            actualfile = fn
        filevars[actualfile].append(v)

    return filevars

def patch_recipe(d, fn, varvalues, patch=False, relpath='', redirect_output=None):
    """Modify a list of variable values in the specified recipe. Handles inc files if
    used by the recipe.
    """
    overrides = d.getVar('OVERRIDES').split(':')
    def override_applicable(hevent):
        op = hevent['op']
        if '[' in op:
            opoverrides = op.split('[')[1].split(']')[0].split(':')
            for opoverride in opoverrides:
                if not opoverride in overrides:
                    return False
        return True

    varlist = varvalues.keys()
    fn = os.path.abspath(fn)
    varfiles = get_var_files(fn, varlist, d)
    locs = localise_file_vars(fn, varfiles, varlist)
    patches = []
    for f,v in locs.items():
        vals = {k: varvalues[k] for k in v}
        f = os.path.abspath(f)
        if f == fn:
            extravals = {}
            for var, value in vals.items():
                if var in list_vars:
                    history = simplify_history(d.varhistory.variable(var), d)
                    recipe_set = False
                    for event in history:
                        if os.path.abspath(event['file']) == fn:
                            if event['op'] == 'set':
                                recipe_set = True
                    if not recipe_set:
                        for event in history:
                            if event['op'].startswith(':remove'):
                                continue
                            if not override_applicable(event):
                                continue
                            newvalue = value.replace(event['detail'], '')
                            if newvalue == value and os.path.abspath(event['file']) == fn and event['op'].startswith(':'):
                                op = event['op'].replace('[', ':').replace(']', '')
                                extravals[var + op] = None
                            value = newvalue
                            vals[var] = ('+=', value)
            vals.update(extravals)
        patchdata = patch_recipe_file(f, vals, patch, relpath, redirect_output)
        if patch:
            patches.append(patchdata)

    if patch:
        return patches
    else:
        return None



def copy_recipe_files(d, tgt_dir, whole_dir=False, download=True, all_variants=False):
    """Copy (local) recipe files, including both files included via include/require,
    and files referred to in the SRC_URI variable."""
    import bb.fetch2
    import oe.path

    # FIXME need a warning if the unexpanded SRC_URI value contains variable references

    uri_values = []
    localpaths = []
    def fetch_urls(rdata):
        # Collect the local paths from SRC_URI
        srcuri = rdata.getVar('SRC_URI') or ""
        if srcuri not in uri_values:
            fetch = bb.fetch2.Fetch(srcuri.split(), rdata)
            if download:
                fetch.download()
            for pth in fetch.localpaths():
                if pth not in localpaths:
                    localpaths.append(os.path.abspath(pth))
            uri_values.append(srcuri)

    fetch_urls(d)
    if all_variants:
        # Get files for other variants e.g. in the case of a SRC_URI:append
        localdata = bb.data.createCopy(d)
        variants = (localdata.getVar('BBCLASSEXTEND') or '').split()
        if variants:
            # Ensure we handle class-target if we're dealing with one of the variants
            variants.append('target')
            for variant in variants:
                localdata.setVar('CLASSOVERRIDE', 'class-%s' % variant)
                fetch_urls(localdata)

    # Copy local files to target directory and gather any remote files
    bb_dir = os.path.abspath(os.path.dirname(d.getVar('FILE'))) + os.sep
    remotes = []
    copied = []
    # Need to do this in two steps since we want to check against the absolute path
    includes = [os.path.abspath(path) for path in d.getVar('BBINCLUDED').split() if os.path.exists(path)]
    # We also check this below, but we don't want any items in this list being considered remotes
    includes = [path for path in includes if path.startswith(bb_dir)]
    for path in localpaths + includes:
        # Only import files that are under the meta directory
        if path.startswith(bb_dir):
            if not whole_dir:
                relpath = os.path.relpath(path, bb_dir)
                subdir = os.path.join(tgt_dir, os.path.dirname(relpath))
                if not os.path.exists(subdir):
                    os.makedirs(subdir)
                shutil.copy2(path, os.path.join(tgt_dir, relpath))
                copied.append(relpath)
        else:
            remotes.append(path)
    # Simply copy whole meta dir, if requested
    if whole_dir:
        shutil.copytree(bb_dir, tgt_dir)

    return copied, remotes


def get_recipe_local_files(d, patches=False, archives=False):
    """Get a list of local files in SRC_URI within a recipe."""
    import oe.patch
    uris = (d.getVar('SRC_URI') or "").split()
    fetch = bb.fetch2.Fetch(uris, d)
    # FIXME this list should be factored out somewhere else (such as the
    # fetcher) though note that this only encompasses actual container formats
    # i.e. that can contain multiple files as opposed to those that only
    # contain a compressed stream (i.e. .tar.gz as opposed to just .gz)
    archive_exts = ['.tar', '.tgz', '.tar.gz', '.tar.Z', '.tbz', '.tbz2', '.tar.bz2', '.txz', '.tar.xz', '.tar.lz', '.zip', '.jar', '.rpm', '.srpm', '.deb', '.ipk', '.tar.7z', '.7z']
    ret = {}
    for uri in uris:
        if fetch.ud[uri].type == 'file':
            if (not patches and
                    oe.patch.patch_path(uri, fetch, '', expand=False)):
                continue
            # Skip files that are referenced by absolute path
            fname = fetch.ud[uri].basepath
            if os.path.isabs(fname):
                continue
            # Handle subdir=
            subdir = fetch.ud[uri].parm.get('subdir', '')
            if subdir:
                if os.path.isabs(subdir):
                    continue
                fname = os.path.join(subdir, fname)
            localpath = fetch.localpath(uri)
            if not archives:
                # Ignore archives that will be unpacked
                if localpath.endswith(tuple(archive_exts)):
                    unpack = fetch.ud[uri].parm.get('unpack', True)
                    if unpack:
                        continue
            if os.path.isdir(localpath):
                for root, dirs, files in os.walk(localpath):
                    for fname in files:
                        fileabspath = os.path.join(root,fname)
                        srcdir = os.path.dirname(localpath)
                        ret[os.path.relpath(fileabspath,srcdir)] = fileabspath
            else:
                ret[fname] = localpath
    return ret


def get_recipe_patches(d):
    """Get a list of the patches included in SRC_URI within a recipe."""
    import oe.patch
    patches = oe.patch.src_patches(d, expand=False)
    patchfiles = []
    for patch in patches:
        _, _, local, _, _, parm = bb.fetch.decodeurl(patch)
        patchfiles.append(local)
    return patchfiles


def get_recipe_patched_files(d):
    """
    Get the list of patches for a recipe along with the files each patch modifies.
    Params:
        d: the datastore for the recipe
    Returns:
        a dict mapping patch file path to a list of tuples of changed files and
        change mode ('A' for add, 'D' for delete or 'M' for modify)
    """
    import oe.patch
    patches = oe.patch.src_patches(d, expand=False)
    patchedfiles = {}
    for patch in patches:
        _, _, patchfile, _, _, parm = bb.fetch.decodeurl(patch)
        striplevel = int(parm['striplevel'])
        patchedfiles[patchfile] = oe.patch.PatchSet.getPatchedFiles(patchfile, striplevel, os.path.join(d.getVar('S'), parm.get('patchdir', '')))
    return patchedfiles


def validate_pn(pn):
    """Perform validation on a recipe name (PN) for a new recipe."""
    reserved_names = ['forcevariable', 'append', 'prepend', 'remove']
    if not re.match('^[0-9a-z-.+]+$', pn):
        return 'Recipe name "%s" is invalid: only characters 0-9, a-z, -, + and . are allowed' % pn
    elif pn in reserved_names:
        return 'Recipe name "%s" is invalid: is a reserved keyword' % pn
    elif pn.startswith('pn-'):
        return 'Recipe name "%s" is invalid: names starting with "pn-" are reserved' % pn
    elif pn.endswith(('.bb', '.bbappend', '.bbclass', '.inc', '.conf')):
        return 'Recipe name "%s" is invalid: should be just a name, not a file name' % pn
    return ''


def get_bbfile_path(d, destdir, extrapathhint=None):
    """
    Determine the correct path for a recipe within a layer
    Parameters:
        d: Recipe-specific datastore
        destdir: destination directory. Can be the path to the base of the layer or a
            partial path somewhere within the layer.
        extrapathhint: a path relative to the base of the layer to try
    """
    import bb.cookerdata

    destdir = os.path.abspath(destdir)
    destlayerdir = find_layerdir(destdir)

    # Parse the specified layer's layer.conf file directly, in case the layer isn't in bblayers.conf
    confdata = d.createCopy()
    confdata.setVar('BBFILES', '')
    confdata.setVar('LAYERDIR', destlayerdir)
    destlayerconf = os.path.join(destlayerdir, "conf", "layer.conf")
    confdata = bb.cookerdata.parse_config_file(destlayerconf, confdata)
    pn = d.getVar('PN')

    # Parse BBFILES_DYNAMIC and append to BBFILES
    bbfiles_dynamic = (confdata.getVar('BBFILES_DYNAMIC') or "").split()
    collections = (confdata.getVar('BBFILE_COLLECTIONS') or "").split()
    invalid = []
    for entry in bbfiles_dynamic:
        parts = entry.split(":", 1)
        if len(parts) != 2:
            invalid.append(entry)
            continue
        l, f = parts
        invert = l[0] == "!"
        if invert:
            l = l[1:]
        if (l in collections and not invert) or (l not in collections and invert):
            confdata.appendVar("BBFILES", " " + f)
    if invalid:
        return None
    bbfilespecs = (confdata.getVar('BBFILES') or '').split()
    if destdir == destlayerdir:
        for bbfilespec in bbfilespecs:
            if not bbfilespec.endswith('.bbappend'):
                for match in glob.glob(bbfilespec):
                    splitext = os.path.splitext(os.path.basename(match))
                    if splitext[1] == '.bb':
                        mpn = splitext[0].split('_')[0]
                        if mpn == pn:
                            return os.path.dirname(match)

    # Try to make up a path that matches BBFILES
    # this is a little crude, but better than nothing
    bpn = d.getVar('BPN')
    recipefn = os.path.basename(d.getVar('FILE'))
    pathoptions = [destdir]
    if extrapathhint:
        pathoptions.append(os.path.join(destdir, extrapathhint))
    if destdir == destlayerdir:
        pathoptions.append(os.path.join(destdir, 'recipes-%s' % bpn, bpn))
        pathoptions.append(os.path.join(destdir, 'recipes', bpn))
        pathoptions.append(os.path.join(destdir, bpn))
    elif not destdir.endswith(('/' + pn, '/' + bpn)):
        pathoptions.append(os.path.join(destdir, bpn))
    closepath = ''
    for pathoption in pathoptions:
        bbfilepath = os.path.join(pathoption, 'test.bb')
        for bbfilespec in bbfilespecs:
            if fnmatch.fnmatchcase(bbfilepath, bbfilespec):
                return pathoption
    return None

def get_bbappend_path(d, destlayerdir, wildcardver=False):
    """Determine how a bbappend for a recipe should be named and located within another layer"""

    import bb.cookerdata

    destlayerdir = os.path.abspath(destlayerdir)
    recipefile = d.getVar('FILE')
    recipefn = os.path.splitext(os.path.basename(recipefile))[0]
    if wildcardver and '_' in recipefn:
        recipefn = recipefn.split('_', 1)[0] + '_%'
    appendfn = recipefn + '.bbappend'

    # Parse the specified layer's layer.conf file directly, in case the layer isn't in bblayers.conf
    confdata = d.createCopy()
    confdata.setVar('BBFILES', '')
    confdata.setVar('LAYERDIR', destlayerdir)
    destlayerconf = os.path.join(destlayerdir, "conf", "layer.conf")
    confdata = bb.cookerdata.parse_config_file(destlayerconf, confdata)

    origlayerdir = find_layerdir(recipefile)
    if not origlayerdir:
        return (None, False)
    # Now join this to the path where the bbappend is going and check if it is covered by BBFILES
    appendpath = os.path.join(destlayerdir, os.path.relpath(os.path.dirname(recipefile), origlayerdir), appendfn)
    closepath = ''
    pathok = True
    for bbfilespec in confdata.getVar('BBFILES').split():
        if fnmatch.fnmatchcase(appendpath, bbfilespec):
            # Our append path works, we're done
            break
        elif bbfilespec.startswith(destlayerdir) and fnmatch.fnmatchcase('test.bbappend', os.path.basename(bbfilespec)):
            # Try to find the longest matching path
            if len(bbfilespec) > len(closepath):
                closepath = bbfilespec
    else:
        # Unfortunately the bbappend layer and the original recipe's layer don't have the same structure
        if closepath:
            # bbappend layer's layer.conf at least has a spec that picks up .bbappend files
            # Now we just need to substitute out any wildcards
            appendsubdir = os.path.relpath(os.path.dirname(closepath), destlayerdir)
            if 'recipes-*' in appendsubdir:
                # Try to copy this part from the original recipe path
                res = re.search('/recipes-[^/]+/', recipefile)
                if res:
                    appendsubdir = appendsubdir.replace('/recipes-*/', res.group(0))
            # This is crude, but we have to do something
            appendsubdir = appendsubdir.replace('*', recipefn.split('_')[0])
            appendsubdir = appendsubdir.replace('?', 'a')
            appendpath = os.path.join(destlayerdir, appendsubdir, appendfn)
        else:
            pathok = False
    return (appendpath, pathok)


def bbappend_recipe(rd, destlayerdir, srcfiles, install=None, wildcardver=False, machine=None, extralines=None, removevalues=None, redirect_output=None, params=None, update_original_recipe=False):
    """
    Writes a bbappend file for a recipe
    Parameters:
        rd: data dictionary for the recipe
        destlayerdir: base directory of the layer to place the bbappend in
            (subdirectory path from there will be determined automatically)
        srcfiles: dict of source files to add to SRC_URI, where the key
            is the full path to the file to be added, and the value is a
            dict with following optional keys:
                path: the original filename as it would appear in SRC_URI
                    or None if it isn't already present.
                patchdir: the patchdir parameter
                newname: the name to give to the new added file. None to use
                    the default value: basename(path)
            You may pass None for this parameter if you simply want to specify
            your own content via the extralines parameter.
        install: dict mapping entries in srcfiles to a tuple of two elements:
            install path (*without* ${D} prefix) and permission value (as a
            string, e.g. '0644').
        wildcardver: True to use a % wildcard in the bbappend filename, or
            False to make the bbappend specific to the recipe version.
        machine:
            If specified, make the changes in the bbappend specific to this
            machine. This will also cause PACKAGE_ARCH = "${MACHINE_ARCH}"
            to be added to the bbappend.
        extralines:
            Extra lines to add to the bbappend. This may be a dict of name
            value pairs, or simply a list of the lines.
        removevalues:
            Variable values to remove - a dict of names/values.
        redirect_output:
            If specified, redirects writing the output file to the
            specified directory (for dry-run purposes)
        params:
            Parameters to use when adding entries to SRC_URI. If specified,
            should be a list of dicts with the same length as srcfiles.
        update_original_recipe:
            Force to update the original recipe instead of creating/updating
            a bbapend. destlayerdir must contain the original recipe
    """

    if not removevalues:
        removevalues = {}

    recipefile = rd.getVar('FILE')
    if update_original_recipe:
        if destlayerdir not in recipefile:
            bb.error("destlayerdir %s doesn't contain the original recipe (%s), cannot update it" % (destlayerdir, recipefile))
            return (None, None)

        appendpath = recipefile
    else:
        # Determine how the bbappend should be named
        appendpath, pathok = get_bbappend_path(rd, destlayerdir, wildcardver)
        if not appendpath:
            bb.error('Unable to determine layer directory containing %s' % recipefile)
            return (None, None)
        if not pathok:
            bb.warn('Unable to determine correct subdirectory path for bbappend file - check that what %s adds to BBFILES also matches .bbappend files. Using %s for now, but until you fix this the bbappend will not be applied.' % (os.path.join(destlayerdir, 'conf', 'layer.conf'), os.path.dirname(appendpath)))

    appenddir = os.path.dirname(appendpath)
    if not redirect_output:
        bb.utils.mkdirhier(appenddir)

    # FIXME check if the bbappend doesn't get overridden by a higher priority layer?

    layerdirs = [os.path.abspath(layerdir) for layerdir in rd.getVar('BBLAYERS').split()]
    if not os.path.abspath(destlayerdir) in layerdirs:
        bb.warn('Specified layer is not currently enabled in bblayers.conf, you will need to add it before this bbappend will be active')

    bbappendlines = []
    if extralines:
        if isinstance(extralines, dict):
            for name, value in extralines.items():
                bbappendlines.append((name, '=', value))
        else:
            # Do our best to split it
            for line in extralines:
                if line[-1] == '\n':
                    line = line[:-1]
                splitline = line.split(None, 2)
                if len(splitline) == 3:
                    bbappendlines.append(tuple(splitline))
                else:
                    raise Exception('Invalid extralines value passed')

    def popline(varname):
        for i in range(0, len(bbappendlines)):
            if bbappendlines[i][0] == varname:
                line = bbappendlines.pop(i)
                return line
        return None

    def appendline(varname, op, value):
        for i in range(0, len(bbappendlines)):
            item = bbappendlines[i]
            if item[0] == varname:
                bbappendlines[i] = (item[0], item[1], item[2] + ' ' + value)
                break
        else:
            bbappendlines.append((varname, op, value))

    destsubdir = rd.getVar('PN')
    if not update_original_recipe and srcfiles:
        bbappendlines.append(('FILESEXTRAPATHS:prepend', ':=', '${THISDIR}/${PN}:'))

    appendoverride = ''
    if machine:
        bbappendlines.append(('PACKAGE_ARCH', '=', '${MACHINE_ARCH}'))
        appendoverride = ':%s' % machine
    copyfiles = {}
    if srcfiles:
        instfunclines = []
        for i, (newfile, param) in enumerate(srcfiles.items()):
            srcurientry = None
            if not 'path' in param or not param['path']:
                if 'newname' in param and param['newname']:
                    srcfile = param['newname']
                else:
                    srcfile = os.path.basename(newfile)
                srcurientry = 'file://%s' % srcfile
                oldentry = None
                for uri in rd.getVar('SRC_URI').split():
                    if srcurientry in uri:
                        oldentry = uri
                if params and params[i]:
                    srcurientry = '%s;%s' % (srcurientry, ';'.join('%s=%s' % (k,v) for k,v in params[i].items()))
                # Double-check it's not there already
                # FIXME do we care if the entry is added by another bbappend that might go away?
                if not srcurientry in rd.getVar('SRC_URI').split():
                    if machine:
                        if oldentry:
                            appendline('SRC_URI:remove%s' % appendoverride, '=', ' ' + oldentry)
                        appendline('SRC_URI:append%s' % appendoverride, '=', ' ' + srcurientry)
                    else:
                        if oldentry:
                            if update_original_recipe:
                                removevalues['SRC_URI'] = oldentry
                            else:
                                appendline('SRC_URI:remove', '=', oldentry)
                        appendline('SRC_URI', '+=', srcurientry)
                param['path'] = srcfile
            else:
                srcfile = param['path']
            copyfiles[newfile] = param
            if install:
                institem = install.pop(newfile, None)
                if institem:
                    (destpath, perms) = institem
                    instdestpath = replace_dir_vars(destpath, rd)
                    instdirline = 'install -d ${D}%s' % os.path.dirname(instdestpath)
                    if not instdirline in instfunclines:
                        instfunclines.append(instdirline)
                    instfunclines.append('install -m %s ${UNPACKDIR}/%s ${D}%s' % (perms, os.path.basename(srcfile), instdestpath))
        if instfunclines:
            bbappendlines.append(('do_install:append%s()' % appendoverride, '', instfunclines))

    if redirect_output:
        bb.note('Writing append file %s (dry-run)' % appendpath)
        outfile = os.path.join(redirect_output, os.path.basename(appendpath))
        # Only take a copy if the file isn't already there (this function may be called
        # multiple times per operation when we're handling overrides)
        if os.path.exists(appendpath) and not os.path.exists(outfile):
            shutil.copy2(appendpath, outfile)
    elif update_original_recipe:
        outfile = recipefile
    else:
        bb.note('Writing append file %s' % appendpath)
        outfile = appendpath

    if os.path.exists(outfile):
        # Work around lack of nonlocal in python 2
        extvars = {'destsubdir': destsubdir}

        def appendfile_varfunc(varname, origvalue, op, newlines):
            if varname == 'FILESEXTRAPATHS:prepend':
                if origvalue.startswith('${THISDIR}/'):
                    popline('FILESEXTRAPATHS:prepend')
                    extvars['destsubdir'] = rd.expand(origvalue.split('${THISDIR}/', 1)[1].rstrip(':'))
            elif varname == 'PACKAGE_ARCH':
                if machine:
                    popline('PACKAGE_ARCH')
                    return (machine, None, 4, False)
            elif varname.startswith('do_install:append'):
                func = popline(varname)
                if func:
                    instfunclines = [line.strip() for line in origvalue.strip('\n').splitlines()]
                    for line in func[2]:
                        if not line in instfunclines:
                            instfunclines.append(line)
                    return (instfunclines, None, 4, False)
            else:
                splitval = split_var_value(origvalue, assignment=False)
                changed = False
                removevar = varname
                if varname in ['SRC_URI', 'SRC_URI:append%s' % appendoverride]:
                    removevar = 'SRC_URI'
                    line = popline(varname)
                    if line:
                        if line[2] not in splitval:
                            splitval.append(line[2])
                            changed = True
                else:
                    line = popline(varname)
                    if line:
                        splitval = [line[2]]
                        changed = True

                if removevar in removevalues:
                    remove = removevalues[removevar]
                    if isinstance(remove, str):
                        if remove in splitval:
                            splitval.remove(remove)
                            changed = True
                    else:
                        for removeitem in remove:
                            if removeitem in splitval:
                                splitval.remove(removeitem)
                                changed = True

                if changed:
                    newvalue = splitval
                    if len(newvalue) == 1:
                        # Ensure it's written out as one line
                        if ':append' in varname:
                            newvalue = ' ' + newvalue[0]
                        else:
                            newvalue = newvalue[0]
                    if not newvalue and (op in ['+=', '.='] or ':append' in varname):
                        # There's no point appending nothing
                        newvalue = None
                    if varname.endswith('()'):
                        indent = 4
                    else:
                        indent = -1
                    return (newvalue, None, indent, True)
            return (origvalue, None, 4, False)

        varnames = [item[0] for item in bbappendlines]
        if removevalues:
            varnames.extend(list(removevalues.keys()))

        with open(outfile, 'r') as f:
            (updated, newlines) = bb.utils.edit_metadata(f, varnames, appendfile_varfunc)

        destsubdir = extvars['destsubdir']
    else:
        updated = False
        newlines = []

    if bbappendlines:
        for line in bbappendlines:
            if line[0].endswith('()'):
                newlines.append('%s {\n    %s\n}\n' % (line[0], '\n    '.join(line[2])))
            else:
                newlines.append('%s %s "%s"\n\n' % line)
        updated = True

    if updated:
        with open(outfile, 'w') as f:
            f.writelines(newlines)

    if copyfiles:
        if machine:
            destsubdir = os.path.join(destsubdir, machine)
        if redirect_output:
            outdir = redirect_output
        else:
            outdir = appenddir
        for newfile, param in copyfiles.items():
            srcfile = param['path']
            patchdir = param.get('patchdir', ".")

            if patchdir != ".":
                newfile = os.path.join(os.path.split(newfile)[0], patchdir, os.path.split(newfile)[1])
            filedest = os.path.join(outdir, destsubdir, os.path.basename(srcfile))
            if os.path.abspath(newfile) != os.path.abspath(filedest):
                if newfile.startswith(tempfile.gettempdir()):
                    newfiledisp = os.path.basename(newfile)
                else:
                    newfiledisp = newfile
                if redirect_output:
                    bb.note('Copying %s to %s (dry-run)' % (newfiledisp, os.path.join(appenddir, destsubdir, os.path.basename(srcfile))))
                else:
                    bb.note('Copying %s to %s' % (newfiledisp, filedest))
                bb.utils.mkdirhier(os.path.dirname(filedest))
                shutil.copyfile(newfile, filedest)

    return (appendpath, os.path.join(appenddir, destsubdir))


def find_layerdir(fn):
    """ Figure out the path to the base of the layer containing a file (e.g. a recipe)"""
    pth = os.path.abspath(fn)
    layerdir = ''
    while pth:
        if os.path.exists(os.path.join(pth, 'conf', 'layer.conf')):
            layerdir = pth
            break
        pth = os.path.dirname(pth)
        if pth == '/':
            return None
    return layerdir


def replace_dir_vars(path, d):
    """Replace common directory paths with appropriate variable references (e.g. /etc becomes ${sysconfdir})"""
    dirvars = {}
    # Sort by length so we get the variables we're interested in first
    for var in sorted(list(d.keys()), key=len):
        if var.endswith('dir') and var.lower() == var:
            value = d.getVar(var)
            if value.startswith('/') and not '\n' in value and value not in dirvars:
                dirvars[value] = var
    for dirpath in sorted(list(dirvars.keys()), reverse=True):
        path = path.replace(dirpath, '${%s}' % dirvars[dirpath])
    return path

def get_recipe_pv_with_pfx_sfx(pv, uri_type):
    """
    Get PV separating prefix and suffix components.

    Returns tuple with pv, prefix and suffix.
    """
    pfx = ''
    sfx = ''

    if uri_type == 'git':
        git_regex = re.compile(r"(?P<pfx>v?)(?P<ver>.*?)(?P<sfx>\+[^\+]*(git)?r?(AUTOINC\+)?)(?P<rev>.*)")
        m = git_regex.match(pv)

        if m:
            pv = m.group('ver')
            pfx = m.group('pfx')
            sfx = m.group('sfx')
    else:
        regex = re.compile(r"(?P<pfx>(v|r)?)(?P<ver>.*)")
        m = regex.match(pv)
        if m:
            pv = m.group('ver')
            pfx = m.group('pfx')

    return (pv, pfx, sfx)

def get_recipe_upstream_version(rd):
    """
        Get upstream version of recipe using bb.fetch2 methods with support for
        http, https, ftp and git.

        bb.fetch2 exceptions can be raised,
            FetchError when don't have network access or upstream site don't response.
            NoMethodError when uri latest_versionstring method isn't implemented.

        Returns a dictonary with version, repository revision, current_version, type and datetime.
        Type can be A for Automatic, M for Manual and U for Unknown.
    """
    from bb.fetch2 import decodeurl
    from datetime import datetime

    ru = {}
    ru['current_version'] = rd.getVar('PV')
    ru['version'] = ''
    ru['type'] = 'U'
    ru['datetime'] = ''
    ru['revision'] = ''

    # XXX: If don't have SRC_URI means that don't have upstream sources so
    # returns the current recipe version, so that upstream version check
    # declares a match.
    src_uris = rd.getVar('SRC_URI')
    if not src_uris:
        ru['version'] = ru['current_version']
        ru['type'] = 'M'
        ru['datetime'] = datetime.now()
        return ru

    # XXX: we suppose that the first entry points to the upstream sources
    src_uri = src_uris.split()[0]
    uri_type, _, _, _, _, _ =  decodeurl(src_uri)

    (pv, pfx, sfx) = get_recipe_pv_with_pfx_sfx(rd.getVar('PV'), uri_type)
    ru['current_version'] = pv

    manual_upstream_version = rd.getVar("RECIPE_UPSTREAM_VERSION")
    if manual_upstream_version:
        # manual tracking of upstream version.
        ru['version'] = manual_upstream_version
        ru['type'] = 'M'

        manual_upstream_date = rd.getVar("CHECK_DATE")
        if manual_upstream_date:
            date = datetime.strptime(manual_upstream_date, "%b %d, %Y")
        else:
            date = datetime.now()
        ru['datetime'] = date

    elif uri_type == "file":
        # files are always up-to-date
        ru['version'] =  pv
        ru['type'] = 'A'
        ru['datetime'] = datetime.now()
    else:
        ud = bb.fetch2.FetchData(src_uri, rd)
        if rd.getVar("UPSTREAM_CHECK_COMMITS") == "1":
            bb.fetch2.get_srcrev(rd)
            revision = ud.method.latest_revision(ud, rd, 'default')
            upversion = pv
            if revision != rd.getVar("SRCREV"):
                upversion = upversion + "-new-commits-available"
        else:
            pupver = ud.method.latest_versionstring(ud, rd)
            (upversion, revision) = pupver

        if upversion:
            ru['version'] = upversion
            ru['type'] = 'A'

        if revision:
            ru['revision'] = revision

        ru['datetime'] = datetime.now()

    return ru

def _get_recipe_upgrade_status(data):
    uv = get_recipe_upstream_version(data)

    pn = data.getVar('PN')
    cur_ver = uv['current_version']

    upstream_version_unknown = data.getVar('UPSTREAM_VERSION_UNKNOWN')
    if not uv['version']:
        status = "UNKNOWN" if upstream_version_unknown else "UNKNOWN_BROKEN"
    else:
        cmp = vercmp_string(uv['current_version'], uv['version'])
        if cmp == -1:
            status = "UPDATE" if not upstream_version_unknown else "KNOWN_BROKEN"
        elif cmp == 0:
            status = "MATCH" if not upstream_version_unknown else "KNOWN_BROKEN"
        else:
            status = "UNKNOWN" if upstream_version_unknown else "UNKNOWN_BROKEN"

    next_ver = uv['version'] if uv['version'] else "N/A"
    revision = uv['revision'] if uv['revision'] else "N/A"
    maintainer = data.getVar('RECIPE_MAINTAINER')
    no_upgrade_reason = data.getVar('RECIPE_NO_UPDATE_REASON')

    return {'pn':pn, 'status':status, 'cur_ver':cur_ver, 'next_ver':next_ver, 'maintainer':maintainer, 'revision':revision, 'no_upgrade_reason':no_upgrade_reason}

def get_recipe_upgrade_status(recipes=None):
    pkgs_list = []
    data_copy_list = []
    copy_vars = ('SRC_URI',
                 'PV',
                 'DL_DIR',
                 'PN',
                 'CACHE',
                 'PERSISTENT_DIR',
                 'BB_URI_HEADREVS',
                 'UPSTREAM_CHECK_COMMITS',
                 'UPSTREAM_CHECK_GITTAGREGEX',
                 'UPSTREAM_CHECK_REGEX',
                 'UPSTREAM_CHECK_URI',
                 'UPSTREAM_VERSION_UNKNOWN',
                 'RECIPE_MAINTAINER',
                 'RECIPE_NO_UPDATE_REASON',
                 'RECIPE_UPSTREAM_VERSION',
                 'RECIPE_UPSTREAM_DATE',
                 'CHECK_DATE',
                 'FETCHCMD_bzr',
                 'FETCHCMD_ccrc',
                 'FETCHCMD_cvs',
                 'FETCHCMD_git',
                 'FETCHCMD_hg',
                 'FETCHCMD_npm',
                 'FETCHCMD_osc',
                 'FETCHCMD_p4',
                 'FETCHCMD_repo',
                 'FETCHCMD_s3',
                 'FETCHCMD_svn',
                 'FETCHCMD_wget',
            )

    with bb.tinfoil.Tinfoil() as tinfoil:
        tinfoil.prepare(config_only=False)

        if not recipes:
            recipes = tinfoil.all_recipe_files(variants=False)

        recipeincludes = {}
        for fn in recipes:
            try:
                if fn.startswith("/"):
                    data = tinfoil.parse_recipe_file(fn)
                else:
                    data = tinfoil.parse_recipe(fn)
            except bb.providers.NoProvider:
                bb.note(" No provider for %s" % fn)
                continue

            unreliable = data.getVar('UPSTREAM_CHECK_UNRELIABLE')
            if unreliable == "1":
                bb.note(" Skip package %s as upstream check unreliable" % pn)
                continue

            data_copy = bb.data.init()
            for var in copy_vars:
                data_copy.setVar(var, data.getVar(var))
            for k in data:
                if k.startswith('SRCREV'):
                    data_copy.setVar(k, data.getVar(k))

            data_copy_list.append(data_copy)

            recipeincludes[data.getVar('FILE')] = {'bbincluded':data.getVar('BBINCLUDED').split(),'pn':data.getVar('PN')}

    from concurrent.futures import ProcessPoolExecutor
    with ProcessPoolExecutor(max_workers=utils.cpu_count()) as executor:
        pkgs_list = executor.map(_get_recipe_upgrade_status, data_copy_list)

    return _group_recipes(pkgs_list, _get_common_include_recipes(recipeincludes))

def get_common_include_recipes():
    with bb.tinfoil.Tinfoil() as tinfoil:
        tinfoil.prepare(config_only=False)

        recipes = tinfoil.all_recipe_files(variants=False)

        recipeincludes = {}
        for fn in recipes:
            data = tinfoil.parse_recipe_file(fn)
            recipeincludes[fn] = {'bbincluded':data.getVar('BBINCLUDED').split(),'pn':data.getVar('PN')}
        return _get_common_include_recipes(recipeincludes)

def _get_common_include_recipes(recipeincludes_all):
        recipeincludes = {}
        for fn,data in recipeincludes_all.items():
            bbincluded_filtered = [i for i in data['bbincluded'] if os.path.dirname(i) == os.path.dirname(fn) and i != fn]
            if bbincluded_filtered:
                recipeincludes[data['pn']] = bbincluded_filtered

        recipeincludes_inverted = {}
        for k,v in recipeincludes.items():
            for i in v:
                recipeincludes_inverted.setdefault(i,set()).add(k)

        recipeincludes_inverted_filtered = {k:v for k,v in recipeincludes_inverted.items() if len(v) > 1}

        recipes_with_shared_includes = list()
        for v in recipeincludes_inverted_filtered.values():
            recipeset = v
            for v1 in recipeincludes_inverted_filtered.values():
                if recipeset.intersection(v1):
                    recipeset.update(v1)
            if recipeset not in recipes_with_shared_includes:
                recipes_with_shared_includes.append(recipeset)

        return recipes_with_shared_includes

def _group_recipes(recipes, groups):
    recipedict = {}
    for r in recipes:
        recipedict[r['pn']] = r

    recipegroups = []
    for g in groups:
        recipeset = []
        for r in g:
            if r in recipedict.keys():
                recipeset.append(recipedict[r])
                del recipedict[r]
        recipegroups.append(recipeset)

    for r in recipedict.values():
        recipegroups.append([r])
    return recipegroups
