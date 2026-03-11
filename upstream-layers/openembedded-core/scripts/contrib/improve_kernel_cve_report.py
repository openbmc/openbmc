#! /usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# The script uses another source of CVE information from linux-vulns
# to enrich the cve-summary from cve-check or vex.
# It can also use the list of compiled files from the kernel spdx to ignore CVEs
# that are not affected since the files are not compiled.
#
# It creates a new json file with updated CVE information
#
# Compiled files can be extracted adding the following in local.conf
# SPDX_INCLUDE_COMPILED_SOURCES:pn-linux-yocto = "1"
#
# Tested with the following CVE sources:
# - https://git.kernel.org/pub/scm/linux/security/vulns.git
# - https://github.com/CVEProject/cvelistV5
#
# Example:
# python3 ./openembedded-core/scripts/contrib/improve_kernel_cve_report.py --spdx tmp/deploy/spdx/3.0.1/qemux86_64/recipes/recipe-linux-yocto.spdx.json --kernel-version 6.12.27 --datadir ./vulns
# python3 ./openembedded-core/scripts/contrib/improve_kernel_cve_report.py --spdx tmp/deploy/spdx/3.0.1/qemux86_64/recipes/recipe-linux-yocto.spdx.json --datadir ./vulns --old-cve-report build/tmp/log/cve/cve-summary.json
#
# SPDX-License-Identifier: GPLv2

import argparse
import json
import sys
import logging
import glob
import os
import pathlib
from packaging.version import Version

def is_linux_cve(cve_info):
    '''Return true is the CVE belongs to Linux'''
    if not "affected" in cve_info["containers"]["cna"]:
        return False
    for affected in cve_info["containers"]["cna"]["affected"]:
        if not "product" in affected:
            return False
        if affected["product"] == "Linux" and affected["vendor"] == "Linux":
            return True
    return False

def get_kernel_cves(datadir, compiled_files, version):
    """
    Get CVEs for the kernel
    """
    cves = {}

    check_config = len(compiled_files) > 0

    base_version = Version(f"{version.major}.{version.minor}")

    # Check all CVES from kernel vulns
    pattern = os.path.join(datadir, '**', "CVE-*.json")
    cve_files = glob.glob(pattern, recursive=True)
    not_applicable_config = 0
    fixed_as_later_backport = 0
    vulnerable = 0
    not_vulnerable = 0
    for cve_file in sorted(cve_files):
        cve_info = {}
        with open(cve_file, "r", encoding='ISO-8859-1') as f:
            cve_info = json.load(f)

        if len(cve_info) == 0:
            logging.error("Not valid data in %s. Aborting", cve_file)
            break

        if not is_linux_cve(cve_info):
            continue
        cve_id = os.path.basename(cve_file)[:-5]
        description = cve_info["containers"]["cna"]["descriptions"][0]["value"]
        if cve_file.find("rejected") >= 0:
            logging.debug("%s is rejected by the CNA", cve_id)
            cves[cve_id] = {
                "id": cve_id,
                "status": "Ignored",
                "detail": "rejected",
                "summary": description,
                "description": f"Rejected by CNA"
            }
            continue
        if any(elem in cve_file for elem in ["review", "reverved", "testing"]):
            continue

        is_vulnerable, first_affected, last_affected, better_match_first, better_match_last, affected_versions = get_cpe_applicability(cve_info, version)

        logging.debug("%s: %s (%s - %s) (%s - %s)", cve_id, is_vulnerable, better_match_first, better_match_last, first_affected, last_affected)

        if is_vulnerable is None:
            logging.warning("%s doesn't have good metadata", cve_id)
        if is_vulnerable:
            is_affected = True
            affected_files = []
            if check_config:
                is_affected, affected_files = check_kernel_compiled_files(compiled_files, cve_info)

            if not is_affected and len(affected_files) > 0:
                logging.debug(
                    "%s - not applicable configuration since affected files not compiled: %s",
                    cve_id, affected_files)
                cves[cve_id] = {
                    "id": cve_id,
                    "status": "Ignored",
                    "detail": "not-applicable-config",
                    "summary": description,
                    "description": f"Source code not compiled by config. {affected_files}"
                }
                not_applicable_config +=1
            # Check if we have backport
            else:
                if not better_match_last:
                    fixed_in = last_affected
                else:
                    fixed_in = better_match_last
                logging.debug("%s needs backporting (fixed from %s)", cve_id, fixed_in)
                cves[cve_id] = {
                        "id": cve_id,
                        "status": "Unpatched",
                        "detail": "version-in-range",
                        "summary": description,
                        "description": f"Needs backporting (fixed from {fixed_in})"
                }
                vulnerable += 1
                if (better_match_last and
                    Version(f"{better_match_last.major}.{better_match_last.minor}") == base_version):
                    fixed_as_later_backport += 1
        # Not vulnerable
        else:
            if not first_affected:
                logging.debug("%s - not known affected %s",
                              cve_id,
                              better_match_last)
                cves[cve_id] = {
                    "id": cve_id,
                    "status": "Patched",
                    "detail": "version-not-in-range",
                    "summary": description,
                    "description": "No CPE match"
                }
                not_vulnerable += 1
                continue
            backport_base = Version(f"{better_match_last.major}.{better_match_last.minor}")
            if version < first_affected:
                logging.debug('%s - fixed-version: only affects %s onwards',
                              cve_id,
                              first_affected)
                cves[cve_id] = {
                    "id": cve_id,
                    "status": "Patched",
                    "detail": "fixed-version",
                    "summary": description,
                    "description": f"only affects {first_affected} onwards"
                }
                not_vulnerable += 1
            elif last_affected <= version:
                logging.debug("%s - fixed-version: Fixed from version %s",
                              cve_id,
                              last_affected)
                cves[cve_id] = {
                    "id": cve_id,
                    "status": "Patched",
                    "detail": "fixed-version",
                    "summary": description,
                    "description": f"fixed-version: Fixed from version {last_affected}"
                }
                not_vulnerable += 1
            elif backport_base == base_version:
                logging.debug("%s - cpe-stable-backport: Backported in %s",
                              cve_id,
                              better_match_last)
                cves[cve_id] = {
                    "id": cve_id,
                    "status": "Patched",
                    "detail": "cpe-stable-backport",
                    "summary": description,
                    "description": f"Backported in {better_match_last}"
                }
                not_vulnerable += 1
            else:
                logging.debug("%s - version not affected %s", cve_id, str(affected_versions))
                cves[cve_id] = {
                    "id": cve_id,
                    "status": "Patched",
                    "detail": "version-not-in-range",
                    "summary": description,
                    "description": f"Range {affected_versions}"
                }
                not_vulnerable += 1

    logging.info("Total CVEs ignored due to not applicable config: %d", not_applicable_config)
    logging.info("Total CVEs not vulnerable due version-not-in-range: %d", not_vulnerable)
    logging.info("Total vulnerable CVEs: %d", vulnerable)

    logging.info("Total CVEs already backported in %s: %s", base_version,
                    fixed_as_later_backport)
    return cves

def read_spdx(spdx_file):
    '''Open SPDX file and extract compiled files'''
    with open(spdx_file, 'r', encoding='ISO-8859-1') as f:
        spdx = json.load(f)
        if "spdxVersion" in spdx:
            if spdx["spdxVersion"] == "SPDX-2.2":
                return read_spdx2(spdx)
        if "@graph" in spdx:
            return read_spdx3(spdx)
    return []

def read_spdx2(spdx):
    '''
    Read spdx2 compiled files from spdx
    '''
    cfiles = set()
    if 'files' not in spdx:
        return cfiles
    for item in spdx['files']:
        for ftype in item['fileTypes']:
            if ftype == "SOURCE":
                filename = item["fileName"][item["fileName"].find("/")+1:]
                cfiles.add(filename)
    return cfiles

def read_spdx3(spdx):
    '''
    Read spdx3 compiled files from spdx
    '''
    cfiles = set()
    for item in spdx["@graph"]:
        if "software_primaryPurpose" not in item:
            continue
        if item["software_primaryPurpose"] == "source":
            filename = item['name'][item['name'].find("/")+1:]
            cfiles.add(filename)
    return cfiles

def check_kernel_compiled_files(compiled_files, cve_info):
    """
    Return if a CVE affected us depending on compiled files
    """
    files_affected = set()
    is_affected = False

    for item in cve_info['containers']['cna']['affected']:
        if "programFiles" in item:
            for f in item['programFiles']:
                if f not in files_affected:
                    files_affected.add(f)

    if len(files_affected) > 0:
        for f in files_affected:
            if f in compiled_files:
                logging.debug("File match: %s", f)
                is_affected = True
    return is_affected, files_affected

def get_cpe_applicability(cve_info, v):
    '''
    Check if version is affected and return affected versions
    '''
    base_branch = Version(f"{v.major}.{v.minor}")
    affected = []
    if not 'cpeApplicability' in cve_info["containers"]["cna"]:
        return None, None, None, None, None, None

    for nodes in cve_info["containers"]["cna"]["cpeApplicability"]:
        for node in nodes.values():
            vulnerable = False
            matched_branch = False
            first_affected = Version("5000")
            last_affected = Version("0")
            better_match_first = Version("0")
            better_match_last = Version("5000")

            if len(node[0]['cpeMatch']) == 0:
                first_affected = None
                last_affected = None
                better_match_first = None
                better_match_last = None

            for cpe_match in node[0]['cpeMatch']:
                version_start_including = Version("0")
                version_end_excluding = Version("0")
                if 'versionStartIncluding' in cpe_match:
                    version_start_including = Version(cpe_match['versionStartIncluding'])
                else:
                    version_start_including = Version("0")
                # if versionEndExcluding is missing we are in a branch, which is not fixed.
                if "versionEndExcluding" in cpe_match:
                    version_end_excluding = Version(cpe_match["versionEndExcluding"])
                else:
                    # if versionEndExcluding is missing we are in a branch, which is not fixed.
                    version_end_excluding = Version(
                        f"{version_start_including.major}.{version_start_including.minor}.5000"
                    )
                affected.append(f" {version_start_including}-{version_end_excluding}")
                # Detect if versionEnd is in fixed in base branch. It has precedence over the rest
                branch_end = Version(f"{version_end_excluding.major}.{version_end_excluding.minor}")
                if branch_end == base_branch:
                    if version_start_including <= v < version_end_excluding:
                        vulnerable = cpe_match['vulnerable']
                    # If we don't match in our branch, we are not vulnerable,
                    # since we have a backport
                    matched_branch = True
                    better_match_first = version_start_including
                    better_match_last = version_end_excluding
                if version_start_including <= v < version_end_excluding and not matched_branch:
                    if version_end_excluding < better_match_last:
                        better_match_first = max(version_start_including, better_match_first)
                        better_match_last = min(better_match_last, version_end_excluding)
                        vulnerable = cpe_match['vulnerable']
                        matched_branch = True

                first_affected = min(version_start_including, first_affected)
                last_affected = max(version_end_excluding, last_affected)
            # Not a better match, we use the first and last affected instead of the fake .5000
            if vulnerable and better_match_last == Version(f"{base_branch}.5000"):
                better_match_last = last_affected
                better_match_first = first_affected
    return vulnerable, first_affected, last_affected, better_match_first, better_match_last, affected

def copy_data(old, new):
    '''Update dictionary with new entries, while keeping the old ones'''
    for k in new.keys():
        old[k] = new[k]
    return old

# Function taken from cve_check.bbclass. Adapted to cve fields
def cve_update(cve_data, cve, entry):
    # If no entry, just add it
    if cve not in cve_data:
        cve_data[cve] = entry
        return
    # If we are updating, there might be change in the status
    if cve_data[cve]['status'] == "Unknown":
        cve_data[cve] = copy_data(cve_data[cve], entry)
        return
    if cve_data[cve]['status'] == entry['status']:
        return
    if entry['status'] == "Unpatched" and cve_data[cve]['status'] == "Patched":
        # Backported-patch (e.g. vendor kernel repo with cherry-picked CVE patch)
        # has priority over unpatch from CNA
        if cve_data[cve]['detail'] == "backported-patch":
            return
        logging.warning("CVE entry %s update from Patched to Unpatched from the scan result", cve)
        cve_data[cve] = copy_data(cve_data[cve], entry)
        return
    if entry['status'] == "Patched" and cve_data[cve]['status'] == "Unpatched":
        logging.warning("CVE entry %s update from Unpatched to Patched from the scan result", cve)
        cve_data[cve] = copy_data(cve_data[cve], entry)
        return
    # If we have an "Ignored", it has a priority
    if cve_data[cve]['status'] == "Ignored":
        logging.debug("CVE %s not updating because Ignored", cve)
        return
    # If we have an "Ignored", it has a priority
    if entry['status'] == "Ignored":
        cve_data[cve] = copy_data(cve_data[cve], entry)
        logging.debug("CVE entry %s updated from Unpatched to Ignored", cve)
        return
    logging.warning("Unhandled CVE entry update for %s %s from %s %s to %s",
        cve, cve_data[cve]['status'], cve_data[cve]['detail'],  entry['status'], entry['detail'])

def main():
    parser = argparse.ArgumentParser(
        description="Update cve-summary with kernel compiled files and kernel CVE information"
    )
    parser.add_argument(
        "-s",
        "--spdx",
        help="SPDX2/3 for the kernel. Needs to include compiled sources",
    )
    parser.add_argument(
        "--datadir",
        type=pathlib.Path,
        help="Directory where CVE data is",
        required=True
    )
    parser.add_argument(
        "--old-cve-report",
        help="CVE report to update. (Optional)",
    )
    parser.add_argument(
        "--kernel-version",
        help="Kernel version. Needed if old cve_report is not provided (Optional)",
        type=Version
    )
    parser.add_argument(
        "--new-cve-report",
        help="Output file",
        default="cve-summary-enhance.json"
    )
    parser.add_argument(
        "-D",
        "--debug",
        help='Enable debug ',
        action="store_true")

    args = parser.parse_args()

    if args.debug:
        log_level=logging.DEBUG
    else:
        log_level=logging.INFO
    logging.basicConfig(format='[%(filename)s:%(lineno)d] %(message)s', level=log_level)

    if not args.kernel_version and not args.old_cve_report:
        parser.error("either --kernel-version or --old-cve-report are needed")
        return -1

    # by default we don't check the compiled files, unless provided
    compiled_files = []
    if args.spdx:
        compiled_files = read_spdx(args.spdx)
        logging.info("Total compiled files %d", len(compiled_files))

    if args.old_cve_report:
        with open(args.old_cve_report, encoding='ISO-8859-1') as f:
            cve_report = json.load(f)
    else:
        #If summary not provided, we create one
        cve_report = {
            "version": "1",
            "package": [
                {
                    "name": "linux-yocto",
                    "version": str(args.kernel_version),
                    "products": [
                        {
                            "product": "linux_kernel",
                            "cvesInRecord": "Yes"
                        }
                    ],
                    "issue": []
                }
            ]
        }

    for pkg in cve_report['package']:
        is_kernel = False
        for product in pkg['products']:
            if product['product'] == "linux_kernel":
                is_kernel=True
        if not is_kernel:
            continue
        # We remove custom versions after -
        upstream_version = Version(pkg["version"].split("-")[0])
        logging.info("Checking kernel %s", upstream_version)
        kernel_cves = get_kernel_cves(args.datadir,
                                      compiled_files,
                                      upstream_version)
        logging.info("Total kernel cves from kernel CNA: %s", len(kernel_cves))
        cves = {issue["id"]: issue for issue in pkg["issue"]}
        logging.info("Total kernel before processing cves: %s", len(cves))

        for cve in kernel_cves:
            cve_update(cves, cve, kernel_cves[cve])

        pkg["issue"] = []
        for cve in sorted(cves):
            pkg["issue"].extend([cves[cve]])
        logging.info("Total kernel cves after processing: %s", len(pkg['issue']))

    with open(args.new_cve_report, "w", encoding='ISO-8859-1') as f:
        json.dump(cve_report, f, indent=2)

    return 0

if __name__ == "__main__":
    sys.exit(main())

