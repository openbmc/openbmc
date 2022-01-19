#
# crate-fetch class
#
# Registers 'crate' method for Bitbake fetch2.
#
# Adds support for following format in recipe SRC_URI:
# crate://<packagename>/<version>
#

def import_crate(d):
    import crate
    if not getattr(crate, 'imported', False):
        bb.fetch2.methods.append(crate.Crate())
        crate.imported = True

python crate_import_handler() {
    import_crate(d)
}

addhandler crate_import_handler
crate_import_handler[eventmask] = "bb.event.RecipePreFinalise"

def crate_get_srcrev(d):
    import_crate(d)
    return bb.fetch2.get_srcrev(d)

# Override SRCPV to make sure it imports the fetcher first
SRCPV = "${@crate_get_srcrev(d)}"
