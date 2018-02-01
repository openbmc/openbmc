# Class for use in BBCLASSEXTEND to make it easier to have a single recipe that
# can build both stable tarballs and snapshots from upstream source
# repositories.
#
# Usage:
# BBCLASSEXTEND = "devupstream:target"
# SRC_URI_class-devupstream = "git://git.example.com/example"
# SRCREV_class-devupstream = "abcdef"
#
# If the first entry in SRC_URI is a git: URL then S is rewritten to
# WORKDIR/git.
#
# There are a few caveats that remain to be solved:
# - You can't build native or nativesdk recipes using for example
#   devupstream:native, you can only build target recipes.
# - If the fetcher requires native tools (such as subversion-native) then
#   bitbake won't be able to add them automatically.

CLASSOVERRIDE .= ":class-devupstream"

python devupstream_virtclass_handler () {
    # Do nothing if this is inherited, as it's for BBCLASSEXTEND
    if "devupstream" not in (d.getVar('BBCLASSEXTEND') or ""):
        bb.error("Don't inherit devupstream, use BBCLASSEXTEND")
        return

    variant = d.getVar("BBEXTENDVARIANT")
    if variant not in ("target"):
        bb.error("Pass the variant when using devupstream, for example devupstream:target")
        return

    # Develpment releases are never preferred by default
    d.setVar("DEFAULT_PREFERENCE", "-1")

    uri = bb.fetch2.URI(d.getVar("SRC_URI").split()[0])

    if uri.scheme == "git":
        d.setVar("S", "${WORKDIR}/git")

    # Modify the PV if the recipe hasn't already overridden it
    pv = d.getVar("PV")
    proto_marker = "+" + uri.scheme
    if proto_marker not in pv:
        d.setVar("PV", pv + proto_marker + "${SRCPV}")
}

addhandler devupstream_virtclass_handler
devupstream_virtclass_handler[eventmask] = "bb.event.RecipePreFinalise"
