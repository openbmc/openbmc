#!/usr/bin/env python3

# Yocto Project test results management tool
# This script is an thin layer over resulttool to manage tes results and regression reports.
# Its main feature is to translate tags or branch names to revisions SHA1, and then to run resulttool
# with those computed revisions
#
# Copyright (C) 2023 OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import sys
import os
import argparse
import subprocess
import tempfile
import lib.scriptutils as scriptutils

script_path = os.path.dirname(os.path.realpath(__file__))
poky_path = os.path.abspath(os.path.join(script_path, ".."))
resulttool = os.path.abspath(os.path.join(script_path, "resulttool"))
logger = scriptutils.logger_create(sys.argv[0])
testresults_default_url="git://git.yoctoproject.org/yocto-testresults"

def create_workdir():
    workdir = tempfile.mkdtemp(prefix='yocto-testresults-query.')
    logger.info(f"Shallow-cloning testresults in {workdir}")
    subprocess.check_call(["git", "clone", testresults_default_url, workdir, "--depth", "1"])
    return workdir

def get_sha1(pokydir, revision):
    try:
        rev = subprocess.check_output(["git", "rev-list", "-n", "1", revision], cwd=pokydir).decode('utf-8').strip()
        logger.info(f"SHA-1 revision for {revision} in {pokydir} is {rev}")
        return rev
    except subprocess.CalledProcessError:
        logger.error(f"Can not find SHA-1 for {revision} in {pokydir}")
        return None

def get_branch(tag):
    # The tags in test results repository, as returned by git rev-list, have the following form:
    # refs/tags/<branch>/<count>-g<sha1>/<num>
    return '/'.join(tag.split("/")[2:-2])

def fetch_testresults(workdir, sha1):
    logger.info(f"Fetching test results for {sha1} in {workdir}")
    rawtags = subprocess.check_output(["git", "ls-remote", "--refs", "--tags", "origin", f"*{sha1}*"], cwd=workdir).decode('utf-8').strip()
    if not rawtags:
        raise Exception(f"No reference found for commit {sha1} in {workdir}")
    branch = ""
    for rev in [rawtag.split()[1] for rawtag in rawtags.splitlines()]:
        if not branch:
            branch = get_branch(rev)
        logger.info(f"Fetching matching revision: {rev}")
        subprocess.check_call(["git", "fetch", "--depth", "1", "origin", f"{rev}:{rev}"], cwd=workdir)
    return branch

def compute_regression_report(workdir, basebranch, baserevision, targetbranch, targetrevision, args):
    logger.info(f"Running resulttool regression between SHA1 {baserevision} and {targetrevision}")
    command = [resulttool, "regression-git", "--branch", basebranch, "--commit", baserevision, "--branch2", targetbranch, "--commit2", targetrevision, workdir]
    if args.limit:
        command.extend(["-l", args.limit])
    report = subprocess.check_output(command).decode("utf-8")
    return report

def print_report_with_header(report, baseversion, baserevision, targetversion, targetrevision):
    print("========================== Regression report ==============================")
    print(f'{"=> Target:": <16}{targetversion: <16}({targetrevision})')
    print(f'{"=> Base:": <16}{baseversion: <16}({baserevision})')
    print("===========================================================================\n")
    print(report, end='')

def regression(args):
    logger.info(f"Compute regression report between {args.base} and {args.target}")
    if args.testresultsdir:
        workdir = args.testresultsdir
    else:
        workdir = create_workdir()

    try:
        baserevision = get_sha1(poky_path, args.base)
        targetrevision = get_sha1(poky_path, args.target)
        if not baserevision or not targetrevision:
            logger.error("One or more revision(s) missing. You might be targeting nonexistant tags/branches, or are in wrong repository (you must use Poky and not oe-core)")
            if not args.testresultsdir:
                subprocess.check_call(["rm", "-rf",  workdir])
            sys.exit(1)
        basebranch = fetch_testresults(workdir, baserevision)
        targetbranch = fetch_testresults(workdir, targetrevision)
        report = compute_regression_report(workdir, basebranch, baserevision, targetbranch, targetrevision, args)
        print_report_with_header(report, args.base, baserevision, args.target, targetrevision)
    finally:
        if not args.testresultsdir:
            subprocess.check_call(["rm", "-rf",  workdir])

def main():
    parser = argparse.ArgumentParser(description="Yocto Project test results helper")
    subparsers = parser.add_subparsers(
        help="Supported commands for test results helper",
        required=True)
    parser_regression_report = subparsers.add_parser(
        "regression-report",
        help="Generate regression report between two fixed revisions. Revisions can be branch name or tag")
    parser_regression_report.add_argument(
        'base',
        help="Revision or tag against which to compare results (i.e: the older)")
    parser_regression_report.add_argument(
        'target',
        help="Revision or tag to compare against the base (i.e: the newer)")
    parser_regression_report.add_argument(
        '-t',
        '--testresultsdir',
        help=f"An existing test results directory. {sys.argv[0]} will automatically clone it and use default branch if not provided")
    parser_regression_report.add_argument(
        '-l',
        '--limit',
        help=f"Maximum number of changes to display per test. Can be set to 0 to print all changes")
    parser_regression_report.set_defaults(func=regression)

    args = parser.parse_args()
    args.func(args)

if __name__ == '__main__':
    try:
        ret =  main()
    except Exception:
        ret = 1
        import traceback
        traceback.print_exc()
    sys.exit(ret)
