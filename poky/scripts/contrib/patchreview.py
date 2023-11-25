#! /usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import argparse
import collections
import json
import os
import os.path
import pathlib
import re
import subprocess

# TODO
# - option to just list all broken files
# - test suite
# - validate signed-off-by

status_values = ("accepted", "pending", "inappropriate", "backport", "submitted", "denied", "inactive-upstream")

class Result:
    # Whether the patch has an Upstream-Status or not
    missing_upstream_status = False
    # If the Upstream-Status tag is malformed in some way (string for bad bit)
    malformed_upstream_status = None
    # If the Upstream-Status value is unknown (boolean)
    unknown_upstream_status = False
    # The upstream status value (Pending, etc)
    upstream_status = None
    # Whether the patch has a Signed-off-by or not
    missing_sob = False
    # Whether the Signed-off-by tag is malformed in some way
    malformed_sob = False
    # The Signed-off-by tag value
    sob = None
    # Whether a patch looks like a CVE but doesn't have a CVE tag
    missing_cve = False

def blame_patch(patch):
    """
    From a patch filename, return a list of "commit summary (author name <author
    email>)" strings representing the history.
    """
    return subprocess.check_output(("git", "log",
                                    "--follow", "--find-renames", "--diff-filter=A",
                                    "--format=%s (%aN <%aE>)",
                                    "--", patch)).decode("utf-8").splitlines()

def patchreview(patches):

    # General pattern: start of line, optional whitespace, tag with optional
    # hyphen or spaces, maybe a colon, some whitespace, then the value, all case
    # insensitive.
    sob_re = re.compile(r"^[\t ]*(Signed[-_ ]off[-_ ]by:?)[\t ]*(.+)", re.IGNORECASE | re.MULTILINE)
    status_re = re.compile(r"^[\t ]*(Upstream[-_ ]Status:?)[\t ]*([\w-]*)", re.IGNORECASE | re.MULTILINE)
    cve_tag_re = re.compile(r"^[\t ]*(CVE:)[\t ]*(.*)", re.IGNORECASE | re.MULTILINE)
    cve_re = re.compile(r"cve-[0-9]{4}-[0-9]{4,6}", re.IGNORECASE)

    results = {}

    for patch in patches:

        result = Result()
        results[patch] = result

        content = open(patch, encoding='ascii', errors='ignore').read()

        # Find the Signed-off-by tag
        match = sob_re.search(content)
        if match:
            value = match.group(1)
            if value != "Signed-off-by:":
                result.malformed_sob = value
            result.sob = match.group(2)
        else:
            result.missing_sob = True


        # Find the Upstream-Status tag
        match = status_re.search(content)
        if match:
            value = match.group(1)
            if value != "Upstream-Status:":
                result.malformed_upstream_status = value

            value = match.group(2).lower()
            # TODO: check case
            if value not in status_values:
                result.unknown_upstream_status = True
            result.upstream_status = value
        else:
            result.missing_upstream_status = True

        # Check that patches which looks like CVEs have CVE tags
        if cve_re.search(patch) or cve_re.search(content):
            if not cve_tag_re.search(content):
                result.missing_cve = True
        # TODO: extract CVE list

    return results


def analyse(results, want_blame=False, verbose=True):
    """
    want_blame: display blame data for each malformed patch
    verbose: display per-file results instead of just summary
    """

    # want_blame requires verbose, so disable blame if we're not verbose
    if want_blame and not verbose:
        want_blame = False

    total_patches = 0
    missing_sob = 0
    malformed_sob = 0
    missing_status = 0
    malformed_status = 0
    missing_cve = 0
    pending_patches = 0

    for patch in sorted(results):
        r = results[patch]
        total_patches += 1
        need_blame = False

        # Build statistics
        if r.missing_sob:
            missing_sob += 1
        if r.malformed_sob:
            malformed_sob += 1
        if r.missing_upstream_status:
            missing_status += 1
        if r.malformed_upstream_status or r.unknown_upstream_status:
            malformed_status += 1
            # Count patches with no status as pending
            pending_patches +=1
        if r.missing_cve:
            missing_cve += 1
        if r.upstream_status == "pending":
            pending_patches += 1

        # Output warnings
        if r.missing_sob:
            need_blame = True
            if verbose:
                print("Missing Signed-off-by tag (%s)" % patch)
        if r.malformed_sob:
            need_blame = True
            if verbose:
                print("Malformed Signed-off-by '%s' (%s)" % (r.malformed_sob, patch))
        if r.missing_cve:
            need_blame = True
            if verbose:
                print("Missing CVE tag (%s)" % patch)
        if r.missing_upstream_status:
            need_blame = True
            if verbose:
                print("Missing Upstream-Status tag (%s)" % patch)
        if r.malformed_upstream_status:
            need_blame = True
            if verbose:
                print("Malformed Upstream-Status '%s' (%s)" % (r.malformed_upstream_status, patch))
        if r.unknown_upstream_status:
            need_blame = True
            if verbose:
                print("Unknown Upstream-Status value '%s' (%s)" % (r.upstream_status, patch))

        if want_blame and need_blame:
            print("\n".join(blame_patch(patch)) + "\n")

    def percent(num):
        try:
            return "%d (%d%%)" % (num, round(num * 100.0 / total_patches))
        except ZeroDivisionError:
            return "N/A"

    if verbose:
        print()

    print("""Total patches found: %d
Patches missing Signed-off-by: %s
Patches with malformed Signed-off-by: %s
Patches missing CVE: %s
Patches missing Upstream-Status: %s
Patches with malformed Upstream-Status: %s
Patches in Pending state: %s""" % (total_patches,
                                   percent(missing_sob),
                                   percent(malformed_sob),
                                   percent(missing_cve),
                                   percent(missing_status),
                                   percent(malformed_status),
                                   percent(pending_patches)))



def histogram(results):
    from toolz import recipes, dicttoolz
    import math

    counts = recipes.countby(lambda r: r.upstream_status, results.values())
    bars = dicttoolz.valmap(lambda v: "#" * int(math.ceil(float(v) / len(results) * 100)), counts)
    for k in bars:
        print("%-20s %s (%d)" % (k.capitalize() if k else "No status", bars[k], counts[k]))

def find_layers(candidate):
    # candidate can either be the path to a layer directly (eg meta-intel), or a
    # repository that contains other layers (meta-arm). We can determine what by
    # looking for a conf/layer.conf file. If that file exists then it's a layer,
    # otherwise its a repository of layers and we can assume they're called
    # meta-*.

    if (candidate / "conf" / "layer.conf").exists():
        return [candidate.absolute()]
    else:
        return [d.absolute() for d in candidate.iterdir() if d.is_dir() and (d.name == "meta" or d.name.startswith("meta-"))]

# TODO these don't actually handle dynamic-layers/

def gather_patches(layers):
    patches = []
    for directory in layers:
        filenames = subprocess.check_output(("git", "-C", directory, "ls-files", "recipes-*/**/*.patch", "recipes-*/**/*.diff"), universal_newlines=True).split()
        patches += [os.path.join(directory, f) for f in filenames]
    return patches

def count_recipes(layers):
    count = 0
    for directory in layers:
        output = subprocess.check_output(["git", "-C", directory, "ls-files", "recipes-*/**/*.bb"], universal_newlines=True)
        count += len(output.splitlines())
    return count

if __name__ == "__main__":
    args = argparse.ArgumentParser(description="Patch Review Tool")
    args.add_argument("-b", "--blame", action="store_true", help="show blame for malformed patches")
    args.add_argument("-v", "--verbose", action="store_true", help="show per-patch results")
    args.add_argument("-g", "--histogram", action="store_true", help="show patch histogram")
    args.add_argument("-j", "--json", help="update JSON")
    args.add_argument("directory", type=pathlib.Path, metavar="DIRECTORY", help="directory to scan (layer, or repository of layers)")
    args = args.parse_args()

    layers = find_layers(args.directory)
    print(f"Found layers {' '.join((d.name for d in layers))}")
    patches = gather_patches(layers)
    results = patchreview(patches)
    analyse(results, want_blame=args.blame, verbose=args.verbose)

    if args.json:
        if os.path.isfile(args.json):
            data = json.load(open(args.json))
        else:
            data = []

        row = collections.Counter()
        row["total"] = len(results)
        row["date"] = subprocess.check_output(["git", "-C", args.directory, "show", "-s", "--pretty=format:%cd", "--date=format:%s"], universal_newlines=True).strip()
        row["commit"] = subprocess.check_output(["git", "-C", args.directory, "rev-parse", "HEAD"], universal_newlines=True).strip()
        row['commit_count'] = subprocess.check_output(["git", "-C", args.directory, "rev-list", "--count", "HEAD"], universal_newlines=True).strip()
        row['recipe_count'] = count_recipes(layers)

        for r in results.values():
            if r.upstream_status in status_values:
                row[r.upstream_status] += 1
            if r.malformed_upstream_status or r.missing_upstream_status:
                row['malformed-upstream-status'] += 1
            if r.malformed_sob or r.missing_sob:
                row['malformed-sob'] += 1

        data.append(row)
        json.dump(data, open(args.json, "w"), sort_keys=True, indent="\t")

    if args.histogram:
        print()
        histogram(results)
