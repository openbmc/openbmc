# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# patchtestdata: module used to share command line arguments between
#                patchtest & test suite and a data store between test cases
#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#
# NOTE: Strictly speaking, unit test should be isolated from outside,
#       but patchtest test suites uses command line input data and
#       pretest and test test cases may use the datastore defined
#       on this module

import os
import argparse

default_testdir = os.path.abspath(os.path.dirname(__file__) + "/tests")
default_repodir = os.path.abspath(os.path.dirname(__file__) + "/../../..")

class PatchtestParser(object):
    """Abstract the patchtest argument parser"""

    @classmethod
    def set_namespace(cls):
        parser = cls.get_parser()
        parser.parse_args(namespace=cls)

    @classmethod
    def get_parser(cls):
        parser = argparse.ArgumentParser()

        target_patch_group = parser.add_mutually_exclusive_group(required=True)

        target_patch_group.add_argument('--patch', metavar='PATCH', dest='patch_path',
                            help='The patch to be tested')

        target_patch_group.add_argument('--directory', metavar='DIRECTORY', dest='patch_path',
                            help='The directory containing patches to be tested')

        parser.add_argument('--repodir', metavar='REPO',
                            default=default_repodir,
                            help="Name of the repository where patch is merged")

        parser.add_argument('--testdir', metavar='TESTDIR',
                            default=default_testdir,
                            help="Directory where test cases are located")

        parser.add_argument('--top-level-directory', '-t',
                            dest='topdir',
                            default=None,
                            help="Top level directory of project (defaults to start directory)")

        parser.add_argument('--pattern', '-p',
                            dest='pattern',
                            default='test*.py',
                            help="Pattern to match test files")

        parser.add_argument('--base-branch', '-b',
                            dest='basebranch',
                            help="Branch name used by patchtest to branch from. By default, it uses the current one.")

        parser.add_argument('--base-commit', '-c',
                            dest='basecommit',
                            help="Commit ID used by patchtest to branch from. By default, it uses HEAD.")

        parser.add_argument('--debug', '-d',
                            action='store_true',
                            help='Enable debug output')

        parser.add_argument('--log-results', 
                            action='store_true', 
                            help='Enable logging to a file matching the target patch name with ".testresult" appended')


        return parser

