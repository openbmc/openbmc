#
# AUTOREV and PV containing '+git' needs to be set early, before any anonymous python
# expands anything containing PV, else the parse process won't trigger the fetcher to
# cache the needed version data
#
python pokybleeding_version_handler () {
    bpn = d.getVar("BPN")
    # We're running before the class extension code at PreFinalise so manually fix BPN
    bpn = bpn.replace("-nativesdk", "").replace("nativesdk-", "")

    if bpn in d.getVar("POKY_AUTOREV_RECIPES").split():
        d.setVar("SRCREV", "${AUTOREV}")
        srcrev_format = d.getVar("SRCREV_FORMAT")
        if srcrev_format:
            for multi_scm in d.getVar("SRCREV_FORMAT").split("_"):
                if multi_scm != "":
                    d.setVar("SRCREV_%s" % multi_scm, "${AUTOREV}")
        if "+git" not in d.getVar("PV"):
            d.appendVar("PV", "+git")
}

addhandler pokybleeding_version_handler
pokybleeding_version_handler[eventmask] = "bb.event.RecipePreFinalise"
