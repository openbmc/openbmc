#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import urllib.parse

PREFIX = "pkg:yocto"


def quote(s):
    """
    Returns the percent encoded version of the string, suitable for including
    in a PURL field
    """
    return urllib.parse.quote(s, safe="")


def get_base_purl(d):
    """
    Returns the base PURL for the current recipe (that is, the PURL without any
    additional qualifiers)
    """
    layername = d.getVar("FILE_LAYERNAME")
    bpn = d.getVar("BPN")
    pv = d.getVar("PV")

    name = f"{quote(bpn.lower())}@{quote(pv)}"

    if layername:
        return f"{PREFIX}/{quote(layername.lower())}/{name}"

    return f"{PREFIX}/{name}"
