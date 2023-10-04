#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import collections

DepRecipe = collections.namedtuple("DepRecipe", ("doc", "doc_sha1", "recipe"))
DepSource = collections.namedtuple("DepSource", ("doc", "doc_sha1", "recipe", "file"))


def get_recipe_spdxid(d):
    return "SPDXRef-%s-%s" % ("Recipe", d.getVar("PN"))


def get_download_spdxid(d, idx):
    return "SPDXRef-Download-%s-%d" % (d.getVar("PN"), idx)


def get_package_spdxid(pkg):
    return "SPDXRef-Package-%s" % pkg


def get_source_file_spdxid(d, idx):
    return "SPDXRef-SourceFile-%s-%d" % (d.getVar("PN"), idx)


def get_packaged_file_spdxid(pkg, idx):
    return "SPDXRef-PackagedFile-%s-%d" % (pkg, idx)


def get_image_spdxid(img):
    return "SPDXRef-Image-%s" % img


def get_sdk_spdxid(sdk):
    return "SPDXRef-SDK-%s" % sdk


def _doc_path_by_namespace(spdx_deploy, arch, doc_namespace):
    return spdx_deploy / "by-namespace" / arch / doc_namespace.replace("/", "_")


def doc_find_by_namespace(spdx_deploy, search_arches, doc_namespace):
    for pkgarch in search_arches:
        p = _doc_path_by_namespace(spdx_deploy, pkgarch, doc_namespace)
        if os.path.exists(p):
            return p
    return None


def _doc_path_by_hashfn(spdx_deploy, arch, doc_name, hashfn):
    return (
        spdx_deploy / "by-hash" / arch / hashfn.split()[1] / (doc_name + ".spdx.json")
    )


def doc_find_by_hashfn(spdx_deploy, search_arches, doc_name, hashfn):
    for pkgarch in search_arches:
        p = _doc_path_by_hashfn(spdx_deploy, pkgarch, doc_name, hashfn)
        if os.path.exists(p):
            return p
    return None


def doc_path(spdx_deploy, doc_name, arch, subdir):
    return spdx_deploy / arch / subdir / (doc_name + ".spdx.json")


def write_doc(d, spdx_doc, arch, subdir, spdx_deploy=None, indent=None):
    from pathlib import Path

    if spdx_deploy is None:
        spdx_deploy = Path(d.getVar("SPDXDEPLOY"))

    dest = doc_path(spdx_deploy, spdx_doc.name, arch, subdir)
    dest.parent.mkdir(exist_ok=True, parents=True)
    with dest.open("wb") as f:
        doc_sha1 = spdx_doc.to_json(f, sort_keys=True, indent=indent)

    l = _doc_path_by_namespace(spdx_deploy, arch, spdx_doc.documentNamespace)
    l.parent.mkdir(exist_ok=True, parents=True)
    l.symlink_to(os.path.relpath(dest, l.parent))

    l = _doc_path_by_hashfn(
        spdx_deploy, arch, spdx_doc.name, d.getVar("BB_HASHFILENAME")
    )
    l.parent.mkdir(exist_ok=True, parents=True)
    l.symlink_to(os.path.relpath(dest, l.parent))

    return doc_sha1


def read_doc(fn):
    import hashlib
    import oe.spdx
    import io
    import contextlib

    @contextlib.contextmanager
    def get_file():
        if isinstance(fn, io.IOBase):
            yield fn
        else:
            with fn.open("rb") as f:
                yield f

    with get_file() as f:
        sha1 = hashlib.sha1()
        while True:
            chunk = f.read(4096)
            if not chunk:
                break
            sha1.update(chunk)

        f.seek(0)
        doc = oe.spdx.SPDXDocument.from_json(f)

    return (doc, sha1.hexdigest())
