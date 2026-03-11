#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import fnmatch
import hashlib
import logging
import os
import re

import bb
import bb.utils

logger = logging.getLogger("BitBake.OE.LicenseFinder")

def _load_hash_csv(d):
    """
    Load a mapping of (checksum: license name) from all files/license-hashes.csv
    files that can be found in the available layers.
    """
    import csv
    md5sums = {}

    # Read license md5sums from csv file
    for path in d.getVar('BBPATH').split(':'):
        csv_path = os.path.join(path, 'files', 'license-hashes.csv')
        if os.path.isfile(csv_path):
            with open(csv_path, newline='') as csv_file:
                reader = csv.DictReader(csv_file, delimiter=',', fieldnames=['md5sum', 'license'])
                for row in reader:
                    md5sums[row['md5sum']] = row['license']

    return md5sums


def _crunch_known_licenses(d):
    """
    Calculate the MD5 checksums for the original and "crunched" versions of all
    known licenses.
    """
    md5sums = {}

    lic_dirs = [d.getVar('COMMON_LICENSE_DIR')] + (d.getVar('LICENSE_PATH') or "").split()
    for lic_dir in lic_dirs:
        for fn in os.listdir(lic_dir):
            path = os.path.join(lic_dir, fn)
            # Hash the exact contents
            md5value = bb.utils.md5_file(path)
            md5sums[md5value] = fn
            # Also hash a "crunched" version
            md5value = _crunch_license(path)
            md5sums[md5value] = fn

    return md5sums


def _crunch_license(licfile):
    '''
    Remove non-material text from a license file and then calculate its
    md5sum. This works well for licenses that contain a copyright statement,
    but is also a useful way to handle people's insistence upon reformatting
    the license text slightly (with no material difference to the text of the
    license).
    '''

    import oe.utils

    # Note: these are carefully constructed!
    license_title_re = re.compile(r'^#*\(? *(This is )?([Tt]he )?.{0,15} ?[Ll]icen[sc]e( \(.{1,10}\))?\)?[:\.]? ?#*$')
    license_statement_re = re.compile(r'^((This (project|software)|.{1,10}) is( free software)? (released|licen[sc]ed)|(Released|Licen[cs]ed)) under the .{1,10} [Ll]icen[sc]e:?$')
    copyright_re = re.compile(r'^ *[#\*]* *(Modified work |MIT LICENSED )?Copyright ?(\([cC]\))? .*$')
    disclaimer_re = re.compile(r'^ *\*? ?All [Rr]ights [Rr]eserved\.$')
    email_re = re.compile(r'^.*<[\w\.-]*@[\w\.\-]*>$')
    header_re = re.compile(r'^(\/\**!?)? ?[\-=\*]* ?(\*\/)?$')
    tag_re = re.compile(r'^ *@?\(?([Ll]icense|MIT)\)?$')
    url_re = re.compile(r'^ *[#\*]* *https?:\/\/[\w\.\/\-]+$')

    lictext = []
    with open(licfile, 'r', errors='surrogateescape') as f:
        for line in f:
            # Drop opening statements
            if copyright_re.match(line):
                continue
            elif disclaimer_re.match(line):
                continue
            elif email_re.match(line):
                continue
            elif header_re.match(line):
                continue
            elif tag_re.match(line):
                continue
            elif url_re.match(line):
                continue
            elif license_title_re.match(line):
                continue
            elif license_statement_re.match(line):
                continue
            # Strip comment symbols
            line = line.replace('*', '') \
                       .replace('#', '')
            # Unify spelling
            line = line.replace('sub-license', 'sublicense')
            # Squash spaces
            line = oe.utils.squashspaces(line.strip())
            # Replace smart quotes, double quotes and backticks with single quotes
            line = line.replace(u"\u2018", "'").replace(u"\u2019", "'").replace(u"\u201c","'").replace(u"\u201d", "'").replace('"', '\'').replace('`', '\'')
            # Unify brackets
            line = line.replace("{", "[").replace("}", "]")
            if line:
                lictext.append(line)

    m = hashlib.md5()
    try:
        m.update(' '.join(lictext).encode('utf-8'))
        md5val = m.hexdigest()
    except UnicodeEncodeError:
        md5val = None
    return md5val


def find_license_files(srctree, first_only=False):
    """
    Search srctree for files that look like they could be licenses.
    If first_only is True, only return the first file found.
    """
    licspecs = ['*LICEN[CS]E*', 'COPYING*', '*[Ll]icense*', 'LEGAL*', '[Ll]egal*', '*GPL*', 'README.lic*', 'COPYRIGHT*', '[Cc]opyright*', 'e[dp]l-v10']
    skip_extensions = (".html", ".js", ".json", ".svg", ".ts", ".go", ".sh")
    licfiles = []
    for root, dirs, files in os.walk(srctree):
        # Sort files so that LICENSE is before LICENSE.subcomponent, which is
        # meaningful if first_only is set.
        for fn in sorted(files):
            if fn.endswith(skip_extensions):
                continue
            for spec in licspecs:
                if fnmatch.fnmatch(fn, spec):
                    fullpath = os.path.join(root, fn)
                    if not fullpath in licfiles:
                        licfiles.append(fullpath)
                        if first_only:
                            return licfiles

    return licfiles


def match_licenses(licfiles, srctree, d, extra_hashes={}):
    md5sums = {}
    md5sums.update(_load_hash_csv(d))
    md5sums.update(_crunch_known_licenses(d))
    md5sums.update(extra_hashes)

    licenses = []
    for licfile in sorted(licfiles):
        resolved_licfile = d.expand(licfile)
        md5value = bb.utils.md5_file(resolved_licfile)
        license = md5sums.get(md5value, None)
        if not license:
            crunched_md5 = _crunch_license(resolved_licfile)
            license = md5sums.get(crunched_md5, None)
            if not license:
                license = 'Unknown'
                logger.info("Please add the following line for '%s' to a 'license-hashes.csv' " \
                    "and replace `Unknown` with the license:\n" \
                    "%s,Unknown" % (os.path.relpath(licfile, srctree + "/.."), md5value))

        licenses.append((license, os.path.relpath(licfile, srctree), md5value))

    return licenses


def find_licenses(srctree, d, first_only=False, extra_hashes={}):
    licfiles = find_license_files(srctree, first_only)
    licenses = match_licenses(licfiles, srctree, d, extra_hashes)

    # FIXME should we grab at least one source file with a license header and add that too?

    return licenses
