#! /usr/bin/env python3

"""
Download the lockfile.yml produced by a CI pipeline, specified by the GitLab
server, full name of the meta-arm project, and the refspec that was executed.

For example,
$ ./download-lockfile.py https://gitlab.com/ rossburton/meta-arm master

SPDX-FileCopyrightText: Copyright 2023 Arm Limited and Contributors
SPDX-License-Identifier: GPL-2.0-only
"""

import argparse
import gitlab
import io
import zipfile

parser = argparse.ArgumentParser()
parser.add_argument("server", help="GitLab server name")
parser.add_argument("project", help="meta-arm project name")
parser.add_argument("refspec", help="Branch/commit")
args = parser.parse_args()

gl = gitlab.Gitlab(args.server)
project = gl.projects.get(args.project)
artefact = project.artifacts.download(ref_name=args.refspec, job="update-repos")

z = zipfile.ZipFile(io.BytesIO(artefact))
z.extract("lockfile.yml")
print("Fetched lockfile.yml")
