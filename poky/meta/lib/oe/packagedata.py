#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import codecs
import os
import json
import bb.compress.zstd
import oe.path

from glob import glob

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

def foreach_runtime_provider_pkgdata(d, rdep, include_rdep=False):
    pkgdata_dir = d.getVar("PKGDATA_DIR")
    possibles = set()
    try:
        possibles |= set(os.listdir("%s/runtime-rprovides/%s/" % (pkgdata_dir, rdep)))
    except OSError:
        pass

    if include_rdep:
        possibles.add(rdep)

    for p in sorted(list(possibles)):
        rdep_data = read_subpkgdata(p, d)
        yield p, rdep_data

def get_package_mapping(pkg, basepkg, d, depversions=None):
    import oe.packagedata

    data = oe.packagedata.read_subpkgdata(pkg, d)
    key = "PKG:%s" % pkg

    if key in data:
        if bb.data.inherits_class('allarch', d) and bb.data.inherits_class('packagegroup', d) and pkg != data[key]:
            bb.error("An allarch packagegroup shouldn't depend on packages which are dynamically renamed (%s to %s)" % (pkg, data[key]))
        # Have to avoid undoing the write_extra_pkgs(global_variants...)
        if bb.data.inherits_class('allarch', d) and not d.getVar('MULTILIB_VARIANTS') \
            and data[key] == basepkg:
            return pkg
        if depversions == []:
            # Avoid returning a mapping if the renamed package rprovides its original name
            rprovkey = "RPROVIDES:%s" % pkg
            if rprovkey in data:
                if pkg in bb.utils.explode_dep_versions2(data[rprovkey]):
                    bb.note("%s rprovides %s, not replacing the latter" % (data[key], pkg))
                    return pkg
        # Do map to rewritten package name
        return data[key]

    return pkg

def get_package_additional_metadata(pkg_type, d):
    base_key = "PACKAGE_ADD_METADATA"
    for key in ("%s_%s" % (base_key, pkg_type.upper()), base_key):
        if d.getVar(key, False) is None:
            continue
        d.setVarFlag(key, "type", "list")
        if d.getVarFlag(key, "separator") is None:
            d.setVarFlag(key, "separator", "\\n")
        metadata_fields = [field.strip() for field in oe.data.typed_value(key, d)]
        return "\n".join(metadata_fields).strip()

def runtime_mapping_rename(varname, pkg, d):
    #bb.note("%s before: %s" % (varname, d.getVar(varname)))

    new_depends = {}
    deps = bb.utils.explode_dep_versions2(d.getVar(varname) or "")
    for depend, depversions in deps.items():
        new_depend = get_package_mapping(depend, pkg, d, depversions)
        if depend != new_depend:
            bb.note("package name mapping done: %s -> %s" % (depend, new_depend))
        new_depends[new_depend] = deps[depend]

    d.setVar(varname, bb.utils.join_deps(new_depends, commasep=False))

    #bb.note("%s after: %s" % (varname, d.getVar(varname)))

def emit_pkgdata(pkgfiles, d):
    def process_postinst_on_target(pkg, mlprefix):
        pkgval = d.getVar('PKG:%s' % pkg)
        if pkgval is None:
            pkgval = pkg

        defer_fragment = """
if [ -n "$D" ]; then
    $INTERCEPT_DIR/postinst_intercept delay_to_first_boot %s mlprefix=%s
    exit 0
fi
""" % (pkgval, mlprefix)

        postinst = d.getVar('pkg_postinst:%s' % pkg)
        postinst_ontarget = d.getVar('pkg_postinst_ontarget:%s' % pkg)

        if postinst_ontarget:
            bb.debug(1, 'adding deferred pkg_postinst_ontarget() to pkg_postinst() for %s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += defer_fragment
            postinst += postinst_ontarget
            d.setVar('pkg_postinst:%s' % pkg, postinst)

    def add_set_e_to_scriptlets(pkg):
        for scriptlet_name in ('pkg_preinst', 'pkg_postinst', 'pkg_prerm', 'pkg_postrm'):
            scriptlet = d.getVar('%s:%s' % (scriptlet_name, pkg))
            if scriptlet:
                scriptlet_split = scriptlet.split('\n')
                if scriptlet_split[0].startswith("#!"):
                    scriptlet = scriptlet_split[0] + "\nset -e\n" + "\n".join(scriptlet_split[1:])
                else:
                    scriptlet = "set -e\n" + "\n".join(scriptlet_split[0:])
            d.setVar('%s:%s' % (scriptlet_name, pkg), scriptlet)

    def write_if_exists(f, pkg, var):
        def encode(str):
            import codecs
            c = codecs.getencoder("unicode_escape")
            return c(str)[0].decode("latin1")

        val = d.getVar('%s:%s' % (var, pkg))
        if val:
            f.write('%s:%s: %s\n' % (var, pkg, encode(val)))
            return val
        val = d.getVar('%s' % (var))
        if val:
            f.write('%s: %s\n' % (var, encode(val)))
        return val

    def write_extra_pkgs(variants, pn, packages, pkgdatadir):
        for variant in variants:
            with open("%s/%s-%s" % (pkgdatadir, variant, pn), 'w') as fd:
                fd.write("PACKAGES: %s\n" % ' '.join(
                            map(lambda pkg: '%s-%s' % (variant, pkg), packages.split())))

    def write_extra_runtime_pkgs(variants, packages, pkgdatadir):
        for variant in variants:
            for pkg in packages.split():
                ml_pkg = "%s-%s" % (variant, pkg)
                subdata_file = "%s/runtime/%s" % (pkgdatadir, ml_pkg)
                with open(subdata_file, 'w') as fd:
                    fd.write("PKG:%s: %s" % (ml_pkg, pkg))

    packages = d.getVar('PACKAGES')
    pkgdest = d.getVar('PKGDEST')
    pkgdatadir = d.getVar('PKGDESTWORK')

    data_file = pkgdatadir + d.expand("/${PN}")
    with open(data_file, 'w') as fd:
        fd.write("PACKAGES: %s\n" % packages)

    pkgdebugsource = d.getVar("PKGDEBUGSOURCES") or []

    pn = d.getVar('PN')
    global_variants = (d.getVar('MULTILIB_GLOBAL_VARIANTS') or "").split()
    variants = (d.getVar('MULTILIB_VARIANTS') or "").split()

    if bb.data.inherits_class('kernel', d) or bb.data.inherits_class('module-base', d):
        write_extra_pkgs(variants, pn, packages, pkgdatadir)

    if bb.data.inherits_class('allarch', d) and not variants \
        and not bb.data.inherits_class('packagegroup', d):
        write_extra_pkgs(global_variants, pn, packages, pkgdatadir)

    workdir = d.getVar('WORKDIR')

    for pkg in packages.split():
        pkgval = d.getVar('PKG:%s' % pkg)
        if pkgval is None:
            pkgval = pkg
            d.setVar('PKG:%s' % pkg, pkg)

        extended_data = {
            "files_info": {}
        }

        pkgdestpkg = os.path.join(pkgdest, pkg)
        files = {}
        files_extra = {}
        total_size = 0
        seen = set()
        for f in pkgfiles[pkg]:
            fpath = os.sep + os.path.relpath(f, pkgdestpkg)

            fstat = os.lstat(f)
            files[fpath] = fstat.st_size

            extended_data["files_info"].setdefault(fpath, {})
            extended_data["files_info"][fpath]['size'] = fstat.st_size

            if fstat.st_ino not in seen:
                seen.add(fstat.st_ino)
                total_size += fstat.st_size

            if fpath in pkgdebugsource:
                extended_data["files_info"][fpath]['debugsrc'] = pkgdebugsource[fpath]
                del pkgdebugsource[fpath]

        d.setVar('FILES_INFO:' + pkg , json.dumps(files, sort_keys=True))

        process_postinst_on_target(pkg, d.getVar("MLPREFIX"))
        add_set_e_to_scriptlets(pkg)

        subdata_file = pkgdatadir + "/runtime/%s" % pkg
        with open(subdata_file, 'w') as sf:
            for var in (d.getVar('PKGDATA_VARS') or "").split():
                val = write_if_exists(sf, pkg, var)

            write_if_exists(sf, pkg, 'FILERPROVIDESFLIST')
            for dfile in sorted((d.getVar('FILERPROVIDESFLIST:' + pkg) or "").split()):
                write_if_exists(sf, pkg, 'FILERPROVIDES:' + dfile)

            write_if_exists(sf, pkg, 'FILERDEPENDSFLIST')
            for dfile in sorted((d.getVar('FILERDEPENDSFLIST:' + pkg) or "").split()):
                write_if_exists(sf, pkg, 'FILERDEPENDS:' + dfile)

            sf.write('%s:%s: %d\n' % ('PKGSIZE', pkg, total_size))

        subdata_extended_file = pkgdatadir + "/extended/%s.json.zstd" % pkg
        num_threads = int(d.getVar("BB_NUMBER_THREADS"))
        with bb.compress.zstd.open(subdata_extended_file, "wt", encoding="utf-8", num_threads=num_threads) as f:
            json.dump(extended_data, f, sort_keys=True, separators=(",", ":"))

        # Symlinks needed for rprovides lookup
        rprov = d.getVar('RPROVIDES:%s' % pkg) or d.getVar('RPROVIDES')
        if rprov:
            for p in bb.utils.explode_deps(rprov):
                subdata_sym = pkgdatadir + "/runtime-rprovides/%s/%s" % (p, pkg)
                bb.utils.mkdirhier(os.path.dirname(subdata_sym))
                oe.path.relsymlink(subdata_file, subdata_sym, True)

        allow_empty = d.getVar('ALLOW_EMPTY:%s' % pkg)
        if not allow_empty:
            allow_empty = d.getVar('ALLOW_EMPTY')
        root = "%s/%s" % (pkgdest, pkg)
        os.chdir(root)
        g = glob('*')
        if g or allow_empty == "1":
            # Symlinks needed for reverse lookups (from the final package name)
            subdata_sym = pkgdatadir + "/runtime-reverse/%s" % pkgval
            oe.path.relsymlink(subdata_file, subdata_sym, True)

            packagedfile = pkgdatadir + '/runtime/%s.packaged' % pkg
            open(packagedfile, 'w').close()

    if bb.data.inherits_class('kernel', d) or bb.data.inherits_class('module-base', d):
        write_extra_runtime_pkgs(variants, packages, pkgdatadir)

    if bb.data.inherits_class('allarch', d) and not variants \
        and not bb.data.inherits_class('packagegroup', d):
        write_extra_runtime_pkgs(global_variants, packages, pkgdatadir)

def mapping_rename_hook(d):
    """
    Rewrite variables to account for package renaming in things
    like debian.bbclass or manual PKG variable name changes
    """
    pkg = d.getVar("PKG")
    oe.packagedata.runtime_mapping_rename("RDEPENDS", pkg, d)
    oe.packagedata.runtime_mapping_rename("RRECOMMENDS", pkg, d)
    oe.packagedata.runtime_mapping_rename("RSUGGESTS", pkg, d)
