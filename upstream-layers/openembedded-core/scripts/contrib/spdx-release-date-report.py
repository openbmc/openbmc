#! /usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
# Author: Daniel Turull <daniel.turull@ericsson.com>
#
# Reports, per recipe, whether a valid releaseTime was recorded in SPDX
# 3.0.1 output.
#
# AI-Generated: Uses Kiro (Claude)

import argparse
import csv
import glob
import json
import logging
import os
import re
import sys


# Each do_create_spdx software_Package's spdxId is
# "<SPDX_NAMESPACE_PREFIX>/<PN>-<uuid5>/<unihash>/...", so the recipe name
# can be recovered directly from the id without walking relationships.
RECIPE_FROM_SPDX_ID_RE = re.compile(
    r"^.*/([^/]+)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}/"
)


def recipe_name_from_spdx_id(spdx_id):
    """Extract the PN that generated an element, from its spdxId, or None."""
    match = RECIPE_FROM_SPDX_ID_RE.match(spdx_id or "")
    return match.group(1) if match else None


def load_jsonld_graph(path):
    """Return the @graph list of a SPDX 3.0.1 JSON-LD document, or [] on error."""
    try:
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)
    except (OSError, json.JSONDecodeError) as e:
        logging.warning("Skipping %s: %s", path, e)
        return []
    return data.get("@graph", [])


def extract_release_dates(elements):
    """
    Map recipe name -> releaseTime (or None), for each downloaded source
    software_Package found in elements. The recipe name is recovered from
    each package's own spdxId (see recipe_name_from_spdx_id). For a given
    name, the first package with a releaseTime wins; if none have one, the
    name still appears, mapped to None.
    """
    release_dates = {}
    for element in elements:
        if element.get("type") != "software_Package":
            continue
        if not element.get("software_downloadLocation"):
            continue
        name = recipe_name_from_spdx_id(element.get("spdxId"))
        if not name:
            continue
        release_dates.setdefault(name, None)
        release_time = element.get("releaseTime")
        if release_time and not release_dates[name]:
            release_dates[name] = release_time
    return release_dates


def iter_spdx_elements(path):
    """Yield @graph elements from path: every build-*.spdx.json under path
    if it's a DEPLOY_DIR_SPDX tree, or path's own @graph if it's a single
    SPDX JSON-LD file (e.g. a merged image SBOM).

    Only build-*.spdx.json is read for a DEPLOY_DIR_SPDX tree: it's the
    output of do_create_spdx, the only recipe task that calls
    add_download_files and so the only one that can carry
    software_downloadLocation/releaseTime. The other per-recipe documents
    (static-*.spdx.json from the fetch-free do_create_recipe_spdx, and
    package-*.spdx.json for binary packages) never have that data.
    """
    if os.path.isdir(path):
        build_pattern = os.path.join(path, "**", "builds", "build-*.spdx.json")
        for build_path in sorted(glob.glob(build_pattern, recursive=True)):
            yield from load_jsonld_graph(build_path)
    else:
        yield from load_jsonld_graph(path)


def _to_row(name, release_date):
    return {
        "recipe": name,
        "release_date": release_date or "",
    }


def build_report(path):
    """
    Build the report: [{"recipe": ..., "release_date": ...}], from either a
    DEPLOY_DIR_SPDX tree or a single merged image SBOM file.
    """
    release_dates = extract_release_dates(iter_spdx_elements(path))
    return [_to_row(name, release_dates[name]) for name in sorted(release_dates)]


def drop_redundant_native(report):
    """Drop foo-native rows when foo is also present in the report."""
    names = {r["recipe"] for r in report}
    return [
        r
        for r in report
        if not (r["recipe"].endswith("-native") and r["recipe"][:-len("-native")] in names)
    ]


def filter_rows(report, sort_by_date=False):
    rows = list(report)
    if sort_by_date:
        rows.sort(key=lambda r: (not r["release_date"], r["release_date"], r["recipe"]))
    return rows


def print_table(rows):
    if not rows:
        print("No matching recipes found.")
        return

    name_width = max(len("recipe"), *(len(r["recipe"]) for r in rows))
    print(f"{'recipe':<{name_width}}  release_date")
    for r in rows:
        print(f"{r['recipe']:<{name_width}}  {r['release_date']}")


def write_csv(rows, path):
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["recipe", "release_date"])
        for r in rows:
            writer.writerow([r["recipe"], r["release_date"]])


def main():
    parser = argparse.ArgumentParser(
        description="Report recipe release dates from SPDX 3.0.1 output"
    )
    parser.add_argument(
        "path",
        help="Path to DEPLOY_DIR_SPDX (e.g. tmp/deploy/spdx/3.0.1) or to a "
             "single merged image SBOM file (e.g. "
             "tmp/deploy/images/<machine>/<image>.rootfs.spdx.json)",
    )
    parser.add_argument(
        "--csv",
        help="Write the report to a CSV file instead of only printing a table",
    )
    parser.add_argument(
        "--sort-by-date",
        action="store_true",
        help="Sort output by release date instead of recipe name (missing dates last)",
    )
    args = parser.parse_args()

    logging.basicConfig(format="[%(filename)s:%(lineno)d] %(message)s", level=logging.INFO)

    if not os.path.isdir(args.path) and not os.path.isfile(args.path):
        parser.error(f"{args.path} does not exist")

    report = build_report(args.path)
    report = drop_redundant_native(report)

    total = len(report)
    found = sum(1 for r in report if r["release_date"])
    logging.info("Recipes with SPDX package data: %d", total)
    logging.info("Recipes with a release date: %d", found)
    logging.info("Recipes missing a release date: %d", total - found)

    rows = filter_rows(report, args.sort_by_date)
    print_table(rows)

    if args.csv:
        write_csv(rows, args.csv)
        logging.info("CSV report written to %s", args.csv)

    return 0


if __name__ == "__main__":
    sys.exit(main())
