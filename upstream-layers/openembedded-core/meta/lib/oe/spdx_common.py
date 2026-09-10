#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import bb
import collections
import json
import oe.packagedata
import os
import re
import shutil

from pathlib import Path
from dataclasses import dataclass

LIC_REGEX = re.compile(
    rb"^\W*SPDX-License-Identifier:\s*([ \w.()+-]+?)(?:\s+\W*)?$",
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
    return "/work-shared/" in d.getVar("S")


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


@dataclass(frozen=True, eq=True, order=True)
class Dep(object):
    pn: str
    hashfn: str
    in_taskhash: bool

    def to_tuple(self):
        return (self.pn, self.hashfn, self.in_taskhash)


def collect_direct_deps(d, dep_task):
    """
    Find direct dependencies of current task

    Returns the list of recipes that have a dep_task that the current task
    depends on
    """
    current_task = "do_" + d.getVar("BB_CURRENTTASK")
    pn = d.getVar("PN")

    taskdepdata = d.getVar("BB_TASKDEPDATA", False)

    # Check that the task is listed one of the task dependency flags of the
    # current task
    depflags = (
        set((d.getVarFlag(current_task, "deptask") or "").split())
        | set((d.getVarFlag(current_task, "rdeptask") or "").split())
        | set((d.getVarFlag(current_task, "recrdeptask") or "").split())
    )

    if not dep_task in depflags:
        bb.fatal(
            f"Task {dep_task} was not found in any dependency flag of {pn}:{current_task}"
        )

    for this_dep in taskdepdata.values():
        if this_dep[0] == pn and this_dep[1] == current_task:
            break
    else:
        bb.fatal(f"Unable to find this {pn}:{current_task} in taskdepdata")

    deps = set()

    for dep_name in this_dep.deps:
        dep_data = taskdepdata[dep_name]
        if dep_data.taskname == dep_task and dep_data.pn != pn:
            deps.add(
                Dep(dep_data.pn, dep_data.hashfn, dep_name in this_dep.taskhash_deps)
            )

    return sorted(deps)


def collect_package_providers(d, direct_deps):
    """
    Returns a dictionary where each RPROVIDES is mapped to the package that
    provides it
    """
    providers = {}

    all_deps = direct_deps + [Dep(d.getVar("PN"), d.getVar("BB_HASHFILENAME"), True)]

    for dep in all_deps:
        localdata = d
        recipe_data = oe.packagedata.read_pkgdata(dep.pn, localdata)
        if not recipe_data:
            localdata = bb.data.createCopy(d)
            localdata.setVar("PKGDATA_DIR", "${PKGDATA_DIR_SDK}")
            recipe_data = oe.packagedata.read_pkgdata(dep.pn, localdata)

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
                providers[r] = (pkg, dep.hashfn)

    return providers


def get_patched_src(d):
    """
    Save patched source of the recipe in SPDXWORK.
    """
    spdx_workdir = d.getVar("SPDXWORK")

    # Do not unpack the sources again for the recipe using work-shared
    if not is_work_shared_spdx(d):
        localdata = d.createCopy()

        # Change the UNPACKDIR to make do_unpack do_patch run in another dir.
        localdata.setVar("UNPACKDIR", spdx_workdir)

        bb.build.exec_func("do_unpack", localdata)

        if localdata.getVar("SRC_URI"):
            if bb.data.inherits_class("dos2unix", localdata):
                bb.build.exec_func("do_convert_crlf_to_lf", localdata)
            bb.build.exec_func("do_patch", localdata)

        if bb.data.inherits_class("kernel", localdata):
            # For kernel source, rename suffix dir to ${BP} (${BPN}-${PV})
            dir_name = localdata.getVar("BP")
            kernel_dst_path = f"{spdx_workdir}/{dir_name}"
            kernel_src_path = localdata.getVar("S")
            if not os.path.exists(kernel_dst_path):
                shutil.move(kernel_src_path, kernel_dst_path)

    # Copy source from work-shared to spdx_workdir
    else:
        share_src = d.getVar("S")

        if bb.data.inherits_class("kernel", d):
            # For kernel source, rename suffix dir 'kernel-source' to ${BP} (${BPN}-${PV})
            dir_name = d.getVar("BP")
        else:
            # Copy source to ${SPDXWORK}, same basename dir of ${S}
            dir_name = os.path.basename(share_src)

        src_dir = f"{spdx_workdir}/{dir_name}"
        bb.note(f"copyhardlinktree {share_src} to {src_dir}")
        oe.path.copyhardlinktree(share_src, src_dir)

    # Some userland has no source.
    if not os.path.exists(spdx_workdir):
        bb.utils.mkdirhier(spdx_workdir)


def has_task(d, task):
    return bool(d.getVarFlag(task, "task", False)) and not bool(
        d.getVarFlag(task, "noexec", False)
    )


def fetch_data_to_uri(fd, name):
    """
    Translates a bitbake FetchData to a string URI
    """
    uri = fd.type

    # crate: is not a valid URL.  Use url field instead if exist
    if uri == "crate" and hasattr(fd, "url"):
        return fd.url

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


def is_compiled_source(filename, compiled_sources, types):
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

    unpackdir = d.getVar("UNPACKDIR")
    srcdir = d.getVar("S")
    bp = d.getVar("BP")
    kernel_src = d.getVar("KERNEL_SRC_PATH")
    dbgsrc_dir = d.getVar("TARGET_DBGSRC_DIR")

    # Compute the relative path of source directory from ${UNPACKDIR}.
    # The goal is to replace ${TARGET_DBGSRC_DIR} by this relative path.
    srcdir_rel = None
    if srcdir and unpackdir:
        srcdir_rel = os.path.relpath(srcdir, unpackdir)
        if srcdir_rel.startswith(".."):
            srcdir_rel = None

    sources = set()
    types = set()

    # Sources are not split now in SPDX, so we aggregate them
    for src in set(itertools.chain.from_iterable(source_info.values())):
        # In the common case, the sources are located in ${S}. To format them as
        # expected by SPDX, we replace /usr/src/debug/${PN}/${PV} with the path
        # of ${S} relative to ${UNPACKDIR}.
        if dbgsrc_dir and srcdir_rel:
            src = src.replace(f"{dbgsrc_dir}/", f"{srcdir_rel}/")

        # Kernel sources are in a different directory and are special case
        # we format the sources as expected by spdx by replacing /usr/src/kernel/
        # into ${BP}/
        if kernel_src and bp:
            src = src.replace(f"{kernel_src}/", f"{bp}/")

        sources.add(src)

        # Check extensions of files
        basename = os.path.basename(src)
        ext = basename.partition(".")[2]
        if ext:
            types.add(ext)

    bb.debug(1, f"Num of sources: {len(sources)} and types: {len(types)} {types!s}")
    return sources, types
