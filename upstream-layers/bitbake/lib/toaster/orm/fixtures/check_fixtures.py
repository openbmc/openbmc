#!/usr/bin/env python3
#
# Copyright (C) 2026 Linux Foundation
# SPDX-License-Identifier: GPL-2.0-only
#

import json
import urllib.request

import gen_fixtures as fixtures

RELEASE_URL = "https://dashboard.yoctoproject.org/releases.json"
LAYER_INDEX_URL = "https://layers.openembedded.org/layerindex/api/branches/"

# Load a JSON file via REST
def fetch_json(url):
    with urllib.request.urlopen(url) as response:
        if response.getcode() == 200:
            data = response.read().decode("utf-8")
            return(json.loads(data))
        else:
            print("Couldn't access %s: %s" % (url, reponse.getcode()))
            exit(1)

# Grab the existing release branches in the Layer Index
branches = fetch_json(LAYER_INDEX_URL)
index_branches = [
    e["name"].lower() for e in branches
]

# Grab the recent release branches and add master, so we can ignore old branches
releases = fetch_json(RELEASE_URL)
active_releases = []
active_but_not_index = []
for e in releases:
    if e["series"] != "current":
        continue
    release = e["release_codename"].lower()
    if not release in index_branches:
        active_but_not_index.append(release)
    else:
        active_releases.append(release)
active_releases.append("master")
active_releases.append("head")
active_releases.sort()

if active_but_not_index:
    print(f"Note: Active releases that are not yet in the Layer Index: {active_but_not_index}" )

# Get the list of releases instantiated in the Toaster fixtures
fixtures_releases = [x[0].lower() for x in fixtures.current_releases]
fixtures_releases.sort()

# Report the resulting status
if set(active_releases) != set(fixtures_releases):
    print(f"WARNING: Active releases don't match toaster configured releases, the difference is: {set(active_releases).difference(set(fixtures_releases))}")
    print(f"Active releases: {sorted(active_releases)}")
    print(f"Toaster configured releases: {sorted(fixtures_releases)}")
else:
    print("Success, configuration matches")

