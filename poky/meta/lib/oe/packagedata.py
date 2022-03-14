#
# SPDX-License-Identifier: GPL-2.0-only
#

import codecs
import os

def packaged(pkg, d):
    return os.access(get_subpkgedata_fn(pkg, d) + '.packaged', os.R_OK)

def read_pkgdatafile(fn):
    pkgdata = {}

    def decode(str):
        c = codecs.getdecoder("unicode_escape")
        return c(str)[0]

    if os.access(fn, os.R_OK):
        import re
        with open(fn, 'r') as f:
            lines = f.readlines()
        r = re.compile(r"(^.+?):\s+(.*)")
        for l in lines:
            m = r.match(l)
            if m:
                pkgdata[m.group(1)] = decode(m.group(2))

    return pkgdata

def get_subpkgedata_fn(pkg, d):
    return d.expand('${PKGDATA_DIR}/runtime/%s' % pkg)

def has_subpkgdata(pkg, d):
    return os.access(get_subpkgedata_fn(pkg, d), os.R_OK)

def read_subpkgdata(pkg, d):
    return read_pkgdatafile(get_subpkgedata_fn(pkg, d))

def has_pkgdata(pn, d):
    fn = d.expand('${PKGDATA_DIR}/%s' % pn)
    return os.access(fn, os.R_OK)

def read_pkgdata(pn, d):
    fn = d.expand('${PKGDATA_DIR}/%s' % pn)
    return read_pkgdatafile(fn)

#
# Collapse FOO:pkg variables into FOO
#
def read_subpkgdata_dict(pkg, d):
    ret = {}
    subd = read_pkgdatafile(get_subpkgedata_fn(pkg, d))
    for var in subd:
        newvar = var.replace(":" + pkg, "")
        if newvar == var and var + ":" + pkg in subd:
            continue
        ret[newvar] = subd[var]
    return ret

def read_subpkgdata_extended(pkg, d):
    import json
    import bb.compress.zstd

    fn = d.expand("${PKGDATA_DIR}/extended/%s.json.zstd" % pkg)
    try:
        num_threads = int(d.getVar("BB_NUMBER_THREADS"))
        with bb.compress.zstd.open(fn, "rt", encoding="utf-8", num_threads=num_threads) as f:
            return json.load(f)
    except FileNotFoundError:
        return None

def _pkgmap(d):
    """Return a dictionary mapping package to recipe name."""

    pkgdatadir = d.getVar("PKGDATA_DIR")

    pkgmap = {}
    try:
        files = os.listdir(pkgdatadir)
    except OSError:
        bb.warn("No files in %s?" % pkgdatadir)
        files = []

    for pn in [f for f in files if not os.path.isdir(os.path.join(pkgdatadir, f))]:
        try:
            pkgdata = read_pkgdatafile(os.path.join(pkgdatadir, pn))
        except OSError:
            continue

        packages = pkgdata.get("PACKAGES") or ""
        for pkg in packages.split():
            pkgmap[pkg] = pn

    return pkgmap

def pkgmap(d):
    """Return a dictionary mapping package to recipe name.
    Cache the mapping in the metadata"""

    pkgmap_data = d.getVar("__pkgmap_data", False)
    if pkgmap_data is None:
        pkgmap_data = _pkgmap(d)
        d.setVar("__pkgmap_data", pkgmap_data)

    return pkgmap_data

def recipename(pkg, d):
    """Return the recipe name for the given binary package name."""

    return pkgmap(d).get(pkg)
