#! /usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only

import argparse
import json
import sys
import urllib.request
from pathlib import Path

TOP_DIR = Path(__file__).parent.parent


def main():
    parser = argparse.ArgumentParser(
        description="Update SPDX License files from upstream"
    )
    parser.add_argument(
        "-v",
        "--version",
        metavar="MAJOR.MINOR[.MICRO]",
        help="Pull specific version of License list instead of latest",
    )
    parser.add_argument(
        "--overwrite",
        action="store_true",
        help="Update existing license file text with upstream text",
    )
    parser.add_argument(
        "--deprecated",
        action="store_true",
        help="Update deprecated licenses",
    )
    parser.add_argument(
        "--dest",
        type=Path,
        default=TOP_DIR / "meta" / "files" / "common-licenses",
        help="Write licenses to directory DEST. Default is %(default)s",
    )

    args = parser.parse_args()

    if args.version:
        version = f"v{args.version}"
    else:
        # Fetch the latest release
        req = urllib.request.Request(
            "https://api.github.com/repos/spdx/license-list-data/releases/latest"
        )
        req.add_header("X-GitHub-Api-Version", "2022-11-28")
        req.add_header("Accept", "application/vnd.github+json")
        with urllib.request.urlopen(req) as response:
            data = json.load(response)
            version = data["tag_name"]

    print(f"Pulling SPDX license list version {version}")
    req = urllib.request.Request(
        f"https://raw.githubusercontent.com/spdx/license-list-data/{version}/json/licenses.json"
    )
    with urllib.request.urlopen(req) as response:
        spdx_licenses = json.load(response)

    with (TOP_DIR / "meta" / "files" / "spdx-licenses.json").open("w") as f:
        json.dump(spdx_licenses, f, sort_keys=True, indent=2)

    total_count = len(spdx_licenses["licenses"])
    updated = 0
    for idx, lic in enumerate(spdx_licenses["licenses"]):
        lic_id = lic["licenseId"]

        print(f"[{idx + 1} of {total_count}] ", end="")

        dest_license_file = args.dest / lic_id
        if dest_license_file.is_file() and not args.overwrite:
            print(f"Skipping {lic_id} since it already exists")
            continue

        print(f"Fetching {lic_id}... ", end="", flush=True)

        req = urllib.request.Request(lic["detailsUrl"])
        with urllib.request.urlopen(req) as response:
            lic_data = json.load(response)

        if lic_data["isDeprecatedLicenseId"] and not args.deprecated:
            print("Skipping (deprecated)")
            continue

        with dest_license_file.open("w") as f:
            f.write(lic_data["licenseText"])
        updated += 1
        print("done")

    print(f"Updated {updated} licenses")

    return 0


if __name__ == "__main__":
    sys.exit(main())
