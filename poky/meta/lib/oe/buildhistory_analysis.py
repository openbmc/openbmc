# Report significant differences in the buildhistory repository since a specific revision
#
# Copyright (C) 2012-2013, 2016-2017 Intel Corporation
# Author: Paul Eggleton <paul.eggleton@linux.intel.com>
#
# Note: requires GitPython 0.3.1+
#
# You can use this from the command line by running scripts/buildhistory-diff
#

import sys
import os.path
import difflib
import git
import re
import hashlib
import collections
import bb.utils
import bb.tinfoil


# How to display fields
list_fields = ['DEPENDS', 'RPROVIDES', 'RDEPENDS', 'RRECOMMENDS', 'RSUGGESTS', 'RREPLACES', 'RCONFLICTS', 'FILES', 'FILELIST', 'USER_CLASSES', 'IMAGE_CLASSES', 'IMAGE_FEATURES', 'IMAGE_LINGUAS', 'IMAGE_INSTALL', 'BAD_RECOMMENDATIONS', 'PACKAGE_EXCLUDE']
list_order_fields = ['PACKAGES']
defaultval_map = {'PKG': 'PKG', 'PKGE': 'PE', 'PKGV': 'PV', 'PKGR': 'PR'}
numeric_fields = ['PKGSIZE', 'IMAGESIZE']
# Fields to monitor
monitor_fields = ['RPROVIDES', 'RDEPENDS', 'RRECOMMENDS', 'RREPLACES', 'RCONFLICTS', 'PACKAGES', 'FILELIST', 'PKGSIZE', 'IMAGESIZE', 'PKG']
ver_monitor_fields = ['PKGE', 'PKGV', 'PKGR']
# Percentage change to alert for numeric fields
monitor_numeric_threshold = 10
# Image files to monitor (note that image-info.txt is handled separately)
img_monitor_files = ['installed-package-names.txt', 'files-in-image.txt']
# Related context fields for reporting (note: PE, PV & PR are always reported for monitored package fields)
related_fields = {}
related_fields['RDEPENDS'] = ['DEPENDS']
related_fields['RRECOMMENDS'] = ['DEPENDS']
related_fields['FILELIST'] = ['FILES']
related_fields['files-in-image.txt'] = ['installed-package-names.txt', 'USER_CLASSES', 'IMAGE_CLASSES', 'ROOTFS_POSTPROCESS_COMMAND', 'IMAGE_POSTPROCESS_COMMAND']
related_fields['installed-package-names.txt'] = ['IMAGE_FEATURES', 'IMAGE_LINGUAS', 'IMAGE_INSTALL', 'BAD_RECOMMENDATIONS', 'NO_RECOMMENDATIONS', 'PACKAGE_EXCLUDE']

colours = {
    'colour_default': '',
    'colour_add':     '',
    'colour_remove':  '',
}

def init_colours(use_colours):
    global colours
    if use_colours:
        colours = {
            'colour_default': '\033[0m',
            'colour_add':     '\033[1;32m',
            'colour_remove':  '\033[1;31m',
        }
    else:
        colours = {
            'colour_default': '',
            'colour_add':     '',
            'colour_remove':  '',
        }

class ChangeRecord:
    def __init__(self, path, fieldname, oldvalue, newvalue, monitored):
        self.path = path
        self.fieldname = fieldname
        self.oldvalue = oldvalue
        self.newvalue = newvalue
        self.monitored = monitored
        self.related = []
        self.filechanges = None

    def __str__(self):
        return self._str_internal(True)

    def _str_internal(self, outer):
        if outer:
            if '/image-files/' in self.path:
                prefix = '%s: ' % self.path.split('/image-files/')[0]
            else:
                prefix = '%s: ' % self.path
        else:
            prefix = ''

        def pkglist_combine(depver):
            pkglist = []
            for k,v in depver.items():
                if v:
                    pkglist.append("%s (%s)" % (k,v))
                else:
                    pkglist.append(k)
            return pkglist

        def detect_renamed_dirs(aitems, bitems):
            adirs = set(map(os.path.dirname, aitems))
            bdirs = set(map(os.path.dirname, bitems))
            files_ab = [(name, sorted(os.path.basename(item) for item in aitems if os.path.dirname(item) == name)) \
                                for name in adirs - bdirs]
            files_ba = [(name, sorted(os.path.basename(item) for item in bitems if os.path.dirname(item) == name)) \
                                for name in bdirs - adirs]
            renamed_dirs = []
            for dir1, files1 in files_ab:
                rename = False
                for dir2, files2 in files_ba:
                    if files1 == files2 and not rename:
                        renamed_dirs.append((dir1,dir2))
                        # Make sure that we don't use this (dir, files) pair again.
                        files_ba.remove((dir2,files2))
                        # If a dir has already been found to have a rename, stop and go no further.
                        rename = True

            # remove files that belong to renamed dirs from aitems and bitems
            for dir1, dir2 in renamed_dirs:
                aitems = [item for item in aitems if os.path.dirname(item) not in (dir1, dir2)]
                bitems = [item for item in bitems if os.path.dirname(item) not in (dir1, dir2)]
            return renamed_dirs, aitems, bitems

        if self.fieldname in list_fields or self.fieldname in list_order_fields:
            renamed_dirs = []
            changed_order = False
            if self.fieldname in ['RPROVIDES', 'RDEPENDS', 'RRECOMMENDS', 'RSUGGESTS', 'RREPLACES', 'RCONFLICTS']:
                (depvera, depverb) = compare_pkg_lists(self.oldvalue, self.newvalue)
                aitems = pkglist_combine(depvera)
                bitems = pkglist_combine(depverb)
            else:
                aitems = self.oldvalue.split()
                bitems = self.newvalue.split()
                if self.fieldname == 'FILELIST':
                    renamed_dirs, aitems, bitems = detect_renamed_dirs(aitems, bitems)

            removed = list(set(aitems) - set(bitems))
            added = list(set(bitems) - set(aitems))

            if not removed and not added:
                depvera = bb.utils.explode_dep_versions2(self.oldvalue, sort=False)
                depverb = bb.utils.explode_dep_versions2(self.newvalue, sort=False)
                for i, j in zip(depvera.items(), depverb.items()):
                    if i[0] != j[0]:
                        changed_order = True
                        break

            lines = []
            if renamed_dirs:
                for dfrom, dto in renamed_dirs:
                    lines.append('directory renamed {colour_remove}{}{colour_default} -> {colour_add}{}{colour_default}'.format(dfrom, dto, **colours))
            if removed or added:
                if removed and not bitems:
                    lines.append('removed all items "{colour_remove}{}{colour_default}"'.format(' '.join(removed), **colours))
                else:
                    if removed:
                        lines.append('removed "{colour_remove}{value}{colour_default}"'.format(value=' '.join(removed), **colours))
                    if added:
                        lines.append('added "{colour_add}{value}{colour_default}"'.format(value=' '.join(added), **colours))
            else:
                lines.append('changed order')

            if not (removed or added or changed_order):
                out = ''
            else:
                out = '%s: %s' % (self.fieldname, ', '.join(lines))

        elif self.fieldname in numeric_fields:
            aval = int(self.oldvalue or 0)
            bval = int(self.newvalue or 0)
            if aval != 0:
                percentchg = ((bval - aval) / float(aval)) * 100
            else:
                percentchg = 100
            out = '{} changed from {colour_remove}{}{colour_default} to {colour_add}{}{colour_default} ({}{:.0f}%)'.format(self.fieldname, self.oldvalue or "''", self.newvalue or "''", '+' if percentchg > 0 else '', percentchg, **colours)
        elif self.fieldname in defaultval_map:
            out = '{} changed from {colour_remove}{}{colour_default} to {colour_add}{}{colour_default}'.format(self.fieldname, self.oldvalue, self.newvalue, **colours)
            if self.fieldname == 'PKG' and '[default]' in self.newvalue:
                out += ' - may indicate debian renaming failure'
        elif self.fieldname in ['pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm']:
            if self.oldvalue and self.newvalue:
                out = '%s changed:\n  ' % self.fieldname
            elif self.newvalue:
                out = '%s added:\n  ' % self.fieldname
            elif self.oldvalue:
                out = '%s cleared:\n  ' % self.fieldname
            alines = self.oldvalue.splitlines()
            blines = self.newvalue.splitlines()
            diff = difflib.unified_diff(alines, blines, self.fieldname, self.fieldname, lineterm='')
            out += '\n  '.join(list(diff)[2:])
            out += '\n  --'
        elif self.fieldname in img_monitor_files or '/image-files/' in self.path:
            if self.filechanges or (self.oldvalue and self.newvalue):
                fieldname = self.fieldname
                if '/image-files/' in self.path:
                    fieldname = os.path.join('/' + self.path.split('/image-files/')[1], self.fieldname)
                    out = 'Changes to %s:\n  ' % fieldname
                else:
                    if outer:
                        prefix = 'Changes to %s ' % self.path
                    out = '(%s):\n  ' % self.fieldname
                if self.filechanges:
                    out += '\n  '.join(['%s' % i for i in self.filechanges])
                else:
                    alines = self.oldvalue.splitlines()
                    blines = self.newvalue.splitlines()
                    diff = difflib.unified_diff(alines, blines, fieldname, fieldname, lineterm='')
                    out += '\n  '.join(list(diff))
                    out += '\n  --'
            else:
                out = ''
        else:
            out = '{} changed from "{colour_remove}{}{colour_default}" to "{colour_add}{}{colour_default}"'.format(self.fieldname, self.oldvalue, self.newvalue, **colours)

        if self.related:
            for chg in self.related:
                if not outer and chg.fieldname in ['PE', 'PV', 'PR']:
                    continue
                for line in chg._str_internal(False).splitlines():
                    out += '\n  * %s' % line

        return '%s%s' % (prefix, out) if out else ''

class FileChange:
    changetype_add = 'A'
    changetype_remove = 'R'
    changetype_type = 'T'
    changetype_perms = 'P'
    changetype_ownergroup = 'O'
    changetype_link = 'L'

    def __init__(self, path, changetype, oldvalue = None, newvalue = None):
        self.path = path
        self.changetype = changetype
        self.oldvalue = oldvalue
        self.newvalue = newvalue

    def _ftype_str(self, ftype):
        if ftype == '-':
            return 'file'
        elif ftype == 'd':
            return 'directory'
        elif ftype == 'l':
            return 'symlink'
        elif ftype == 'c':
            return 'char device'
        elif ftype == 'b':
            return 'block device'
        elif ftype == 'p':
            return 'fifo'
        elif ftype == 's':
            return 'socket'
        else:
            return 'unknown (%s)' % ftype

    def __str__(self):
        if self.changetype == self.changetype_add:
            return '%s was added' % self.path
        elif self.changetype == self.changetype_remove:
            return '%s was removed' % self.path
        elif self.changetype == self.changetype_type:
            return '%s changed type from %s to %s' % (self.path, self._ftype_str(self.oldvalue), self._ftype_str(self.newvalue))
        elif self.changetype == self.changetype_perms:
            return '%s changed permissions from %s to %s' % (self.path, self.oldvalue, self.newvalue)
        elif self.changetype == self.changetype_ownergroup:
            return '%s changed owner/group from %s to %s' % (self.path, self.oldvalue, self.newvalue)
        elif self.changetype == self.changetype_link:
            return '%s changed symlink target from %s to %s' % (self.path, self.oldvalue, self.newvalue)
        else:
            return '%s changed (unknown)' % self.path


def blob_to_dict(blob):
    alines = [line for line in blob.data_stream.read().decode('utf-8').splitlines()]
    adict = {}
    for line in alines:
        splitv = [i.strip() for i in line.split('=',1)]
        if len(splitv) > 1:
            adict[splitv[0]] = splitv[1]
    return adict


def file_list_to_dict(lines):
    adict = {}
    for line in lines:
        # Leave the last few fields intact so we handle file names containing spaces
        splitv = line.split(None,4)
        # Grab the path and remove the leading .
        path = splitv[4][1:].strip()
        # Handle symlinks
        if(' -> ' in path):
            target = path.split(' -> ')[1]
            path = path.split(' -> ')[0]
            adict[path] = splitv[0:3] + [target]
        else:
            adict[path] = splitv[0:3]
    return adict


def compare_file_lists(alines, blines):
    adict = file_list_to_dict(alines)
    bdict = file_list_to_dict(blines)
    filechanges = []
    for path, splitv in adict.items():
        newsplitv = bdict.pop(path, None)
        if newsplitv:
            # Check type
            oldvalue = splitv[0][0]
            newvalue = newsplitv[0][0]
            if oldvalue != newvalue:
                filechanges.append(FileChange(path, FileChange.changetype_type, oldvalue, newvalue))
            # Check permissions
            oldvalue = splitv[0][1:]
            newvalue = newsplitv[0][1:]
            if oldvalue != newvalue:
                filechanges.append(FileChange(path, FileChange.changetype_perms, oldvalue, newvalue))
            # Check owner/group
            oldvalue = '%s/%s' % (splitv[1], splitv[2])
            newvalue = '%s/%s' % (newsplitv[1], newsplitv[2])
            if oldvalue != newvalue:
                filechanges.append(FileChange(path, FileChange.changetype_ownergroup, oldvalue, newvalue))
            # Check symlink target
            if newsplitv[0][0] == 'l':
                if len(splitv) > 3:
                    oldvalue = splitv[3]
                else:
                    oldvalue = None
                newvalue = newsplitv[3]
                if oldvalue != newvalue:
                    filechanges.append(FileChange(path, FileChange.changetype_link, oldvalue, newvalue))
        else:
            filechanges.append(FileChange(path, FileChange.changetype_remove))

    # Whatever is left over has been added
    for path in bdict:
        filechanges.append(FileChange(path, FileChange.changetype_add))

    return filechanges


def compare_lists(alines, blines):
    removed = list(set(alines) - set(blines))
    added = list(set(blines) - set(alines))

    filechanges = []
    for pkg in removed:
        filechanges.append(FileChange(pkg, FileChange.changetype_remove))
    for pkg in added:
        filechanges.append(FileChange(pkg, FileChange.changetype_add))

    return filechanges


def compare_pkg_lists(astr, bstr):
    depvera = bb.utils.explode_dep_versions2(astr)
    depverb = bb.utils.explode_dep_versions2(bstr)

    # Strip out changes where the version has increased
    remove = []
    for k in depvera:
        if k in depverb:
            dva = depvera[k]
            dvb = depverb[k]
            if dva and dvb and len(dva) == len(dvb):
                # Since length is the same, sort so that prefixes (e.g. >=) will line up
                dva.sort()
                dvb.sort()
                removeit = True
                for dvai, dvbi in zip(dva, dvb):
                    if dvai != dvbi:
                        aiprefix = dvai.split(' ')[0]
                        biprefix = dvbi.split(' ')[0]
                        if aiprefix == biprefix and aiprefix in ['>=', '=']:
                            if bb.utils.vercmp(bb.utils.split_version(dvai), bb.utils.split_version(dvbi)) > 0:
                                removeit = False
                                break
                        else:
                            removeit = False
                            break
                if removeit:
                    remove.append(k)

    for k in remove:
        depvera.pop(k)
        depverb.pop(k)

    return (depvera, depverb)


def compare_dict_blobs(path, ablob, bblob, report_all, report_ver):
    adict = blob_to_dict(ablob)
    bdict = blob_to_dict(bblob)

    pkgname = os.path.basename(path)

    defaultvals = {}
    defaultvals['PKG'] = pkgname
    defaultvals['PKGE'] = '0'

    changes = []
    keys = list(set(adict.keys()) | set(bdict.keys()) | set(defaultval_map.keys()))
    for key in keys:
        astr = adict.get(key, '')
        bstr = bdict.get(key, '')
        if key in ver_monitor_fields:
            monitored = report_ver or astr or bstr
        else:
            monitored = key in monitor_fields
        mapped_key = defaultval_map.get(key, '')
        if mapped_key:
            if not astr:
                astr = '%s [default]' % adict.get(mapped_key, defaultvals.get(key, ''))
            if not bstr:
                bstr = '%s [default]' % bdict.get(mapped_key, defaultvals.get(key, ''))

        if astr != bstr:
            if (not report_all) and key in numeric_fields:
                aval = int(astr or 0)
                bval = int(bstr or 0)
                if aval != 0:
                    percentchg = ((bval - aval) / float(aval)) * 100
                else:
                    percentchg = 100
                if abs(percentchg) < monitor_numeric_threshold:
                    continue
            elif (not report_all) and key in list_fields:
                if key == "FILELIST" and path.endswith("-dbg") and bstr.strip() != '':
                    continue
                if key in ['RPROVIDES', 'RDEPENDS', 'RRECOMMENDS', 'RSUGGESTS', 'RREPLACES', 'RCONFLICTS']:
                    (depvera, depverb) = compare_pkg_lists(astr, bstr)
                    if depvera == depverb:
                        continue
                alist = astr.split()
                alist.sort()
                blist = bstr.split()
                blist.sort()
                # We don't care about the removal of self-dependencies
                if pkgname in alist and not pkgname in blist:
                    alist.remove(pkgname)
                if ' '.join(alist) == ' '.join(blist):
                    continue

            if key == 'PKGR' and not report_all:
                vers = []
                # strip leading 'r' and dots
                for ver in (astr.split()[0], bstr.split()[0]):
                    if ver.startswith('r'):
                        ver = ver[1:]
                    vers.append(ver.replace('.', ''))
                maxlen = max(len(vers[0]), len(vers[1]))
                try:
                    # pad with '0' and convert to int
                    vers = [int(ver.ljust(maxlen, '0')) for ver in vers]
                except ValueError:
                    pass
                else:
                     # skip decrements and increments
                    if abs(vers[0] - vers[1]) == 1:
                        continue

            chg = ChangeRecord(path, key, astr, bstr, monitored)
            changes.append(chg)
    return changes


def compare_siglists(a_blob, b_blob, taskdiff=False):
    # FIXME collapse down a recipe's tasks?
    alines = a_blob.data_stream.read().decode('utf-8').splitlines()
    blines = b_blob.data_stream.read().decode('utf-8').splitlines()
    keys = []
    pnmap = {}
    def readsigs(lines):
        sigs = {}
        for line in lines:
            linesplit = line.split()
            if len(linesplit) > 2:
                sigs[linesplit[0]] = linesplit[2]
                if not linesplit[0] in keys:
                    keys.append(linesplit[0])
                pnmap[linesplit[1]] = linesplit[0].rsplit('.', 1)[0]
        return sigs
    adict = readsigs(alines)
    bdict = readsigs(blines)
    out = []

    changecount = 0
    addcount = 0
    removecount = 0
    if taskdiff:
        with bb.tinfoil.Tinfoil() as tinfoil:
            tinfoil.prepare(config_only=True)

            changes = collections.OrderedDict()

            def compare_hashfiles(pn, taskname, hash1, hash2):
                hashes = [hash1, hash2]
                hashfiles = bb.siggen.find_siginfo(pn, taskname, hashes, tinfoil.config_data)

                if not taskname:
                    (pn, taskname) = pn.rsplit('.', 1)
                    pn = pnmap.get(pn, pn)
                desc = '%s.%s' % (pn, taskname)

                if len(hashfiles) == 0:
                    out.append("Unable to find matching sigdata for %s with hashes %s or %s" % (desc, hash1, hash2))
                elif not hash1 in hashfiles:
                    out.append("Unable to find matching sigdata for %s with hash %s" % (desc, hash1))
                elif not hash2 in hashfiles:
                    out.append("Unable to find matching sigdata for %s with hash %s" % (desc, hash2))
                else:
                    out2 = bb.siggen.compare_sigfiles(hashfiles[hash1], hashfiles[hash2], recursecb, collapsed=True)
                    for line in out2:
                        m = hashlib.sha256()
                        m.update(line.encode('utf-8'))
                        entry = changes.get(m.hexdigest(), (line, []))
                        if desc not in entry[1]:
                            changes[m.hexdigest()] = (line, entry[1] + [desc])

            # Define recursion callback
            def recursecb(key, hash1, hash2):
                compare_hashfiles(key, None, hash1, hash2)
                return []

            for key in keys:
                siga = adict.get(key, None)
                sigb = bdict.get(key, None)
                if siga is not None and sigb is not None and siga != sigb:
                    changecount += 1
                    (pn, taskname) = key.rsplit('.', 1)
                    compare_hashfiles(pn, taskname, siga, sigb)
                elif siga is None:
                    addcount += 1
                elif sigb is None:
                    removecount += 1
        for key, item in changes.items():
            line, tasks = item
            if len(tasks) == 1:
                desc = tasks[0]
            elif len(tasks) == 2:
                desc = '%s and %s' % (tasks[0], tasks[1])
            else:
                desc = '%s and %d others' % (tasks[-1], len(tasks)-1)
            out.append('%s: %s' % (desc, line))
    else:
        for key in keys:
            siga = adict.get(key, None)
            sigb = bdict.get(key, None)
            if siga is not None and sigb is not None and siga != sigb:
                out.append('%s changed from %s to %s' % (key, siga, sigb))
                changecount += 1
            elif siga is None:
                out.append('%s was added' % key)
                addcount += 1
            elif sigb is None:
                out.append('%s was removed' % key)
                removecount += 1
    out.append('Summary: %d tasks added, %d tasks removed, %d tasks modified (%.1f%%)' % (addcount, removecount, changecount, (changecount / float(len(bdict)) * 100)))
    return '\n'.join(out)


def process_changes(repopath, revision1, revision2='HEAD', report_all=False, report_ver=False,
                    sigs=False, sigsdiff=False, exclude_path=None):
    repo = git.Repo(repopath)
    assert repo.bare == False
    commit = repo.commit(revision1)
    diff = commit.diff(revision2)

    changes = []

    if sigs or sigsdiff:
        for d in diff.iter_change_type('M'):
            if d.a_blob.path == 'siglist.txt':
                changes.append(compare_siglists(d.a_blob, d.b_blob, taskdiff=sigsdiff))
        return changes

    for d in diff.iter_change_type('M'):
        path = os.path.dirname(d.a_blob.path)
        if path.startswith('packages/'):
            filename = os.path.basename(d.a_blob.path)
            if filename == 'latest':
                changes.extend(compare_dict_blobs(path, d.a_blob, d.b_blob, report_all, report_ver))
            elif filename.startswith('latest.'):
                chg = ChangeRecord(path, filename, d.a_blob.data_stream.read().decode('utf-8'), d.b_blob.data_stream.read().decode('utf-8'), True)
                changes.append(chg)
        elif path.startswith('images/'):
            filename = os.path.basename(d.a_blob.path)
            if filename in img_monitor_files:
                if filename == 'files-in-image.txt':
                    alines = d.a_blob.data_stream.read().decode('utf-8').splitlines()
                    blines = d.b_blob.data_stream.read().decode('utf-8').splitlines()
                    filechanges = compare_file_lists(alines,blines)
                    if filechanges:
                        chg = ChangeRecord(path, filename, None, None, True)
                        chg.filechanges = filechanges
                        changes.append(chg)
                elif filename == 'installed-package-names.txt':
                    alines = d.a_blob.data_stream.read().decode('utf-8').splitlines()
                    blines = d.b_blob.data_stream.read().decode('utf-8').splitlines()
                    filechanges = compare_lists(alines,blines)
                    if filechanges:
                        chg = ChangeRecord(path, filename, None, None, True)
                        chg.filechanges = filechanges
                        changes.append(chg)
                else:
                    chg = ChangeRecord(path, filename, d.a_blob.data_stream.read().decode('utf-8'), d.b_blob.data_stream.read().decode('utf-8'), True)
                    changes.append(chg)
            elif filename == 'image-info.txt':
                changes.extend(compare_dict_blobs(path, d.a_blob, d.b_blob, report_all, report_ver))
            elif '/image-files/' in path:
                chg = ChangeRecord(path, filename, d.a_blob.data_stream.read().decode('utf-8'), d.b_blob.data_stream.read().decode('utf-8'), True)
                changes.append(chg)

    # Look for added preinst/postinst/prerm/postrm
    # (without reporting newly added recipes)
    addedpkgs = []
    addedchanges = []
    for d in diff.iter_change_type('A'):
        path = os.path.dirname(d.b_blob.path)
        if path.startswith('packages/'):
            filename = os.path.basename(d.b_blob.path)
            if filename == 'latest':
                addedpkgs.append(path)
            elif filename.startswith('latest.'):
                chg = ChangeRecord(path, filename[7:], '', d.b_blob.data_stream.read().decode('utf-8'), True)
                addedchanges.append(chg)
    for chg in addedchanges:
        found = False
        for pkg in addedpkgs:
            if chg.path.startswith(pkg):
                found = True
                break
        if not found:
            changes.append(chg)

    # Look for cleared preinst/postinst/prerm/postrm
    for d in diff.iter_change_type('D'):
        path = os.path.dirname(d.a_blob.path)
        if path.startswith('packages/'):
            filename = os.path.basename(d.a_blob.path)
            if filename != 'latest' and filename.startswith('latest.'):
                chg = ChangeRecord(path, filename[7:], d.a_blob.data_stream.read().decode('utf-8'), '', True)
                changes.append(chg)

    # Link related changes
    for chg in changes:
        if chg.monitored:
            for chg2 in changes:
                # (Check dirname in the case of fields from recipe info files)
                if chg.path == chg2.path or os.path.dirname(chg.path) == chg2.path:
                    if chg2.fieldname in related_fields.get(chg.fieldname, []):
                        chg.related.append(chg2)
                    elif chg.path == chg2.path and chg.path.startswith('packages/') and chg2.fieldname in ['PE', 'PV', 'PR']:
                        chg.related.append(chg2)

    # filter out unwanted paths
    if exclude_path:
        for chg in changes:
            if chg.filechanges:
                fchgs = []
                for fchg in chg.filechanges:
                    for epath in exclude_path:
                        if fchg.path.startswith(epath):
                           break
                    else:
                        fchgs.append(fchg)
                chg.filechanges = fchgs

    if report_all:
        return changes
    else:
        return [chg for chg in changes if chg.monitored]
