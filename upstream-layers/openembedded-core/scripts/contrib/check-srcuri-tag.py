#!/usr/bin/env python3
# SPDX-FileCopyrightText: OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT

# pylint: disable=invalid-name

"""
Scan parsed recipes for git/gitsm SRC_URI entries missing a ;tag= parameter.

Default mode reports only.  With --query-remote the script queries upstream
tags and, when a well-known tag format (v${PV}, ${PV}, ${BPN}-${PV} or the
recipe's UPSTREAM_CHECK_GITTAGREGEX) resolves to the recipe's SRCREV, suggests
it as a ;tag= candidate.  With --write (which implies --query-remote) the
unambiguous candidate is written directly into the file that literally
defines the SRC_URI entry (which may be a .inc file required by the
recipe).  Only literal SRC_URI entries (no ${...} in the URL token) are
modified.

Must be run from an initialized build environment (oe-init-build-env).

Usage examples:
  check-srcuri-tag.py
  check-srcuri-tag.py --query-remote
  check-srcuri-tag.py --write
  check-srcuri-tag.py --recipe busybox --write
  check-srcuri-tag.py --layer /path/to/meta-foo
  check-srcuri-tag.py --fail-on-missing
"""

import argparse
import logging
import os
import re
import sys
from dataclasses import dataclass

scripts_path = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
sys.path.insert(0, os.path.join(scripts_path, 'lib'))

import scriptpath  # pylint: disable=import-error,wrong-import-position
if not scriptpath.add_bitbake_lib_path():
    sys.exit("Unable to find bitbake libraries")
scriptpath.add_oe_lib_path()

import bb.cache        # pylint: disable=wrong-import-position
import bb.fetch       # pylint: disable=wrong-import-position
import bb.tinfoil      # pylint: disable=wrong-import-position
import oe.recipeutils  # pylint: disable=wrong-import-position

# Well-known tag formats tried in order when --query-remote is active.
TAG_FORMATS = ["v${PV}", "${PV}", "${BPN}-${PV}"]

# Cache of remote tags keyed by upstream repository identity.
_lsremote_cache = {}


@dataclass(eq=False)
class UrlResult:
    """Outcome for a single git URL within a recipe."""
    entry: str          # unexpanded SRC_URI token
    base_url: str       # expanded scheme://host/path (no parameters)
    name: str           # name= parameter, or ""
    status: str         # tagged|missing|remote-error|parse-error
    tag: str = ""       # existing ;tag= value (status == tagged)
    tag_fmt: str = ""   # suggested tag candidate (status == missing)
    detail: str = ""    # human-readable note


def get_literal_srcrev(name, data):
    """Return (sha, None) or (None, reason) for the named git URL.

    SRCREV candidates are read unexpanded (to avoid triggering AUTOREV
    resolution) in the same fallback order used by bb.fetch, accepting only a
    plain hex SHA (or the SHA embedded in a cached AUTOINC+<sha> value).
    """
    pn = data.getVar("PN") or ""
    attempts = []
    if name and name != "default":
        if pn:
            attempts.append(f"SRCREV_{name}:pn-{pn}")
        attempts.append(f"SRCREV_{name}")
    if pn:
        attempts.append(f"SRCREV:pn-{pn}")
    attempts.append("SRCREV")

    for key in attempts:
        val = data.getVar(key, False)
        if not val or val == "INVALID":
            continue
        if val.startswith("AUTOINC+"):
            val = val[len("AUTOINC+"):]
        if "AUTOREV" in val or "AUTOINC" in val:
            return None, "SRCREV is a floating revision"
        if "${" in val:
            return None, "SRCREV is not a literal value"
        val = val.lower()
        if re.fullmatch(r'[0-9a-f]{40}|[0-9a-f]{64}', val):
            return val, None
        return None, "SRCREV is not a literal SHA"
    return None, "SRCREV is not set"


def get_release_pv(data):
    """Return (pv, None) for a plain release PV, or (None, reason).

    The unexpanded PV is checked first so PV = "${SRCPV}" or "1.0+git" are
    rejected before any expansion side effects occur.
    """
    pv_raw = data.getVar("PV", False) or ""
    if not pv_raw or "git" in pv_raw or "SRCPV" in pv_raw:
        return None, f"PV '{pv_raw}' is not a plain release version"
    return data.getVar("PV") or pv_raw, None


def lsremote_tags(ud, data):
    """Return ({tag_name: frozenset(shas)}, None) or (None, reason).

    Uses the Git fetcher's _lsremote() so mirrors, credentials and
    BB_NO_NETWORK are honoured.  Both the tag-object and peeled-commit SHAs of
    annotated tags are collected under the same tag name.
    """
    key = (getattr(ud, 'user', ''), getattr(ud, 'proto', ''),
           getattr(ud, 'host', ''), getattr(ud, 'path', ''))
    if key in _lsremote_cache:
        return _lsremote_cache[key]

    try:
        output = ud.method._lsremote(ud, data, "refs/tags/*")
    except (bb.fetch.NetworkAccess, bb.fetch.FetchError) as exc:
        result = (None, f"remote error: {exc}")
        _lsremote_cache[key] = result
        return result

    tag_map = {}
    for line in output.splitlines():
        parts = line.split(None, 1)
        if len(parts) != 2:
            continue
        sha, ref = parts
        tag = ref.removeprefix("refs/tags/").removesuffix("^{}")
        tag_map.setdefault(tag, set()).add(sha)

    result = ({k: frozenset(v) for k, v in tag_map.items()}, None)
    _lsremote_cache[key] = result
    return result


def deduce_tag_format(ud, srcrev, pv, bpn, data):
    """Return (tag_fmt, None) if a well-known format resolves to srcrev on the
    remote, else (None, reason).

    Tries TAG_FORMATS first; if none match and the recipe defines
    UPSTREAM_CHECK_GITTAGREGEX, that regex is used to find the tag whose
    captured 'pver' group (normalized from '_' to '.') equals PV and whose SHA
    matches SRCREV.  A unique regex hit is returned as a literal tag name.
    """
    tag_map, err = lsremote_tags(ud, data)
    if tag_map is None:
        return None, err
    if not tag_map:
        return None, "remote has no tags"

    matches = [fmt for fmt in TAG_FORMATS
               if srcrev in tag_map.get(
                   fmt.replace("${PV}", pv).replace("${BPN}", bpn), frozenset())]
    if len(matches) == 1:
        return matches[0], None
    if len(matches) > 1:
        return None, f"ambiguous: multiple formats match ({', '.join(matches)})"

    check_regex = data.getVar("UPSTREAM_CHECK_GITTAGREGEX", False)
    if check_regex:
        try:
            compiled = re.compile(check_regex)
        except re.error as exc:
            return None, f"UPSTREAM_CHECK_GITTAGREGEX is invalid: {exc}"
        regex_matches = []
        for tag_name, shas in tag_map.items():
            m = compiled.fullmatch(tag_name)
            if (m and "pver" in m.groupdict()
                    and m.group("pver").replace("_", ".") == pv
                    and srcrev in shas):
                regex_matches.append(tag_name)
        if len(regex_matches) == 1:
            return regex_matches[0], None
        if len(regex_matches) > 1:
            return None, ("ambiguous: UPSTREAM_CHECK_GITTAGREGEX matched "
                          f"multiple tags ({', '.join(sorted(regex_matches))})")

    return None, "no well-known tag format matched SRCREV on remote"


def add_candidate(result, exp_url, name, data):
    """Fill in result.tag_fmt/detail for a missing-tag URL under --query-remote.

    Sets result.status to 'remote-error' if the remote could not be queried.
    """
    srcrev, reason = get_literal_srcrev(name or "default", data)
    if not srcrev:
        result.detail = f"no suggestion: {reason}"
        return
    pv, reason = get_release_pv(data)
    if not pv:
        result.detail = f"no suggestion: {reason}"
        return

    try:
        ud = bb.fetch.FetchData(exp_url, data)
    except bb.fetch.FetchError as exc:
        result.status = "remote-error"
        result.detail = str(exc)
        return

    fmt, reason = deduce_tag_format(ud, srcrev, pv, data.getVar("BPN") or "", data)
    if fmt:
        result.tag_fmt = fmt
        result.detail = f"candidate: ;tag={fmt}"
    elif reason.startswith("remote error"):
        result.status = "remote-error"
        result.detail = reason
    else:
        result.detail = f"no suggestion: {reason}"


def check_url(entry, data, args):
    """Process a single git/gitsm SRC_URI entry.  Returns a UrlResult."""
    exp = data.expand(entry)
    try:
        scheme, host, path, _u, _p, parm = bb.fetch.decodeurl(exp)
    except bb.fetch.MalformedUrl as exc:
        return UrlResult(entry, exp, "", "parse-error", detail=str(exc))

    base_url = f"{scheme}://{host}{path}"
    name = parm.get("name", "")
    # The git fetcher no longer supports comma-separated name= values.
    if "," in name:
        return UrlResult(entry, base_url, name, "parse-error",
                         detail="comma in name= parameter")
    if parm.get("tag"):
        return UrlResult(entry, base_url, name, "tagged", tag=parm["tag"])

    result = UrlResult(entry, base_url, name, "missing")
    if args.query_remote:
        add_candidate(result, exp, name, data)
    return result


def check_recipe(data, args):
    """Return a list of UrlResult for every git/gitsm SRC_URI entry."""
    entries = oe.recipeutils.split_var_value(
        data.getVar("SRC_URI", False) or "", assignment=False)
    return [check_url(e, data, args) for e in entries
            if e.startswith(("git://", "gitsm://"))]


def add_tag_to_entry(entry, tag_fmt):
    """Return entry with ;tag=<tag_fmt> added (entry must be a literal URL)."""
    decoded = list(bb.fetch.decodeurl(entry))
    decoded[5]["tag"] = tag_fmt
    return bb.fetch.encodeurl(decoded)


def find_source_file(entry, data):
    """Return (filepath, None) for the one file whose SRC_URI fragment
    literally contains entry, or (None, reason) if that can't be determined
    unambiguously.

    entry must contain no variable references (checked by the caller).  Only
    files recorded in SRC_URI's variable history are considered, and entry
    must appear as a whole space-separated token in exactly one of them (this
    also catches the case where the git URL is set in a .inc file that is
    require'd by the .bb, which is common for git recipes).
    """
    matches = []
    for event in data.varhistory.variable("SRC_URI") or []:
        fn = event.get("file")
        detail = event.get("detail")
        if not fn or not detail:
            continue
        if entry in detail.split() and fn not in matches:
            matches.append(fn)

    if not matches:
        return None, "URL not found literally in SRC_URI history"
    if len(matches) > 1:
        return None, f"URL set in multiple files: {', '.join(matches)}"
    return matches[0], None


def apply_tag(fn, entry, tag_fmt):
    """Replace entry with entry;tag=<tag_fmt> in fn.  entry must appear as a
    whole space/quote-delimited token exactly once.

    Returns (True, None) on success, or (False, reason) on error.
    """
    try:
        with open(fn, encoding="utf-8") as f:
            content = f.read()
    except OSError as exc:
        return False, f"read error: {exc}"

    pattern = re.compile(r'(?<![^\s"])%s(?=[\s"\\]|$)' % re.escape(entry))
    replacement = add_tag_to_entry(entry, tag_fmt)
    new_content, count = pattern.subn(lambda _m: replacement, content)
    if count != 1:
        return False, f"expected 1 occurrence of URL in {fn}, found {count}"

    try:
        with open(fn, "w", encoding="utf-8") as f:
            f.write(new_content)
    except OSError as exc:
        return False, f"write error: {exc}"
    return True, None


def write_candidates(data, results, layer_path):
    """Write unambiguous ;tag= candidates directly into their source file(s).

    Only literal SRC_URI entries (no ${...}) are touched.  Returns the list
    of (UrlResult, filepath) pairs that were written.
    """
    written = []
    for r in results:
        if r.status != "missing" or not r.tag_fmt:
            continue
        if "${" in r.entry:
            r.detail = f"write skipped: URL contains variable references ({r.entry})"
            continue
        fn, reason = find_source_file(r.entry, data)
        if not fn:
            r.detail = f"write skipped: {reason}"
            continue
        if layer_path and not under_layer(fn, layer_path):
            r.detail = f"write skipped: {fn} is outside --layer"
            continue
        ok, reason = apply_tag(fn, r.entry, r.tag_fmt)
        if ok:
            written.append((r, fn))
        else:
            r.detail = f"write skipped: {reason}"
    return written


def under_layer(fn, layer_path):
    """Return True if fn is under layer_path (already realpath'd)."""
    try:
        return os.path.commonpath([os.path.realpath(fn), layer_path]) == layer_path
    except ValueError:
        return False


def main():
    """Main entry point."""
    parser = argparse.ArgumentParser(
        description="Report (and optionally fix) git SRC_URI entries missing ;tag=",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="NOTE: --query-remote (implied by --write) contacts upstream "
               "git servers.")
    parser.add_argument("--query-remote", action="store_true",
                        help="Query upstream remotes to suggest a ;tag= for "
                             "missing entries (requires network access)")
    parser.add_argument("--write", action="store_true",
                        help="Apply unambiguous tag candidates to their source "
                             "recipe/.inc files; implies --query-remote.  Only "
                             "literal SRC_URI entries (no ${...}) are modified.")
    parser.add_argument("--recipe", metavar="PN",
                        help="Check only this recipe name")
    parser.add_argument("--layer", metavar="PATH",
                        help="Check only recipes under this layer directory")
    parser.add_argument("--fail-on-missing", action="store_true",
                        help="Exit non-zero if any URLs are still missing ;tag= "
                             "after this run (remote errors are not counted)")
    parser.add_argument("--verbose", "-v", action="store_true",
                        help="Show tagged and parse-error URLs as well")
    args = parser.parse_args()

    if args.write:
        args.query_remote = True

    layer_path = None
    if args.layer:
        layer_path = os.path.realpath(args.layer)
        if not os.path.isdir(layer_path):
            sys.exit(f"error: --layer is not a directory: {args.layer!r}")

    logging.basicConfig(
        format="%(levelname)s: %(message)s",
        level=logging.DEBUG if args.verbose else logging.WARNING)

    stats = dict.fromkeys(
        ("total", "tagged", "missing", "candidate", "written",
         "remote_error", "parse_error"), 0)
    modified_files = []

    with bb.tinfoil.Tinfoil(tracking=True) as tinfoil:
        tinfoil.prepare()
        pkg_pn = tinfoil.cooker.recipecaches[''].pkg_pn
        checked_files = set()

        for pn in sorted(pkg_pn):
            if args.recipe and pn != args.recipe:
                continue

            for fn in pkg_pn[pn]:
                realfn, _, _ = bb.cache.virtualfn2realfn(fn)
                if realfn in checked_files:
                    continue
                checked_files.add(realfn)
                if layer_path and not under_layer(realfn, layer_path):
                    continue

                data = tinfoil.parse_recipe_file(realfn)
                results = check_recipe(data, args)
                if not results:
                    continue

                if args.write:
                    written = write_candidates(data, results, layer_path)
                    for _r, wfn in written:
                        if wfn not in modified_files:
                            modified_files.append(wfn)
                else:
                    written = []
                written_results = {r for r, _fn in written}

                basename = os.path.basename(realfn)
                for r in results:
                    stats["total"] += 1
                    label = f"{pn}  {basename}"
                    if r.name and r.name != "default":
                        label += f"  name={r.name}"
                    else:
                        label += f"  {r.base_url}"

                    if r.status == "tagged":
                        stats["tagged"] += 1
                        if args.verbose:
                            print(f"OK:           {label}  tag={r.tag}")
                    elif r.status == "missing":
                        stats["missing"] += 1
                        if r.tag_fmt:
                            stats["candidate"] += 1
                        if r in written_results:
                            stats["written"] += 1
                            print(f"WRITTEN:      {label}  ;tag={r.tag_fmt}")
                        else:
                            suffix = f"  {r.detail}" if r.detail else ""
                            print(f"MISSING:      {label}{suffix}")
                    elif r.status == "remote-error":
                        stats["remote_error"] += 1
                        print(f"REMOTE-ERROR: {label}  {r.detail}")
                    elif r.status == "parse-error":
                        stats["parse_error"] += 1
                        if args.verbose:
                            print(f"PARSE-ERROR:  {label}  {r.detail}")

    print("\n--- Summary ---")
    print(f"Git URLs checked : {stats['total']}")
    print(f"  Tagged         : {stats['tagged']}")
    print(f"  Missing tag    : {stats['missing']}")
    if args.query_remote:
        print(f"    candidate found: {stats['candidate']}")
        print(f"  Remote error   : {stats['remote_error']}")
    if args.write:
        print(f"  Written        : {stats['written']}")
    if stats["parse_error"]:
        print(f"  Parse error    : {stats['parse_error']}")

    if modified_files:
        print("\nModified files:")
        for fn in modified_files:
            print(f"  {fn}")
        print("\nReview changes with:  git diff")
        print("Test affected recipes with:  bitbake -c fetch <recipe>")

    if args.fail_on_missing and stats["missing"] - stats["written"] > 0:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
