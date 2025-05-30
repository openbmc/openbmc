#!/usr/bin/env python3
#
# Copyright (C) 2025 Linux Foundation
# SPDX-License-Identifier: GPL-2.0-only
#

import json
import urllib.request

import gen_fixtures as fixtures

RELEASE_URL = "https://dashboard.yoctoproject.org/releases.json"

with urllib.request.urlopen(RELEASE_URL) as response:
    if response.getcode() == 200:
        data = response.read().decode("utf-8")
        releases = json.loads(data)
    else:
        print("Couldn't access %s: %s" % (RELEASE_URL, reponse.getcode()))
        exit(1)


# grab the recent release branches and add master, so we can ignore old branches
active_releases = [
    e["release_codename"].lower() for e in releases if e["series"] == "current"
]
active_releases.append("master")
active_releases.append("head")

fixtures_releases = [x[0].lower() for x in fixtures.current_releases]

if set(active_releases) != set(fixtures_releases):
    print("WARNING: Active releases don't match toaster configured releases, the difference is: %s" % set(active_releases).difference(set(fixtures_releases)))
    print("Active releases: %s" % sorted(active_releases))
    print("Toaster configured releases: %s" % sorted(fixtures_releases))
else:
    print("Success, configuration matches")

