#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb
import collections
import json
import oe.packagedata
import re
import shutil

from pathlib import Path
from dataclasses import dataclass

LIC_REGEX = re.compile(
    rb"^\W*SPDX-License-Identifier:\s*([ \w\d.()+-]+?)(?:\s+\W*)?$",
    re.MULTILINE,
)


def extract_licenses(filename):
    """
    Extract SPDX License identifiers from a file
    """
    try:
        with open(filename, "rb") as f:
            size = min(15000, os.stat(filename).st_size)
            txt = f.read(size)
            licenses = re.findall(LIC_REGEX, txt)
            if licenses:
                ascii_licenses = [lic.decode("ascii") for lic in licenses]
                return ascii_licenses
    except Exception as e:
        bb.warn(f"Exception reading {filename}: {e}")
    return []


def is_work_shared_spdx(d):
    return '/work-shared/' in d.getVar('S')


def load_spdx_license_data(d):
    with open(d.getVar("SPDX_LICENSES"), "r") as f:
        data = json.load(f)
        # Transform the license array to a dictionary
        data["licenses"] = {l["licenseId"]: l for l in data["licenses"]}

    return data


def process_sources(d):
    """
    Returns True if the sources for this recipe should be included in the SPDX
    or False if not
    """
    pn = d.getVar("PN")
    assume_provided = (d.getVar("ASSUME_PROVIDED") or "").split()
    if pn in assume_provided:
        for p in d.getVar("PROVIDES").split():
            if p != pn:
                pn = p
                break

    # glibc-locale: do_fetch, do_unpack and do_patch tasks have been deleted,
    # so avoid archiving source here.
    if pn.startswith("glibc-locale"):
        return False
    if d.getVar("PN") == "libtool-cross":
        return False
    if d.getVar("PN") == "libgcc-initial":
        return False
    if d.getVar("PN") == "shadow-sysroot":
        return False

    return True


@dataclass(frozen=True)
class Dep(object):
    pn: str
    hashfn: str
    in_taskhash: bool


def collect_direct_deps(d, dep_task):
    """
    Find direct dependencies of current task

    Returns the list of recipes that have a dep_task that the current task
    depends on
    """
    current_task = "do_" + d.getVar("BB_CURRENTTASK")
    pn = d.getVar("PN")

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)

    for this_dep in taskdepdata.values():
        if this_dep[0] == pn and this_dep[1] == current_task:
            break
    else:
        bb.fatal(f"Unable to find this {pn}:{current_task} in taskdepdata")

    deps = set()

    for dep_name in this_dep.deps:
        dep_data = taskdepdata[dep_name]
        if dep_data.taskname == dep_task and dep_data.pn != pn:
            deps.add((dep_data.pn, dep_data.hashfn, dep_name in this_dep.taskhash_deps))

    return sorted(deps)


def get_spdx_deps(d):
    """
    Reads the SPDX dependencies JSON file and returns the data
    """
    spdx_deps_file = Path(d.getVar("SPDXDEPS"))

    deps = []
    with spdx_deps_file.open("r") as f:
        for d in json.load(f):
            deps.append(Dep(*d))
    return deps


def collect_package_providers(d):
    """
    Returns a dictionary where each RPROVIDES is mapped to the package that
    provides it
    """
    deploy_dir_spdx = Path(d.getVar("DEPLOY_DIR_SPDX"))

    providers = {}

    deps = collect_direct_deps(d, "do_create_spdx")
    deps.append((d.getVar("PN"), d.getVar("BB_HASHFILENAME"), True))

    for dep_pn, dep_hashfn, _ in deps:
        localdata = d
        recipe_data = oe.packagedata.read_pkgdata(dep_pn, localdata)
        if not recipe_data:
            localdata = bb.data.createCopy(d)
            localdata.setVar("PKGDATA_DIR", "${PKGDATA_DIR_SDK}")
            recipe_data = oe.packagedata.read_pkgdata(dep_pn, localdata)

        for pkg in recipe_data.get("PACKAGES", "").split():
            pkg_data = oe.packagedata.read_subpkgdata_dict(pkg, localdata)
            rprovides = set(
                n
                for n, _ in bb.utils.explode_dep_versions2(
                    pkg_data.get("RPROVIDES", "")
                ).items()
            )
            rprovides.add(pkg)

            if "PKG" in pkg_data:
                pkg = pkg_data["PKG"]
                rprovides.add(pkg)

            for r in rprovides:
                providers[r] = (pkg, dep_hashfn)

    return providers


def get_patched_src(d):
    """
    Save patched source of the recipe in SPDX_WORKDIR.
    """
    spdx_workdir = d.getVar("SPDXWORK")
    spdx_sysroot_native = d.getVar("STAGING_DIR_NATIVE")
    pn = d.getVar("PN")

    workdir = d.getVar("WORKDIR")

    try:
        # The kernel class functions require it to be on work-shared, so we dont change WORKDIR
        if not is_work_shared_spdx(d):
            # Change the WORKDIR to make do_unpack do_patch run in another dir.
            d.setVar("WORKDIR", spdx_workdir)
            # Restore the original path to recipe's native sysroot (it's relative to WORKDIR).
            d.setVar("STAGING_DIR_NATIVE", spdx_sysroot_native)

            # The changed 'WORKDIR' also caused 'B' changed, create dir 'B' for the
            # possibly requiring of the following tasks (such as some recipes's
            # do_patch required 'B' existed).
            bb.utils.mkdirhier(d.getVar("B"))

            bb.build.exec_func("do_unpack", d)

            if d.getVar("SRC_URI") != "":
                if bb.data.inherits_class('dos2unix', d):
                    bb.build.exec_func('do_convert_crlf_to_lf', d)
                bb.build.exec_func("do_patch", d)

        # Copy source from work-share to spdx_workdir
        if is_work_shared_spdx(d):
            share_src = d.getVar('S')
            d.setVar("WORKDIR", spdx_workdir)
            d.setVar("STAGING_DIR_NATIVE", spdx_sysroot_native)
            # Copy source to ${SPDXWORK}, same basename dir of ${S};
            src_dir = (
                spdx_workdir
                + "/"
                + os.path.basename(share_src)
            )
            # For kernel souce, rename suffix dir 'kernel-source'
            # to ${BP} (${BPN}-${PV})
            if bb.data.inherits_class("kernel", d):
                src_dir = spdx_workdir + "/" + d.getVar('BP')

            bb.note(f"copyhardlinktree {share_src} to {src_dir}")
            oe.path.copyhardlinktree(share_src, src_dir)

        # Some userland has no source.
        if not os.path.exists(spdx_workdir):
            bb.utils.mkdirhier(spdx_workdir)
    finally:
        d.setVar("WORKDIR", workdir)


def has_task(d, task):
    return bool(d.getVarFlag(task, "task", False)) and not bool(d.getVarFlag(task, "noexec", False))


def fetch_data_to_uri(fd, name):
    """
    Translates a bitbake FetchData to a string URI
    """
    uri = fd.type
    # Map gitsm to git, since gitsm:// is not a valid URI protocol
    if uri == "gitsm":
        uri = "git"
    proto = getattr(fd, "proto", None)
    if proto is not None:
        uri = uri + "+" + proto
    uri = uri + "://" + fd.host + fd.path

    if fd.method.supports_srcrev():
        uri = uri + "@" + fd.revision

    return uri

def is_compiled_source (filename, compiled_sources, types):
    """
    Check if the file is a compiled file
    """
    import os
    # If we don't have compiled source, we assume all are compiled.
    if not compiled_sources:
        return True

    # We return always true if the file type is not in the list of compiled files.
    # Some files in the source directory are not compiled, for example, Makefiles,
    # but also python .py file. We need to include them in the SPDX.
    basename = os.path.basename(filename)
    ext = basename.partition(".")[2]
    if ext not in types:
        return True
    # Check that the file is in the list
    return filename in compiled_sources

def get_compiled_sources(d):
    """
    Get list of compiled sources from debug information and normalize the paths
    """
    import itertools
    source_info = oe.package.read_debugsources_info(d)
    if not source_info:
        bb.debug(1, "Do not have debugsources.list. Skipping")
        return [], []

    # Sources are not split now in SPDX, so we aggregate them
    sources = set(itertools.chain.from_iterable(source_info.values()))
    # Check extensions of files
    types = set()
    for src in sources:
        basename = os.path.basename(src)
        ext = basename.partition(".")[2]
        if ext not in types and ext:
            types.add(ext)
    bb.debug(1, f"Num of sources: {len(sources)} and types: {len(types)} {str(types)}")
    return sources, types
